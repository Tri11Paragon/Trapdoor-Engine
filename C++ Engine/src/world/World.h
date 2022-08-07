//
// Created by brett on 29/07/22.
//

#ifndef ENGINE_WORLD_H
#define ENGINE_WORLD_H

#include <utility>

#include "../std.h"
#include "../renderer/gl.h"
#include "../renderer/camera.h"
#include "../renderer/shader.h"
#include "../renderer/renderer.h"
#include "../hashmaps.h"
#include "GameRegistry.h"

namespace TD {

#define MESH_RENDERER_SYSTEM "MeshComponent"
#define TRANSFORM_SYSTEM "TransformComponent"

    typedef unsigned int ID;

    extern ID entityID;

    /**
     * Components are the data handlers of the trapdoor engine. They are to purely store the data of the entity.]
     * There should be a corresponding system for the component.
     */
    class Component {
    private:
        // the ID used to index the array containing the vectors of the component type.
        const std::string name;
        ID associatedEntity;
    public:
        explicit Component(std::string  name): name(std::move(name)) {}
        const std::string& getName(){return name;}
        const ID getAssociatedEntity() const {return associatedEntity;}
        void setAssociatedEntity(ID id) {this->associatedEntity = id;}
    };

    class TransformComponent : public Component {
    private:
        glm::vec3 translate = glm::vec3(0.0);
        glm::vec3 rotation = glm::vec3(0.0);
        glm::vec3 scale = glm::vec3(1.0);
    public:
        explicit TransformComponent(): Component(TRANSFORM_SYSTEM) {}
        void setTranslation(glm::vec3 vec){this->translate = vec;}
        void setRotation(glm::vec3 vec){this->rotation = vec;}
        void setScale(glm::vec3 vec){this->scale = vec;}
        const glm::vec3& getTranslation(){return translate;}
        const glm::vec3& getRotation(){return rotation;}
        const glm::vec3& getScale(){return scale;}
    };

    class MeshComponent : public Component {
    private:
        const std::string modelName;
    public:
        explicit MeshComponent(std::string  modelName): modelName(std::move(modelName)), Component(MESH_RENDERER_SYSTEM) {}
        inline std::string getModelName(){return modelName;}
    };

    /**
     * Entity is basically just a glorified struct
     */
    class Entity {
    private:
        const ID id;
        const std::string name;
        std::vector<dPtr<Component>> entityComponents;
    public:
        explicit Entity(std::string name): name(std::move(name)), id(entityID++) { dPtr<Component> ptr(new TransformComponent()); addComponent(ptr);}
        void addComponent(dPtr<Component> ptr){
            ptr->setAssociatedEntity(id);
            entityComponents.push_back(ptr);
        }
        void removeAllComponentByName(const std::string& entName){
            // likely not the best way to do this, but I needed to access the dPtr's data
            for (int i = 0; i < entityComponents.size(); i++){
                auto c = entityComponents[i];
                if (c.isValid()) {
                    if (c->getName() == entName) {
                        entityComponents.erase(entityComponents.begin() + i);
                    }
                }
            }
        }
        dPtr<Component> getComponent(std::string str) const {
            for (auto i : entityComponents){
                if (i->getName() == str)
                    return i;
            }
            wlog << "Unable to find component!";
            wlog << "Of Name: " << str;
            return dPtr<Component>(nullptr);
        }
        [[nodiscard]] const std::vector<dPtr<Component>>& getComponents() const {return entityComponents;}
        [[nodiscard]] const std::string& getName() const {return name;}
        [[nodiscard]] const ID getID() const {return id;}
    };

    class World;

    /**
     * Systems define functions of what to do with entities
     */
    class System {
    protected:
        World& world;
    public:
        // systems get a reference to the world.
        explicit System(World& world): world(world) {}
        // called every time that the world needs to render something to the screen.
        // the shader provided is the shader which should be used to render
        // you are not required to use it however there is always a reason for using the supplied shader.
        virtual void render(shader* shader) = 0;
        // only called once per render cycle, normally during the screen rendering proportion
        // only use this if you need to draw something to the screen that doesn't need to go on extras
        // like the shader buffer, you wouldn't want particles to show up in it,
        // so render particles here. -- use a particle system btw just an example
        virtual void renderOnce() = 0;
        virtual void update() = 0;
    };

    /*class Entity {
    protected:
        // TODO: replace with bullet stuff,
        glm::vec3 position = glm::vec3(0, 0, 0);
        glm::vec3 rotation = glm::vec3(0, 0, 0);
        glm::vec3 scale = glm::vec3(1, 1, 1);
        std::string modelName;
    public:
        Entity(std::string modelName) { this->modelName = modelName; }

        inline glm::mat4 getTranslationMatrix() {
            glm::mat4 trans(1.0);
            trans = glm::translate(trans, position);
            // rotates are relatively expensive, so don't do them unless we have to.
            if (rotation.x != 0)
                trans = glm::rotate(trans, glm::radians(rotation.x), glm::vec3(1,0,0));
            if (rotation.y != 0)
                trans = glm::rotate(trans, glm::radians(rotation.y), glm::vec3(0,1,0));
            if (rotation.z != 0)
                trans = glm::rotate(trans, glm::radians(rotation.z), glm::vec3(0,0,1));
            trans = glm::scale(trans, scale);
            return trans;
        }

        // called when the entity is rendered
        virtual void render() = 0;

        // called when the entity is updated, world;
        virtual void update() = 0;

        inline glm::vec3 getPosition() { return position; }

        inline glm::vec3 getRotation() { return rotation; }

        inline glm::vec3 getScale() { return scale; }

        inline std::string getModelName() { return modelName; }
    };

    class StaticEntity : public Entity {
    public:
        StaticEntity(std::string modelName, glm::vec3 pos): StaticEntity(modelName, pos, glm::vec3(0), glm::vec3(1)){}
        StaticEntity(std::string modelName, glm::vec3 pos, glm::vec3 scale): StaticEntity(modelName, pos, glm::vec3(0), scale) {}
        StaticEntity(std::string modelName, glm::vec3 pos, glm::vec3 rotation, glm::vec3 scale): Entity(modelName) {
            this->position = pos;
            this->rotation = rotation;
            this->scale = scale;
        }
        virtual void render(){

        }
        virtual void update(){

        }
    };*/

    class World {
    private:
        parallel_flat_hash_map<std::string, dPtr<TD::Entity>> entityMap;
        parallel_flat_hash_map<std::string, flat_hash_map<ID, dPtr<Component>>> components;
        std::vector<dPtr<System>> systems;

        TD::skyboxRenderer skyboxRenderer;
        TD::gBufferFBO gBufferFbo;
        TD::shadowFBO shadowFbo;
        TD::shader fxaaShader = TD::shader("../assets/shaders/postprocessing/filter-fxaa.vert", "../assets/shaders/postprocessing/filter-fxaa.frag");
    public:
        World();
        void render();
        void update();

        inline flat_hash_map<ID, dPtr<Component>>& getComponents(const std::string& name){return components.at(name);}

        void createSystem(System* system){dPtr<System> ptr(system); systems.push_back(ptr);}

        // the entity pointer should be a pointer to a *new* entity
        // this will be deleted when the world is deleted or
        // when deleteEntity(entityName) is called.
        void spawnEntity(Entity* entity);
        void addComponentToEntity(const std::string& name, Component* component);
        void deleteEntity(const std::string& entityName);
        void updateLights(std::vector<TD::Light> lights);
        inline TD::shader* getFirstPassShader() { return gBufferFbo.getFirstPassShader(); }
        inline void updateDirectionalLighting(glm::vec3 dir, glm::vec3 color, bool enabled) {shadowFbo.updateLightDirection(dir); gBufferFbo.updateDirLight(dir, color, enabled);}
        ~World();
    };

    class MeshRendererSystem : public System {
    private:

    public:
        explicit MeshRendererSystem(World& world): System(world) {}
        virtual void render(shader* shader);
        virtual void renderOnce();
        virtual void update();
    };

}


#endif //ENGINE_WORLD_H

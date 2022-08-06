//
// Created by brett on 29/07/22.
//

#ifndef ENGINE_WORLD_H
#define ENGINE_WORLD_H

#include "../std.h"
#include "../renderer/gl.h"
#include "../renderer/camera.h"
#include "../renderer/shader.h"
#include "../renderer/renderer.h"
#include "../hashmaps.h"
#include "GameRegistry.h"

namespace TD {

    typedef unsigned long ID;

    /**
     * Entity is basically just a glorified struct
     */
    class Entity {
    private:
        // ID is unique to every entity
        const ID id;
    public:
        explicit Entity(ID id): id(id){}
        ID getID() const {return id;}
    };

    /**
     * Components are the data handlers of the trapdoor engine. They are to purely store the data of the entity.]
     * There should be a corresponding system for the component.
     */
    class Component {
    private:
        // MAX 4bil components in the engine, that's enough right?
        // these are not unique to every entity, they are unique to every component type.
        const ID id;
    public:
        explicit Component(ID id): id(id){}
        ID getID() const {return id;}
    };

    class World;

    /**
     * Systems define functions of what to do with entities
     */
    class System {
        const World& world;
    public:
        // systems get a reference to the world.
        explicit System(World& world): world(world) {}
        virtual void render() = 0;
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

    class ECSRegsitry {
        
    };

    class World {
    private:
        std::vector<TD::Entity*> entities;

        TD::camera* camera;
        TD::skyboxRenderer skyboxRenderer;
        parallel_flat_hash_map<std::string, TD::Entity*> entityMap;
        TD::gBufferFBO gBufferFbo;
        TD::shadowFBO shadowFbo;
        TD::shader fxaaShader = TD::shader("../assets/shaders/postprocessing/filter-fxaa.vert", "../assets/shaders/postprocessing/filter-fxaa.frag");
    public:
        World();
        void render();
        void update();
        // the entity pointer should be a pointer to a *new* entity
        // this will be deleted when the world is deleted or
        // when deleteEntity(entityName) is called.
        void spawnEntity(std::string entityName, Entity* entity);
        void deleteEntity(std::string entityName);
        void updateLights(std::vector<TD::Light> lights);
        inline TD::shader* getFirstPassShader() { return gBufferFbo.getFirstPassShader(); }
        inline void updateDirectionalLighting(glm::vec3 dir, glm::vec3 color, bool enabled) {shadowFbo.updateLightDirection(dir); gBufferFbo.updateDirLight(dir, color, enabled);}
        ~World();
    };

}


#endif //ENGINE_WORLD_H

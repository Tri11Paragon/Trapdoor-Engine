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
#include "../imgui/imgui.h"
#include <data/NBT.h>

namespace TD {

#define MESH_RENDERER_COMPONENT "MeshComponent"
#define TRANSFORM_COMPONENT "TransformComponent"
#define AUDIO_PLAYER_COMPONENT "AudioPlayerComponent"

#define MESH_RENDERER_SYSTEM "MeshSystem"

    typedef unsigned int ID;

    extern ID entityID;

    /**
     * Components are the data handlers of the trapdoor engine. They are to purely store the data of the entity.]
     * There should be a corresponding system for the component.
     */
    class Component {
    private:
        // the ID used to index the array containing the vectors of the component type.
        ID associatedEntity{0};
        std::vector<std::string> associatedSystems {};
    public:
        Component() = default;
        explicit Component(std::vector<std::string> associatedSystems): associatedSystems(std::move(associatedSystems)) {}
        virtual constexpr std::string getName() = 0;
        // use this function to add your variables to the imgui inspector.
        virtual void drawImGuiVariables() = 0;
        virtual Component* allocateDefault() = 0;
        virtual Component* allocateData() = 0;
        virtual TAG_COMPOUND* save() = 0;
        virtual void load(TAG_COMPOUND* tag) = 0;
        void setAssociatedEntity(ID id) {this->associatedEntity = id;}
        [[nodiscard]] ID getAssociatedEntity() const {return associatedEntity;}
        [[nodiscard]] std::vector<std::string> getAssociatedSystems(){return associatedSystems;}
        virtual ~Component() = default;
    };

    class TransformComponent : public Component {
    private:
        glm::vec3 translate = glm::vec3(0.0);
        glm::vec3 rotation = glm::vec3(0.0);
        glm::vec3 scale = glm::vec3(1.0);
    public:
        TransformComponent() = default;
        void setTranslation(glm::vec3 vec){this->translate = vec;}
        void setRotation(glm::vec3 vec){this->rotation = vec;}
        void setScale(glm::vec3 vec){this->scale = vec;}
        virtual void drawImGuiVariables(){
            float transFloat[3], rotFloat[3], scaleFloat[3];
            transFloat[0] = translate.x;transFloat[1] = translate.y;transFloat[2] = translate.z;
            rotFloat[0] = rotation.x;rotFloat[1] = rotation.y;rotFloat[2] = rotation.z;
            scaleFloat[0] = scale.x;scaleFloat[1] = scale.y;scaleFloat[2] = scale.z;
            if (ImGui::InputFloat3("Translation", transFloat, nullptr, ImGuiInputTextFlags_EnterReturnsTrue)) {
                translate = glm::vec3(transFloat[0], transFloat[1], transFloat[2]);
            }
            if(ImGui::InputFloat3("Rotation", rotFloat, nullptr, ImGuiInputTextFlags_EnterReturnsTrue))
                rotation = glm::vec3(rotFloat[0], rotFloat[1], rotFloat[2]);
            if(ImGui::InputFloat3("Scale", scaleFloat, nullptr, ImGuiInputTextFlags_EnterReturnsTrue))
                scale = glm::vec3(scaleFloat[0], scaleFloat[1], scaleFloat[2]);
        }
        virtual Component* allocateDefault() {
            return new TransformComponent();
        }
        virtual Component* allocateData() {
            return new TransformComponent();
        }
        virtual TAG_COMPOUND* save(){
            auto* compound = new TAG_COMPOUND(this->getName());

            compound->put(new TAG_FLOAT("tx", translate.x));
            compound->put(new TAG_FLOAT("ty", translate.y));
            compound->put(new TAG_FLOAT("tz", translate.z));

            compound->put(new TAG_FLOAT("rx", rotation.x));
            compound->put(new TAG_FLOAT("ry", rotation.y));
            compound->put(new TAG_FLOAT("rz", rotation.z));

            compound->put(new TAG_FLOAT("sx", scale.x));
            compound->put(new TAG_FLOAT("sy", scale.y));
            compound->put(new TAG_FLOAT("sz", scale.z));

            return compound;
        }
        virtual void load(TAG_COMPOUND* tag){
            this->translate.x = tag->get<TAG_FLOAT>("tx")->getPayload();
            this->translate.y = tag->get<TAG_FLOAT>("ty")->getPayload();
            this->translate.z = tag->get<TAG_FLOAT>("tz")->getPayload();

            this->rotation.x = tag->get<TAG_FLOAT>("rx")->getPayload();
            this->rotation.y = tag->get<TAG_FLOAT>("ry")->getPayload();
            this->rotation.z = tag->get<TAG_FLOAT>("rz")->getPayload();

            this->scale.x = tag->get<TAG_FLOAT>("sx")->getPayload();
            this->scale.y = tag->get<TAG_FLOAT>("sy")->getPayload();
            this->scale.z = tag->get<TAG_FLOAT>("sz")->getPayload();
        }
        glm::mat4 getTranslationMatrix(){
            glm::mat4 trans(1.0);
            trans = glm::translate(trans, translate);
            //tlog << meshPtr->getAssociatedEntity() << " " << transform->getTranslation().x << " " << transform->getTranslation().y << " " << transform->getTranslation().z;
            // rotates are relatively expensive, so don't do them unless we have to.
            if (rotation.x != 0)
                trans = glm::rotate(trans, glm::radians(rotation.x), glm::vec3(1, 0, 0));
            if (rotation.y != 0)
                trans = glm::rotate(trans, glm::radians(rotation.y), glm::vec3(0, 1, 0));
            if (rotation.z != 0)
                trans = glm::rotate(trans, glm::radians(rotation.z), glm::vec3(0, 0, 1));
            trans = glm::scale(trans, scale);
            return trans;
        }
        const glm::vec3& getTranslation(){return translate;}
        const glm::vec3& getRotation(){return rotation;}
        const glm::vec3& getScale(){return scale;}
        virtual constexpr std::string getName(){return TRANSFORM_COMPONENT;}
    };

    class MeshComponent : public Component {
    private:
        std::string modelName;
        char stringBuffer[512]{};
    public:
        explicit MeshComponent(const std::string&  modelName): modelName(modelName), Component({MESH_RENDERER_SYSTEM}) {
            strcpy(stringBuffer, modelName.c_str());
        }
        virtual void drawImGuiVariables(){
            ImGui::Text("Model Path: ");
            ImGui::SameLine();

            ImGui::InputText("01", stringBuffer, 512);
            Strtrim(stringBuffer);
            if (stringBuffer[0]){
                modelName = std::string(stringBuffer);
            }
        }
        virtual Component* allocateDefault() {
            return new MeshComponent("");
        }
        virtual Component* allocateData() {
            return new MeshComponent(modelName);
        }
        virtual TAG_COMPOUND* save(){
            auto* compound = new TAG_COMPOUND(this->getName());

            compound->put(new TAG_STRING("modelName", modelName));

            return compound;
        }
        virtual void load(TAG_COMPOUND* tag){
            this->modelName = tag->get<TAG_STRING>("modelName")->getPayload();
            strcpy(stringBuffer, modelName.c_str());
        }
        inline std::string getModelName(){return modelName;}
        virtual constexpr std::string getName(){return MESH_RENDERER_COMPONENT;}
    };

    class AudioPlayerComponent : public Component {
    private:
        std::string audioFile;
        char stringBuffer[512]{};
    public:
        explicit AudioPlayerComponent(const std::string&  audioFile): audioFile(audioFile) {
            strcpy(stringBuffer, audioFile.c_str());
        }
        virtual void drawImGuiVariables(){
            ImGui::Text("Audio Path: ");
            ImGui::SameLine();

            ImGui::InputText("02", stringBuffer, 512, ImGuiInputTextFlags_EnterReturnsTrue);
            Strtrim(stringBuffer);
            if (stringBuffer[0]){
                audioFile = std::string(stringBuffer);
            }
        }
        virtual Component* allocateDefault() {
            return new AudioPlayerComponent("");
        }
        virtual Component* allocateData() {
            return new AudioPlayerComponent(audioFile);
        }
        virtual TAG_COMPOUND* save(){
            auto* compound = new TAG_COMPOUND(this->getName());

            compound->put(new TAG_STRING("audioFile", audioFile));

            return compound;
        }
        virtual void load(TAG_COMPOUND* tag){
            this->audioFile = tag->get<TAG_STRING>("audioFile")->getPayload();
            strcpy(stringBuffer, audioFile.c_str()
        }
        inline std::string getAudioFile(){return audioFile;}
        virtual constexpr std::string getName(){return AUDIO_PLAYER_COMPONENT;}
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
        [[nodiscard]] dPtr<Component> getComponent(std::string str) const {
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
        [[nodiscard]] ID getID() const {return id;}
    };

    class World;

    /**
     * Systems define functions of what to do with entities
     */
    class System {
    protected:
        World* world;
    public:
        // systems get a reference to the world.
        explicit System(World* world): world(world) {}
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

        virtual System* allocateDefault(World* world) = 0;

        virtual ~System() = default;
    };

    // deallocated at the closing of the window.
    extern parallel_flat_hash_map<std::string, Component*> componentAllocators;
    extern parallel_flat_hash_map<std::string, System*> systemAllocators;

    class World {
    private:
        parallel_flat_hash_map<std::string, dPtr<TD::Entity>> entityMap;
        parallel_flat_hash_map<std::string, flat_hash_map<ID, dPtr<Component>>> components;
        std::vector<dPtr<TD::Entity>> entityList;
        std::vector<System*> systems;
        flat_hash_map<std::string, System*> systemMap;

        TD::skyboxRenderer skyboxRenderer;
        TD::gBufferFBO gBufferFbo;
        TD::shadowFBO shadowFbo;
        //TD::shader fxaaShader = TD::shader("../assets/shaders/postprocessing/filter-fxaa.vert", "../assets/shaders/postprocessing/filter-fxaa.frag");

        World(const World& that); // Disable Copy Constructor
        World& operator=(const World& that); // Disable Copy Assignment
    public:
        World(World &&) noexcept = delete; // Disable move constructor.
        World& operator=(World &&) noexcept = delete; // Disable Move Assignment
        World();
        void render();
        void update();
        virtual TAG_COMPOUND* save();
        virtual void load(TAG_COMPOUND* tag);

        inline flat_hash_map<ID, dPtr<Component>>& getComponents(const std::string& name){return components.at(name);}

        // the entity pointer should be a pointer to a *new* entity
        // this will be deleted when the world is deleted or
        // when deleteEntity(entityName) is called.
        void spawnEntity(Entity* entity);
        void addComponentToEntity(const std::string& name, Component* component);
        void deleteEntity(const std::string& entityName);
        void updateLights(std::vector<TD::Light> lights);
        inline bool hasComponent(const std::string& componentName, ID entID){
            try {
                auto comp = components.at(componentName);
                return comp.find(entID) != comp.end();
            } catch (std::exception& e){}
            return false;
        }
        template<class T>
        inline bool hasComponent(ID entID){
            T t {};
            return hasComponent(t.getName(), entID);
        }
        inline std::string getEntityNameByID(ID id){
            for (auto e : entityMap){
                if (e.second.isValid()){
                    if(e.second->getID() == id)
                        return e.second->getName();
                }
            }
            return "";
        }
        inline dPtr<Entity> getEntity(const std::string& entityName){return entityMap.at(entityName);}
        /**
         * This version of getComponent will return the dPtr to the entity component in question
         * will throw an exception if T isn't a component or entity ID doesn't have said component
         * @tparam T
         * @param entID
         * @return
         */
        template<class T>
        inline dPtr<Component> getComponent(ID entID){
            T t {};
            return components.at(t.getName()).at(entID);
        }
        /**
         * this version of get component will return a raw ptr to the component, if it exists
         * otherwise will return a nullptr.
         * @tparam T
         * @param entID
         * @return
         */
        template<class T>
        inline T* getComponentRaw(ID entID){
            try {
                T t {};
                auto cmpMap = components.at(t.getName());
                auto cmp = cmpMap.at(entID);
                try {
                    if (cmp.isValid())
                        return static_cast<T *>(cmp.getRaw());
                } catch (std::exception &e) {
                    wlog << "Warning!! Error occurred trying to cast raw dPtr! Are your types correct? " << e.what();
                }
            } catch (std::exception &e){
                wlog << "Warning!! Error occurred trying to get component. Does entID have this component? Does this component exist?" << e.what();
            }
            return nullptr;
        }
        inline TD::shader* getFirstPassShader() { return gBufferFbo.getFirstPassShader(); }
        inline void updateDirectionalLighting(glm::vec3 dir, glm::vec3 color, bool enabled) {shadowFbo.updateLightDirection(dir); gBufferFbo.updateDirLight(dir, color, enabled);}
        inline auto begin() noexcept { return entityMap.begin(); }
        inline auto cbegin() const noexcept { return entityMap.cbegin(); }
        inline auto end() noexcept { return entityMap.end(); }
        inline auto cend() const noexcept { return entityMap.cend(); }
        inline parallel_flat_hash_map<std::string, dPtr<TD::Entity>> getEntities(){return entityMap;}
        inline std::vector<dPtr<Entity>> getEntitiesList(){return entityList;}
        ~World();
    };

    class MeshRendererSystem : public System {
    private:

    public:
        explicit MeshRendererSystem(World* world): System(world) {}
        virtual void render(shader* shader);
        virtual void renderOnce();
        virtual void update();
        virtual System* allocateDefault(World* wrld){
            return new MeshRendererSystem(wrld);
        }
    };

    static void registerAllocators(){
        componentAllocators.insert(std::pair(MESH_RENDERER_COMPONENT, new MeshComponent("")));
        componentAllocators.insert(std::pair(AUDIO_PLAYER_COMPONENT, new AudioPlayerComponent("")));

        systemAllocators.insert(std::pair(MESH_RENDERER_SYSTEM, new MeshRendererSystem(nullptr)));
    }

}


#endif //ENGINE_WORLD_H

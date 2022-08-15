//
// Created by brett on 29/07/22.
//

#include "world/World.h"
#include "renderer/ui/debug.h"
#include <config.h>

namespace TD {

    // ---------------{World}---------------

    extern TD::camera* activeCamera;

    World::World() {
        tlog << "World Created";
        TD::shader* shader = gBufferFbo.getSecondPassShader();
        shader->use();
        shader->setInt("cascadeCount", shadowFbo.shadowCascadeLevels.size());
        for (size_t i = 0; i < shadowFbo.shadowCascadeLevels.size(); ++i) {
            shader->setFloat("cascadePlaneDistances[" + std::to_string(i) + "]", shadowFbo.shadowCascadeLevels[i]);
        }
    }

    World::~World() {
        for (auto ptr : entityMap)
            ptr.second.free();
        for (const auto& ptr : components){
            for (auto ele : ptr.second){
                ele.second.free();
            }
        }
        for (auto ptr : systems)
            delete(ptr);
        tlog << "World Deleted";
    }

    void World::render() {
        shadowFbo.bind();
        for (auto system : systems)
            system->render(shadowFbo.getShader());
        shadowFbo.finish();
        gBufferFbo.bindFirstPass();
        for (auto system : systems) {
            system->render(gBufferFbo.getFirstPassShader());
            system->renderOnce();
        }
#ifdef DEBUG_ENABLED
        TD::Editor::renderGBuffer();
#endif
        skyboxRenderer.render();
        gBufferFbo.unbindFBO();
        // FBO is required
        //fxaaShader.use();
        shadowFbo.bindDepthTextureArray();
        gBufferFbo.bindSecondPass(*activeCamera);
    }

    void World::update() {
        for (auto system : systems)
            system->update();
    }

    void World::spawnEntity(Entity *entity) {
        if (entityMap.find(entity->getName()) == entityMap.end()) {
            dPtr<Entity> entPtr(entity);
            entityList.push_back(entPtr);
            entityMap.insert(std::pair(entity->getName(), entPtr));
            for (auto c : entity->getComponents()){
                if (components.find(c->getName()) == components.end()){
                    flat_hash_map<ID, dPtr<Component>> vtr;
                    components.insert(std::pair(c->getName(), vtr));
                }
                components.at(c->getName()).insert(std::pair(entity->getID(), c));
            }
        } else
            wlog << "Entity {" << entity->getName() << "} with name already exists, not adding to world!";
    }

    void World::addComponentToEntity(const std::string& name, Component *component) {
        if (entityMap.find(name) == entityMap.end()) {
            wlog << "Unable to find entity. Not adding component! (Component has been deleted)";
            delete(component);
            return;
        }
        dPtr<Component> compPtr(component);
        dPtr<Entity> entPtr = entityMap.at(name);
        entPtr->addComponent(compPtr);
        if (components.find(component->getName()) == components.end()){
            flat_hash_map<ID, dPtr<Component>> vtr;
            components.insert(std::pair(component->getName(), vtr));
        }
        components.at(component->getName()).insert(std::pair(entPtr->getID(), compPtr));
    }

    void World::deleteEntity(const std::string& entityName) {
        auto it = entityMap.find(entityName);
        // only remove the entity if it exists
        if (it != entityMap.end()) {
            dPtr<Entity> ptr = entityMap.at(entityName);

            // free all the component memory
            for (auto c : ptr->getComponents()){
                flat_hash_map<ID, dPtr<Component>>& mapVtr = components.at(c->getName());
                auto mapItr = mapVtr.find(ptr->getID());
                if (mapItr != mapVtr.end()){
                    mapVtr.erase(mapItr);
                }
                c.free();
            }
            // delete ent memory, which is actually pretty small now.
            entityMap.erase(it);
            ptr.free();
        }
    }

    void World::updateLights(std::vector<TD::Light> lights) {
        gBufferFbo.updateLights(lights);
    }

    void MeshRendererSystem::render(shader* shader) {
        auto &meshComponents = world.getComponents(MESH_RENDERER_COMPONENT);
        auto &transforms = world.getComponents(TRANSFORM_COMPONENT);
        for (auto mesh : meshComponents){
            auto meshPtr = mesh.second.get();
            if (meshPtr){
                try {
                    auto *castedMeshPtr = static_cast<MeshComponent *>(meshPtr);
                    std::string model = castedMeshPtr->getModelName();
                    // transform always exists on entity, no need to check.
                    auto *transform = static_cast<TransformComponent*>(transforms.at(meshPtr->getAssociatedEntity()).get());
                    glm::mat4 trans(1.0);
                    trans = glm::translate(trans, transform->getTranslation());
                    //tlog << meshPtr->getAssociatedEntity() << " " << transform->getTranslation().x << " " << transform->getTranslation().y << " " << transform->getTranslation().z;
                    // rotates are relatively expensive, so don't do them unless we have to.
                    glm::vec3 rotation = transform->getRotation();
                    if (rotation.x != 0)
                        trans = glm::rotate(trans, glm::radians(rotation.x), glm::vec3(1, 0, 0));
                    if (rotation.y != 0)
                        trans = glm::rotate(trans, glm::radians(rotation.y), glm::vec3(0, 1, 0));
                    if (rotation.z != 0)
                        trans = glm::rotate(trans, glm::radians(rotation.z), glm::vec3(0, 0, 1));
                    trans = glm::scale(trans, transform->getScale());
                    GameRegistry::getModel(model)->draw(*shader, trans);
                } catch (std::exception& e){}
            } else {
                flog << "Mesh isn't valid! Did you fail to delete it correctly?";
                flog << "(if you are seeing this the engine has failed, please report this.)";
                flog << "(use debugger tools to give a complete report, maybe even supply your project files UwU)";
                flog << "The engine does not support furries.";
                exit(-1);
            }

        }
    }

    void MeshRendererSystem::renderOnce() {

    }

    void MeshRendererSystem::update() {

    }
}

//
// Created by brett on 29/07/22.
//

#include "World.h"

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
        for (auto ptr : components){
            for (auto ele : ptr.second){
                ele.free();
            }
        }
        tlog << "World Deleted";
    }

    void World::render() {
        shadowFbo.bind();
        for (auto system : systems)
            system->render();
        shadowFbo.finish();
        gBufferFbo.bindFirstPass();
        for (auto system : systems) {
            system->render();
            system->renderOnce();
        }
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
            entityMap.insert(std::pair(entity->getName(), entPtr));
            for (auto c : entity->getComponents()){
                if (components.find(c->getName()) == components.end()){
                    std::vector<dPtr<Component>> vtr;
                    components.insert(std::pair(c->getName(), vtr));
                }
                components.at(c->getName()).push_back(c);
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
        entityMap.at(name)->addComponent(compPtr);
        if (components.find(component->getName()) == components.end()){
            std::vector<dPtr<Component>> vtr;
            components.insert(std::pair(component->getName(), vtr));
        }
        components.at(component->getName()).push_back(compPtr);
    }

    void World::deleteEntity(std::string entityName) {
        auto it = entityMap.find(entityName);
        // only remove the entity if it exists
        if (it != entityMap.end()) {
            dPtr<Entity> ptr = entityMap.at(entityName);

            // free all the component memory
            for (auto c : ptr->getComponents()){
                std::vector<dPtr<Component>>& vectorVtr = components.at(c->getName());
                for (int i = 0; i < vectorVtr.size(); i++){
                    if (vectorVtr[i]->getAssociatedEntity() == entityName){
                        vectorVtr.erase(vectorVtr.begin() + i);
                    }
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

}

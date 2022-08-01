//
// Created by brett on 29/07/22.
//

#include "World.h"

namespace TD {

    // ---------------{World}---------------

    extern TD::camera* activeCamera;

    World::World() {

    }

    World::~World() {
        for (auto ptr : entityMap)
            delete(ptr.second);
    }

    void World::render() {
        gBufferFbo.bindFirstPass();
        for (auto ptr : entityMap) {
            // TODO: batching / instancing, components?
            ptr.second->render();
            std::string modelName = ptr.second->getModelName();
            try {
                TD::GameRegistry::getModel(modelName)->draw(*gBufferFbo.getFirstPassShader(), ptr.second->getTranslationMatrix());
            } catch(std::out_of_range& e) {
                flog << "Unable to find " << modelName << " in the loaded model list. (Did you forget to register it?)";
            }
        }
        skyboxRenderer.render();
        gBufferFbo.unbindFBO();
        // FBO is required
        //fxaaShader.use();
        gBufferFbo.bindSecondPass(*activeCamera);
    }

    void World::update() {
        for (auto ptr : entityMap)
            ptr.second->update();
    }

    void World::spawnEntity(std::string entityName, Entity *entity) {
        if (entityMap.find(entityName) == entityMap.end())
            entityMap.insert(std::pair(entityName, entity));
        else
            wlog << "Entity {" << entityName << "} with name already exists, not adding to world!";
    }

    void World::deleteEntity(std::string entityName) {
        TD::Entity* ptr = entityMap.at(entityName);
        auto it = entityMap.find(entityName);
        if (it != entityMap.end()) {
            entityMap.erase(it);
            delete(ptr);
        }
    }

    void World::updateLights(std::vector<TD::Light> lights) {
        gBufferFbo.updateLights(lights);
    }
}

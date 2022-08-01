//
// Created by brett on 29/07/22.
//

#include "World.h"
#include <thread>
#include <atomic>
#include "../font.h"
#include "../logging.h"
#include "../hashmaps.h"
#include <chrono>

namespace TD {

    // ---------------{Threads}---------------

    const unsigned int processor_count = std::thread::hardware_concurrency() > 0 ? std::thread::hardware_concurrency() : 4;
    extern volatile bool _isWindowOpen;
    extern std::vector<std::queue<std::pair<std::string, std::string>>> unloadedModels;
    extern std::vector<std::queue<std::pair<std::string, std::string>>> unloadedTextures;
    extern parallel_node_hash_map<std::string, TD::model*> loadedModels;
    extern parallel_node_hash_map<std::string, TD::Texture> loadedTextures;
    extern bool queuesCreated;
    extern vector<TD::font> fonts;
    std::vector<std::thread*> modelThreads;
    std::vector<std::thread*> textureThreads;
    std::atomic<int> done(0);
    std::atomic<int> modelLoaded(0);

    void runModelThread(int threadID){
        std::queue<std::pair<std::string, std::string>>& modelQueue = unloadedModels[threadID];
        // first load all the models
        while (!modelQueue.empty()){
            auto modelToLoad = modelQueue.front();

            std::string ident = modelToLoad.first;
            std::string path = modelToLoad.second;
            TD::model* model = new TD::model(false, path);
            loadedModels.insert(std::pair(ident, model));
            dlog << "Loaded Model " << ident << " From " << path;

            modelQueue.pop();
        }
        modelLoaded++;
        std::queue<std::pair<std::string, std::string>>& textureQueue = unloadedTextures[threadID];
        // then load all the textures
        while (!textureQueue.empty() || modelLoaded < modelThreads.size()){
            if (!textureQueue.empty()) {
                auto textureToLoad = textureQueue.front();

                std::string ident = textureToLoad.first;
                std::string path = textureToLoad.second;
                TD::texture *tex = new TD::texture(false, path);
                loadedTextures.insert(std::pair(ident, TD::Texture(tex, DIFFUSE, path)));
                dlog << "Loaded Texture " << ident << " From " << path;

                textureQueue.pop();
            } else
                std::this_thread::sleep_for(std::chrono::milliseconds(16));
        }
        // finally then we are done and the loaded assets can be sent to the GPU.
        done++;
    }

    void Threadpool::createThreadPool() {
        for (int i = 0; i < processor_count; i++)
            modelThreads.push_back(new std::thread(runModelThread, i));
    }

    void Threadpool::deleteThreads() {
        for (int i = 0; i < modelThreads.size(); i++) {
            modelThreads[i]->join();
            delete(modelThreads[i]);
        }
        for (int i = 0; i < textureThreads.size(); i++) {
            textureThreads[i]->join();
            delete(textureThreads[i]);
        }
    }

    bool Threadpool::loadingComplete() {
        return done >= textureThreads.size() && modelLoaded >= modelThreads.size();
    }

    void Threadpool::createQueues() {
        for (int i = 0; i < processor_count; i++) {
            unloadedModels.emplace_back();
            unloadedTextures.emplace_back();
        }
        queuesCreated = true;
    }

    // ---------------{GameRegistry}---------------

    std::vector<void* (*)()> callbacks;

    void GameRegistry::registerRegistrationCallback(void *(*funcion)()) {
        callbacks.push_back(funcion);
    }

    void GameRegistry::registerModel(std::string unlocalizedName, std::string modelPath) {
        if (!queuesCreated)
            TD::Threadpool::createQueues();
        int smallest = 1073741824;
        int smallestPos = 0;
        for (int i = 0; i < unloadedModels.size(); i++){
            if (unloadedModels[i].size() < smallest) {
                smallest = unloadedModels[i].size();
                smallestPos = i;
            }
        }
        unloadedModels[smallestPos].push(std::pair(unlocalizedName, modelPath));
    }

    void GameRegistry::registerTexture(std::string unlocalizedName, std::string texturePath) {
        if (!queuesCreated)
            TD::Threadpool::createQueues();
        int smallest = 1073741824;
        int smallestPos = 0;
        for (int i = 0; i < unloadedTextures.size(); i++){
            if (unloadedTextures[i].size() < smallest) {
                smallest = unloadedTextures[i].size();
                smallestPos = i;
            }
        }
        unloadedTextures[smallestPos].push(std::pair(unlocalizedName, texturePath));
    }

    void GameRegistry::registerThreaded() {
        for (int i = 0; i < callbacks.size(); i++){
            callbacks[i]();
        }
        TD::Threadpool::createThreadPool();
    }

    void GameRegistry::deleteResources() {
        for (auto pair : loadedModels)
            delete(pair.second);
        for (auto pair : loadedTextures)
            delete(pair.second.texture);
    }

    TD::model* GameRegistry::getModel(std::string unlocalizedName) {
        return loadedModels.at(unlocalizedName);
    }

    TD::Texture GameRegistry::getTexture(std::string unlocalizedName) {
        return loadedTextures.at(unlocalizedName);
    }

    void GameRegistry::loadToGPU() {
        for (auto pair : loadedModels)
            pair.second->loadToGL();
        for (auto pair : loadedTextures)
            pair.second.texture->loadGLTexture();
    }

    void GameRegistry::registerFont(std::string id, std::string path, float size) {
        fonts.emplace_back(id, path, size);
    }

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

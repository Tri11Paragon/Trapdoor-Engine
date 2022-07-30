//
// Created by brett on 29/07/22.
//

#include "World.h"
#include <thread>

namespace TD {

    const unsigned int processor_count = std::thread::hardware_concurrency();
    extern volatile bool _isWindowOpen;

    void runThread(int threadID){
        while (_isWindowOpen){

        }
    }

    std::vector<std::thread*> createdThreads;

    // // ---------------{Threads}---------------

    void Threadpool::createThreadPool() {
        int threadsToStart = processor_count;
        if (processor_count == 0)
            threadsToStart = 4;
        for (int i = 0; i < threadsToStart; i++)
            createdThreads.push_back(new std::thread(runThread, i));
    }

    void Threadpool::deleteThreads() {
        for (std::thread* thread : createdThreads)
            delete(thread);
    }

    // ---------------{GameRegistry}---------------

    extern std::vector<std::pair<std::string, std::string>> unloadedModels;
    extern std::vector<std::pair<std::string, std::string>> unloadedTextures;
    extern std::unordered_map<std::string, TD::model*> loadedModels;
    extern std::unordered_map<std::string, TD::texture*> loadedTextures;

    void GameRegistry::registerRegistrationCallback(void *(*funcion)()) {

    }

    void GameRegistry::registerModel(std::string unlocalizedName, std::string modelPath) {
        unloadedModels.push_back(std::pair(unlocalizedName, modelPath));
    }

    void GameRegistry::registerTexture(std::string unlocalizedName, std::string texturePath) {
        unloadedTextures.push_back(std::pair(unlocalizedName, texturePath));
    }

    void GameRegistry::registerBlocking() {

    }

    void GameRegistry::registerThreaded() {

    }

    void GameRegistry::deleteResources() {
        for (auto pair : loadedModels)
            delete(pair.second);
        for (auto pair : loadedTextures)
            delete(pair.second);
    }

    // ---------------{World}---------------

    World::World() {

    }

    World::~World() {

    }
}

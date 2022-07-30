//
// Created by brett on 29/07/22.
//

#include "World.h"
#include <thread>

namespace TD {

    // ---------------{Threads}---------------

    const unsigned int processor_count = std::thread::hardware_concurrency() > 0 ? std::thread::hardware_concurrency() : 4;
    extern volatile bool _isWindowOpen;

    void runThread(int threadID){
        while (_isWindowOpen){

        }
    }

    std::vector<std::queue<std::pair<std::string, std::string>>> unloadedModels;
    std::vector<std::queue<std::pair<std::string, std::string>>> unloadedTextures;
    std::vector<std::thread*> createdThreads;

    void Threadpool::createThreadPool() {
        for (int i = 0; i < processor_count; i++) {
            unloadedModels.emplace_back();
            unloadedTextures.emplace_back();
            createdThreads.push_back(new std::thread(runThread, i));
        }
    }

    void Threadpool::deleteThreads() {
        for (std::thread* thread : createdThreads)
            delete(thread);
    }

    // ---------------{GameRegistry}---------------

    extern std::unordered_map<std::string, TD::model*> loadedModels;
    extern std::unordered_map<std::string, TD::texture*> loadedTextures;
    std::vector<void* (*)()> callbacks;

    void GameRegistry::registerRegistrationCallback(void *(*funcion)()) {
        callbacks.push_back(funcion);
    }

    void GameRegistry::registerModel(std::string unlocalizedName, std::string modelPath) {
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

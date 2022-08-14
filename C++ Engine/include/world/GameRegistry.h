//
// Created by brett on 01/08/22.
//

#ifndef ENGINE_GAMEREGISTRY_H
#define ENGINE_GAMEREGISTRY_H

#include "../std.h"
#include "../glm.h"
#include "../renderer/gl.h"

namespace TD {

    class GameRegistry {
    public:
        // Threading
        static void createQueues();
        static void createThreadPool();
        static void deleteThreads();
        static bool loadingComplete();
        // Registering
        // used to define global registers required for engine function,
        // local registration is coming soom, likely in the form of 'regions'
        // which will contain assets required for function, all of which can be multithreaded and preloaded as the player gets close.
        static void registerRegistrationCallback(void* (*funcion)());
        static void registerModel(const std::string& id, const std::string& modelPath);
        static void registerTexture(const std::string& id, const std::string& texturePath);
        static void registerFont(std::string id, std::string path, float size);
        static void registerThreaded();
        static void loadToGPU();
        // Getting
        static TD::model* getModel(std::string unlocalizedName);
        static TD::Texture getTexture(std::string unlocalizedName);
        // Deleting
        static void deleteResources();
    };

} // TD

#endif //ENGINE_GAMEREGISTRY_H

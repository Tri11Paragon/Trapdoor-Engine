//
// Created by brett on 29/07/22.
//

#ifndef ENGINE_WORLD_H
#define ENGINE_WORLD_H

#include "../std.h"
#include "../renderer/gl.h"
#include "../renderer/camera.h"

namespace TD {

    class Threadpool {
    public:
        static void createThreadPool();
        static void deleteThreads();
    };

    class GameRegistry {
    public:
        // used to define global registers required for engine function,
        // local registration is coming soom, likely in the form of 'regions'
        // which will contain assets required for function, all of which can be multithreaded and preloaded as the player gets close.
        static void registerRegistrationCallback(void* (*funcion)());
        static void registerModel(std::string unlocalizedName, std::string modelPath);
        static void registerTexture(std::string unlocalizedName, std::string texturePath);
        static void registerThreaded();
        static void deleteResources();
    };

    class World {
    private:
        TD::camera* camera;
    public:
        World();
        ~World();
    };

}


#endif //ENGINE_WORLD_H

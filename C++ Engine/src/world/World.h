//
// Created by brett on 29/07/22.
//

#ifndef ENGINE_WORLD_H
#define ENGINE_WORLD_H

#include "../std.h"
#include "../renderer/gl.h"
#include "../renderer/camera.h"
#include "Entity.h"
#include "../renderer/shader.h"
#include "../renderer/renderer.h"

namespace TD {

    class Threadpool {
    public:
        static void createQueues();
        static void createThreadPool();
        static void deleteThreads();
        static bool loadingComplete();
    };

    class GameRegistry {
    public:
        // used to define global registers required for engine function,
        // local registration is coming soom, likely in the form of 'regions'
        // which will contain assets required for function, all of which can be multithreaded and preloaded as the player gets close.
        static void registerRegistrationCallback(void* (*funcion)());
        static void registerModel(std::string id, std::string modelPath);
        static void registerTexture(std::string id, std::string texturePath);
        static void registerFont(std::string id, std::string path, float size);
        static void registerThreaded();
        static void loadToGPU();
        static TD::model* getModel(std::string unlocalizedName);
        static TD::Texture getTexture(std::string unlocalizedName);
        static void deleteResources();
    };

    class World {
    private:
        TD::camera* camera;
        TD::skyboxRenderer skyboxRenderer;
        std::unordered_map<std::string, TD::Entity*> entityMap;
        TD::gBufferFBO gBufferFbo;
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
        ~World();
    };

}


#endif //ENGINE_WORLD_H

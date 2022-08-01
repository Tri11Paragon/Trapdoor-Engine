#include <iostream>

// https://github.com/ocornut/imgui/tree/master/docs

#include "logging.h"
#include "window.h"
#include <nfd.h>
#include <stdio.h>
#include "input.h"
#include "renderer/ui/ui.h"
#include "renderer/shader.h"
#include "renderer/camera.h"
#include "util.h"
#include "profiler.h"
#include "world/World.h"

int main(int, char**){
    TD::profiler loadTimer("Load Time");
    loadTimer.start();
    init_logging("output");

    TD::GameRegistry::registerRegistrationCallback([]() -> void* {
        // Register Models
        TD::GameRegistry::registerModel("taylor_plane", "../assets/models/32x32plane_sided.dae");
        TD::GameRegistry::registerModel("kent", "../assets/models/kent.dae");
        TD::GameRegistry::registerModel("plane", "../assets/models/32x32plane.dae");
        // Register Fonts
        TD::GameRegistry::registerFont("quicksand", "../assets/fonts/quicksand/Quicksand-Regular.ttf", 16.0f);
        TD::GameRegistry::registerFont("roboto", "../assets/fonts/roboto/Roboto-Regular.ttf", 16.0f);
        return nullptr;
    });

    TD::DisplayManager::init("GLFW Test");

    TD::firstPersonCamera camera;
    TD::DisplayManager::changeActiveCamera(&camera);

    TD::gBufferFBO gBufferFbo;

    TD::shader fxaaShader("../assets/shaders/postprocessing/filter-fxaa.vert", "../assets/shaders/postprocessing/filter-fxaa.frag");
    //TD::fbo fxaaFBO(TD::DEPTH_BUFFER);

    float height = 5;
    
    std::vector<TD::Light> lights = {
            TD::Light(glm::vec3(0,height,0),glm::vec3(1,1,1),0.07,0.20,65),
            TD::Light(glm::vec3(5,height,0),glm::vec3(1,1,1),0.07,0.20,65),
            TD::Light(glm::vec3(0,height,5),glm::vec3(1,1,1),0.07,0.20,65),
            TD::Light(glm::vec3(-5,height,0),glm::vec3(1,1,1),0.07,0.20,65),
            TD::Light(glm::vec3(0,height,-5),glm::vec3(1,1,1),0.07,0.20,65),
            TD::Light(glm::vec3(5,height,5),glm::vec3(1,1,1),0.07,0.20,65),
            TD::Light(glm::vec3(-5,height,-5),glm::vec3(1,1,1),0.07,0.20,65),
            TD::Light(glm::vec3(25,height,0),glm::vec3(1,1,1),0.07,0.20,65),
            TD::Light(glm::vec3(0,height,25),glm::vec3(1,1,1),0.07,0.20,65),
            TD::Light(glm::vec3(-25,height,0),glm::vec3(1,1,1),0.07,0.20,65),
            TD::Light(glm::vec3(0,height,-25),glm::vec3(1,1,1),0.07,0.20,65),
            TD::Light(glm::vec3(25,height,25),glm::vec3(1,1,1),0.07,0.20,65),
            TD::Light(glm::vec3(-25,height,-25),glm::vec3(1,1,1),0.07,0.20,65),
    };

    // Standard Defered about 120fps @ 1024 lights (8.5ms)

    TD::profiler renderTimer("Render");
    int MAX_LIGHTS = 50;
    TD::random pos(-75, 75);
    TD::random color(0, 1);
    for (int i = 0; i < MAX_LIGHTS; i++){
        TD::Light light(
                pos.getVec3(),
                color.getVec3(),
                0.07,
                0.20,
                65
        );
        //lights.push_back(light);
    }

    gBufferFbo.updateLights(lights);


    while (!TD::Threadpool::loadingComplete()){
        //tlog << "Waiting for load!";
    }
    TD::GameRegistry::loadToGPU();
    TD::Threadpool::deleteThreads();
    loadTimer.endAndPrint();
    // Main loop
    while (!TD::window::isCloseRequested()) {
        TD::window::startRender();
        camera.update();

        TD::debugUI::render();
        //ImGui::ShowDemoWindow();

        renderTimer.start("Geometry Pass");
        gBufferFbo.bindFirstPass();

        TD::GameRegistry::getModel("plane")->draw(*gBufferFbo.getFirstPassShader(), glm::vec3(0,0,0));

        TD::GameRegistry::getModel("kent")->draw(*gBufferFbo.getFirstPassShader(), std::vector<glm::vec3> {
            glm::vec3(0, 0, -1), glm::vec3(0, 0, -10), glm::vec3(12, 0, -1), glm::vec3(4, 21, -1), glm::vec3(6, -5, -1)
        });

        TD::GameRegistry::getModel("taylor_plane")->draw(*gBufferFbo.getFirstPassShader(), glm::vec3(-1, 15, -1));

        gBufferFbo.unbindFBO();
        renderTimer.end("Geometry Pass");

        //fxaaFBO.bindFBODraw();

        renderTimer.start("Lighting Pass");
        gBufferFbo.bindSecondPass(camera);
        //fxaaFBO.unbindFBO();

        //fxaaFBO.bindColorTexture(GL_TEXTURE0, GL_COLOR_ATTACHMENT0);
        //fxaaFBO.renderToQuad(fxaaShader);

        TD::window::finishRender();
        renderTimer.end("Lighting Pass");
    }
    TD::DisplayManager::close();
    return 0;
}

#include <iostream>

// https://github.com/ocornut/imgui/tree/master/docs

#include "logging.h"
#include "window.h"
#include <nfd.h>
#include <stdio.h>
#include "input.h"
#include "renderer/ui/debug.h"
#include "renderer/shader.h"
#include "renderer/camera.h"
#include "util.h"
#include "profiler.h"
#include "world/World.h"
#include "game/TestDisplay.h"
#include "world/GameRegistry.h"

int main(int, char**){
    TD::profiler loadTimer("Load Time");
    loadTimer.start("Load Time");
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

    // will automatically be cleaned up when the display manager exits
    new TD::TestDisplay("TestDisplay");
    TD::DisplayManager::changeDisplay("TestDisplay");

    // Standard Defered about 120fps @ 1024 lights (8.5ms)

    while (!TD::GameRegistry::loadingComplete()){
        //tlog << "Waiting for load!";
    }
    tlog << "Loading Complete";
    TD::GameRegistry::loadToGPU();
    tlog << "GL Complete";
    TD::GameRegistry::deleteThreads();
    loadTimer.end("Load Time");
    loadTimer.print();
    TD::DisplayManager::update();
    TD::DisplayManager::close();
    return 0;
}

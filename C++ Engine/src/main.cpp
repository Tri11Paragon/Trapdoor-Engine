#include <iostream>

// https://github.com/ocornut/imgui/tree/master/docs

#include "logging.h"
#include "window.h"
#include "input.h"
#include "renderer/ui/debug.h"
#include "renderer/shader.h"
#include "renderer/camera.h"
#include "util.h"
#include "profiler.h"
#include "world/World.h"
#include "game/TestDisplay.h"
#include "world/GameRegistry.h"
#include <config.h>
#include <data/resources.h>

int main(int, char**){
    TD::profiler loadTimer("Load Time");
    loadTimer.start("Load Time");
    init_logging("output");
    TD::Resources::init();
    TD::Project::init();
    ilog << "Running with debugging systems? " << DEBUG_ENABLED_BOOL;

    /*TD::GameRegistry::registerRegistrationCallback([]() -> void* {
        // Register Models
        TD::GameRegistry::registerModel("taylor_plane", "../assets/models/32x32plane_sided.dae");
        TD::GameRegistry::registerModel("kent", "../assets/models/kent.dae");
        TD::GameRegistry::registerModel("plane", "../assets/models/32x32plane.dae");
        TD::GameRegistry::registerModel("sponza", "../assets/models/sponzame/sponza.dae");
        // Register Fonts
        return nullptr;
    });*/

    TD::DisplayManager::init("Trapdoor " + std::to_string(ENGINE_VERSION_MAJOR) + "." + std::to_string(ENGINE_VERSION_MINOR) + "."
            + std::to_string(ENGINE_VERSION_PATCH) + " // C++ Test");

    TD::GameRegistry::registerDisplayType("TestDisplay", new TD::TestDisplay());

    // Standard Defered about 120fps @ 1024 lights (8.5ms)

    // will automatically be cleaned up when the display manager exits
    /*new TD::TestDisplay("TestDisplay");
    TD::DisplayManager::changeDisplay("TestDisplay");*/
    loadTimer.end("Load Time");
    loadTimer.print();
    TD::DisplayManager::update();
    TD::DisplayManager::close();
    TD::Project::close();
    return 0;
}

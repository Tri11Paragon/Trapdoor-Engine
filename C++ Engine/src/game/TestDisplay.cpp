//
// Created by brett on 31/07/22.
//

#include "TestDisplay.h"

namespace TD {
    TestDisplay::TestDisplay(std::string name) : Display(name) {
        TD::DisplayManager::changeActiveCamera(&camera);
        world.updateLights(lights);
        world.spawnEntity("TaylorPlane", new StaticEntity("plane", glm::vec3(0)));
        world.spawnEntity("TaylorFace", new StaticEntity("taylor_plane", glm::vec3(0, 20, 0)));
        world.spawnEntity("Kent1", new StaticEntity("kent", glm::vec3(0, 1, 0)));
        world.spawnEntity("Kent2", new StaticEntity("kent", glm::vec3(5, 1, 0)));
        world.spawnEntity("Kent3", new StaticEntity("kent", glm::vec3(0, 1, 5)));
        world.spawnEntity("Kent4", new StaticEntity("kent", glm::vec3(12, 5, -5)));
        world.updateDirectionalLighting(direction, color, dirEnabled);
    }

    void TestDisplay::onSwitch() {

    }

    void TestDisplay::render() {
        world.render();
    }

    void TestDisplay::update() {
        camera.update();
        world.update();
    }

    void TestDisplay::onLeave() {

    }

    TestDisplay::~TestDisplay() {

    }
} // TD
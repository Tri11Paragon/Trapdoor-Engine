//
// Created by brett on 31/07/22.
//

#include "TestDisplay.h"

namespace TD {
    TestDisplay::TestDisplay(std::string name) : Display(name) {
        TD::DisplayManager::changeActiveCamera(&camera);
        world.updateLights(lights);
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
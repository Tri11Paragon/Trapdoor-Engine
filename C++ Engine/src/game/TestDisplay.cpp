//
// Created by brett on 31/07/22.
//

#include "TestDisplay.h"

namespace TD {
    TestDisplay::TestDisplay(std::string name) : Display(name) {
        TD::DisplayManager::changeActiveCamera(&camera);
        world.updateLights(lights);
        /**
         * create world systems, stuff we require ya know
         */
         world.createSystem(new MeshRendererSystem(world));
         /**
          * create our entities
          */
        Entity* e = new Entity("Lewis");
        dPtr<Component> meshComponentKent = dPtr<Component>(new MeshComponent("Kent"));
        e->addComponent(meshComponentKent);
        world.spawnEntity(e);
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
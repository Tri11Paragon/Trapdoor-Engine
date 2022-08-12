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
        auto* Lewis = new Entity("Lewis");
        static_cast<TransformComponent*>(Lewis->getComponent(TRANSFORM_SYSTEM).get())->setTranslation(glm::vec3(0, 1, 0));
        Lewis->addComponent(dPtr<Component>(new MeshComponent("kent")));
        world.spawnEntity(Lewis);

        auto* Simon = new Entity("Simon");
        static_cast<TransformComponent*>(Simon->getComponent(TRANSFORM_SYSTEM).get())->setTranslation(glm::vec3(5, 1, 0));
        Simon->addComponent(dPtr<Component>(new MeshComponent("kent")));
        world.spawnEntity(Simon);

        auto* Kenny = new Entity("Kenny");
        static_cast<TransformComponent*>(Kenny->getComponent(TRANSFORM_SYSTEM).get())->setTranslation(glm::vec3(0, 1, 5));
        Kenny->addComponent(dPtr<Component>(new MeshComponent("kent")));
        world.spawnEntity(Kenny);

        auto* George = new Entity("George");
        static_cast<TransformComponent*>(George->getComponent(TRANSFORM_SYSTEM).get())->setTranslation(glm::vec3(12, 5, -5));
        George->addComponent(dPtr<Component>(new MeshComponent("kent")));
        world.spawnEntity(George);

        auto* Victor = new Entity("Victor");
        static_cast<TransformComponent*>(Victor->getComponent(TRANSFORM_SYSTEM).get())->setTranslation(glm::vec3(0, -1, 0));
        static_cast<TransformComponent*>(Victor->getComponent(TRANSFORM_SYSTEM).get())->setRotation(glm::vec3(90, 0, 0));
        Victor->addComponent(dPtr<Component>(new MeshComponent("plane")));
        world.spawnEntity(Victor);

        auto* Alejandro = new Entity("Alejandro");
        static_cast<TransformComponent*>(Alejandro->getComponent(TRANSFORM_SYSTEM).get())->setTranslation(glm::vec3(0, 20, 0));
        static_cast<TransformComponent*>(Alejandro->getComponent(TRANSFORM_SYSTEM).get())->setRotation(glm::vec3(90, 0, 0));
        Alejandro->addComponent(dPtr<Component>(new MeshComponent("taylor_plane")));
        world.spawnEntity(Alejandro);

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
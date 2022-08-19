//
// Created by brett on 31/07/22.
//

#include "game/TestDisplay.h"

#include <utility>
#include <util.h>

namespace TD {

    TestDisplay::TestDisplay(const std::string& name) : Display(std::move(name)) {
        TD::DisplayManager::changeActiveCamera(&camera);
        world = new World();
        world->updateLights(lights);
         /**
          * create our entities
          */
        auto* Lewis = new Entity("Lewis");
        static_cast<TransformComponent*>(Lewis->getComponent(TRANSFORM_COMPONENT).get())->setTranslation(glm::vec3(0, 1, 0));
        Lewis->addComponent(dPtr<Component>(new MeshComponent("kent")));
        world->spawnEntity(Lewis);

        auto* Simon = new Entity("Simon");
        static_cast<TransformComponent*>(Simon->getComponent(TRANSFORM_COMPONENT).get())->setTranslation(glm::vec3(5, 1, 0));
        Simon->addComponent(dPtr<Component>(new MeshComponent("kent")));
        world->spawnEntity(Simon);

        auto* Kenny = new Entity("Kenny");
        static_cast<TransformComponent*>(Kenny->getComponent(TRANSFORM_COMPONENT).get())->setTranslation(glm::vec3(0, 1, 5));
        Kenny->addComponent(dPtr<Component>(new MeshComponent("kent")));
        world->spawnEntity(Kenny);

        auto* George = new Entity("George");
        static_cast<TransformComponent*>(George->getComponent(TRANSFORM_COMPONENT).get())->setTranslation(glm::vec3(12, 5, -5));
        George->addComponent(dPtr<Component>(new MeshComponent("kent")));
        world->spawnEntity(George);

        auto* Victor = new Entity("Victor");
        static_cast<TransformComponent*>(Victor->getComponent(TRANSFORM_COMPONENT).get())->setTranslation(glm::vec3(0, -1, 0));
        static_cast<TransformComponent*>(Victor->getComponent(TRANSFORM_COMPONENT).get())->setRotation(glm::vec3(90, 0, 0));
        Victor->addComponent(dPtr<Component>(new MeshComponent("plane")));
        world->spawnEntity(Victor);

        auto* Alejandro = new Entity("Alejandro");
        static_cast<TransformComponent*>(Alejandro->getComponent(TRANSFORM_COMPONENT).get())->setTranslation(glm::vec3(0, 20, 0));
        static_cast<TransformComponent*>(Alejandro->getComponent(TRANSFORM_COMPONENT).get())->setRotation(glm::vec3(90, 0, 0));
        Alejandro->addComponent(dPtr<Component>(new MeshComponent("taylor_plane")));
        world->spawnEntity(Alejandro);

        world->updateDirectionalLighting(direction, color, dirEnabled);
    }

    void TestDisplay::onSwitch() {

    }

    void TestDisplay::render() {
        if (world != nullptr)
            world->render();
    }

    void TestDisplay::update() {
        camera.update();
        if (world != nullptr)
            world->update();
    }

    void TestDisplay::onLeave() {

    }

    TestDisplay::~TestDisplay() {
        delete(world);
    }

    TAG_COMPOUND* TestDisplay::onSave() {
        random rnd {4096 * 1024, 8192 * 1024};
        // since this is the root tag, we should name it what it is called in the allocator.
        // otherwise the engine will not be able to load this display!
        auto* root = new TAG_COMPOUND("TestDisplay");
        root->put(new TAG_STRING("name", this->name));
        if (world != nullptr){
            root->put(world->save());
        }
        return root;
    }

    void TestDisplay::onLoad(TAG_COMPOUND* tag) {
        if (tag->hasTag("World") && world != nullptr){
            world->load(tag->get<TAG_COMPOUND>("World"));
        }
    }
} // TD
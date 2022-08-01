//
// Created by brett on 31/07/22.
//

#ifndef ENGINE_TESTDISPLAY_H
#define ENGINE_TESTDISPLAY_H

#include "../window.h"
#include "../glm.h"
#include "../util.h"
#include "../profiler.h"
#include "../world/World.h"

namespace TD {

    class TestDisplay : Display {
    private:
        TD::profiler renderTimer = TD::profiler("Render");
        TD::firstPersonCamera camera;
        const float height = 5;
        TD::random color = TD::random(0, 1);
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
        TD::World world;
    public:
        TestDisplay(std::string name);
        virtual void onSwitch();
        virtual void render();
        virtual void update();
        virtual void onLeave();
        ~TestDisplay();
    };

} // TD

#endif //ENGINE_TESTDISPLAY_H

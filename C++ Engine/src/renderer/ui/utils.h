//
// Created by brett on 21/07/22.
//

#ifndef ENGINE_UTILS_H
#define ENGINE_UTILS_H

#include "../../imgui/imgui.h"
#include "../../font.h"
#include "../camera.h"

namespace TD {

    class renderable {
    public:
        virtual void render() = 0;
    };

    class debugUI {
    private:
        debugUI(TD::camera* camera);
    public:
        static void toggle();
        static void render();
        static void addTab();
        static void changeActiveCamera(TD::camera* camera);
    };

}


#endif //ENGINE_UTILS_H

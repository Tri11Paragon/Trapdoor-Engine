//
// Created by brett on 21/07/22.
//

#ifndef ENGINE_DEBUG_H
#define ENGINE_DEBUG_H

#include "../../imgui/imgui.h"
#include "../../font.h"
#include "../camera.h"

namespace TD {

    class DebugTab{
    protected:
        std::string name;
    public:
        virtual void render() {}
        std::string getName() {
            return name;
        }
    };

    class renderable {
    public:
        virtual void render() = 0;
    };

    class debugUI {
    private:
        debugUI(TD::camera* camera) {}
    public:
        static void toggle();
        static void render();
        static void addTab(DebugTab* tab);
        static void deleteTab(DebugTab* tab);
        static void deleteAllTabs();
    };

}


#endif //ENGINE_DEBUG_H

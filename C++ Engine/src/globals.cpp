//
// Created by brett on 24/07/22.
//
#include "std.h"
#include "glm.h"
#include "input.h"
#include "window.h"
namespace TD {
    double lastTime;
    double frameTimeMS, frameTimeS, fps;

    std::vector<pkeyfunc_t> keyListeners;
    std::vector<pmousefunc_t> mouseListeners;
    bool keyDown[1024];
    bool mouseDown[512];
    window* _window;
}
//
// Created by brett on 24/07/22.
//
// The globals class contains all the globals used for the trapdoor engine
// this is done this way to provide a simple unified source file for all the global definitions
// Yes putting them in the corresponding cpp file may be better however I prefer it this way
// you are not required to follow this code style and in fact I discourage it.
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
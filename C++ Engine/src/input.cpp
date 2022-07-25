//
// Created by brett on 21/07/22.
//
#include "input.h"
#include "window.h"

namespace TD {

    extern std::vector<pkeyfunc_t> keyListeners;
    extern std::vector<pmousefunc_t> mouseListeners;
    extern bool keyDown[1024];
    extern bool mouseDown[512];

    void IM_KeyPressed(int keycode){
        keyDown[keycode] = true;
        for (pkeyfunc_t fun : keyListeners){
            fun(true, keycode);
        }
    }

    void IM_KeyReleased(int keycode){
        keyDown[keycode] = false;
        for (pkeyfunc_t fun : keyListeners){
            fun(false, keycode);
        }
    }

    void IM_MousePressed(int mousecode){
        mouseDown[mousecode] = true;
        for (pmousefunc_t fun : mouseListeners){
            fun(true, mousecode);
        }
    }

    void IM_MouseReleased(int mousecode){
        mouseDown[mousecode] = false;
        for (pmousefunc_t fun : mouseListeners){
            fun(false, mousecode);
        }
    }

    void IM_RegisterKeyListener(pkeyfunc_t fun){
        keyListeners.push_back(fun);
    }

    void IM_RegisterMouseListener(pmousefunc_t fun){
        mouseListeners.push_back(fun);
    }

    bool isMouseDown(int code){
        return mouseDown[code];
    }

    bool isKeyDown(int code){
        return keyDown[code];
    }

    bool isMouseGrabbed() {
        return TD::window::isMouseGrabbed();
    }

    void setMouseGrabbed(bool grabbed) {
        TD::window::setMouseGrabbed(grabbed);
    }

    double getMouseDX() {
        return TD::window::getMouseDX();
    }

    double getMouseDY() {
        return TD::window::getMouseDY();
    }

    double getMouseX() {
        return TD::window::getMouseX();
    }

    double getMouseY() {
        return TD::window::getMouseY();
    }

}
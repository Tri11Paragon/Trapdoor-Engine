//
// Created by brett on 21/07/22.
//
#include "input.h"

namespace TD {

    static std::vector<pkeyfunc_t> keyListeners;
    static std::vector<pmousefunc_t> mouseListeners;
    static bool keyDown[1024];
    static bool mouseDown[512];

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

}
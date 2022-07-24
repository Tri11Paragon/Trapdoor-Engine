//
// Created by brett on 21/07/22.
//

#ifndef ENGINE_INPUT_H
#define ENGINE_INPUT_H

#include <vector>

namespace TD {

    // non-member
    typedef void keyfunc_t(bool, int);
    typedef keyfunc_t* pkeyfunc_t;

    typedef void mousefunc_t(bool, int);
    typedef keyfunc_t* pmousefunc_t;

    void IM_KeyPressed(int keycode);
    void IM_KeyReleased(int keycode);
    void IM_MousePressed(int mousecode);
    void IM_MouseReleased(int mousecode);
    void IM_RegisterKeyListener(pkeyfunc_t fun);
    void IM_RegisterMouseListener(pmousefunc_t fun);
    bool isMouseDown(int code);
    bool isKeyDown(int code);

    // for static versions
    void updateWindow(void* theWindow);
    bool isMouseGrabbed();
    void setMouseGrabbed(bool grabbed);
    double getMouseDX();
    double getMouseDY();
    double getMouseX();
    double getMouseY();

}

#endif //ENGINE_INPUT_H

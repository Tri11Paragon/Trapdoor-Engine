//
// Created by brett on 21/07/22.
//

#ifndef ENGINE_INPUT_H
#define ENGINE_INPUT_H

#include <vector>
#include "gladsources/glad_gl_core/include/glad/gl.h"
#include <GLFW/glfw3.h>

namespace TD {

    // non-member
    typedef void keyfunc_t(bool, int);
    typedef keyfunc_t* pkeyfunc_t;

    typedef void mousefunc_t(bool, int);
    typedef keyfunc_t* pmousefunc_t;

    class Input {
    public:
        static void glfw_WindowFocusCallback(GLFWwindow* window, int focused);
        static void glfw_CursorEnterCallback(GLFWwindow* window, int entered);
        static void glfw_CursorPosCallback(GLFWwindow* window, double x, double y);
        static void glfw_MouseButtonCallback(GLFWwindow* window, int button, int action, int mods);
        static void glfw_ScrollCallback(GLFWwindow* window, double xoffset, double yoffset);
        static void glfw_KeyCallback(GLFWwindow* window, int key, int scancode, int action, int mods);
        static void glfw_CharCallback(GLFWwindow* window, unsigned int c);
        static void glfw_MonitorCallback(GLFWmonitor* monitor, int event);
        static void update();
        static bool state();
        static void IM_RegisterKeyListener(pkeyfunc_t fun);
        static void IM_RegisterMouseListener(pmousefunc_t fun);
        static bool isMouseDown(int code);
        static bool isKeyDown(int code);
        static bool isMouseGrabbed();
        static void setMouseGrabbed(bool grabbed);
        static double getMouseDX();
        static double getMouseDY();
        static double getMouseX();
        static double getMouseY();
        static double getMouseScrollYLastFrame();
        static double getMouseScrollXLastFrame();
        static double getMouseScrollY();
        static double getMouseScrollX();
    };

}

#endif //ENGINE_INPUT_H

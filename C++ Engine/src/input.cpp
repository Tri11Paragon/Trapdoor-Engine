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
    static bool _state = false;

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

    void Input::glfw_WindowFocusCallback(GLFWwindow *window, int focused) {

    }

    void Input::glfw_CursorEnterCallback(GLFWwindow *window, int entered) {

    }

    void Input::glfw_CursorPosCallback(GLFWwindow *window, double x, double y) {

    }

    void Input::glfw_MouseButtonCallback(GLFWwindow *window, int button, int action, int mods) {
        if (action == GLFW_PRESS){
            mouseDown[button] = true;
            for (pmousefunc_t fun : mouseListeners){
                fun(true, button);
            }
        } else if (action == GLFW_RELEASE) {
            mouseDown[button] = false;
            for (pmousefunc_t fun : mouseListeners){
                fun(false, button);
            }
        }
    }

    void Input::glfw_ScrollCallback(GLFWwindow *window, double xoffset, double yoffset) {

    }

    void Input::glfw_KeyCallback(GLFWwindow *window, int key, int scancode, int action, int mods) {
        if (action == GLFW_PRESS){
            _state = true;
            keyDown[key] = true;
            for (pkeyfunc_t fun : keyListeners){
                fun(true, key);
            }
        } else if (action == GLFW_RELEASE) {
            keyDown[key] = false;
            for (pkeyfunc_t fun : keyListeners){
                fun(false, key);
            }
        }
    }

    void Input::glfw_CharCallback(GLFWwindow *window, unsigned int c) {

    }

    void Input::glfw_MonitorCallback(GLFWmonitor *monitor, int event) {

    }

    void Input::update() {
        _state = false;
    }

    bool Input::state() {
        return _state;
    }
}
//
// Created by brett on 20/07/22.
//
#ifndef ENGINE_WINDOW_H
#define ENGINE_WINDOW_H

#include "std.h"
#include "renderer/gl.h" // Will drag system OpenGL headers
#include "font.h"

using namespace std;

namespace TD {

    class window {
    private:
        static void glfw_error_callback(int error, const char *description) {
            elog << "Glfw Error " << error << ": " << description << std::endl;
            exit(error);
        }
    public:
        static void initWindow() {initWindow("Generic GLFW Window");};
        static void initWindow(string title);

        static void startRender(float r, float g, float b, float a);
        static void finishRender();
        static bool isCloseRequested();
        static int width();
        static int height();
        static bool loadingCompleted();
        static bool isMouseGrabbed();
        static void setMouseGrabbed(bool grabbed);
        static double getMouseDX();
        static double getMouseDY();
        static double getMouseX();
        static double getMouseY();

        static void deleteWindow();
    };

    class DisplayManager {
    private:
    public:
        static void init(std::string windowName);
        static void update();
        static void close();
        static void changeActiveCamera(TD::camera* camera);
    };

    // key defs
    #define KEY_A GLFW_KEY_A
    #define KEY_B GLFW_KEY_B
    #define KEY_C GLFW_KEY_C
    #define KEY_D GLFW_KEY_D
    #define KEY_E GLFW_KEY_E
    #define KEY_F GLFW_KEY_F
    #define KEY_G GLFW_KEY_G
    #define KEY_H GLFW_KEY_H
    #define KEY_I GLFW_KEY_I
    #define KEY_J GLFW_KEY_J
    #define KEY_K GLFW_KEY_K
    #define KEY_L GLFW_KEY_L
    #define KEY_M GLFW_KEY_M
    #define KEY_N GLFW_KEY_N
    #define KEY_O GLFW_KEY_O
    #define KEY_P GLFW_KEY_P
    #define KEY_Q GLFW_KEY_Q
    #define KEY_R GLFW_KEY_R
    #define KEY_S GLFW_KEY_S
    #define KEY_T GLFW_KEY_T
    #define KEY_U GLFW_KEY_U
    #define KEY_V GLFW_KEY_V
    #define KEY_W GLFW_KEY_W
    #define KEY_X GLFW_KEY_X
    #define KEY_Y GLFW_KEY_Y
    #define KEY_Z GLFW_KEY_Z
    #define KEY_LEFT_SHIFT GLFW_KEY_LEFT_SHIFT
    #define KEY_RIGHT_SHIFT GLFW_KEY_RIGHT_SHIFT
    #define KEY_L_SHIFT GLFW_KEY_LEFT_SHIFT
    #define KEY_R_SHIFT GLFW_KEY_RIGHT_SHIFT
    #define KEY_LEFT_CONTROL GLFW_KEY_LEFT_CONTROL
    #define KEY_L_CONTROL GLFW_KEY_LEFT_CONTROL
    #define KEY_SPACE GLFW_KEY_SPACE
    #define KEY_UP GLFW_KEY_UP
    #define KEY_DOWN GLFW_KEY_DOWN
    #define KEY_LEFT GLFW_KEY_LEFT
    #define KEY_RIGHT GLFW_KEY_RIGHT

}

#endif //ENGINE_WINDOW_H

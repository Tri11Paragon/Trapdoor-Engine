//
// Created by brett on 2022-01-31.
//

#include "DisplayManager.h"

int createDisplay() {
    std::cout << "Setting up GLFW context" << std::endl;
    if (setupGLFWContext())
        return -1;

    std::cout << "Setting up GLFW window" << std::endl;
    window = glfwCreateWindow(currentWidth, currentHeight, (std::string(TRAPDOOR_NAME) + " v" + std::string(TRAPDOOR_VERSION)).c_str(), NULL, NULL);
    if (window == NULL)
    {
        std::cout << "Failed to create GLFW window" << std::endl;
        glfwTerminate();
        return -2;
    }

    std::cout << "Setting up GLFW position" << std::endl;
    if (setupGLFWWindowPosition())
        return -3;

    glfwMakeContextCurrent(window);

    // vsync
    glfwSwapInterval(0);
    setupGLFWCallbacks();
    // finally after all creation is complete, show the window
    glfwShowWindow(window);

    if (!gladLoadGLLoader((GLADloadproc) glfwGetProcAddress)) {
        std::cerr << "Unable to init GLAD" << std::endl;
        return -4;
    }
    updateWindowSize();

    return 0;
}
void updateDisplay(){



}
void closeDisplay(){

}

int isCloseRequested(){
    return glfwWindowShouldClose(window);
}

void errorCallback(int error_code, const char* description){
    std::cerr << "GLFW ERrOR HAS OCCURRED!\n Error Code: " << error_code << "\n\tDescription: " << description << "\n";
}

void updateWindowSize(){
    glViewport(0,0, currentWidth, currentHeight);

}

int setupGLFWContext(){
    glfwSetErrorCallback(errorCallback);
    if (!glfwInit()){
        std::cerr << "Unable to open GLFW!" << std::endl;
        return -1;
    }
    glfwDefaultWindowHints();
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
    return 0;
}

int setupGLFWWindowPosition(){
    int width = 0;
    int height = 0;
    glfwGetWindowSize(window, &width, &height);

    const GLFWvidmode* mode = glfwGetVideoMode(glfwGetPrimaryMonitor());

    glfwSetWindowPos(window, (mode->width - width) / 2, (mode->height - height) / 2);

    return 0;
}

void setupGLFWCallbacks(){
    glfwSetWindowSizeCallback(window, [](GLFWwindow* window, int width, int height) -> void {
        currentWidth = width;
        currentHeight = height;
        updateWindowSize();
    });
}
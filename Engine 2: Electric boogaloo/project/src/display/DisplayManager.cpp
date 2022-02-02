//
// Created by brett on 2022-01-31.
//

#include "DisplayManager.h"
#include <filesystem>
#include <glad/glad.h>
#include "GLFW/glfw3.h"
#include <iostream>
#include "config/config.h"

const int startingWidth = 800;
const int startingHeight = 600;

int currentWidth = 800;
int currentHeight = 600;

int createDisplay() {
    /*setupGLFWContext();

    GLFWwindow* window = glfwCreateWindow(currentWidth, currentHeight, TRAPDOOR_NAME, NULL, NULL);
    if (window == NULL)
    {
        std::cout << "Failed to create GLFW window" << std::endl;
        glfwTerminate();
        return -1;
    }*/

    return 0;
}
void updateDisplay(){

}
void closeDisplay(){

}

int isCloseRequested(){
    return 1;
}

void setupGLFWContext(){
    /*glfwInit();
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);*/
}
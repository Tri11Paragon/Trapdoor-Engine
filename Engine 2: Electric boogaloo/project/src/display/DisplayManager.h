//
// Created by brett on 2022-01-31.
//

#ifndef TRAPDOOR_DISPLAYMANAGER_H
#define TRAPDOOR_DISPLAYMANAGER_H

#include <filesystem>
#include <glad/glad.h>
#include <GLFW/glfw3.h>
#include <iostream>
#include "config/config.h"

static const int startingWidth = 800;
static const int startingHeight = 600;

static int currentWidth = startingWidth;
static int currentHeight = startingHeight;
static GLFWwindow* window;

void errorCallback(int error_code, const char* description);
int setupGLFWContext();
int setupGLFWWindowPosition();
void setupGLFWCallbacks();
void updateWindowSize();

int createDisplay();
void updateDisplay();
void closeDisplay();

int isCloseRequested();

#endif //TRAPDOOR_DISPLAYMANAGER_H

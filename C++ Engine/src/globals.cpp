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
#include "renderer/gl.h"
#include "renderer/camera.h"
#include "renderer/ui/utils.h"

namespace TD {
    double lastTime;
    double frameTimeMS, frameTimeS, fps;

    std::vector<pkeyfunc_t> keyListeners;
    std::vector<pmousefunc_t> mouseListeners;
    bool keyDown[1024];
    bool mouseDown[512];

    // Texturing
    std::map<std::string, Texture> loadedTextures;

    // Window / GLFW
    GLFWwindow *_window;
    bool _isMouseGrabbed = false;
    int _display_w = 1280, _display_h = 720;
    bool _loadingComplete = false;
    double _dx, _dy, _lx, _ly, _mx, _my;
    glm::mat4 projectionMatrix;
    glm::mat4 projectionViewMatrix;
    glm::mat4 viewMatrix;

    // Developer / Debug
    bool debugMenuEnabled = false;
    TD::camera* activeCamera;

    // IMGUI Fonts
    unordered_map<string, ImFont*> loadedFonts;
    vector<font> _fonts;
    unordered_map<string, DebugTab*> debugTabs;
}
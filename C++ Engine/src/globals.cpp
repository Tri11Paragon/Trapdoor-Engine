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
#include "renderer/shader.h"
#include "renderer/ui/debug.h"
#include "hashmaps.h"
#include "world/World.h"

namespace TD {
    double lastTime;
    double frameTimeMS, frameTimeS, fps;

    std::vector<pkeyfunc_t> keyListeners;
    std::vector<pmousefunc_t> mouseListeners;
    bool keyDown[1024];
    bool mouseDown[512];

    // Window / GLFW
    GLFWwindow *_window;
    volatile bool _isWindowOpen = true;
    bool _isMouseGrabbed = false;
    int _display_w = 1280, _display_h = 720;
    bool _loadingComplete = false;
    double _dx, _dy, _lx, _ly, _mx, _my;
    float camera_far_plane = 300.0f;
    float fov = 90;
    glm::mat4 projectionMatrix;
    glm::mat4 projectionViewMatrix;
    glm::mat4 viewMatrix;
    std::vector<WindowResize*> windowResizeCallbacks;
    std::unordered_map<std::string, TD::Display*> displays;
    std::string activeDisplay = "NULL";

    // Developer / Debug
    bool debugMenuEnabled = false;
    TD::camera* activeCamera;

    // IMGUI Fonts
    unordered_map<string, ImFont*> loadedFonts;
    vector<font> _fonts;
    unordered_map<string, DebugTab*> debugTabs;

    // Texturing / Modeling
    std::string assetsPath = "../assets/";

    // ---------------{GameRegistry}---------------
    // all of these get init before threads are created, therefore it is fine that they are not thread safe
    // since all threads get their own queue it doesn't matter as well.
    std::vector<std::queue<std::pair<std::string, std::string>>> unloadedModels;
    std::vector<std::queue<std::pair<std::string, std::string>>> unloadedTextures;

    // final loaded resources
    parallel_node_hash_map<std::string, TD::model*> loadedModels;
    parallel_node_hash_map<std::string, TD::Texture> loadedTextures;

    vector<TD::font> fonts;
    bool queuesCreated = false;

    // World
    ID entityID = 0;
}
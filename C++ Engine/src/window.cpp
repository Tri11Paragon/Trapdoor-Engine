//
// Created by brett on 23/07/22.
//

#include "window.h"
#include "logging.h"
#include "imgui/imgui.h"
#include "imgui/imgui_impl_glfw.h"
#include "imgui/imgui_impl_opengl3.h"
#include "clock.h"
#include "input.h"
#include "renderer/ui/debug.h"
#include "world/World.h"
#include <config.h>

namespace TD {

    extern GLFWwindow *_window;
    extern bool _isMouseGrabbed;
    extern int _display_w, _display_h;
    extern bool _loadingComplete;
    extern double _dx, _dy, _lx, _ly, _mx, _my;
    extern glm::mat4 projectionMatrix;
    extern bool _isWindowOpen;
    extern bool _listenToResize;
    extern float fov;

    void updateProjections(){
        glViewport(0, 0, _display_w, _display_h);
        projectionMatrix = glm::perspective(glm::radians(fov), (float) _display_w / (float) _display_h, 0.1f, camera_far_plane);
        TD::updateProjectionMatrixUBO(projectionMatrix);
        TD::updateOrthoMatrixUBO(glm::ortho(0.0f, (float)_display_w, 0.0f, (float)_display_h, 0.1f, 1000.0f));
    }

    void window::initWindow(string title) {
        // Setup window
        glfwSetErrorCallback(glfw_error_callback);
        if (!glfwInit()) {
            flog << "GLFW Init Error";
            return;
        }

        // Setup GL Version
        const char *glsl_version = "#version 130";
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);  // 3.2+ only
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);            // 3.0+ only

        ilog << "GLFW Window Setup complete, using GL4.5" << "\n";

        // Create window with graphics context
        _window = glfwCreateWindow(_display_w, _display_h, title.c_str(), nullptr, NULL);
        if (_window == NULL) {
            flog << "Unable to create GLFW window\n";
            return;
        }
        maximizeWindow();
        glfwMakeContextCurrent(_window);
        glfwSwapInterval(0); // Enable vsync

        ilog << "GLFW Window Created.\n";
        ilog << "Creating GLAD2 GL Context\n";

        int version = gladLoadGL(glfwGetProcAddress);
        if (version == 0) {
            elog << "Failed to initialize OpenGL context\n";
            return;
        }

        ilog << "Glad Init Complete. Loaded GL" << GLAD_VERSION_MAJOR(version) << "." << GLAD_VERSION_MINOR(version)
        << "\n";
        ilog << "Creating Dear ImGUI Context.\n";

        // Setup Dear ImGui context
        IMGUI_CHECKVERSION();
        ImGui::CreateContext();
        ImGuiIO &io = ImGui::GetIO();
        (void) io;
        //io.ConfigFlags |= ImGuiConfigFlags_NavEnableKeyboard;     // Enable Keyboard Controls
        //io.ConfigFlags |= ImGuiConfigFlags_NavEnableGamepad;      // Enable Gamepad Controls

        // Setup Dear ImGui style
        ImGui::StyleColorsDark();
        //ImGui::StyleColorsLight();

        // Setup Platform/Renderer backends
        ImGui_ImplGlfw_InitForOpenGL(_window, true);
        ImGui_ImplOpenGL3_Init(glsl_version);

        // Load Fonts
        // - If no fonts are loaded, dear imgui will use the default font. You can also load multiple fonts and use ImGui::PushFont()/PopFont() to select them.
        // - AddFontFromFileTTF() will return the ImFont* so you can store it if you need to select the font among multiple.
        // - If the file cannot be loaded, the function will return NULL. Please handle those errors in your application (e.g. use an assertion, or display an error and quit).
        // - The fonts will be rasterized at a given size (w/ oversampling) and stored into a texture when calling ImFontAtlas::Build()/GetTexDataAsXXXX(), which ImGui_ImplXXXX_NewFrame below will call.
        // - Read 'docs/FONTS.md' for more instructions and details.
        // - Remember that in C/C++ if you want to include a backslash \ in a string literal you need to write a double backslash \\ !
        io.Fonts->AddFontDefault();
        //io.Fonts->AddFontFromFileTTF("../../misc/fonts/Roboto-Medium.ttf", 16.0f);
        //io.Fonts->AddFontFromFileTTF("../../misc/fonts/Cousine-Regular.ttf", 15.0f);
        //io.Fonts->AddFontFromFileTTF("../../misc/fonts/DroidSans.ttf", 16.0f);
        //io.Fonts->AddFontFromFileTTF("../../misc/fonts/ProggyTiny.ttf", 10.0f);
        //ImFont* font = io.Fonts->AddFontFromFileTTF("c:\\Windows\\Fonts\\ArialUni.ttf", 18.0f, NULL, io.Fonts->GetGlyphRangesJapanese());
        //IM_ASSERT(font != NULL);

        TD::fontContext::load(io);

        TD::createMatrixUBO();

        updateProjections();

        _loadingComplete = true;
    }

    extern std::vector<WindowResize*> windowResizeCallbacks;;

    void window::startRender() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        // Poll and handle events (inputs, window resize, etc.)
        // You can read the io.WantCaptureMouse, io.WantCaptureKeyboard flags to tell if dear imgui wants to use your inputs.
        // - When io.WantCaptureMouse is true, do not dispatch mouse input data to your main application, or clear/overwrite your copy of the mouse data.
        // - When io.WantCaptureKeyboard is true, do not dispatch keyboard input data to your main application, or clear/overwrite your copy of the keyboard data.
        // Generally you may always pass all inputs to dear imgui, and hide them from your application based on those two flags.
        glfwPollEvents();
        // Start the Dear ImGui frame
        ImGui_ImplOpenGL3_NewFrame();
        ImGui_ImplGlfw_NewFrame();
        ImGui::NewFrame();

        _lx = _mx;
        _ly = _my;
        glfwGetCursorPos(_window, &_mx, &_my);

        if (_isMouseGrabbed) {
            _dx = _mx - (double)_display_w/2.0;
            _dy = _my - (double)_display_h/2.0;
            // TODO: read this lol
            glfwSetCursorPos(_window, (double)_display_w/2.0, (double)_display_h/2.0);
        } else {
            _dx = _lx - _mx;
            _dy = _ly - _my;
        }

        if (_listenToResize) {
            forceWindowUpdate();
        }
    }

    void window::finishRender() {
        // Rendering
        ImGui::Render();
        ImGui_ImplOpenGL3_RenderDrawData(ImGui::GetDrawData());

        TD::updateClock(glfwGetTime() * 1000.0);

        glfwSwapBuffers(_window);
    }

    bool window::isCloseRequested() {
        bool close = glfwWindowShouldClose(_window);
        _isWindowOpen = !close;
        return close;
    }

    int window::width() {
        return _display_w;
    }

    int window::height() {
        return _display_h;
    }

    bool window::loadingCompleted() {
        return _loadingComplete;
    }

    void window::deleteWindow() {
        TD::GameRegistry::deleteResources();
        TD::debugUI::deleteAllTabs();
        // Cleanup
        ImGui_ImplOpenGL3_Shutdown();
        ImGui_ImplGlfw_Shutdown();
        ImGui::DestroyContext();
        ilog << "ImGui Successfully closed.\n";

        glfwDestroyWindow(_window);
        glfwTerminate();
        ilog << "Window Successfully deleted.\n";
    }

    bool window::isMouseGrabbed() {
        return _isMouseGrabbed;
    }

    void window::setMouseGrabbed(bool grabbed) {
        _isMouseGrabbed = grabbed;
        glfwSetInputMode(_window, GLFW_CURSOR, grabbed ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
    }

    double window::getMouseDX() {
        return _dx;
    }

    double window::getMouseDY() {
        return _dy;
    }

    double window::getMouseX() {
        return _lx;
    }

    double window::getMouseY() {
        return _ly;
    }

    void window::maximizeWindow() {
        glfwMaximizeWindow(_window);
    }

    void window::restoreWindow() {
        glfwRestoreWindow(_window);
    }

    void window::setListenToResize(bool state) {
        _listenToResize = state;
    }

    void window::setRenderFrameBufferSize(int width, int height) {
        int pastValueW = _display_w, pastValueH = _display_h;
        if ((pastValueW != width || pastValueH != height)) {
            updateProjections();
            for (int i = 0; i < windowResizeCallbacks.size(); i++)
                windowResizeCallbacks[i]->windowResized(width, height);
            dlog << "Changing Projection Matrix to " << width << "w " << height << "h\n";
        }
    }

    bool window::isListeningToResize() {
        return _listenToResize;
    }

    void window::forceWindowUpdate() {
        int pastValueW = _display_w, pastValueH = _display_h;
        glfwGetFramebufferSize(_window, &_display_w, &_display_h);
        if ((pastValueW != _display_w || pastValueH != _display_h)) {
            updateProjections();
            for (int i = 0; i < windowResizeCallbacks.size(); i++)
                windowResizeCallbacks[i]->windowResized(_display_w, _display_h);
            dlog << "Changing Projection Matrix to " << _display_w << "w " << _display_h << "h\n";
        }
    }

    extern vector<TD::font> fonts;
    extern TD::camera *activeCamera;

    static void keyCallBack(bool pressed, int code){
        if (code == GLFW_KEY_F3 && pressed)
            TD::debugUI::toggle();
        if (code == GLFW_KEY_ESCAPE && pressed)
            TD::setMouseGrabbed(!TD::isMouseGrabbed());
    }

    extern std::unordered_map<std::string, TD::Display*> displays;
    extern std::string activeDisplay;
    DefaultLoadingScreenDisplay* defaultLoadDisplay = new DefaultLoadingScreenDisplay("_TD::LOADING_SCREEN_DISPLAY");

    void DisplayManager::init(std::string window) {
        TD::GameRegistry::registerThreaded();
        TD::fontContext::loadContexts(fonts);
        TD::window::initWindow(window);
        TD::IM_RegisterKeyListener(&keyCallBack);
        defaultLoadDisplay->onSwitch();

        while (!TD::GameRegistry::loadingComplete()){
            TD::window::startRender();
            defaultLoadDisplay->render();
            defaultLoadDisplay->update();
            //tlog << "Waiting for load!";
            TD::window::finishRender();
        }
        defaultLoadDisplay->onLeave();
        tlog << "Loading Complete";
        TD::GameRegistry::loadToGPU();
        tlog << "GL Complete";
        TD::GameRegistry::deleteThreads();
#ifdef DEBUG_ENABLED
        TD::Editor::init();
#endif
    }

    void DisplayManager::update() {
        // Main loop
        while (!TD::window::isCloseRequested()) {
            TD::window::startRender();

            TD::debugUI::render();
#ifdef DEBUG_ENABLED
            TD::Editor::render();
#endif
            //ImGui::ShowDemoWindow();

            if (activeDisplay != "NULL") {
                try {
                    Display* ptr = displays.at(activeDisplay);
                    ptr->render();
                    ptr->update();
                } catch (std::exception& e){
                    elog << e.what();
                    elog << "ERROR RENDERING ACTIVE DISPLAY.";
                    break;
                }
            }

            //fxaaFBO.bindFBODraw();

            //fxaaFBO.unbindFBO();

            //fxaaFBO.bindColorTexture(GL_TEXTURE0, GL_COLOR_ATTACHMENT0);
            //fxaaFBO.renderToQuad(fxaaShader);

            TD::window::finishRender();
        }
    }

    void DisplayManager::close() {
        TD::window::deleteWindow();
        for (auto pa : displays)
            delete(pa.second);
    }

    void DisplayManager::changeActiveCamera(camera *camera) {
        activeCamera = camera;
    }

    void DisplayManager::changeDisplay(std::string name) {
        if (activeDisplay != "NULL")
            displays.at(activeDisplay)->onLeave();
        displays.at(name)->onSwitch();
        activeDisplay = name;
    }

    void DisplayManager::changeLoadingScreenDisplay(std::string name) {
        if (!(defaultLoadDisplay = dynamic_cast<DefaultLoadingScreenDisplay *>(displays.at(name)))){
            elog << "Unable to change loading screen display, not of type DefaultLoadingScreenDisplay!";
        }
    }

    Display::Display(std::string name) {
        displays.insert(std::pair(name, this));
    }

    DefaultLoadingScreenDisplay::DefaultLoadingScreenDisplay(std::string name) : Display(name) {

    }

    void DefaultLoadingScreenDisplay::onSwitch() {
        lsTexture.loadGL();
    }

    void DefaultLoadingScreenDisplay::render() {
        constexpr int flags = ImGuiWindowFlags_AlwaysAutoResize | ImGuiWindowFlags_NoCollapse | ImGuiWindowFlags_NoTitleBar | ImGuiWindowFlags_NoScrollbar |
                ImGuiWindowFlags_NoResize | ImGuiWindowFlags_NoDecoration | ImGuiWindowFlags_NoMove;

        ImGui::PushFont(TD::fontContext::get("roboto"));
        ImGui::PushStyleColor(ImGuiCol_WindowBg, ImVec4(0.5444f, 0.62f, 0.69f, 1.0));
        ImGui::SetNextWindowPos(ImVec2(0,0), ImGuiCond_Appearing);
        ImGui::SetNextWindowSize(ImVec2((float)_display_w, (float)_display_h), ImGuiCond_Always);
        ImGui::Begin("Loading Screen", nullptr, flags);

        float progressWidth = (float)_display_w/2;

        // add the change in time to the time counter, when above the delay thresh, goto next frame.
        currentTime += getFrameTimeMilliseconds();
        if (currentTime >= lsTexture.getDelays()[currentFrame]) {
            currentFrame++;
            currentTime = 0;
        }
        // make sure we don't exceed the max frames
        if (currentFrame >= lsTexture.getNumberOfFrames())
            currentFrame = 0;

        float aspect = (float)lsTexture.getWidth() / (float)lsTexture.getHeight();
        const float padding = 10;
        float remHe = _display_h/2 - padding*2;
        float calcWidth = remHe * aspect;

        ImGui::SetCursorPos(ImVec2(progressWidth - calcWidth/2, padding));
        ImGui::Image(lsTexture.getTextures()[currentFrame], ImVec2(calcWidth, remHe));

        ImGui::SetCursorPos(ImVec2(progressWidth - progressWidth/2, (float)_display_h/2));
        // changes background of the progress bar
        //ImGui::PushStyleColor(ImGuiCol_FrameBg, ImVec4(0.270, 0.960, 0.0192, 1.0));
        ImGui::PushStyleColor(ImGuiCol_PlotHistogram, ImVec4(0.225, 0.820, 0.00820, 1.0));
        ImGui::ProgressBar(modelsLoaded / modelCount, ImVec2(progressWidth, 60));
        ImGui::NewLine();
        ImGui::SetCursorPosX(progressWidth - progressWidth/2);
        ImGui::ProgressBar(texturesLoaded / textureCount, ImVec2(progressWidth, 60));
        ImGui::PopStyleColor();
        const ImVec2 cons = ImGui::CalcTextSize(lastLoaded.c_str(), nullptr);
        ImGui::NewLine();
        ImGui::SetCursorPosX(progressWidth - cons.x/2);
        ImGui::Text("%s", lastLoaded.c_str());

        ImGui::End();
        ImGui::PopStyleColor();
        ImGui::PopFont();
    }

    void DefaultLoadingScreenDisplay::update() {

    }

    void DefaultLoadingScreenDisplay::onLeave() {

    }

    DefaultLoadingScreenDisplay::~DefaultLoadingScreenDisplay() {

    }

    void DefaultLoadingScreenDisplay::modelRegistered(std::string ident, std::string path) {
        modelCount++;
        lastLoaded = "Registered Model " + ident + " @ " + path;
    }

    void DefaultLoadingScreenDisplay::textureRegisted(std::string ident, std::string path) {
        textureCount++;
        lastLoaded = "Registered Texture " + ident + " @ " + path;
    }

    void DefaultLoadingScreenDisplay::modelLoaded(std::string ident, std::string path) {
        modelsLoaded++;
        stringstream stream;
        stream << "Loaded Model ";
        stream << ident;
        stream << " @ ";
        stream << path;
        lastLoaded = stream.str();
    }

    void DefaultLoadingScreenDisplay::textureLoaded(std::string ident, std::string path) {
        texturesLoaded++;
        lastLoaded = "Loaded Texture " + ident + " @ " + path;
    }
}

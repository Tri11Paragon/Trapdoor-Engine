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
#include <encoder.h>
#include <cmath>
#include <discord.h>
#include <profiler.h>

#include <utility>

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
    extern atg_dtv::Encoder encoder;
    extern parallel_flat_hash_map<std::string, Component*> componentAllocators;
    extern parallel_flat_hash_map<std::string, System*> systemAllocators;

    glm::mat4 perspectiveWithCenter(float width, float height, float offsetX = 0, float offsetY = 0) {
        float aspect = width / height;

        // compute the top and bottom of the near plane of the view frustum
        float top = (float) tan(glm::radians(fov) * 0.5) * 0.1f;
        float bottom = -top;

        // compute the left and right of the near plane of the view frustum
        float left = aspect * bottom;
        float right = aspect * top;

        // compute width and height of the near plane of the view frustum
        float nearWidth = right - left;
        float nearHeight = top - bottom;

        // convert the offset from canvas units to near plane units
        float offX = (offsetX) * nearWidth / width;
        float offY = (-offsetY) * nearHeight / height;

        return glm::frustum(
                left + offX,
                right + offX,
                bottom + offY,
                top + offY,
                0.1f,
                camera_far_plane);
    }

    void window::updateOnlyOrtho(int width, int height){
        updateOnlyOrtho(0,0, width,height);
    }

    void window::updateOnlyOrtho(int ox, int oy, int width, int height){
        TD::updateOrthoMatrixUBO(glm::ortho((float)ox, (float)width, (float)oy, (float)height, -5.0f, 5.0f));
    }

    void window::updateOnlyProjection(int width, int height){
        projectionMatrix = glm::perspective(glm::radians(fov), (float) width / (float) height, 0.1f, camera_far_plane);
        TD::updateProjectionMatrixUBO(projectionMatrix);
    }

    void window::updateOnlyProjection(int x, int y, int width, int height) {
        projectionMatrix = glm::perspective(glm::radians(fov), (float) width / (float) height, 0.1f, camera_far_plane);
        TD::updateProjectionMatrixUBO(projectionMatrix);
    }


    void window::initWindow(string title) {
        profiler::start("Window", "Setup");
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
        profiler::end("Window", "Setup");

        profiler::start("Window", "GL Setup");
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
        profiler::end("Window", "GL Setup");

        profiler::start("Window", "ImGUI Setup");
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

        // setup input handling
        // yes I am including window changes as a form of input
        // perhaps I shouldn't, yet I am.
        glfwSetWindowFocusCallback(_window, TD::Input::glfw_WindowFocusCallback);
        glfwSetCursorEnterCallback(_window, TD::Input::glfw_CursorEnterCallback);
        glfwSetCursorPosCallback(_window, TD::Input::glfw_CursorPosCallback);
        glfwSetMouseButtonCallback(_window, TD::Input::glfw_MouseButtonCallback);
        glfwSetScrollCallback(_window, TD::Input::glfw_ScrollCallback);
        glfwSetKeyCallback(_window, TD::Input::glfw_KeyCallback);
        glfwSetCharCallback(_window, TD::Input::glfw_CharCallback);
        glfwSetMonitorCallback(TD::Input::glfw_MonitorCallback);
        TD::Input::update();

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

        glfwGetFramebufferSize(_window, &_display_w, &_display_h);
        updateOnlyProjection(_display_w, _display_h);
        updateOnlyOrtho(_display_w, _display_h);
        profiler::endAndPrint("Window", "ImGUI Setup");

        _loadingComplete = true;
    }

    extern std::vector<WindowResize*> windowResizeCallbacks;

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
    }

    void window::finishRender() {
        forceWindowUpdate();
        // Rendering
        ImGui::Render();
        ImGui_ImplOpenGL3_RenderDrawData(ImGui::GetDrawData());

        TD::updateClock(glfwGetTime() * 1000.0);

        TD::Input::update();
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
        for (const auto& o : componentAllocators)
            delete(o.second);
        for (const auto& o : systemAllocators)
            delete(o.second);
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

    void window::setRenderFrameBufferSize(int x, int y, int width, int height) {
        updateOnlyProjection(x, y, width, height);
        for (auto & windowResizeCallback : windowResizeCallbacks)
            windowResizeCallback->windowResized(x, y, width, height);
        dlog << "Changing Projection Matrix to " << width << "w " << height << "h\n";
    }

    bool window::isListeningToResize() {
        return _listenToResize;
    }

    void window::forceWindowUpdate() {
        int pastValueW = _display_w, pastValueH = _display_h;
        glfwGetFramebufferSize(_window, &_display_w, &_display_h);
#ifdef DEBUG_ENABLED
        if (Editor::isOpen())
            glViewport(0,0, _display_w, _display_h);
#endif
        if ((pastValueW != _display_w || pastValueH != _display_h)) {
            glViewport(0,0, _display_w, _display_h);
            if (_listenToResize) {
                updateOnlyOrtho(_display_w, _display_h);
                setRenderFrameBufferSize(0, 0, _display_w, _display_h);
            }
        }
    }

    extern vector<TD::font> fonts;
    extern TD::camera *activeCamera;
    extern atg_dtv::Encoder encoder;

    static void keyCallBack(bool pressed, int code){
        if (code == GLFW_KEY_F3 && pressed)
            TD::debugUI::toggle();
#ifdef DEBUG_ENABLED
        if (code == GLFW_KEY_INSERT && pressed)
            TD::Editor::toggle();
#endif
        if (code == GLFW_KEY_ESCAPE && pressed)
            TD::Input::setMouseGrabbed(!TD::Input::isMouseGrabbed());
    }

    extern std::unordered_map<std::string, TD::Display*> displays;
    extern std::string activeDisplay;
    extern std::string projectName;
    DefaultLoadingScreenDisplay* defaultLoadDisplay = new DefaultLoadingScreenDisplay("_TD::LOADING_SCREEN_DISPLAY");
    discord::Core* discore;

    void DisplayManager::init(std::string window) {
        profiler::start("Display Manager Init", "Setup");
        // we use these and are therefore required. The TODO: is to change the paths
        TD::GameRegistry::registerFont("quicksand", "../assets/fonts/quicksand/Quicksand-Regular.ttf", 16.0f);
        TD::GameRegistry::registerFont("roboto", "../assets/fonts/roboto/Roboto-Regular.ttf", 16.0f);
        TD::GameRegistry::registerThreaded();
        TD::fontContext::loadContexts(fonts);
        TD::window::initWindow(std::move(window));
        TD::Input::IM_RegisterKeyListener(&keyCallBack);
        defaultLoadDisplay->onSwitch();
        profiler::endAndPrint("Display Manager Init", "Setup");

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
        atg_dtv::Encoder::VideoSettings settings;
        settings.fname = "Test.mp4";
        settings.width = 1280;
        settings.height = 720;
        settings.inputWidth = _display_w;
        settings.inputHeight = _display_h;
        settings.hardwareEncoding = false;
        settings.bitRate = 4500 * 1024; // 4500 KBS -- OBS setting
        settings.inputAlpha = true;
        //encoder.run(settings, 16);
        if (discore) {
            discord::Activity activity{};
            activity.SetState(projectName.c_str());
            activity.SetDetails("Making a game");
            activity.GetAssets().SetLargeImage("ben");
            activity.GetAssets().SetSmallImage("character_texture");
            discore->ActivityManager().UpdateActivity(activity, [](discord::Result result) {});
        }
    }

    void runEncoder(){
        auto* fptr = encoder.newFrame(true);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        if (fptr != nullptr)
            glReadPixels(0, _display_h, _display_w, _display_h, GL_RGBA, GL_UNSIGNED_BYTE, fptr->m_rgb);
        encoder.submitFrame();
    }

    void DisplayManager::update() {
        // Main loop
        while (!TD::window::isCloseRequested()) {
            TD::window::startRender();
            if(discore)
                discore->RunCallbacks();

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
            //runEncoder();
        }
    }

    void DisplayManager::close() {
#ifdef DEBUG_ENABLED
        TD::Editor::cleanup();
#endif
        //encoder.commit();
        //encoder.stop();
        TD::window::deleteWindow();
        for (const auto& pa : displays)
            delete(pa.second);
        profiler::cleanup();
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

    Display::Display(const std::string& name) {
        this->name = name;
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
        currentTime += (float)getFrameTimeMilliseconds();
        if (currentTime >= (float)lsTexture.getDelays()[currentFrame]) {
            currentFrame++;
            currentTime = 0;
        }
        // make sure we don't exceed the max frames
        if (currentFrame >= lsTexture.getNumberOfFrames())
            currentFrame = 0;

        float aspect = (float)lsTexture.getWidth() / (float)lsTexture.getHeight();
        const float padding = 10;
        float remHe = (float)_display_h/2 - padding*2;
        float calcWidth = remHe * aspect;

        ImGui::SetCursorPos(ImVec2(progressWidth - calcWidth/2, padding));
        ImGui::Image(lsTexture.getTextures()[currentFrame], ImVec2(calcWidth, remHe));

        ImGui::SetCursorPos(ImVec2(progressWidth - progressWidth/2, (float)_display_h/2));
        // changes background of the progress bar
        //ImGui::PushStyleColor(ImGuiCol_FrameBg, ImVec4(0.270, 0.960, 0.0192, 1.0));
        ImGui::PushStyleColor(ImGuiCol_PlotHistogram, ImVec4(0.225, 0.820, 0.00820, 1.0));
        ImGui::ProgressBar(min((float)modelsLoaded / (float)(modelCount - 1), 1.0f), ImVec2(progressWidth, 60));
        ImGui::NewLine();
        ImGui::SetCursorPosX(progressWidth - progressWidth/2);
        ImGui::ProgressBar(min((float)texturesLoaded / (float)(textureCount - 1), 1.0f), ImVec2(progressWidth, 60));
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

    DefaultLoadingScreenDisplay::~DefaultLoadingScreenDisplay() = default;

    void DefaultLoadingScreenDisplay::modelRegistered(const std::string& ident, const std::string& path) {
        modelCount++;
        loadLocked.lock();
        lastLoaded = "Registered Model " + ident + " @ " + path;
        loadLocked.unlock();
    }

    void DefaultLoadingScreenDisplay::textureRegisted(const std::string& ident, const std::string& path) {
        textureCount++;
        loadLocked.lock();
        lastLoaded = "Registered Texture " + ident + " @ " + path;
        loadLocked.unlock();
    }

    void DefaultLoadingScreenDisplay::modelLoaded(const std::string& ident, const std::string& path) {
        modelsLoaded++;
        loadLocked.lock();
        lastLoaded = "Loaded Model " + ident + " @ " + path;
        loadLocked.unlock();
    }

    void DefaultLoadingScreenDisplay::textureLoaded(const std::string& ident, const std::string& path) {
        texturesLoaded++;
        loadLocked.lock();
        lastLoaded = "Loaded Texture " + ident + " @ " + path;
        loadLocked.unlock();
    }

    TAG_COMPOUND* DefaultLoadingScreenDisplay::onSave() { return nullptr; }

    void DefaultLoadingScreenDisplay::onLoad(TAG_COMPOUND* tag) {}
}

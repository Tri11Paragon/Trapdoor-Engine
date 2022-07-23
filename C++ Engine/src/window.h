//
// Created by brett on 20/07/22.
//
#ifndef ENGINE_WINDOW_H
#define ENGINE_WINDOW_H

#include "std.h"
#include "logging.h"
#include "renderer/gl.h" // Will drag system OpenGL headers
#include "imgui/imgui.h"
#include "imgui/imgui_impl_glfw.h"
#include "imgui/imgui_impl_opengl3.h"

using namespace std;

#include "font.h"
#include "input.h"

static fontContext NULL_CONTEXT((vector<font>()));

class window {
private:
    GLFWwindow* _window;
    int _display_w, _display_h;
    bool _loadingComplete = false;
    static void glfw_error_callback(int error, const char* description) {
        elog << "Glfw Error " << error << ": " <<description << std::endl;
    }
public:
    window(): window("Generic GLFW Window") {};
    window(string title): window(title, NULL_CONTEXT) {};
    window(string title, fontContext& fonts){
        // Setup window
        glfwSetErrorCallback(glfw_error_callback);
        if (!glfwInit()) {
            flog << "GLFW Init Error";
            return;
        }

        // Setup GL Version
        const char* glsl_version = "#version 130";
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);  // 3.2+ only
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);            // 3.0+ only

        ilog << "GLFW Window Setup complete, using GL4.5" << "\n";

        // Create window with graphics context
        _window = glfwCreateWindow(1280, 720, title.c_str(), NULL, NULL);
        if (_window == NULL) {
            flog << "Unable to create GLFW window\n";
            return;
        }
        glfwMakeContextCurrent(_window);
        glfwSwapInterval(0); // Enable vsync

        ilog << "GLFW Window Created.\n";
        ilog << "Creating GLAD2 GL Context\n";

        int version = gladLoadGL(glfwGetProcAddress);
        if (version == 0) {
            elog << "Failed to initialize OpenGL context\n";
            return;
        }

        ilog << "Glad Init Complete. Loaded GL" << GLAD_VERSION_MAJOR(version) << "." << GLAD_VERSION_MINOR(version) << "\n";
        ilog << "Creating Dear ImGUI Context.\n";

        // Setup Dear ImGui context
        IMGUI_CHECKVERSION();
        ImGui::CreateContext();
        ImGuiIO& io = ImGui::GetIO(); (void)io;
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

        fonts.load(io);

        _loadingComplete = true;
    }
    void startRender(float r, float g, float b, float a){
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

        glfwGetFramebufferSize(_window, &_display_w, &_display_h);
        glViewport(0, 0, _display_w, _display_h);
        glClearColor(r * a, g * a, b * a, a);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
    void finishRender(){
        // Rendering
        ImGui::Render();
        ImGui_ImplOpenGL3_RenderDrawData(ImGui::GetDrawData());

        glfwSwapBuffers(_window);
    }
    bool isCloseRequested(){
        return glfwWindowShouldClose(_window);
    }
    int width(){
        return _display_w;
    }
    int height(){
        return _display_h;
    }
    bool loadingCompleted(){
        return _loadingComplete;
    }
    ~window(){
        // Cleanup
        ImGui_ImplOpenGL3_Shutdown();
        ImGui_ImplGlfw_Shutdown();
        ImGui::DestroyContext();
        ilog << "ImGui Successfully closed.\n";

        glfwDestroyWindow(_window);
        glfwTerminate();
        ilog << "Window Successfully deleted.\n";
    }
};


#endif //ENGINE_WINDOW_H

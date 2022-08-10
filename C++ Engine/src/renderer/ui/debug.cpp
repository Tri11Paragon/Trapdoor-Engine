//
// Created by brett on 21/07/22.
//

#include "debug.h"
#include "../../window.h"
#include "../../world/World.h"
#include "../../imgui/ImGuizmo.h"
#include <cmath>
//#include "raycasting.h"

namespace TD {
    extern TD::camera *activeCamera;
    extern bool debugMenuEnabled;
    extern bool editorMenuEnabled;
    extern unordered_map<string, DebugTab*> debugTabs;
    extern int _display_w, _display_h;
    extern GLFWwindow *_window;
    extern std::vector<WindowResize*> windowResizeCallbacks;
    extern std::unordered_map<std::string, TD::Display*> displays;
    extern std::string activeDisplay;
    extern glm::mat4 viewMatrix;
    extern glm::mat4 projectionMatrix;
    extern float fov;

    void TD::debugUI::toggle() {
        debugMenuEnabled = !debugMenuEnabled;
    }

    void TD::debugUI::render() {
        if (!debugMenuEnabled)
            return;
        ImGui::PushFont(TD::fontContext::get("roboto"));
        static int corner = 0;
        ImGuiIO& io = ImGui::GetIO();
        //ImGuiWindowFlags window_flags =
        //        ImGuiWindowFlags_NoDecoration | ImGuiWindowFlags_AlwaysAutoResize | ImGuiWindowFlags_NoSavedSettings |
        //        ImGuiWindowFlags_NoFocusOnAppearing | ImGuiWindowFlags_NoNav;
        ImGuiWindowFlags window_flags = ImGuiWindowFlags_AlwaysAutoResize;
        //ImGui::SetNextWindowBgAlpha(0.35f);
        if (!ImGui::Begin("Debug Menu", nullptr, window_flags)) {
            ImGui::End();
            return;
        }
        if (ImGui::BeginTabBar("debugTabs")) {
            if (ImGui::BeginTabItem("General")) {
                ImGui::Text("Application average %.3f ms/frame (%.1f FPS)", 1000.0f / ImGui::GetIO().Framerate,
                            ImGui::GetIO().Framerate);
                ImGui::Text("Camera Position: %f, %f, %f", activeCamera->getPosition().x, activeCamera->getPosition().y,
                            activeCamera->getPosition().z);
                ImGui::EndTabItem();
            }

            for (std::pair<string, DebugTab *> tab: debugTabs) {
                if (ImGui::BeginTabItem(tab.first.c_str())) {
                    tab.second->render();
                    ImGui::EndTabItem();
                }
            }

            ImGui::EndTabBar();
        }

        ImGui::End();
        ImGui::PopFont();
    }
    void debugUI::addTab(DebugTab* tab) {
        debugTabs.insert(std::pair<string, DebugTab*>(tab->getName(), tab));
    }

    void debugUI::deleteTab(DebugTab* tab) {
        debugTabs.erase(tab->getName());
        //delete(tab);
    }

    void debugUI::deleteAllTabs() {
        for (std::pair<string, DebugTab*> tab : debugTabs){
            //delete(tab.second);
        }
    }

    // for entity view
    int sceneHierarchyWidth = 255;
    int sceneHierarchyHeight = 0;
    // for inspector view
    int sceneInspectorWidth = 275;
    int sceneInspectorHeight = 0;
    // console window
    int sceneConsoleWidth = 0;
    int sceneConsoleHeight = 255;
    // menu bar
    int menuBarWidth = 0;
    int menuBarHeight = 22;
    int lWindowWidth = 0, lWindowHeight = 0;
    int iWidth = 0, iHeight = 0, offsetX=0, offsetY=0;

    glm::vec2 getScreenPos(glm::vec3 pos){
        glm::vec4 clip = projectionMatrix * viewMatrix * glm::vec4(pos.x, pos.y, pos.z, 1.0f);
        glm::vec3 ndc = glm::vec3(clip.x / clip.w, clip.y / clip.w, clip.z / clip.w);
        float sx = (float) ((ndc.x + 1.0) / 2.0f) * (float)_display_w;
        float sy = (float) ((ndc.y + 1.0) / 2.0f) * (float)_display_h;
        return {sx, (float)_display_h - sy};
    }

    void updateWindowSizes(){
        if (lWindowWidth != _display_w || lWindowHeight != _display_h) {
            lWindowWidth = _display_w;
            lWindowHeight = _display_h;

            offsetX = sceneHierarchyWidth;
            //offsetY = menuBarHeight;
            sceneHierarchyHeight = lWindowHeight - menuBarHeight;
            menuBarWidth = lWindowWidth;


            iHeight = lWindowHeight - menuBarHeight;
            iWidth = lWindowWidth - sceneHierarchyWidth;

            TD::window::setRenderFrameBufferSize(offsetX, offsetY, iWidth, iHeight);
        }
        //glViewport(offsetX, offsetY, iWidth, iHeight);
    }

    static std::string activeEntity;
    static ID activeEntityID = 0;

    void Editor::init() {
        tlog << "Loading debug editor resources";
    }

    void Editor::render() {
        if (!editorMenuEnabled)
            return;
        updateWindowSizes();
        ImGui::PushFont(TD::fontContext::get("roboto"));

        ImGui::ShowDemoWindow();

        ImGui::SetNextWindowPos(ImVec2(0,(float)menuBarHeight));
        ImGui::SetNextWindowSize(ImVec2((float)sceneHierarchyWidth, (float)sceneHierarchyHeight));
        constexpr auto flags = ImGuiWindowFlags_NoResize | ImGuiWindowFlags_NoCollapse;
        ImGui::PushStyleColor(ImGuiCol_TitleBg, ImGui::GetStyleColorVec4(ImGuiCol_TitleBgActive));
        ImGui::Begin("SceneView", nullptr, flags);

        static glm::mat4 trans(1.0);

        auto* world = displays[activeDisplay]->getWorld();
        if (ImGui::CollapsingHeader(activeDisplay.c_str())){
            if (world != nullptr){
                ImGui::BeginChild("_EntityDisplay");
                    for (auto& e : *world) {
                        if (ImGui::Selectable(e.first.c_str(), e.first == activeEntity)) {
                            activeEntity = e.first;
                            activeEntityID = e.second->getID();
                            trans = world->getComponent<TransformComponent>(TRANSFORM_SYSTEM, activeEntityID)->getTranslationMatrix();
                        }
                    }
                ImGui::EndChild();
            }
        }

        ImGui::End();
        ImGui::PopStyleColor();

        if (ImGui::BeginMainMenuBar()){
            if (ImGui::BeginMenu("File")){
                if (ImGui::BeginMenu("Open")){
                    for (const auto& p : displays){
                        if (p.first == activeDisplay)
                            continue;
                        ImGui::MenuItem(p.first.c_str());
                    }
                    ImGui::EndMenu();
                }
                if (ImGui::MenuItem("New")){}
                if (ImGui::MenuItem("Save")){}
                if (ImGui::MenuItem("Save As..")){}
                ImGui::EndMenu();
            }
            if (ImGui::BeginMenu("Edit")){
                if (ImGui::MenuItem("Undo", "CTRL+Z")) {}
                if (ImGui::MenuItem("Redo", "CTRL+Y", false, false)) {}  // Disabled item
                ImGui::Separator();
                if (ImGui::MenuItem("Cut", "CTRL+X")) {}
                if (ImGui::MenuItem("Copy", "CTRL+C")) {}
                if (ImGui::MenuItem("Paste", "CTRL+V")) {}
                ImGui::EndMenu();
            }

            menuBarHeight = (int)ImGui::GetWindowSize().y;
            ImGui::EndMainMenuBar();
        }

        ImGuizmo::SetOrthographic(false);
        ImGuizmo::SetDrawlist();
        ImGuizmo::BeginFrame();

        static ImGuizmo::OPERATION mCurrentGizmoOperation(ImGuizmo::ROTATE);
        static ImGuizmo::MODE mCurrentGizmoMode(ImGuizmo::WORLD);

        if (ImGui::RadioButton("Translate", mCurrentGizmoOperation == ImGuizmo::TRANSLATE))
            mCurrentGizmoOperation = ImGuizmo::TRANSLATE;
        ImGui::SameLine();
        if (ImGui::RadioButton("Rotate", mCurrentGizmoOperation == ImGuizmo::ROTATE))
            mCurrentGizmoOperation = ImGuizmo::ROTATE;
        ImGui::SameLine();
        if (ImGui::RadioButton("Scale", mCurrentGizmoOperation == ImGuizmo::SCALE))
            mCurrentGizmoOperation = ImGuizmo::SCALE;
        /*float matrixTranslation[3], matrixRotation[3], matrixScale[3];
        ImGuizmo::DecomposeMatrixToComponents(glm::value_ptr(trans), matrixTranslation, matrixRotation, matrixScale);
        ImGui::InputFloat3("Tr", matrixTranslation);
        ImGui::InputFloat3("Rt", matrixRotation);
        ImGui::InputFloat3("Sc", matrixScale);
        ImGuizmo::RecomposeMatrixFromComponents(matrixTranslation, matrixRotation, matrixScale, glm::value_ptr(trans));*/

        if (mCurrentGizmoOperation != ImGuizmo::SCALE)
        {
            if (ImGui::RadioButton("Local", mCurrentGizmoMode == ImGuizmo::LOCAL))
                mCurrentGizmoMode = ImGuizmo::LOCAL;
            ImGui::SameLine();
            if (ImGui::RadioButton("World", mCurrentGizmoMode == ImGuizmo::WORLD))
                mCurrentGizmoMode = ImGuizmo::WORLD;
        }
        static bool useSnap(false);
        if (ImGui::IsKeyPressed(83))
            useSnap = !useSnap;
        ImGui::Checkbox("Use Snap?", &useSnap);
        ImGui::SameLine();
        static glm::vec3 snap{5, 5, 5};
        switch (mCurrentGizmoOperation) {
            case ImGuizmo::TRANSLATE:
                ImGui::InputFloat3("Snap", &snap.x);
                break;
            case ImGuizmo::ROTATE:
                ImGui::InputFloat("Angle Snap", &snap.x);
                break;
            case ImGuizmo::SCALE:
                ImGui::InputFloat("Scale Snap", &snap.x);
                break;
            default:
                tlog << "Default Branch!";
                break;
        }
        ImGuiIO& io = ImGui::GetIO();
        ImGuizmo::SetRect((float)offsetX, (float)offsetY, (float)_display_w, (float)_display_h);
        ImGuizmo::Manipulate(glm::value_ptr(viewMatrix), glm::value_ptr(projectionMatrix), mCurrentGizmoOperation, mCurrentGizmoMode, glm::value_ptr(trans), nullptr, useSnap ? &snap.x : nullptr);

        float matrixTranslation[3], matrixRotation[3], matrixScale[3];
        ImGuizmo::DecomposeMatrixToComponents(glm::value_ptr(trans), matrixTranslation, matrixRotation, matrixScale);
        auto transformComp = world->getComponent<TransformComponent>(TRANSFORM_SYSTEM, activeEntityID);
        transformComp->setTranslation(glm::vec3(matrixTranslation[0], matrixTranslation[1], matrixTranslation[2]));
        transformComp->setRotation(glm::vec3(matrixRotation[0], matrixRotation[1], matrixRotation[2]));
        transformComp->setScale(glm::vec3(matrixScale[0], matrixScale[1], matrixScale[2]));
        ImGui::PopFont();
    }

    void Editor::renderGBuffer() {
        if (!editorMenuEnabled)
            return;
    }

    void Editor::toggle() {
        if (editorMenuEnabled)
            close();
        else
            open();
    }

    void Editor::open() {
        editorMenuEnabled = true;
        TD::window::setListenToResize(false);

        lWindowWidth = 0;
        lWindowHeight = 0;
        updateWindowSizes();
    }

    void Editor::close() {
        editorMenuEnabled = false;
        TD::window::setListenToResize(true);
        TD::window::setRenderFrameBufferSize(0, 0, _display_w, _display_h);
        TD::window::forceWindowUpdate();
    }

    bool Editor::isOpen() {
        return editorMenuEnabled;
    }

    void Editor::cleanup() {

    }

}

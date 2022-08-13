//
// Created by brett on 21/07/22.
//

#include "renderer/ui/debug.h"
#include "window.h"
#include "world/World.h"
#include "imgui/ImGuizmo.h"
#include "imgui/imgui_internal.h"
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
        if (ImGui::Begin("Debug Menu", nullptr, window_flags)) {
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

#ifdef DEBUG_ENABLED

    // for entity view
    int sceneHierarchyWidth = 255;
    int sceneHierarchyHeight = 0;
    // for inspector view
    int sceneInspectorWidth = 275;
    int sceneInspectorHeight = 0;
    // console window
    int sceneConsoleWidth = 0;
    int sceneConsoleHeight = 320;
    // component add size
    int sceneComponentAddHeight = 0;
    // menu bar
    int menuBarWidth = 0;
    int menuBarHeight = 22;
    int lWindowWidth = 0, lWindowHeight = 0;
    int iWidth = 0, iHeight = 0, offsetX=0, offsetY=0, offsetNegX = 0, offsetNegY = 0;

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
            offsetNegX = sceneInspectorWidth;
            offsetY = sceneConsoleHeight;
            //offsetY = menuBarHeight;
            sceneHierarchyHeight = lWindowHeight - menuBarHeight;
            sceneInspectorHeight = lWindowHeight - menuBarHeight;
            sceneConsoleWidth = lWindowWidth - offsetX - offsetNegX;
            menuBarWidth = lWindowWidth;


            iHeight = lWindowHeight - menuBarHeight - sceneConsoleHeight;
            iWidth = lWindowWidth - sceneHierarchyWidth - sceneInspectorWidth;

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
        ImGui::SetNextWindowBgAlpha(1.0);
        ImGui::Begin("SceneView", nullptr, flags);

        static glm::mat4 trans(1.0);

        auto* world = displays[activeDisplay]->getWorld();
        if (ImGui::CollapsingHeader(activeDisplay.c_str(), nullptr, ImGuiTreeNodeFlags_DefaultOpen)){
            if (world != nullptr){
                ImGui::BeginChild("_EntityDisplay", ImVec2(0,(float)sceneHierarchyHeight - 120));
                    for (auto& e : *world) {
                        if (ImGui::Selectable(e.first.c_str(), e.first == activeEntity)) {
                            activeEntity = e.first;
                            activeEntityID = e.second->getID();
                            trans = world->getComponentRaw<TransformComponent>(activeEntityID)->getTranslationMatrix();
                        }
                    }
                ImGui::EndChild();
                static char stringBuffer[512]{};
                std::string name;
                ImGui::Text("Name: ");
                ImGui::SameLine();
                ImGui::InputText("##", stringBuffer, 512, ImGuiInputTextFlags_EnterReturnsTrue);
                Strtrim(stringBuffer);
                if (stringBuffer[0]){name = std::string(stringBuffer);}
                if (ImGui::Button("Add Entity", ImVec2((float)sceneHierarchyWidth - 16, 25))){
                    if (name.empty())
                        wlog << "Entity name cannot be empty!";
                    else
                        world->spawnEntity(new Entity(name));
                }
            }
        }

        ImGui::End();

        /** Inspector! */
        ImGui::SetNextWindowBgAlpha(1.0);
        ImGui::SetNextWindowPos(ImVec2((float)(_display_w - sceneInspectorWidth),(float)menuBarHeight));
        ImGui::SetNextWindowSize(ImVec2((float)sceneInspectorWidth, (float)sceneInspectorHeight));
        ImGui::Begin("Inspector", nullptr, flags);
        ImGui::BeginChild("__Components", ImVec2((float)sceneInspectorWidth, (float)sceneInspectorHeight - 340));

        static ImGuizmo::OPERATION mCurrentGizmoOperation(ImGuizmo::TRANSLATE);
        static ImGuizmo::MODE mCurrentGizmoMode(ImGuizmo::WORLD);

        if (ImGui::RadioButton("Translate", mCurrentGizmoOperation == ImGuizmo::TRANSLATE))
            mCurrentGizmoOperation = ImGuizmo::TRANSLATE;
        ImGui::SameLine();
        if (ImGui::RadioButton("Rotate", mCurrentGizmoOperation == ImGuizmo::ROTATE))
            mCurrentGizmoOperation = ImGuizmo::ROTATE;
        ImGui::SameLine();
        if (ImGui::RadioButton("Scale", mCurrentGizmoOperation == ImGuizmo::SCALE))
            mCurrentGizmoOperation = ImGuizmo::SCALE;

        if (mCurrentGizmoOperation != ImGuizmo::SCALE)
        {
            if (ImGui::RadioButton("Local", mCurrentGizmoMode == ImGuizmo::LOCAL))
                mCurrentGizmoMode = ImGuizmo::LOCAL;
            ImGui::SameLine();
            if (ImGui::RadioButton("World", mCurrentGizmoMode == ImGuizmo::WORLD))
                mCurrentGizmoMode = ImGuizmo::WORLD;
        }
        static bool useSnap(false);
        ImGui::Checkbox("Use Snap?", &useSnap);
        ImGui::SameLine();
        static glm::vec3 snap{5, 5, 5};
        static int snapPos = 0;
        switch (mCurrentGizmoOperation) {
            case ImGuizmo::TRANSLATE:
                ImGui::InputFloat3("Snap", &snap.x);
                snapPos = 0;
                break;
            case ImGuizmo::ROTATE:
                ImGui::InputFloat("Angle Snap", &snap.y);
                snapPos = 1;
                break;
            case ImGuizmo::SCALE:
                ImGui::InputFloat("Scale Snap", &snap.z);
                snapPos = 2;
                break;
            default:
                snapPos = 0;
                tlog << "Default Branch!";
                break;
        }
        ImGui::NewLine();

        if (world != nullptr) {
            if (activeEntity.empty())
                activeEntity = world->getEntityNameByID(activeEntityID);
            if (!activeEntity.empty()) {
                try {
                    auto localEnt = world->getEntity(activeEntity);
                    if (localEnt.isValid()) {
                        for (auto c: localEnt->getComponents()) {
                            if (ImGui::CollapsingHeader(c->getName().c_str(), nullptr, ImGuiTreeNodeFlags_DefaultOpen)) {
                                c->drawImGuiVariables();
                                ImGui::NewLine();
                            }
                        }
                    }
                } catch (std::exception &e) {
                    wlog << e.what();
                }
            }
        }
        ImGui::EndChild();
        ImGui::BeginChild("__ComponentsAllocator", ImVec2(0, 290), false, ImGuiWindowFlags_NoScrollbar);
            //static char stringBuffer[512]{};
            static ImGuiTextFilter filter;
            static std::string selectedCompAlloc;
            static Component* allocator = nullptr;
            //ImGui::Text("Filter: ");
            //ImGui::InputText("##", stringBuffer, 512, ImGuiInputTextFlags_EnterReturnsTrue);
            //Strtrim(stringBuffer);
            filter.Draw();
            ImGui::BeginChild("__ComAllocPart", ImVec2(0,235), true, ImGuiWindowFlags_AlwaysAutoResize);
            for (const auto& compAlloc : componentAllocators) {
                if (filter.PassFilter(compAlloc.first.c_str())){
                    if (ImGui::Selectable(compAlloc.first.c_str(), selectedCompAlloc == compAlloc.first)){
                        selectedCompAlloc = compAlloc.first;
                        allocator = compAlloc.second;
                    }
                }
            }
            ImGui::EndChild();
            if (ImGui::Button("Add Component", ImVec2((float)sceneInspectorWidth - 16, 25))){
                if (allocator != nullptr && !world->hasComponent(allocator->getName(), activeEntityID)) {
                    world->addComponentToEntity(activeEntity, allocator->allocateDefault());
                } else
                    wlog << "Entity already has component!";
            }
            sceneComponentAddHeight = (int) ImGui::GetWindowHeight();
        ImGui::EndChild();
        ImGui::End();

        ImGui::SetNextWindowBgAlpha(1.0);
        ImGui::SetNextWindowPos(ImVec2((float)(sceneHierarchyWidth),(float)(_display_h - sceneConsoleHeight)));
        ImGui::SetNextWindowSize(ImVec2((float)sceneConsoleWidth, (float)sceneConsoleHeight));
        ImGui::Begin("Console", nullptr, flags | ImGuiWindowFlags_NoTitleBar);
            if(ImGui::BeginTabBar("bar")) {
                if (ImGui::BeginTabItem("Console")) {
                    const float footer_height_to_reserve = ImGui::GetStyle().ItemSpacing.y + ImGui::GetFrameHeightWithSpacing();
                    ImGui::BeginChild("ScrollingRegion", ImVec2(0, -footer_height_to_reserve), false, ImGuiWindowFlags_HorizontalScrollbar);
                        bool copyToClip = false;
                        static bool autoScroll = true;
                        if (ImGui::BeginPopupContextWindow()) {
                            if (ImGui::Selectable("Clear")) td_logItems.clear();
                            copyToClip = ImGui::SmallButton("Copy");
                            ImGui::EndPopup();
                        }

                        ImGui::PushStyleVar(ImGuiStyleVar_ItemSpacing, ImVec2(4, 1)); // Tighten spacing
                            ImGuiListClipper clipper;
                            clipper.Begin((int)td_logItems.size());
                            if (copyToClip)
                                ImGui::LogToClipboard();
                            while (clipper.Step()) {
                                for (int row_n = clipper.DisplayStart; row_n < clipper.DisplayEnd; row_n++) {
                                    auto item = td_logItems[row_n];
                                    auto colorData = colorArray[item.color];
                                    ImVec4 color(colorData[0], colorData[1], colorData[2], 1.0f);
                                    ImGui::PushStyleColor(ImGuiCol_Text, color);
                                    ImGui::TextUnformatted(item.log.c_str());
                                    ImGui::PopStyleColor();
                                }
                            }
                            if (copyToClip)
                                ImGui::LogFinish();
                            if ((autoScroll && ImGui::GetScrollY() >= ImGui::GetScrollMaxY()))
                                ImGui::SetScrollHereY(1.0f);

                        ImGui::PopStyleVar();
                    ImGui::EndChild();
                    ImGui::EndTabItem();
                }
                if (ImGui::BeginTabItem("Assets")) {
                    ImGui::BeginChild("AssetsBrowser");
                        ImGui::Text("Not implemented yet.");
                    ImGui::EndChild();
                    ImGui::EndTabItem();
                }
                ImGui::EndTabBar();
                ImGui::Separator();
            }
        ImGui::End();

        ImGui::PopStyleColor();
        /** Menu Bar */
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
        ImGuizmo::BeginFrame();

        ImGuizmo::SetRect((float)offsetX, (float)-offsetY, (float)(_display_w), (float)(_display_h));
        ImGuizmo::Manipulate(glm::value_ptr(viewMatrix), glm::value_ptr(projectionMatrix), mCurrentGizmoOperation, mCurrentGizmoMode, glm::value_ptr(trans), nullptr, useSnap ? &snap[snapPos] : nullptr);

        float matrixTranslation[3], matrixRotation[3], matrixScale[3];
        ImGuizmo::DecomposeMatrixToComponents(glm::value_ptr(trans), matrixTranslation, matrixRotation, matrixScale);
        auto transformComp = world->getComponentRaw<TransformComponent>(activeEntityID);
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
#endif

}

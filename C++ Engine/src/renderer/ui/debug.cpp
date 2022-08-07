//
// Created by brett on 21/07/22.
//

#include "debug.h"

namespace TD {
    extern TD::camera *activeCamera;
    extern bool debugMenuEnabled;
    extern bool editorMenuEnabled;
    extern unordered_map<string, DebugTab*> debugTabs;

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
        if (!ImGui::Begin("Debug Menu", NULL, window_flags)) {
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

    void Editor::init() {

    }

    void Editor::render() {

    }

    void Editor::toggle() {
        editorMenuEnabled = !editorMenuEnabled;
    }
}

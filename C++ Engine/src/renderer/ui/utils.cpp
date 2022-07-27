//
// Created by brett on 21/07/22.
//

#include "utils.h"

namespace TD {
    extern TD::camera *activeCamera;
    extern bool debugMenuEnabled;

    void TD::debugUI::toggle() {
        debugMenuEnabled = !debugMenuEnabled;
    }

    void TD::debugUI::changeActiveCamera(TD::camera *camera) {
        activeCamera = camera;
    }

    void TD::debugUI::render() {
        if (!debugMenuEnabled)
            return;
        ImGui::PushFont(TD::fontContext::get("roboto"));
        static int corner = 0;
        ImGuiIO& io = ImGui::GetIO();
        ImGuiWindowFlags window_flags =
                ImGuiWindowFlags_NoDecoration | ImGuiWindowFlags_AlwaysAutoResize | ImGuiWindowFlags_NoSavedSettings |
                ImGuiWindowFlags_NoFocusOnAppearing | ImGuiWindowFlags_NoNav;
        //ImGui::SetNextWindowBgAlpha(0.35f);
        if (!ImGui::Begin("Debug Menu", NULL, window_flags)) {
            ImGui::End();
            return;
        }
        ImGui::BeginTabBar("debugTabs");

        ImGui::BeginTabItem("General");
            ImGui::Text("Application average %.3f ms/frame (%.1f FPS)", 1000.0f / ImGui::GetIO().Framerate, ImGui::GetIO().Framerate);
            ImGui::Text("Camera Position: %f, %f, %f", activeCamera->getPosition().x, activeCamera->getPosition().y, activeCamera->getPosition().z);
        ImGui::EndTabItem();

        ImGui::EndTabBar();

        ImGui::End();
        ImGui::PopFont();
    }
}

//
// Created by brett on 21/07/22.
//

#ifndef ENGINE_UTILS_H
#define ENGINE_UTILS_H

#include "../../imgui/imgui.h"
#include "../../font.h"
#include "../camera.h"

namespace TD {

    class renderable {
    public:
        virtual void render(fontContext& fonts) = 0;
    };

    class debugUI : renderable {
    private:
        bool enabled = false;
        TD::camera* camera;
    public:
        debugUI(TD::camera* camera);
        void toggle(){
            enabled = !enabled;
        }
        void render(fontContext& fc) override {
            if (!enabled)
                return;
            ImGui::PushFont(fc["roboto"]);
            ImGui::Begin("Debug Info");

            ImGui::Text("Application average %.3f ms/frame (%.1f FPS)", 1000.0f / ImGui::GetIO().Framerate, ImGui::GetIO().Framerate);
            ImGui::Text("Camera Position: %f, %f, %f", camera->getPosition().x, camera->getPosition().y, camera->getPosition().z);

            ImGui::End();
            ImGui::PopFont();
        }
    };

}


#endif //ENGINE_UTILS_H

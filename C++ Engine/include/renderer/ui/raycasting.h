//
// Created by brett on 08/08/22.
//

#ifndef ENGINE_RAYCASTING_H
#define ENGINE_RAYCASTING_H

#include "../../glm.h"
#include "../../input.h"

namespace TD{

    extern int _display_w, _display_h;
    extern glm::mat4 viewMatrix;
    extern glm::mat4 projectionMatrix;

    class Raycasting {
    private:
        static glm::vec3 currentScreenRay;
        static glm::vec3 currentMouseRay;
    public:
        static glm::vec3 getCurrentScreenRay() {
            return currentScreenRay;
        }
        static glm::vec3 getCurrentMouseRay() {
            return currentMouseRay;
        }
        static void update() {
            currentScreenRay = calculateScreenRay();
            currentMouseRay = calculateMouseRay();
        }
        static glm::vec3 calculateScreenRay() {
            float mouseX = (float) _display_w / 2;
            float mouseY = (float) _display_h /2;
            glm::vec2 normalizedCoords = getNormalisedDeviceCoordinates(mouseX, mouseY);
            glm::vec4 clipCoords = glm::vec4(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f);
            glm::vec4 eyeCoords = toEyeCoords(clipCoords);
            glm::vec3 worldRay = toWorldCoords(eyeCoords);
            return worldRay;
        }
        static glm::vec3 calculateMouseRay() {
            auto mouseX = (float) getMouseX();
            auto mouseY = (float) getMouseY();
            glm::vec2 normalizedCoords = getNormalisedDeviceCoordinates(mouseX, mouseY);
            glm::vec4 clipCoords = glm::vec4(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f);
            glm::vec4 eyeCoords = toEyeCoords(clipCoords);
            glm::vec3 worldRay = toWorldCoords(eyeCoords);
            return worldRay;
        }
        static glm::vec3 toWorldCoords(glm::vec4 eyeCoords) {
            glm::mat4 invertedView = glm::inverse(viewMatrix);
            glm::vec4 rayWorld = invertedView * eyeCoords;
            return glm::normalize(glm::vec3(rayWorld.x, rayWorld.y, rayWorld.z));
        }

        static glm::vec4 toEyeCoords(glm::vec4 clipCoords) {
            glm::mat4 invertedProjection = glm::inverse(projectionMatrix);
            glm::vec4 eyeCoords = invertedProjection * clipCoords;
            return {eyeCoords.x, eyeCoords.y, -1.0f, 0.0f};
        }

        static glm::vec2 getNormalisedDeviceCoordinates(float mouseX, float mouseY) {
            float x = (2.0f * mouseX) / _display_w - 1.0f;
            float y = (2.0f * mouseY) / _display_h - 1.0f;
            return {x, y};
        }
    };

}

#endif //ENGINE_RAYCASTING_H

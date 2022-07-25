//
// Created by brett on 23/07/22.
//

#include "camera.h"
#include "../input.h"
#include "../window.h"
#include "gl.h"
#include <math.h>
#include "../clock.h"

namespace TD {

    void firstPersonCamera::update() {
        if (TD::isMouseGrabbed()) {
            _pitch += TD::getMouseDY() * (turnSpeedY) / 100;
            _yaw += TD::getMouseDX() * (turnSpeedX) / 100;

            const float speedd = 30.0f;

            if (TD::isKeyDown(KEY_LEFT))
                _yaw += -speedd * turnSpeedX * TD::getFrameTimeSeconds();
            if (TD::isKeyDown(KEY_RIGHT))
                _yaw += speedd * turnSpeedX * TD::getFrameTimeSeconds();
            if (TD::isKeyDown(KEY_UP))
                _pitch += -speedd * turnSpeedY * TD::getFrameTimeSeconds();
            if (TD::isKeyDown(KEY_DOWN))
                _pitch += speedd * turnSpeedY * TD::getFrameTimeSeconds();

            if (_pitch > 90)
                _pitch = 90;
            if (_pitch < -90)
                _pitch = -90;
            if (_yaw < -360)
                _yaw = 0;
            if (_yaw > 360)
                _yaw = 0;

            if (TD::isKeyDown(GLFW_KEY_LEFT_ALT))
                speed = 5.0f;
            else if (TD::isKeyDown(KEY_L_CONTROL))
                speed = 150.0f;
            else
                speed = 40.0f;

            if (TD::isKeyDown(KEY_W))
                _moveAtX = (float) (-speed * TD::getFrameTimeSeconds());

            else if (TD::isKeyDown(KEY_S))
                _moveAtX = (float) (speed * TD::getFrameTimeSeconds());
            else
                _moveAtX = 0;

            if (TD::isKeyDown(KEY_A))
                _moveAtZ = (float) (speed * TD::getFrameTimeSeconds());
            else if (TD::isKeyDown(KEY_D))
                _moveAtZ = (float) (-speed * TD::getFrameTimeSeconds());
            else
                _moveAtZ = 0;

            if (TD::isKeyDown(KEY_SPACE))
                _moveAtY = (float) (speed * TD::getFrameTimeSeconds());
            else
                _moveAtY = 0;

            if (TD::isKeyDown(KEY_LEFT_SHIFT))
                _moveAtY = (float) (-speed * TD::getFrameTimeSeconds());

            // TODO: remove this shit
            float dx = (float) (-(_moveAtX * sin(glm::radians(_yaw))) + -(_moveAtZ * cos(glm::radians(_yaw))));
            float dy = (float) (_moveAtX * (sin(glm::radians(_roll)))) + _moveAtY;
            float dz = (float) (((_moveAtX * cos(glm::radians(_yaw))) + -(_moveAtZ * sin(glm::radians(_yaw)))));

            _cameraPos.x += dx;
            _cameraPos.y += dy;
            _cameraPos.z += dz;
        }

        glm::mat4 viewMatrix = glm::mat4(1.0);
        viewMatrix = glm::rotate(viewMatrix, glm::radians(_pitch), glm::vec3(1, 0, 0));
        viewMatrix = glm::rotate(viewMatrix, glm::radians(_yaw), glm::vec3(0, 1, 0));
        viewMatrix = glm::translate(viewMatrix, -_cameraPos);

        TD::updateViewMatrixUBO(viewMatrix);
    }

    glm::vec3 camera::getPosition() {
        return _cameraPos;
    }

    glm::vec3 camera::getRotation() {
        return glm::vec3(_yaw, _pitch, _roll);
    }

    float camera::getYaw() {
        return _yaw;
    }

    float camera::getPitch() {
        return _pitch;
    }

    float camera::getRoll() {
        return _roll;
    }
}

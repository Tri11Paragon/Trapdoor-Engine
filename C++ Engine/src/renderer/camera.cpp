//
// Created by brett on 23/07/22.
//

#include "renderer/camera.h"
#include "input.h"
#include "window.h"
#include "renderer/gl.h"
#include <math.h>
#include "clock.h"
#include <cmath>

namespace TD {

    void firstPersonCamera::update() {
        if (TD::Input::isMouseGrabbed()) {
            _pitch += TD::Input::getMouseDY() * (turnSpeedY) / 100;
            _yaw += TD::Input::getMouseDX() * (turnSpeedX) / 100;

            constexpr double speedd = 30.0f;

            if (TD::Input::isKeyDown(KEY_LEFT))
                _yaw += -speedd * turnSpeedX * TD::getFrameTimeSeconds();
            if (TD::Input::isKeyDown(KEY_RIGHT))
                _yaw += speedd * turnSpeedX * TD::getFrameTimeSeconds();
            if (TD::Input::isKeyDown(KEY_UP))
                _pitch += -speedd * turnSpeedY * TD::getFrameTimeSeconds();
            if (TD::Input::isKeyDown(KEY_DOWN))
                _pitch += speedd * turnSpeedY * TD::getFrameTimeSeconds();

            if (_pitch > 90)
                _pitch = 90;
            if (_pitch < -90)
                _pitch = -90;
            if (_yaw < -360)
                _yaw = 0;
            if (_yaw > 360)
                _yaw = 0;

            if (TD::Input::isKeyDown(GLFW_KEY_LEFT_ALT))
                speed = 5.0f;
            else if (TD::Input::isKeyDown(KEY_L_CONTROL))
                speed = 150.0f;
            else
                speed = 40.0f;

            if (TD::Input::isKeyDown(KEY_W))
                _moveAtX = (-speed * TD::getFrameTimeSeconds());

            else if (TD::Input::isKeyDown(KEY_S))
                _moveAtX = (speed * TD::getFrameTimeSeconds());
            else
                _moveAtX = 0;

            if (TD::Input::isKeyDown(KEY_A))
                _moveAtZ = (speed * TD::getFrameTimeSeconds());
            else if (TD::Input::isKeyDown(KEY_D))
                _moveAtZ = (-speed * TD::getFrameTimeSeconds());
            else
                _moveAtZ = 0;

            if (TD::Input::isKeyDown(KEY_SPACE))
                _moveAtY = (speed * TD::getFrameTimeSeconds());
            else
                _moveAtY = 0;

            if (TD::Input::isKeyDown(KEY_LEFT_SHIFT))
                _moveAtY = (-speed * TD::getFrameTimeSeconds());

            // TODO: remove this shit
            double dx = (-(_moveAtX * sin(glm::radians(_yaw))) + -(_moveAtZ * cos(glm::radians(_yaw))));
            double dy = (_moveAtX * (sin(glm::radians(_roll)))) + _moveAtY;
            double dz = (((_moveAtX * cos(glm::radians(_yaw))) + -(_moveAtZ * sin(glm::radians(_yaw)))));

            _cameraPos.x += (float)dx;
            _cameraPos.y += (float)dy;
            _cameraPos.z += (float)dz;
        }

        glm::mat4 viewMatrix = glm::mat4(1.0);
        viewMatrix = glm::rotate(viewMatrix, glm::radians((float)_pitch), glm::vec3(1, 0, 0));
        viewMatrix = glm::rotate(viewMatrix, glm::radians((float)_yaw), glm::vec3(0, 1, 0));
        viewMatrix = glm::translate(viewMatrix, -_cameraPos);

        TD::updateViewMatrixUBO(viewMatrix);
        calculateFrustum();
    }

    void editorCamera::update() {
        if (TD::Input::isMouseGrabbed()) {
            _pitch += TD::Input::getMouseDY() * (turnSpeedY) / 100;
            _yaw += TD::Input::getMouseDX() * (turnSpeedX) / 100;

            const float speedd = 30.0f;

            if (TD::Input::isKeyDown(KEY_LEFT))
                _yaw += -speedd * turnSpeedX * TD::getFrameTimeSeconds();
            if (TD::Input::isKeyDown(KEY_RIGHT))
                _yaw += speedd * turnSpeedX * TD::getFrameTimeSeconds();
            if (TD::Input::isKeyDown(KEY_UP))
                _pitch += -speedd * turnSpeedY * TD::getFrameTimeSeconds();
            if (TD::Input::isKeyDown(KEY_DOWN))
                _pitch += speedd * turnSpeedY * TD::getFrameTimeSeconds();

            if (_pitch > 90)
                _pitch = 90;
            if (_pitch < -90)
                _pitch = -90;
            if (_yaw < -360)
                _yaw = 0;
            if (_yaw > 360)
                _yaw = 0;

            if (TD::Input::isKeyDown(GLFW_KEY_LEFT_ALT))
                speed = 5.0f;
            else if (TD::Input::isKeyDown(KEY_L_CONTROL))
                speed = 150.0f;
            else
                speed = 40.0f;

            if (TD::Input::isKeyDown(KEY_W))
                _moveAtX = (float) (-speed * TD::getFrameTimeSeconds());

            else if (TD::Input::isKeyDown(KEY_S))
                _moveAtX = (float) (speed * TD::getFrameTimeSeconds());
            else
                _moveAtX = 0;

            if (TD::Input::isKeyDown(KEY_A))
                _moveAtZ = (float) (speed * TD::getFrameTimeSeconds());
            else if (TD::Input::isKeyDown(KEY_D))
                _moveAtZ = (float) (-speed * TD::getFrameTimeSeconds());
            else
                _moveAtZ = 0;

            if (TD::Input::isKeyDown(KEY_SPACE))
                _moveAtY = (float) (speed * TD::getFrameTimeSeconds());
            else
                _moveAtY = 0;

            if (TD::Input::isKeyDown(KEY_LEFT_SHIFT))
                _moveAtY = (float) (-speed * TD::getFrameTimeSeconds());

            // TODO: remove this shit
            double dx = (-(_moveAtX * sin(glm::radians(_yaw))) + -(_moveAtZ * cos(glm::radians(_yaw))));
            double dy = (_moveAtX * (sin(glm::radians(_roll)))) + _moveAtY;
            double dz = (((_moveAtX * cos(glm::radians(_yaw))) + -(_moveAtZ * sin(glm::radians(_yaw)))));

            _cameraPos.x += (float)dx;
            _cameraPos.y += (float)dy;
            _cameraPos.z += (float)dz;

        }
        if (TD::Input::getMouseScrollYLastFrame() != 0){
            double movingScroll = TD::Input::getMouseScrollYLastFrame() * speed * TD::getFrameTimeSeconds();
            double dx = -movingScroll * sin(glm::radians(_yaw));
            double dy = movingScroll * sin(glm::radians(_roll));
            double dz = movingScroll * cos(glm::radians(_yaw));

            _cameraPos.x += (float)dx;
            _cameraPos.y += (float)dy;
            _cameraPos.z += (float)dz;
        }

        glm::mat4 viewMatrix = glm::mat4(1.0);
        viewMatrix = glm::rotate(viewMatrix, glm::radians((float)_pitch), glm::vec3(1, 0, 0));
        viewMatrix = glm::rotate(viewMatrix, glm::radians((float)_yaw), glm::vec3(0, 1, 0));
        viewMatrix = glm::translate(viewMatrix, -_cameraPos);

        TD::updateViewMatrixUBO(viewMatrix);
        calculateFrustum();
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

    extern glm::mat4 projectionMatrix;
    extern glm::mat4 projectionViewMatrix;
    extern glm::mat4 viewMatrix;

    void camera::normalizePlane(double (*frustum)[4], int side) {
        double nm = sqrt(frustum[side][0] * frustum[side][0] + frustum[side][1] * frustum[side][1] + frustum[side][2] * frustum[side][2]);

        frustum[side][0] /= nm;
        frustum[side][1] /= nm;
        frustum[side][2] /= nm;
        frustum[side][3] /= nm;
    }

    void camera::calculateFrustum() {
        clippingPlanes[0] = (getInMatrix(viewMatrix,0) * getInMatrix(projectionMatrix,0) + getInMatrix(viewMatrix,1) * getInMatrix(projectionMatrix,4) + getInMatrix(viewMatrix,2) * getInMatrix(projectionMatrix,8) + getInMatrix(viewMatrix,3) * getInMatrix(projectionMatrix,12));
        clippingPlanes[1] = (getInMatrix(viewMatrix,0) * getInMatrix(projectionMatrix,1) + getInMatrix(viewMatrix,1) * getInMatrix(projectionMatrix,5) + getInMatrix(viewMatrix,2) * getInMatrix(projectionMatrix,9) + getInMatrix(viewMatrix,3) * getInMatrix(projectionMatrix,13));
        clippingPlanes[2] = (getInMatrix(viewMatrix,0) * getInMatrix(projectionMatrix,2) + getInMatrix(viewMatrix,1) * getInMatrix(projectionMatrix,6) + getInMatrix(viewMatrix,2) * getInMatrix(projectionMatrix,10) + getInMatrix(viewMatrix,3) * getInMatrix(projectionMatrix,14));
        clippingPlanes[3] = (getInMatrix(viewMatrix,0) * getInMatrix(projectionMatrix,3) + getInMatrix(viewMatrix,1) * getInMatrix(projectionMatrix,7) + getInMatrix(viewMatrix,2) * getInMatrix(projectionMatrix,11) + getInMatrix(viewMatrix,3) * getInMatrix(projectionMatrix,15));

        clippingPlanes[4] = (getInMatrix(viewMatrix,4) * getInMatrix(projectionMatrix,0) + getInMatrix(viewMatrix,5) * getInMatrix(projectionMatrix,4) + getInMatrix(viewMatrix,6) * getInMatrix(projectionMatrix,8) + getInMatrix(viewMatrix,7) * getInMatrix(projectionMatrix,12));
        clippingPlanes[5] = (getInMatrix(viewMatrix,4) * getInMatrix(projectionMatrix,1) + getInMatrix(viewMatrix,5) * getInMatrix(projectionMatrix,5) + getInMatrix(viewMatrix,6) * getInMatrix(projectionMatrix,9) + getInMatrix(viewMatrix,7) * getInMatrix(projectionMatrix,13));
        clippingPlanes[6] = (getInMatrix(viewMatrix,4) * getInMatrix(projectionMatrix,2) + getInMatrix(viewMatrix,5) * getInMatrix(projectionMatrix,6) + getInMatrix(viewMatrix,6) * getInMatrix(projectionMatrix,10) + getInMatrix(viewMatrix,7) * getInMatrix(projectionMatrix,14));
        clippingPlanes[7] = (getInMatrix(viewMatrix,4) * getInMatrix(projectionMatrix,3) + getInMatrix(viewMatrix,5) * getInMatrix(projectionMatrix,7) + getInMatrix(viewMatrix,6) * getInMatrix(projectionMatrix,11) + getInMatrix(viewMatrix,7) * getInMatrix(projectionMatrix,15));

        clippingPlanes[8] = (getInMatrix(viewMatrix,8) * getInMatrix(projectionMatrix,0) + getInMatrix(viewMatrix,9) * getInMatrix(projectionMatrix,4) + getInMatrix(viewMatrix,10) * getInMatrix(projectionMatrix,8) + getInMatrix(viewMatrix,11) * getInMatrix(projectionMatrix,12));
        clippingPlanes[9] = (getInMatrix(viewMatrix,8) * getInMatrix(projectionMatrix,1) + getInMatrix(viewMatrix,9) * getInMatrix(projectionMatrix,5) + getInMatrix(viewMatrix,10) * getInMatrix(projectionMatrix,9) + getInMatrix(viewMatrix,11) * getInMatrix(projectionMatrix,13));
        clippingPlanes[10] = (getInMatrix(viewMatrix,8) * getInMatrix(projectionMatrix,2) + getInMatrix(viewMatrix,9) * getInMatrix(projectionMatrix,6) + getInMatrix(viewMatrix,10) * getInMatrix(projectionMatrix,10) + getInMatrix(viewMatrix,11) * getInMatrix(projectionMatrix,14));
        clippingPlanes[11] = (getInMatrix(viewMatrix,8) * getInMatrix(projectionMatrix,3) + getInMatrix(viewMatrix,9) * getInMatrix(projectionMatrix,7) + getInMatrix(viewMatrix,10) * getInMatrix(projectionMatrix,11) + getInMatrix(viewMatrix,11) * getInMatrix(projectionMatrix,15));

        clippingPlanes[12] = (getInMatrix(viewMatrix,12) * getInMatrix(projectionMatrix,0) + getInMatrix(viewMatrix,13) * getInMatrix(projectionMatrix,4) + getInMatrix(viewMatrix,14) * getInMatrix(projectionMatrix,8) + getInMatrix(viewMatrix,15) * getInMatrix(projectionMatrix,12));
        clippingPlanes[13] = (getInMatrix(viewMatrix,12) * getInMatrix(projectionMatrix,1) + getInMatrix(viewMatrix,13) * getInMatrix(projectionMatrix,5) + getInMatrix(viewMatrix,14) * getInMatrix(projectionMatrix,9) + getInMatrix(viewMatrix,15) * getInMatrix(projectionMatrix,13));
        clippingPlanes[14] = (getInMatrix(viewMatrix,12) * getInMatrix(projectionMatrix,2) + getInMatrix(viewMatrix,13) * getInMatrix(projectionMatrix,6) + getInMatrix(viewMatrix,14) * getInMatrix(projectionMatrix,10) + getInMatrix(viewMatrix,15) * getInMatrix(projectionMatrix,14));
        clippingPlanes[15] = (getInMatrix(viewMatrix,12) * getInMatrix(projectionMatrix,3) + getInMatrix(viewMatrix,13) * getInMatrix(projectionMatrix,7) + getInMatrix(viewMatrix,14) * getInMatrix(projectionMatrix,11) + getInMatrix(viewMatrix,15) * getInMatrix(projectionMatrix,15));


        m_Frustum[0][0] = (clippingPlanes[3] - clippingPlanes[0]);
        m_Frustum[0][1] = (clippingPlanes[7] - clippingPlanes[4]);
        m_Frustum[0][2] = (clippingPlanes[11] - clippingPlanes[8]);
        m_Frustum[0][3] = (clippingPlanes[15] - clippingPlanes[12]);
        normalizePlane(m_Frustum, 0);

        m_Frustum[1][0] = (clippingPlanes[3] + clippingPlanes[0]);
        m_Frustum[1][1] = (clippingPlanes[7] + clippingPlanes[4]);
        m_Frustum[1][2] = (clippingPlanes[11] + clippingPlanes[8]);
        m_Frustum[1][3] = (clippingPlanes[15] + clippingPlanes[12]);

        normalizePlane(m_Frustum, 1);

        m_Frustum[2][0] = (clippingPlanes[3] + clippingPlanes[1]);
        m_Frustum[2][1] = (clippingPlanes[7] + clippingPlanes[5]);
        m_Frustum[2][2] = (clippingPlanes[11] + clippingPlanes[9]);
        m_Frustum[2][3] = (clippingPlanes[15] + clippingPlanes[13]);
        normalizePlane(m_Frustum, 2);


        m_Frustum[3][0] = (clippingPlanes[3] - clippingPlanes[1]);
        m_Frustum[3][1] = (clippingPlanes[7] - clippingPlanes[5]);
        m_Frustum[3][2] = (clippingPlanes[11] - clippingPlanes[9]);
        m_Frustum[3][3] = (clippingPlanes[15] - clippingPlanes[13]);
        normalizePlane(m_Frustum, 3);


        m_Frustum[4][0] = (clippingPlanes[3] - clippingPlanes[2]);
        m_Frustum[4][1] = (clippingPlanes[7] - clippingPlanes[6]);
        m_Frustum[4][2] = (clippingPlanes[11] - clippingPlanes[10]);
        m_Frustum[4][3] = (clippingPlanes[15] - clippingPlanes[14]);
        normalizePlane(m_Frustum, 4);


        m_Frustum[5][0] = (clippingPlanes[3] + clippingPlanes[2]);
        m_Frustum[5][1] = (clippingPlanes[7] + clippingPlanes[6]);
        m_Frustum[5][2] = (clippingPlanes[11] + clippingPlanes[10]);
        m_Frustum[5][3] = (clippingPlanes[15] + clippingPlanes[14]);
        normalizePlane(m_Frustum, 5);
    }

    bool camera::pointInFrustum(double x, double y, double z) {
        for (int i = 0; i < 6; i++) {
            if (m_Frustum[i][0] * x + m_Frustum[i][1] * y + m_Frustum[i][2] * z + m_Frustum[i][3] <= 0.0F) {
                return false;
            }
        }

        return true;
    }

    bool camera::sphereInFrustum(double x, double y, double z, double radius) {
        for (int i = 0; i < 6; i++) {
            if (m_Frustum[i][0] * x + m_Frustum[i][1] * y + m_Frustum[i][2] * z + m_Frustum[i][3] <= -radius) {
                return false;
            }
        }

        return true;
    }

    bool camera::cubeInFrustum(double x1, double y1, double z1, double x2, double y2, double z2) {
        for (int i = 0; i < 6; i++) {
            if ((m_Frustum[i][0] * x1 + m_Frustum[i][1] * y1 + m_Frustum[i][2] * z1 + m_Frustum[i][3] <= 0.0F)
                && (m_Frustum[i][0] * x2 + m_Frustum[i][1] * y1 + m_Frustum[i][2] * z1 + m_Frustum[i][3] <= 0.0F)
                && (m_Frustum[i][0] * x1 + m_Frustum[i][1] * y2 + m_Frustum[i][2] * z1 + m_Frustum[i][3] <= 0.0F)
                && (m_Frustum[i][0] * x2 + m_Frustum[i][1] * y2 + m_Frustum[i][2] * z1 + m_Frustum[i][3] <= 0.0F)
                && (m_Frustum[i][0] * x1 + m_Frustum[i][1] * y1 + m_Frustum[i][2] * z2 + m_Frustum[i][3] <= 0.0F)
                && (m_Frustum[i][0] * x2 + m_Frustum[i][1] * y1 + m_Frustum[i][2] * z2 + m_Frustum[i][3] <= 0.0F)
                && (m_Frustum[i][0] * x1 + m_Frustum[i][1] * y2 + m_Frustum[i][2] * z2 + m_Frustum[i][3] <= 0.0F)
                && (m_Frustum[i][0] * x2 + m_Frustum[i][1] * y2 + m_Frustum[i][2] * z2 + m_Frustum[i][3] <= 0.0F)) {
                return false;
            }
        }
        return true;
    }
}

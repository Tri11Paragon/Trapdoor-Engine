//
// Created by brett on 23/07/22.
//

#ifndef ENGINE_CAMERA_H
#define ENGINE_CAMERA_H

#include "../glm.h"

namespace TD {
    class camera {
    protected:
        glm::vec3 _cameraPos = glm::vec3(0.0f);
        float _moveAtX = 0, _moveAtY = 0, _moveAtZ = 0;
        float _yaw = 0, _pitch = 0, _roll = 0;
    public:
        virtual void update() {}
        glm::vec3 getPosition();
        glm::vec3 getRotation();
        float getYaw();
        float getPitch();
        float getRoll();
    };

    class firstPersonCamera : public camera {
    protected:
        float speed = 0.0f;
        constexpr static const float turnSpeedY = 5.0f;
        constexpr static const float turnSpeedX = 4.5f;
    public:
        void update();
    };

}

#endif //ENGINE_CAMERA_H

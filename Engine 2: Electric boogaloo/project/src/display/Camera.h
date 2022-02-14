//
// Created by laptop on 2022-02-14.
//

#ifndef TRAPDOOR_CAMERA_H
#define TRAPDOOR_CAMERA_H

#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>
#include <glm/gtc/type_ptr.hpp>
#include "DisplayManager.h"

class Camera {
private:
    glm::mat4x4 projectionMatrix;
    glm::mat4x4 viewMatrix;
    glm::vec3 position;
    float yaw, pitch, roll;
public:
    Camera(){

    }
    ~Camera(){

    }
    glm::mat4x4 getPerspective();
    glm::mat4x4 getView();
    void createPerspective();

    void update();
};

#endif //TRAPDOOR_CAMERA_H

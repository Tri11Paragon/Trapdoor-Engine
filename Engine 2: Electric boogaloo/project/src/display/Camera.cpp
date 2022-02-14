//
// Created by laptop on 2022-02-14.
//

#include "Camera.h"

void Camera::update(){

}

void Camera::createPerspective(){
    projectionMatrix = glm::perspective(70.0f, (float)currentWidth/(float)currentHeight, 0.1f, 1000.0f);
}

glm::mat4x4 Camera::getPerspective(){
    return projectionMatrix;
}
glm::mat4x4 Camera::getView(){
    return viewMatrix;
}
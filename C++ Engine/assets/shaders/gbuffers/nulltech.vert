#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoord;
layout (location = 2) in vec3 aNormal;
layout (location = 3) in mat4 transform;

layout (location = 7) in vec3 position;
layout (location = 8) in vec3 color;
layout (location = 9) in float linear;
layout (location = 10) in float quadratic;
layout (location = 11) in float radius;

layout (std140) uniform Matrices {
    mat4 projectionMatrix;
    mat4 viewMatrix;
    mat4 orthoMatrix;
    mat4 projectViewMatrix;
};

void main() {
    vec4 worldPos = transform * vec4(aPos, 1.0);

    gl_Position = projectViewMatrix * worldPos;
}
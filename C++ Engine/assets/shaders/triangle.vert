#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoord;
layout (location = 2) in vec3 aNormal;

out vec2 TexCoord;

uniform mat4 transform;

layout (std140) uniform Matrices {
    mat4 projectionMatrix;
    mat4 viewMatrix;
    mat4 orthoMatrix;
    mat4 projectViewMatrix;
};

void main() {
    gl_Position = projectViewMatrix * transform * vec4(aPos, 1.0);
    TexCoord = aTexCoord;
}
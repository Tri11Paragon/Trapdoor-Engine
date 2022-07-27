#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoords;

out vec2 TexCoords;

layout (std140) uniform Matrices {
    mat4 projectionMatrix;
    mat4 viewMatrix;
    mat4 orthoMatrix;
    mat4 projectViewMatrix;
};

uniform mat4 transform;

void main() {
    TexCoords = aTexCoords;
    gl_Position = orthoMatrix * transform * vec4(aPos, 1.0);
}
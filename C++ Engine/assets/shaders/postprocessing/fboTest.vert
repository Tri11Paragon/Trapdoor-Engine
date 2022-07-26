#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoord;

uniform mat4 transform;

out vec2 TexCoord;

layout (std140) uniform Matrices {
    mat4 projectionMatrix;
    mat4 viewMatrix;
    mat4 orthoMatrix;
    mat4 projectViewMatrix;
};

void main() {
    gl_Position = orthoMatrix * transform * vec4(aPos, 1.0);
    TexCoord = aTexCoord;
}
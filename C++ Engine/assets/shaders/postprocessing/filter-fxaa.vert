#version 430 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoord;

uniform mat4 transform;
uniform sampler2D screenTexture;

out vec2 TexCoord;
// TODO: might want to pass this as a uniform?
out vec2 invserseTexSize;

layout (std140) uniform Matrices {
    mat4 projectionMatrix;
    mat4 viewMatrix;
    mat4 orthoMatrix;
    mat4 projectViewMatrix;
};

void main() {
    gl_Position = orthoMatrix * transform * vec4(aPos, 1.0);
    TexCoord = aTexCoord;
    invserseTexSize = 1.0/textureSize(screenTexture, 0);
}
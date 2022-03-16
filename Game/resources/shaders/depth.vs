#version 330 core

in vec3 position;
in vec2 textureCoordinates;
in vec3 normal;
in vec3 tangent;
in vec3 bitangent;

out vec2 textureCoord;

layout (std140) uniform Matricies {
    mat4 projectionMatrix;
    mat4 viewMatrix;
    mat4 projectionViewMatrix;
    mat4 othroMatrix;
   	mat4 shadowMatrix;
};

uniform mat4 transformMatrix;

void main() {
    gl_Position = projectionViewMatrix * transformMatrix * vec4(position, 1.0f);
    textureCoord = textureCoordinates;
}
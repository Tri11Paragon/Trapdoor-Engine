#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 textureCoordinates;
layout (location=2) in vec3 normal;

out vec2 textureCoord;

uniform mat4 transformMatrix;
uniform mat4 perspectiveMatrix;


void main() {
    gl_Position = perspectiveMatrix * transformMatrix * vec4(position, 1.0f);
    textureCoord = textureCoordinates;
}
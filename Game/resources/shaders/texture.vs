#version 140

in vec2 position;

out vec2 textureCoords;

uniform mat4 transformationMatrix;
layout (std140) uniform Matricies {
    mat4 projectionMatrix;
    mat4 viewMatrix;
    mat4 projectionViewMatrix;
    mat4 othroMatrix;
};

void main(void){
	gl_Position = othroMatrix * transformationMatrix * vec4(position, 0.0, 1.0);
	textureCoords = vec2(position.x, 1.0f-position.y);
}
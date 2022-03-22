#version 140

in vec2 position;
in mat4 modelMatrix;
in vec3 textureData;

out vec2 textureCoords;
out vec3 textureInfo;
out vec4 worldPos;
out vec3 normal;

layout (std140) uniform Matricies {
    mat4 projectionMatrix;
    mat4 viewMatrix;
    mat4 projectionViewMatrix;
    mat4 othroMatrix;
   	mat4 shadowMatrix;
};

uniform vec3 viewPos;

void main(void){
	textureCoords = position + vec2(0.5, 0.5);
	textureCoords.y = 1.0 - textureCoords.y;
	//textureCoords = vec2(position.x, 1.0f-position.y);
	textureInfo = textureData;
	worldPos = modelMatrix * vec4(position, 0.0, 1.0);
	gl_Position = projectionMatrix * worldPos;
	normal = vec3(0.0f, 0.0f, -1.0f);
}
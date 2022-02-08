#version 330

in vec3 position;
in vec2 textureCoordinates;
in vec3 normal;

out vec2 textureCoords;
out vec3 normalo;
out vec3 fragpos;

layout (std140) uniform Matricies {
    mat4 projectionMatrix;
    mat4 viewMatrix;
    mat4 projectionViewMatrix;
};

uniform mat4 translationMatrix;

void main(void){
	vec4 worldPosition = translationMatrix * vec4(position,1.0);
	vec4 positionRelativeToCam = projectionViewMatrix * worldPosition;

    fragpos = worldPosition.xyz;
    // inverse?
    // normal matrix on cpu?
	normalo = normal * transpose((mat3(translationMatrix)));

    gl_Position = positionRelativeToCam;
	textureCoords = textureCoordinates;
}
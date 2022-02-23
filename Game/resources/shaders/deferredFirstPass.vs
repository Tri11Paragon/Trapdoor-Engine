#version 330

in vec3 position;
in vec2 textureCoordinates;
in vec3 normal;

out vec2 textureCoords;
out vec3 normalo;
out vec3 fragpos;
out vec4 shadowCoords;

layout (std140) uniform Matricies {
    mat4 projectionMatrix;
    mat4 viewMatrix;
    mat4 projectionViewMatrix;
    mat4 othroMatrix;
   	mat4 shadowMatrix;
};

uniform mat4 translationMatrix;

void main(void){
	vec4 worldPosition = translationMatrix * vec4(position,1.0);
	vec4 positionRelativeToCam = projectionViewMatrix * worldPosition;
	shadowCoords = shadowMatrix * worldPosition;

    fragpos = worldPosition.xyz;
	normalo = normal * transpose(mat3(translationMatrix));

    gl_Position = positionRelativeToCam;
	textureCoords = textureCoordinates;
}
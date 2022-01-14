#version 330

in vec3 position;
in vec2 textureCoordinates;
in vec3 normal;

out vec2 textureCoords;
out vec3 normalo;
out vec3 fragpos;

uniform mat4 translationMatrix;
uniform mat4 projectViewMatrix;

void main(void){
	vec4 worldPosition = translationMatrix * vec4(position,1.0);
	vec4 positionRelativeToCam = projectViewMatrix * worldPosition;

    fragpos = worldPosition.xyz;
	normalo = normal * transpose(inverse(mat3(translationMatrix)));

    gl_Position = positionRelativeToCam;
	textureCoords = textureCoordinates;
}
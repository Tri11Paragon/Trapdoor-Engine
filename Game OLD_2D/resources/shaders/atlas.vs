#version 330 core
layout (location = 0) in vec2 aPos;
layout (location = 1) in vec2 aTexCoord;
layout (location = 2) in mat4 modelViewMatrix;
layout (location = 6) in float textureID;

out vec2 TexCoord;
out float texturePos;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 translationMatrix;

void main()
{
    //gl_Position = ((vec4(aPos, 1.0) * translationMatrix) * viewMatrix) * projectionMatrix;
    //vec4 worldPosition = translationMatrix * vec4(aPos,1.0);
	//vec4 positionRelativeToCam = viewMatrix * worldPosition;
	//gl_Position = projectionMatrix * positionRelativeToCam;
    gl_Position = projectionMatrix * viewMatrix * modelViewMatrix * vec4(aPos, 0.0, 1.0);
    TexCoord = aTexCoord;
    texturePos = textureID;
}
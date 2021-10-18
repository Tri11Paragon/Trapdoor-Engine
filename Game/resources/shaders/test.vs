 #version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoord;

out vec2 TexCoord;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 translationMatrix;

void main()
{
    //gl_Position = ((vec4(aPos, 1.0) * translationMatrix) * viewMatrix) * projectionMatrix;
    vec4 worldPosition = translationMatrix * vec4(aPos,1.0);
	vec4 positionRelativeToCam = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCam;
    TexCoord = aTexCoord;
}
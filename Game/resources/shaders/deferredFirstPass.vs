#version 330 core

in vec3 position;
in vec2 textureCoordinates;
in vec3 normal;
in vec3 tangent;
in vec3 bitangent;

out vec2 textureCoords;
out vec3 normalo;
out vec3 fragpos;
out vec3 fragPosWorldSpace;
out mat3 tbnMat;
out vec3 TangentViewPos;
out vec3 TangentFragPos;

uniform vec3 viewPos;

layout (std140) uniform Matricies {
    mat4 projectionMatrix;
    mat4 viewMatrix;
    mat4 projectionViewMatrix;
    mat4 othroMatrix;
   	mat4 shadowMatrix;
};

uniform mat4 translationMatrix;

void main(void){
	mat4 viewTrans = viewMatrix * translationMatrix;
	vec4 viewSpacePos = viewTrans * vec4(position,1.0);

	vec4 worldPosition = translationMatrix * vec4(position,1.0);
	vec4 positionRelativeToCam = projectionViewMatrix * worldPosition;

    fragPosWorldSpace = worldPosition.xyz;
	//mat3 viewTransTrans = transpose(mat3(viewTrans));
	normalo = normal;

	fragpos = viewSpacePos.xyz;

	mat3 tangMat = mat3(translationMatrix);

	vec3 T = normalize(tangMat * tangent);
	vec3 B = normalize(tangMat * bitangent);
   	vec3 N = normalize(tangMat * normal);
	//T = normalize(T - dot(T, N) * N);
	//vec3 B = cross(T, N);
   	tbnMat = mat3(T, B, N);

	//mat3 TBN = transpose(tbnMat);

    //TangentViewPos  = TBN * viewPos;
    //TangentFragPos  = TBN * fragPosWorldSpace;

    gl_Position = positionRelativeToCam;
	textureCoords = textureCoordinates;
}
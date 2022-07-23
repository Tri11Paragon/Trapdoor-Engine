#version 330 core

in vec3 position;
in vec2 textureCoordinates;
in vec3 normal;
in vec3 tangent;
//in mat4 transformMatrix;

out vec2 textureCoords;
out vec3 normalo;
out vec3 fragpos;
out vec3 fragPosWorldSpace;
out mat3 tbnMat;
out vec3 TangentViewPos;
out vec3 TangentFragPos;

layout (std140) uniform Matricies {
    mat4 projectionMatrix;
    mat4 viewMatrix;
    mat4 projectionViewMatrix;
    mat4 othroMatrix;
   	mat4 shadowMatrix;
};

uniform mat4 transformMatrix;
uniform vec3 viewPos;

void main(void){
	mat4 viewTrans = viewMatrix * transformMatrix;
	vec4 viewSpacePos = viewTrans * vec4(position,1.0);

	vec4 worldPosition = transformMatrix * vec4(position,1.0);
	vec4 positionRelativeToCam = projectionViewMatrix * worldPosition;

    fragPosWorldSpace = worldPosition.xyz;
	//mat3 viewTransTrans = transpose(mat3(viewTrans));
	normalo = normal;

	fragpos = viewSpacePos.xyz;

	//mat3 tangMat = mat3(transformMatrix);

	vec3 T = normalize(vec3(transformMatrix * vec4(tangent, 0.0)));
	//vec3 B = normalize(vec3(transformMatrix * vec4(bitangent, 0.0)));
   	vec3 N = normalize(vec3(transformMatrix * vec4(normal, 0.0)));
	T = normalize(T - dot(T, N) * N);
	vec3 B = cross(T, N);
	// TODO: option 2 normal mapping
   	tbnMat = mat3(T, B, N);

	mat3 TBN = transpose(tbnMat);

    TangentViewPos  = TBN * viewPos;
    TangentFragPos  = TBN * fragPosWorldSpace;

    gl_Position = positionRelativeToCam;
	textureCoords = textureCoordinates;
}
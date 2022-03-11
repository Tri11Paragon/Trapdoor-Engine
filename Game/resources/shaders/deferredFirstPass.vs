#version 330

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
	fragPosWorldSpace = worldPosition.xyz;
	vec4 positionRelativeToCam = projectionViewMatrix * worldPosition;

    fragpos = fragPosWorldSpace.xyz;
	//mat3 viewTransTrans = transpose(mat3(viewTrans));
	normalo = normal;

	mat3 viewTang = mat3(translationMatrix);

	vec3 T = normalize(viewTang * tangent);
	vec3 B = normalize(viewTang * bitangent);
   	//vec3 B = normalize(vec3(viewMatrix * vec4(bitangent, 0.0)));
   	vec3 N = normalize(viewTang * normal);
	//T = normalize(T - dot(T, N) * N);
	//vec3 B = cross(T, N);
   	tbnMat = mat3(T, B, N);

    gl_Position = positionRelativeToCam;
	textureCoords = textureCoordinates;
}
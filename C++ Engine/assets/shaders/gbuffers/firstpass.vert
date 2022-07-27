#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoord;
layout (location = 2) in vec3 aNormal;

out vec2 TexCoords;
out vec3 FragPos;
out vec3 Normal;

uniform mat4 transform;

layout (std140) uniform Matrices {
    mat4 projectionMatrix;
    mat4 viewMatrix;
    mat4 orthoMatrix;
    mat4 projectViewMatrix;
};

void main() {
    vec4 worldPos = transform * vec4(aPos, 1.0);
    FragPos = worldPos.xyz;

    TexCoords = aTexCoord;

    mat3 normalMatrix = transpose(inverse(mat3(transform)));
    Normal = normalMatrix * aNormal;

    gl_Position = projectViewMatrix * worldPos;
}
#version 330 core

in vec2 textureCoords;

in vec3 normalo;
in vec3 fragpos;

layout (location = 0) out vec4 gPosition;
layout (location = 1) out vec4 gNormal;
layout (location = 2) out vec4 gAlbedoSpec;
layout (location = 3) out vec4 gRenderState;

uniform sampler2D diffuseTexture;
uniform sampler2D normalMap;

void main(){
    gPosition = vec4(fragpos, 1.0f);
    gNormal = vec4(normalize(normalo), 1.0f);
    gAlbedoSpec = texture(diffuseTexture, textureCoords);
    if (gAlbedoSpec.a < 0.1f)
        discard;
    gAlbedoSpec.a = 0.0f;
    gRenderState = vec4(1.0f, 0.0f, 0.0f, 0.0f);
}
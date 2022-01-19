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
uniform sampler2D displacementMap;
uniform sampler2D aoMap;
uniform sampler2D specMap;

uniform vec3 viewPos;

uniform float specAmount;

void main(){
    vec3 normali = normalize(normalo);

    vec3 viewDir  = normalize(viewPos - fragpos);

    gPosition = vec4(fragpos, 1.0f);
    gNormal = vec4(normali, 1.0f);
    gAlbedoSpec = texture(diffuseTexture, textureCoords);


    if (gAlbedoSpec.a < 0.1f)
        discard;
    
    vec4 specMapTexture = texture(specMap, textureCoords);
    if (specMapTexture.a > 0)
        gAlbedoSpec.a = specMapTexture.r;
    else
        gAlbedoSpec.a = specAmount;
    

    vec4 displacementMapTexture = texture(displacementMap, textureCoords);
    float displacement = -1.0f;
    if (displacementMapTexture.a > 0)
        displacement = displacementMapTexture.r;

    gRenderState = vec4(1.0f, displacement, 0.0f, 0.0f);
}
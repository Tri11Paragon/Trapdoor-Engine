#version 330 core

in vec2 textureCoords;

in vec3 normalo;
in vec3 fragpos;
in vec4 shadowCoords;

layout (location = 0) out vec4 gPosition;
layout (location = 1) out vec4 gNormal;
layout (location = 2) out vec4 gAlbedoSpec;
layout (location = 3) out vec4 gRenderState;

uniform sampler2D diffuseTexture;
uniform sampler2D normalMap;
uniform sampler2D displacementMap;
uniform sampler2D aoMap;
uniform sampler2D specMap;
uniform sampler2D shadowMap;

uniform vec3 viewPos;

uniform float specAmount;

const int pcfCount = 2;
const float totalTexels = (pcfCount * 2.0 + 1.0) * (pcfCount * 2.0 + 1.0);
const float bias = 0.05f;

void main(){
	
	float texelSize = 1.0 / textureSize(shadowMap, 0).x;
	float total = 0.0;
	for (int x=-pcfCount; x<=pcfCount; x++){
		for (int y=-pcfCount; y<=pcfCount; y++) {
			float objectNearestLength = texture(shadowMap, shadowCoords.xy + vec2(x,y) * texelSize).r;
			if (shadowCoords.z - bias > objectNearestLength){
				total++;
			}
		}
	}
	total /= totalTexels;
	float lightFactor = 1.0 - (0.6 *(total * shadowCoords.w));

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

    gRenderState = vec4(1.0f, displacement, lightFactor, 0.0f);
}
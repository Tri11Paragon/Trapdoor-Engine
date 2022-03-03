#version 420 core

in vec2 textureCoords;

in vec3 normalo;
in vec3 fragpos;
in vec3 fragPosWorldSpace;
in vec4 shadowCoords;


layout (location = 0) out vec4 gPosition;
layout (location = 1) out vec4 gNormal;
layout (location = 2) out vec4 gAlbedoSpec;
layout (location = 3) out vec4 gRenderState;

layout (std140, binding = 3) uniform LightSpaceMatrices {
    mat4 lightSpaceMatrices[5];
};
uniform float cascadePlaneDistances[5];
uniform int cascadeCount;
uniform vec3 lightDir;
uniform float farPlane;

uniform sampler2D diffuseTexture;
uniform sampler2D normalMap;
uniform sampler2D displacementMap;
uniform sampler2D aoMap;
uniform sampler2D specMap;
uniform sampler2DArray shadowMap;

uniform vec3 viewPos;

uniform float specAmount;

const int pcfCount = 2;
const float totalTexels = (pcfCount * 2.0 + 1.0) * (pcfCount * 2.0 + 1.0);
const float bias = 0.05f;

float shadowCalc(vec3 normal){
    float depthValue = abs(fragpos.z);

    int layer = -1;
    for (int i = 0; i < cascadeCount; ++i) {
        if (depthValue < cascadePlaneDistances[i]) {
            layer = i;
            break;
        }
    }
    if (layer == -1) {
        layer = cascadeCount;
    }

    vec4 fragPosLightSpace = lightSpaceMatrices[layer] * vec4(fragPosWorldSpace, 1.0);
    // perform perspective divide
    vec3 projCoords = fragPosLightSpace.xyz / fragPosLightSpace.w;
    // transform to [0,1] range
    projCoords = projCoords * 0.5 + 0.5;

    // get depth of current fragment from light's perspective
    float currentDepth = projCoords.z;
    // keep the shadow at 0.0 when outside the far_plane region of the light's frustum.
    if (currentDepth > 1.0) {
        return 0.0;
    }

    // calculate bias (based on depth map resolution and slope)
    float bias = max(0.05 * (1.0 - dot(normal, lightDir)), 0.005);
    const float biasModifier = 0.5f;
    if (layer == cascadeCount) {
        bias *= 1 / (farPlane * biasModifier);
    } else {
        bias *= 1 / (cascadePlaneDistances[layer] * biasModifier);
    }

    // PCF
    float shadow = 0.0;
    vec2 texelSize = 1.0 / vec2(textureSize(shadowMap, 0));
    for(int x = -1; x <= 1; ++x) {
        for(int y = -1; y <= 1; ++y) {
            float pcfDepth = texture(shadowMap, vec3(projCoords.xy + vec2(x, y) * texelSize, layer)).r;
            shadow += (currentDepth - bias) > pcfDepth ? 1.0 : 0.0;        
        }    
    }
    shadow /= 9.0;
        
    return shadow;
}

void main(){
	
    vec3 normali = normalize(normalo);

	float shadower = shadowCalc(normali);
	float lightFactor = 1.0 - (0.6 * shadower);

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
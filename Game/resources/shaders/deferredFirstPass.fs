#version 420 core

in vec2 textureCoords;

in vec3 normalo;
in vec3 fragpos;
in vec3 fragPosWorldSpace;
in vec4 shadowCoords;
in mat3 tbnMat;
in vec3 TangentViewPos;
in vec3 TangentFragPos;


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
uniform sampler2D specMap;
uniform sampler2DArray shadowMap;

uniform vec3 viewPos;

uniform float specAmount;
uniform vec3 diffuse;

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

const float heightScale = 0.05f;

// vec2 ParallaxMapping(vec2 texCoords, vec3 viewDir) { 
//     float height =  texture(displacementMap, texCoords).r;    
//     vec2 p = viewDir.xy / viewDir.z * (height * height_scale);
//     return texCoords - p;    
// } 

vec2 ParallaxMapping(vec2 texCoords, vec3 viewDir)
{ 
    // number of depth layers
    const float minLayers = 8;
    const float maxLayers = 32;
    float numLayers = mix(maxLayers, minLayers, abs(dot(vec3(0.0, 0.0, 1.0), viewDir)));  
    // calculate the size of each layer
    float layerDepth = 1.0 / numLayers;
    // depth of current layer
    float currentLayerDepth = 0.0;
    // the amount to shift the texture coordinates per layer (from vector P)
    vec2 P = viewDir.xy / viewDir.z * heightScale; 
    vec2 deltaTexCoords = P / numLayers;
  
    // get initial values
    vec2  currentTexCoords     = texCoords;
    float currentDepthMapValue = texture(displacementMap, currentTexCoords).r;
      
    while(currentLayerDepth < currentDepthMapValue)
    {
        // shift texture coordinates along direction of P
        currentTexCoords -= deltaTexCoords;
        // get depthmap value at current texture coordinates
        currentDepthMapValue = texture(displacementMap, currentTexCoords).r;  
        // get depth of next layer
        currentLayerDepth += layerDepth;  
    }
    
    // get texture coordinates before collision (reverse operations)
    vec2 prevTexCoords = currentTexCoords + deltaTexCoords;

    // get depth after and before collision for linear interpolation
    float afterDepth  = currentDepthMapValue - currentLayerDepth;
    float beforeDepth = texture(displacementMap, prevTexCoords).r - currentLayerDepth + layerDepth;
 
    // interpolation of texture coordinates
    float weight = afterDepth / (afterDepth - beforeDepth);
    vec2 finalTexCoords = prevTexCoords * weight + currentTexCoords * (1.0 - weight);

    return finalTexCoords;
}

void main(){

    vec3 viewDir = normalize(TangentViewPos - TangentFragPos);
    //vec2 texCoords = ParallaxMapping(textureCoords,  viewDir);
    vec2 texCoords = textureCoords;

    vec3 normaltbn = normalize(texture(normalMap, texCoords).rgb);
    vec3 normali = normalize(tbnMat * normaltbn);

    // tbnMat * normaltbn

	float shadower = shadowCalc(normali);
	float lightFactor = 1.0 - (0.8 * shadower);

    //

    gPosition = vec4(fragPosWorldSpace, 1.0f);
    gNormal = vec4(normali, 1.0f);

    if (diffuse.x != -1){
        gAlbedoSpec.rgb = diffuse;
        gAlbedoSpec.a = 1.0f;
    } else
        gAlbedoSpec = texture(diffuseTexture, texCoords);


    if (gAlbedoSpec.a < 0.1f)
        discard;
    
    vec4 specMapTexture = texture(specMap, texCoords);
    if (specMapTexture.a > 0)
        gAlbedoSpec.a = specMapTexture.r;
    else
        gAlbedoSpec.a = specAmount;
    

    //  vec4 displacementMapTexture = texture(displacementMap, textureCoords);
    //  float displacement = -1.0f;
    //  if (displacementMapTexture.a > 0)
    //      displacement = displacementMapTexture.r;

    gRenderState = vec4(1.0f, 0.0f, lightFactor, 0.0f);
}
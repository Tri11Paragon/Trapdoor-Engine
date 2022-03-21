#version 420 core

layout(early_fragment_tests) in;
in vec2 textureCoords;
in vec3 normalo;
in vec3 fragpos;
in vec3 fragPosWorldSpace;
in vec4 shadowCoords;
in mat3 tbnMat;
in vec3 TangentViewPos;
in vec3 TangentFragPos;

const int NR_LIGHTS = 32;

layout (location = 0) out vec4 out_Color;
layout (location = 1) out vec4 bright_Color;

layout (std140, binding = 3) uniform LightSpaceMatrices {
    mat4 lightSpaceMatrices[5];
};
layout (std140) uniform Lightings {
    // x y z, l
    vec4 Position[NR_LIGHTS];
    // q, r g b
    vec4 LightInfo[NR_LIGHTS];
};
layout (std140) uniform Matricies {
    mat4 projectionMatrix;
    mat4 viewMatrix;
    mat4 projectionViewMatrix;
    mat4 othroMatrix;
   	mat4 shadowMatrix;
};
uniform mat4 transformMatrix;

uniform float cascadePlaneDistances[5];
uniform int cascadeCount;
uniform vec3 lightDir;
uniform float farPlane;

uniform sampler2D diffuseTexture;
uniform sampler2D normalMap;
uniform sampler2D displacementMap;
uniform sampler2D specMap;
uniform sampler2D emissionMap;
uniform sampler2DArray shadowMap;

uniform vec3 viewPos;

uniform float specAmount;
uniform vec3 diffuseValue;

uniform vec3 directLightColor;

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

vec3 calculateLighting(vec3 FragPos, vec3 Normal, vec3 Diffuse, vec3 directlightDir, float specularMapAmount, float shadowAmount){
    //float AmbientOcclusion = texture(ssaoColor, textureCoords).r;
    // then calculate lighting as usual
    //vec3 lighting = Diffuse * 0.03f * (AmbientOcclusion); // hard-coded ambient component
    vec3 lighting = Diffuse * 0.03f;

    // add directional lighting
    lighting += Diffuse * (max(dot(Normal, directlightDir), 0.0) * directLightColor) * shadowAmount;

    vec3 viewDir  = normalize(viewPos-FragPos);

    // Normal Lighting
    for(int i = 0; i < NR_LIGHTS; ++i) {
        // calculate distance between light source and current fragment
        vec3 lmp = Position[i].xyz - FragPos;
        float distance = length(lmp);
            // diffuse
        vec3 lightDir = normalize(lmp);
        vec3 lightColor = vec3(LightInfo[i].yzw);
        vec3 diffuse = max(dot(Normal, lightDir), 0.0) * Diffuse * lightColor;

        vec3 specular = vec3(0.0f);
        if (diffuse != vec3(0.0f)){
                // specular (blinn phong)
            vec3 halfwayDir = normalize(lightDir + viewDir);  
            
            float specAmount = pow(max(dot(Normal, halfwayDir), 0.0), 16.0);
            
            specular = lightColor * specAmount * specularMapAmount;
        }
            // attenuation, 0.7f (l) 1.2f (q)
        float attenuation = 1.0 / (1.0 + Position[i].w * distance + LightInfo[i].x * distance * distance);
        diffuse *= attenuation;
        specular *= attenuation;

        //lighting += (diffuse * AmbientOcclusion + specular);
        lighting += (diffuse + specular);
    }    

    return lighting;
}

const float heightScale = 0.05f;

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

    vec4 diffuseT = texture(diffuseTexture, texCoords);
    vec4 specT = texture(specMap, texCoords);

    // tbnMat * normaltbn

	float shadower = shadowCalc(normali);
	float lightFactor = 1.0 - (0.8 * shadower);

    out_Color = vec4(calculateLighting(fragPosWorldSpace, normalo, diffuseT.rgb, lightDir, specT.r, lightFactor), 1.0);

    float brightness = dot(out_Color.rgb, vec3(0.2126, 0.7152, 0.0722));
    bright_Color = vec4(out_Color.rgb, 1.0) * brightness * brightness;
}
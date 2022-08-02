#version 460 core
out vec4 FragColor;

in vec2 TexCoords;

uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D gAlbedoSpec;
uniform sampler2DArray shadowMap;

struct Light {
    vec3 Position;
    vec3 Color;

    float Linear;
    float Quadratic;
    float Radius;
};
const int NR_LIGHTS = 50;
uniform Light lights[NR_LIGHTS];
uniform vec3 viewPos;

layout (std140) uniform Matrices {
    mat4 projectionMatrix;
    mat4 viewMatrix;
    mat4 orthoMatrix;
    mat4 projectViewMatrix;
};


uniform vec3 direction;
uniform vec4 color;
uniform bool enabled;

layout (std140, binding = 3) uniform LightSpaceMatrices {
    mat4 lightSpaceMatrices[16];
};
uniform float cascadePlaneDistances[16];
uniform int cascadeCount;   // number of frusta - 1
uniform float farPlane;

int layer;

float ShadowCalculation(vec3 fragPosWorldSpace, vec3 Normal) {
    // select cascade layer
    vec4 fragPosViewSpace = viewMatrix * vec4(fragPosWorldSpace, 1.0);
    float depthValue = abs(fragPosViewSpace.z);

    layer = -1;
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
    vec3 normal = normalize(Normal);
    float bias = max(0.05 * (1.0 - dot(normal, direction)), 0.005);
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

vec3 calcDirLight(vec3 FragPos, vec3 Diffuse, vec3 Normal, float Specular){
    if (!enabled)
        return vec3(0);

    vec3 lightDir = normalize(direction);
    vec3 norm = normalize(Normal);
    float diff = max(dot(norm, lightDir), 0.0);

    vec3 diffuse = color.xyz * diff * Diffuse;

    vec3 viewDir = normalize(viewPos - FragPos);
    vec3 halfwayDir = normalize(lightDir + viewDir);
    float spec = pow(max(dot(norm, halfwayDir), 0.0), 16.0);
    vec3 specular = color.xyz * spec * Specular;

    return vec3(diffuse + specular);
}

vec3 CalcDirLight(vec3 FragPos, vec3 Diffuse, vec3 normal, float Specular) {
    vec3 viewDir = normalize(viewPos - FragPos);
    vec3 lightDir = normalize(direction);
    // diffuse shading
    float diff = max(dot(normal, lightDir), 0.0);
    // specular shading
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 16);
    // combine results
    vec3 diffuse  = color.xyz  * diff * Diffuse;
    vec3 specular = color.xyz * spec * vec3(Specular);
    return (diffuse + specular);
}

const int NB_STEPS = 100;
const float G_SCATTERING = 0.5;
const float PI = 3.14159265359;

float ComputeScattering(float lightDotView) {
    float result = 1.0f - G_SCATTERING * G_SCATTERING;
    result /= (4.0f * PI * pow(1.0f + G_SCATTERING * G_SCATTERING - (2.0f * G_SCATTERING) * lightDotView, 1.5f));
    return result;
}

vec3 accumFog(vec3 worldPos, vec3 startPosition){
    vec3 rayVector = worldPos.xyz - startPosition;

    float rayLength = length(rayVector);
    vec3 rayDirection = rayVector / rayLength;

    float stepLength = rayLength / NB_STEPS;

    vec3 step = rayDirection * stepLength;

    vec3 currentPosition = startPosition;

    vec3 accumFog = vec3(0.0f);

    for (int i = 0; i < NB_STEPS; i++) {
        vec4 worldInShadowCameraSpace = lightSpaceMatrices[layer] * vec4(currentPosition, 1.0f);
        vec3 projCoords = worldInShadowCameraSpace.xyz / worldInShadowCameraSpace.w;

        // transform to [0,1] range
        projCoords = projCoords * 0.5 + 0.5;
        float currentDepth = projCoords.z;


        float shadowMapValue = texture(shadowMap, vec3(projCoords.xy, layer)).r;

        if (shadowMapValue > currentDepth) { // worldByShadowCamera.z
            accumFog += vec3(ComputeScattering(dot(rayDirection, direction))) * color.xyz;
        }
        currentPosition += step;
    }
    accumFog /= NB_STEPS;
    return accumFog;
}


void main() {
    // retrieve data from gbuffer
    vec4 posTexture = texture(gPosition, TexCoords);
    vec3 FragPos = posTexture.rgb;
    vec3 Normal = texture(gNormal, TexCoords).rgb;
    vec3 Diffuse = texture(gAlbedoSpec, TexCoords).rgb;
    float Specular = texture(gAlbedoSpec, TexCoords).a;

    if (posTexture.a >= 1.0){
        FragColor = vec4(Diffuse, 1.0);
        return;
    }

    // then calculate lighting as usual
    float shadow = ShadowCalculation(FragPos, Normal);
    vec3 lighting = ((Diffuse * 0.1) + calcDirLight(FragPos, Diffuse, Normal, Specular)) * (1.0 - 0.8 * shadow); // hard-coded ambient component
    vec3 viewDir = normalize(viewPos - FragPos);
    for(int i = 0; i < NR_LIGHTS; ++i) {
        // calculate distance between light source and current fragment
        float distance = length(lights[i].Position - FragPos);
        if(distance < lights[i].Radius) {
            // diffuse
            vec3 lightDir = normalize(lights[i].Position - FragPos);
            vec3 diffuse = max(dot(Normal, lightDir), 0.0) * Diffuse * lights[i].Color;
            // specular
            vec3 halfwayDir = normalize(lightDir + viewDir);
            float spec = pow(max(dot(Normal, halfwayDir), 0.0), 16.0);
            vec3 specular = lights[i].Color * spec * Specular;
            // attenuation
            float attenuation = 1.0 / (1.0 + lights[i].Linear * distance + lights[i].Quadratic * distance * distance);
            diffuse *= attenuation;
            specular *= attenuation;
            lighting += diffuse + specular;
        }
    }
    FragColor = vec4(lighting + accumFog(FragPos, viewPos), 1.0);
}
/**#version 330 core

in float pass_height;
in vec3 fragpos;

uniform vec4 color1;
uniform vec4 color2;

uniform float useColor;

uniform samplerCube cubeMap;

layout (location = 0) out vec4 gPosition;
layout (location = 1) out vec4 gNormal;
layout (location = 2) out vec4 gAlbedoSpec;
layout (location = 3) out vec4 gRenderState;

//I want to use the glsl smoothstep function, but for some unknown reason it doesn't work on my laptop when exported as a jar. But always works fine in Eclipse!
float smoothlyStep(float edge0, float edge1, float x){
    float t = clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0);
    return t * t * (3.0 - 2.0 * t);
}

void main(void){

    gPosition = vec4(fragpos, 1.0f);

    if (useColor == 1){
        float fadeFactor = 1.0 - smoothlyStep(-50.0, 70.0, pass_height);
        gAlbedoSpec = mix(color2, color1, fadeFactor);
    } else {
        gAlbedoSpec = texture(cubeMap, fragpos);
    }
    gRenderState = vec4(0.0f);
	
}**/

#version 330 core

in float pass_height;
in vec3 fragpos;

uniform vec4 color1;
uniform vec4 color2;

uniform float useColor;

uniform samplerCube cubeMap;

layout (location = 0) out vec4 gPosition;
layout (location = 1) out vec4 gNormal;
layout (location = 2) out vec4 gAlbedoSpec;

float smoothlyStep(float edge0, float edge1, float x){
    float t = clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0);
    return t * t * (3.0 - 2.0 * t);
}

void main(void){
    gPosition = vec4(fragpos, 1.0f);

    if (useColor == 1){
        float fadeFactor = 1.0 - smoothlyStep(-50.0, 70.0, pass_height);
        gAlbedoSpec = mix(color2, color1, fadeFactor);
    } else {
        gAlbedoSpec = texture(cubeMap, fragpos);
    }
}
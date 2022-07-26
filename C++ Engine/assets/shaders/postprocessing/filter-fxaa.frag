#version 430 core
out vec4 FragColor;

in vec2 TexCoord;
in vec2 invserseTexSize;


uniform sampler2D screenTexture;

const vec3 luma = vec3(0.299, 0.587, 0.114);
const float FXAA_SPAN_MAX = 8.0;
const float FXAA_REDUCE_MIN = 1.0/128.0;
const float FXAA_REDUCE_MUL = 1.0/8.0;
const float FXAA_BLUR_THRES = 1.0/4.0;

// 1/3 – too little
// 1/4 – low quality
// 1/8 – high quality
// 1/16 – overkill
const float FXAA_EDGE_THRESHOLD = 1.0/8.0;

// 1/32 – visible limit
// 1/16 – high quality
// 1/12 – upper limit (start of visible unfiltered edges)
const float FXAA_EDGE_THRESHOLD_MIN = 1.0/16.0;

void main() {
    float lumaTL = dot(luma, texture(screenTexture, TexCoord + (vec2(-1.0, -1.0) * invserseTexSize)).xyz);
    float lumaTR = dot(luma, texture(screenTexture, TexCoord + (vec2(1.0, -1.0) * invserseTexSize)).xyz);
    float lumaBL = dot(luma, texture(screenTexture, TexCoord + (vec2(-1.0, 1.0) * invserseTexSize)).xyz);
    float lumaBR = dot(luma, texture(screenTexture, TexCoord + (vec2(1.0, 1.0) * invserseTexSize)).xyz);
    float lumaM = dot(luma, texture(screenTexture, TexCoord).xyz);

    float lumaMin = min(lumaM, min(lumaTL, min(lumaBL, min(lumaBR, lumaTR))));
    float lumaMax = max(lumaM, max(lumaTL, max(lumaTR, max(lumaBL, lumaBR))));

    float range = lumaMax - lumaMin;
    if(range < max(FXAA_EDGE_THRESHOLD_MIN, lumaMax * FXAA_EDGE_THRESHOLD)) {
        FragColor = texture(screenTexture, TexCoord);
        return;
    }

    vec2 dir;
    dir.x = -((lumaTL + lumaTR) - (lumaBL + lumaBR));
    dir.y = ((lumaTL + lumaBL) - (lumaTR + lumaBR));

    if (abs(dir.x) > FXAA_BLUR_THRES || abs(dir.y) > FXAA_BLUR_THRES){
        // make the smallest component have a length of 1.
        const float dirZeroPreventinator = max((lumaTL + lumaTR + lumaBL + lumaBR) * (FXAA_REDUCE_MUL * 0.25), FXAA_REDUCE_MIN);
        float inverseNormalizer = 1/(min(abs(dir.x), abs(dir.y)) + dirZeroPreventinator);

        dir = min(vec2(FXAA_SPAN_MAX, FXAA_SPAN_MAX), max(vec2(-FXAA_SPAN_MAX, -FXAA_SPAN_MAX), dir * inverseNormalizer)) * invserseTexSize;

        vec3 firstResult = 0.5 * (
        texture(screenTexture, TexCoord + (dir * vec2(1.0/3.0 - 0.5))).xyz +
        texture(screenTexture, TexCoord + (dir * vec2(2.0/3.0 - 0.5))).xyz
        );

        vec3 secondResult = (firstResult * 0.5) + 0.25 * (
        texture(screenTexture, TexCoord + (dir * vec2(0.0/3.0 - 0.5))).xyz +
        texture(screenTexture, TexCoord + (dir * vec2(3.0/3.0 - 0.5))).xyz
        );

        float lumaResult = dot(luma, secondResult);

        if (lumaResult < lumaMin || lumaResult > lumaMax)
        FragColor = vec4(firstResult, 1.0f);
        else
        FragColor = vec4(secondResult, 1.0f);
    } else
        FragColor = texture(screenTexture, TexCoord);
}
#version 330 core
out vec4 FragColor;

in vec2 textureCoords;

uniform sampler2D mainTexture;
uniform sampler2D combineTexture;

uniform float exposure;

const float gamma = 2.2;

void main()
{             
    vec3 hdrColor = texture(mainTexture, textureCoords).rgb;      
    vec3 bloomColor = texture(combineTexture, textureCoords).rgb;

    hdrColor += bloomColor; // additive blending

    // tone mapping
    //vec3 result = vec3(1.0) - exp(-hdrColor * exposure);
    // also gamma correct while we're at it       
    vec3 result = pow(hdrColor, vec3(1.0 / gamma));
    FragColor = vec4(result, 1.0);
}
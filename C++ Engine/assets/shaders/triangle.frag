#version 330 core
out vec4 FragColor;

in vec2 TexCoord;

uniform sampler2D ourTexture;
uniform vec3 lightColor;

void main() {
    vec4 textureColor = texture(ourTexture, TexCoord);

    float ambientStrength = 0.1;
    vec3 ambient = ambientStrength * lightColor;

    vec3 result = ambient * textureColor.rgb;

    FragColor = vec4(textureColor.rgb, 1.0f);
}
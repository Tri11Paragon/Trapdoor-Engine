#version 330 core
out vec4 FragColor;
  
in vec2 TexCoord;
in vec2 texturePos;

uniform sampler2DArray ourTexture[16];

void main() {
    FragColor = texture(ourTexture[int(texturePos.x)], vec3(TexCoord, int(texturePos.y)));
    if (FragColor.a < 0.1f)
        discard;
} 

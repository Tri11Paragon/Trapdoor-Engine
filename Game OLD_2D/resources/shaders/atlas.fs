#version 330 core
out vec4 FragColor;
  
in vec2 TexCoord;
in float texturePos;

uniform sampler2DArray ourTexture;

void main()
{
    FragColor = texture(ourTexture, vec3(TexCoord, texturePos));
    if (FragColor.a < 0.1f)
        discard;
} 

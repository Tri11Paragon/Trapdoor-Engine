#version 330 core
out vec4 FragColor;
  
in vec2 TexCoord;

uniform sampler2DArray ourTexture;
uniform float textureID;

void main()
{
    FragColor = texture(ourTexture, vec3(TexCoord, textureID));
} 

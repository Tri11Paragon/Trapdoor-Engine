#version 330

in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D tex;


void main(void){
	
	out_Color = vec4(texture(tex, textureCoords).r,
						texture(tex, textureCoords).r,
						texture(tex, textureCoords).r, 1.0f
						);
	
}
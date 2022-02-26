#version 330

in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D tex;


void main(void){
	
	vec3 t = texture(tex, textureCoords).rgb;
	
	if (t.g == 0 && t.b == 0) {
		out_Color = vec4(t.r, t.r, t.r, 1.0f);
	} else {
		out_Color = vec4(t, 1.0f);
	}
}
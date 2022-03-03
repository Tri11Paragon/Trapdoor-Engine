#version 330

in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2DArray tex;
uniform float level;


void main(void){
	
	vec3 t = texture(tex, vec3(textureCoords, int(level))).rgb;
	
	if (t.g == 0 && t.b == 0) {
		out_Color = vec4(t.r, t.r, t.r, 1.0f);
	} else {
		out_Color = vec4(t, 1.0f);
	}
}
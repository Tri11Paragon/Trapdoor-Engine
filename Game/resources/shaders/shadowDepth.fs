#version 330 core

out vec4 out_Color;

in vec2 textureCoord;

uniform sampler2D tex;

void main() {
	#ifndef $intel
		if (texture(tex, textureCoord).a < 0.1f)
			discard;
	#endif
	out_Color = vec4(1.0f);
    gl_FragDepth = gl_FragCoord.z;
}
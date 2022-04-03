#version 420 core

out vec4 out_Color;

layout(early_fragment_tests) in;
in vec2 textureCoord;

uniform sampler2DArray tex;
uniform float flags;

void main() {
	int f = int(flags);

    int base = (f << 4) >> 4;
	#ifndef $intel
		if (texture(tex, vec3(textureCoord, base)).a < 0.1f)
			discard;
	#endif
	//out_Color = vec4(1.0f);
    //gl_FragDepth = gl_FragCoord.z;
}
#version 420 core

out vec4 out_Color;

in vec2 textureCoord;

uniform sampler2D tex;

void main() {
	#ifndef $intel
		#ifndef $amd
			if (texture(tex, textureCoord).a < 0.1f)
				discard;
		#endif
	#endif
	out_Color = vec4(1.0f);
    gl_FragDepth = gl_FragCoord.z;
}
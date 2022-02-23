#version 330

out vec4 out_Color;


void main() {
	out_Color = vec4(1.0f);
    gl_FragDepth = gl_FragCoord.z;
}
/*#version 420 core

out vec4 out_colour;

in vec2 textureCoords;
in vec3 textureInfo;

uniform sampler2DArray tex;

void main(void){
	out_colour = smoothstep(
		texture(tex, vec3(textureCoords, textureInfo.x)), 
		texture(tex, vec3(textureCoords, textureInfo.y)), 
		vec4(textureInfo.z)
	);
	out_colour = vec4(1.0f);
}*/



#version 420 core

//out vec4 out_colour;

layout (location = 0) out vec4 gPosition;
layout (location = 1) out vec4 gNormal;
layout (location = 2) out vec4 gAlbedoSpec;
layout (location = 3) out vec4 gRenderState;

in vec2 textureCoords;
in vec3 textureInfo;
in vec4 worldPos;
in vec3 normal;

uniform sampler2DArray tex;

void main(void){
	vec4 t1 = texture(tex, vec3(textureCoords, textureInfo.x));
	vec4 t2 = texture(tex, vec3(textureCoords, textureInfo.y));

	gAlbedoSpec = mix(
		t1, 
		t2, 
		textureInfo.z
	);

	if (gAlbedoSpec.a < 0.01f)
		discard;

	gPosition = vec4(worldPos.xyz, 1.0f);
	gNormal = vec4(normal, 1.0f);
	gRenderState = vec4(0.5f, 0.0f, 0.0f, 1.0f);
}


#version 420 core

layout (location = 0) out vec4 out_Color;
layout (location = 1) out vec4 bright_Color;

in vec2 textureCoords;
in vec3 textureInfo;
in vec4 worldPos;
in vec3 normal;
in float distance;

uniform sampler2DArray tex;

void main(void){
	vec4 t1 = texture(tex, vec3(textureCoords, textureInfo.x));
	vec4 t2 = texture(tex, vec3(textureCoords, textureInfo.y));

	//vec4 t1 = vec4(vec3(distance / 100000f, order, 0.0f), 1.0f);
	//vec4 t2 = t1;
	//vec4 t2 = vec4(vec3(distance / 100000f), 1.0f);

	out_Color = mix(
		t1, 
		t2, 
		textureInfo.z
	);

	bright_Color = vec4(0.0f);
}



/*#version 420 core

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
*/

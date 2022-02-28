#version 330 core

out float FragColor;
  
in vec2 textureCoords;

uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D gAlbedoSpec;
uniform sampler2D gRenderState;
uniform sampler2D texNoise;

uniform vec3 samples[64];

layout (std140) uniform Matricies {
    mat4 projectionMatrix;
    mat4 viewMatrix;
    mat4 projectionViewMatrix;
    mat4 othroMatrix;
    mat4 shadowMatrix;
};

// tile noise texture over screen, based on screen dimensions divided by noise size
uniform vec2 noiseScale;

const float bias = 0.025f;
const int kernelSize = 16;
const float radius = 2.5f;

void main() {
    vec3 fragPos   = texture(gPosition, textureCoords).xyz;
	vec3 normal    = normalize(texture(gNormal, textureCoords).rgb);
	vec3 randomVec = normalize(texture(texNoise, textureCoords * noiseScale).xyz);  
	
	vec3 tangent   = normalize(randomVec - normal * dot(randomVec, normal));
	vec3 bitangent = cross(normal, tangent);
	mat3 TBN       = mat3(tangent, bitangent, normal);  
	
	float occlusion = 0.0;
	for(int i = 0; i < kernelSize; ++i) {
	    // get sample position
	    vec3 samplePos = TBN * samples[i]; // from tangent to view-space
	    samplePos = fragPos + samplePos * radius; 
	    
	    vec4 offset = vec4(samplePos, 1.0);
		offset      = projectionMatrix * offset;    // from view to clip-space
		offset.xyz /= offset.w;               // perspective divide
		offset.xyz  = offset.xyz * 0.5 + 0.5; // transform to range 0.0 - 1.0  
		
		float sampleDepth = texture(gPosition, offset.xy).z; 
		
		float rangeCheck = smoothstep(0.0, 1.0, radius / abs(fragPos.z - sampleDepth));
		occlusion += (sampleDepth >= samplePos.z + bias ? 1.0 : 0.0) * rangeCheck;         
	}  
	occlusion = 1.0 - (occlusion / kernelSize);
	FragColor = occlusion;  
}

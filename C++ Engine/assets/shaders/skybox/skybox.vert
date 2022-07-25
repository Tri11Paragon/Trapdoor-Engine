#version 330 core

in vec3 in_position;

out float pass_height;
out vec3 fragpos;

layout (std140) uniform Matrices {
    mat4 projectionMatrix;
    mat4 viewMatrix;
};

void main(void){
	fragpos = in_position;
    vec4 pos = projectionMatrix * mat4(mat3(viewMatrix)) * vec4(in_position, 1.0);
	gl_Position = pos.xyww;
	pass_height = in_position.y;
	
}
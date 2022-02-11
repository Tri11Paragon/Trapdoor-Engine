#version 330 core

in vec3 in_position;

out float pass_height;
out vec3 fragpos;

layout (std140) uniform Matricies {
    mat4 projectionMatrix;
    mat4 viewMatrix;
    mat4 projectionViewMatrix;
};

void main(void){
	fragpos = in_position;

	gl_Position = projectionViewMatrix * vec4(in_position, 1.0);
	pass_height = in_position.y;
	
}
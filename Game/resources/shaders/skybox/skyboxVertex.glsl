#version 330 core

in vec3 in_position;

out float pass_height;
out vec3 fragpos;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main(void){
	fragpos = in_position;

	gl_Position = projectionMatrix * viewMatrix * vec4(in_position, 1.0);
	pass_height = in_position.y;
	
}
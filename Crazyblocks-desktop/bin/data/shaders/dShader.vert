attribute vec3 a_position;
attribute float a_light;
attribute vec2 a_texCoord;

uniform mat4 u_projTrans;

varying vec4 v_color;
varying vec2 v_texCoord;

void main() {

	v_color = vec4(a_light);
	v_color.a = 1.0;

	v_texCoord= a_texCoord;
	gl_Position= u_projTrans*vec4(a_position,1.0);

}
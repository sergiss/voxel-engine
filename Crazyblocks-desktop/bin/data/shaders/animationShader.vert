attribute vec3 a_position;
attribute vec2 a_lighting;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;
uniform float u_light1;
uniform float u_light2;
uniform vec2 u_tOffset;
uniform vec3 u_cameraPosition;
uniform float u_fogDst;

varying vec2 v_texCoord;
varying vec4 v_color;
varying float v_fog;

void main() {

    float c2= a_lighting.y*u_light2;
    float c1= a_lighting.x*u_light1;

    v_color= vec4(min(c1+c2,1.0),min(c1*0.95+c2,1.0),min(c1*0.6+c2,1.0),1.0);

	v_texCoord= a_texCoord0 + u_tOffset;

	gl_Position= u_projTrans*vec4(a_position,1.0);

	// Fog
	//vec3 flen = u_cameraPosition - a_position;
	//v_fog = clamp((dot(flen, flen) - 128.0 * 128.0) * 0.0625, 0.0, 1.0);
	float l = distance(u_cameraPosition, a_position);
	v_fog = clamp((l - u_fogDst) * 0.0625, 0.0, 1.0);

}
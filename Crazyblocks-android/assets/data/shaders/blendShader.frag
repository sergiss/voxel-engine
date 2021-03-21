#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform vec4 u_fogColor;

varying vec2 v_texCoord;
varying vec4 v_color;
varying float v_fog;

void main() {

	    gl_FragColor = texture2D(u_texture, v_texCoord) * v_color;

	    if(gl_FragColor.a <= 0.0)
        	discard;

    	// Fog
        gl_FragColor.rgb = mix(gl_FragColor.rgb, u_fogColor.rgb, v_fog);
	  	 	
}
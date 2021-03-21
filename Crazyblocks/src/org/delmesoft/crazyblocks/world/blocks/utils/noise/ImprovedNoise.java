package org.delmesoft.crazyblocks.world.blocks.utils.noise;

import java.util.Random;

import static org.delmesoft.crazyblocks.math.MathHelper.fastFloor;

public class ImprovedNoise implements PerlinNoise {

	public static final int SAMPLE_SIZE = 512;

	private final int p[] = new int[SAMPLE_SIZE];

	private final Random random;

	public ImprovedNoise(long seed) {

		random = new Random(seed);

		final int[] p = this.p;

		for (int i = 0; i < 256; ++i) {
			p[i] = i;
		}

		for (int i = 0; i < 256; i++) {
			int index = random.nextInt(i + 1);
			int temp = p[index];
			p[index + 256] = p[index] = p[i];
			p[i + 256] = p[i] = temp;
		}

	}

	public float noise(float x) {
		int xi = fastFloor(x) & 0xFF;
		x -= fastFloor(x);
		float u = fade(x);
		return lerp(u, grad(p[xi], x), grad(p[xi+1], x-1));
	}

	private float grad(int hash, float x) {
		return (hash & 1) == 0 ? x : -x;
	}

	@Override
	public float noise(float x, float y) {

		int xi = fastFloor(x) & 0xFF;
		int yi = fastFloor(y) & 0xFF;

		x -= fastFloor(x);
		y -= fastFloor(y);

		int g1 = p[xi    ] + yi;
		int g2 = p[xi + 1] + yi;

		float d1 = grad(p[g1    ], x, y);
		float d2 = grad(p[g2    ], x - 1, y);
		float d3 = grad(p[g1 + 1], x, y - 1);
		float d4 = grad(p[g2 + 1], x - 1, y - 1);

		float u = fade(x);

		return lerp(fade(y), lerp(u, d1, d2), lerp(u, d3, d4));
	}

	private float grad(int hash, float x, float y){
		switch(hash & 3){
			case 0: return  x + y;
			case 1: return -x + y;
			case 2: return  x - y;
			case 3: return -x - y;
			default: return 0; // never happens
		}
	}

	@Override
	public float noise(float x, float y, float z) {

		int ix = fastFloor(x) & 0xFF,                  	   // FIND UNIT CUBE THAT
			iy = fastFloor(y) & 0xFF,                 	   // CONTAINS POINT.
			iz = fastFloor(z) & 0xFF;

		x -= fastFloor(x);                                 // FIND RELATIVE ix,iy,iz
		y -= fastFloor(y);                                 // OF POINT IN CUBE.
		z -= fastFloor(z);

		float u = fade(x),                                 // COMPUTE FADE CURVES
			   v = fade(y),                                 // FOR EACH OF ix,iy,iz.
			   w = fade(z);

		int A = p[ix  ]+iy, AA = p[A]+iz, AB = p[A+1]+iz,      // HASH COORDINATES OF
			B = p[ix+1]+iy, BA = p[B]+iz, BB = p[B+1]+iz;      // THE 8 CUBE CORNERS,

		return lerp(w, lerp(v, lerp(u, grad(p[AA  ], x  , y  , z   ),  // AND ACTION2
				                       grad(p[BA  ], x-1, y  , z   )), // BLENDED
				               lerp(u, grad(p[AB  ], x  , y-1, z   ),  // RESULTS
						               grad(p[BB  ], x-1, y-1, z   ))),// FROM  8
					   lerp(v, lerp(u, grad(p[AA+1], x  , y  , z-1 ),  // CORNERS
								       grad(p[BA+1], x-1, y  , z-1 )), // OF CUBE
							   lerp(u, grad(p[AB+1], x  , y-1, z-1 ),
									   grad(p[BB+1], x-1, y-1, z-1 ))));
	}

	@Override
	public Random getRandom() {
		return random;
	}

	private float fade(float t) {
		return t * t * t * (t * (t * 6 - 15) + 10);
	}

	private float lerp(float t, float a, float b) {
		return t * (b - a) + a;
	}
	
	// http://riven8192.blogspot.com.es/2010/08/calculate-perlinnoise-twice-as-fast.html
	static float grad(int hash, float x, float y, float z) {

		switch (hash & 0xF) {
		case 0x0: return  x + y;
		case 0x1: return -x + y;
		case 0x2: return  x - y;
		case 0x3: return -x - y;
		case 0x4: return  x + z;
		case 0x5: return -x + z;
		case 0x6: return  x - z;
		case 0x7: return -x - z;
		case 0x8: return  y + z;
		case 0x9: return -y + z;
		case 0xA: return  y - z;
		case 0xB: return -y - z;
		case 0xC: return  y + x;
		case 0xD: return -y + z;
		case 0xE: return  y - x;
		case 0xF: return -y - z;
		default: return 0; // never happens
		}
	}

	private float lerp(float x, float x1, float x2, float q00, float q01) {
		return ((x2 - x) / (x2 - x1)) * q00 + ((x - x1) / (x2 - x1)) * q01;
	}

	private float biLerp(float x, float y, float q11, float q12, float q21, float q22, float x1, float x2, float y1, float y2) {
		float r1 = lerp(x, x1, x2, q11, q21);
		float r2 = lerp(x, x1, x2, q12, q22);

		return lerp(y, y1, y2, r1, r2);
	}

	private float triLerp(float x, float y, float z, float q000, float q001, float q010, float q011, float q100, float q101, float q110, float q111, float x1, float x2, float y1, float y2, float z1, float z2) {
		
		float x00 = lerp(x, x1, x2, q000, q100);
		float x10 = lerp(x, x1, x2, q010, q110);
		float x01 = lerp(x, x1, x2, q001, q101);
		float x11 = lerp(x, x1, x2, q011, q111);
		float r0  = lerp(y, y1, y2, x00, x01);
		float r1  = lerp(y, y1, y2, x10, x11);

		return lerp(z, z1, z2, r0, r1);
	}

	public float fractal(float x, float y, int octaves)  {

		float output = 0.f;
		float denom  = 0.f;
		float frequency = 1;
		float amplitude = 1;

		for (int i = 0; i < octaves; i++) {
			output += (amplitude * noise(x * frequency, y * frequency));
			denom += amplitude;

			frequency *= 2;
			amplitude *= 1;
		}

		return (output / denom);
	}

}

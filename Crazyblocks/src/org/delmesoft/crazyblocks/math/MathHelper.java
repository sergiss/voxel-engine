package org.delmesoft.crazyblocks.math;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.StringBuilder;

public class MathHelper {

	public static final float EPSILON = 0.0001F;
	
	public static final float HALF_PI = (float) (Math.PI * 0.5);
	
	private static final float byteToMega = 0.0000009536743164f;
	
	public static float bytesToMagaBytes(long bytes) {
		return bytes * byteToMega;
	}
	
	public static float interpolateLinear(float scale, float startValue, float endValue) {

		if (startValue == endValue || scale <= 0f) {
			return startValue;
		}

		if (scale >= 1f) {
			return endValue;
		}

		return (1f - scale) * startValue + (scale * endValue);

	}
	
	public static Vector2 interpolateLinear(float scale, Vector2 start, float endX, float endY) {
		
		start.x = interpolateLinear(scale, start.x, endX);
		start.y = interpolateLinear(scale, start.y, endY);
		
		return start;
	}
	
	public static Vector2 interpolateLinear(float scale, Vector2 start, Vector2 end) {		
		return interpolateLinear(scale, start, end.x, end.y);
	}
	
	public static Vector3 interpolateLinear(float scale, Vector3 start, float endX, float endY, float endZ) {
		
		start.x = interpolateLinear(scale, start.x, endX);
		start.y = interpolateLinear(scale, start.y, endY);
		start.z = interpolateLinear(scale, start.z, endZ);
		
		return start;
	}
	
	public static Vector3 interpolateLinear(float scale, Vector3 start, Vector3 end) {		
		return interpolateLinear(scale, start, end.x, end.y, end.z);
	}
	
    public static int fastAbs(int i) {
        return (i >= 0) ? i : -i;
    }
    
    public static float fastAbs(float d) {
        return (d >= 0) ? d : -d;
    }
	
	public static int fastFloor(float x) {
		int xi = (int) x;
		return x < xi ? xi - 1 : xi;
	}
	
	public static int fastFloor(double x) {
		int xi = (int) x;
		return x < xi ? xi - 1 : xi;
	}

	public static int fastCeil(float x) {
		int xi = (int) x;
		return x > xi ? xi + 1 : xi;
	}

    public static int fastCeil(double x) {
        int xi = (int) x;
        return x > xi ? xi + 1 : xi;
    }

	public static int oscillate(int input, int min, int max) {
		int range = max - min;
		return min + Math.abs(((input + range) % (range << 1)) - range);
	}

	public static int nextPowerOf2(int v) {
		v |= v >> 1;
		v |= v >> 2;
		v |= v >> 4;
		v |= v >> 8;
		v |= v >> 16;
		return v + 1;
	}

	public static boolean isPowOf2(int n) {
		return (n & (n-1)) == 0;
	}

	public static float len2(float x1, float y1, float x2, float y2) {
		float dx = x2 - x1;
		float dy = y2 - y1;
		return dx * dx + dy * dy;
	}

	public static float len(float x1, float y1, float x2, float y2) {
		float len2 = len2(x1, y1, x2, y2);
		return len2 != 0 ? (float) Math.sqrt(len2) : 0F;
	}

	public static int max(int...values) {
		int max = values[0];
		for(int i = 1; i < values.length; ++i) {
			if (max < values[i]) {
				max = values[i];
			}
		}
		return max;
	}

    public static void stdOut(Object...objs) {
		StringBuilder sb = new StringBuilder();
		sb.append(objs[0]);
		for(int i = 1; i < objs.length; i++) {
			sb.append(", ").append(objs[i]);
		}
		System.out.println(sb.toString());
    }

	public static float getAngle(float x1, float z1, float x2, float z2) {
		return (float) Math.atan2(z2 - z1, x2 - x1);
	}

}

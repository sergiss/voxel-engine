package org.delmesoft.crazyblocks.world.blocks.utils;


import com.badlogic.gdx.math.Vector3;

import org.delmesoft.crazyblocks.world.blocks.Blocks.Side;


public class BlockIntersector {

	private static Vector3 v0 = new Vector3();
	
	public static Side intersectRayBounds(float originX, float originY, float originZ, float directionX, float directionY, float directionZ, float x, float y, float z) {
	
		float maxX = x + 1;
		float maxY = y + 1;
		float maxZ = z + 1;
		
		float lowest = 0, t;
		
		Side side = null;
		// min x
		if (originX <= x && directionX > 0) {
			t = (x - originX) / directionX;
			if (t >= 0 && (t < lowest || side == null)) {
				v0.set(directionX, directionY, directionZ).scl(t).add(originX, originY, originZ);
				if (v0.y >= y && v0.y <= maxY && v0.z >= z && v0.z <= maxZ) {
					side = Side.LEFT;
					lowest = t;
				}
			}
		}
		
		// max x
		if (originX >= maxX && directionX < 0) {
			t = (maxX - originX) / directionX;
			if (t >= 0 && (t < lowest || side == null)) {
				v0.set(directionX, directionY, directionZ).scl(t).add(originX, originY, originZ);
				if (v0.y >= y && v0.y <= maxY && v0.z >= z && v0.z <= maxZ) {
					side = Side.RIGHT;
					lowest = t;
				}
			}
		}
		// min y
		if (originY <= y && directionY > 0) {
			t = (y - originY) / directionY;
			if (t >= 0 && (t < lowest || side == null)) {
				v0.set(directionX, directionY, directionZ).scl(t).add(originX, originY, originZ);
				if (v0.x >= x && v0.x <= maxX && v0.z >= z && v0.z <= maxZ) {
					side = Side.BOTTOM;
					lowest = t;
				}
			}
		}
		// max y
		if (originY >= maxY && directionY < 0) {
			t = (maxY - originY) / directionY;
			if (t >= 0 && (t < lowest || side == null)) {
				v0.set(directionX, directionY, directionZ).scl(t).add(originX, originY, originZ);
				if (v0.x >= x && v0.x <= maxX && v0.z >= z && v0.z <= maxZ) {
					side = Side.TOP;
					lowest = t;
				}
			}
		}
		// min z
		if (originZ <= z && directionZ > 0) {
			t = (z - originZ) / directionZ;
			if (t >= 0 && (t < lowest || side == null)) {
				v0.set(directionX, directionY, directionZ).scl(t).add(originX, originY, originZ);
				if (v0.x >= x && v0.x <= maxX && v0.y >= y && v0.y <= maxY) {
					side = Side.BACK;
					lowest = t;
				}
			}
		}
		
		// max z
		if (originZ >= maxZ && directionZ < 0) {
			t = (maxZ - originZ) / directionZ;
			if (t >= 0 && (t < lowest || side == null)) {
				v0.set(directionX, directionY, directionZ).scl(t).add(originX, originY, originZ);
				if (v0.x >= x && v0.x <= maxX && v0.y >= y && v0.y <= maxY) {
					side = Side.FRONT;
					//lowest = t;
				}
			}
		}
			
		return side;
	}


}

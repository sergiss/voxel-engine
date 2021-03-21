package org.delmesoft.crazyblocks.world.blocks;

import org.delmesoft.crazyblocks.graphics.g3d.FastMesh;

import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.utils.Disposable;

public class Plot implements Disposable {

	public static final int BIT_OFFSET = 4;

	public static final int SIZE = 1 << BIT_OFFSET; // (1 << 4) = 16
		
	public static final int HALF_SIZE = SIZE >> 1;

	public static final float RADIUS = (float) (Math.sqrt(3) * HALF_SIZE); // For frustum
	
	// Local position
	private final int localY;
	// World position
	private final int worldY;

	public final Chunk chunk;

	public final FastMesh[] meshes;

	public boolean rendering;
	public boolean modified;

	public Plot(int y, Chunk chunk) {

		this.localY = y;
		this.worldY = y << BIT_OFFSET;

		this.chunk = chunk;

		meshes = new FastMesh[4];

	}

	public int getLocalX() {
		return chunk.localX;
	}

	public int getLocalY() {
		return localY;
	}

	public int getLocalZ() {
		return chunk.localZ;
	}

	public int getWorldX() {
		return chunk.worldX;
	}

	public int getWorldY() {
		return worldY;
	}

	public int getWorldZ() {
		return chunk.worldZ;
	}

	public Chunk getchunk() {
		return chunk;
	}

	public void clear() {
		modified  = false;
		rendering = false;
	}
	
	public float dst2(float worldX, float worldZ) {
		
		float dx = worldX - (chunk.worldX + HALF_SIZE);
		float dz = worldZ - (chunk.worldZ + HALF_SIZE);
		
		return dx * dx + dz * dz;
	}

	public boolean isVisible(Plane[] planes) {

		final int x = chunk.worldX + HALF_SIZE,
				  y = worldY       + HALF_SIZE,
				  z = chunk.worldZ + HALF_SIZE;

		Plane plane;

		for (int i = 0; i < 6; i++){
			plane = planes[i];
			if ((plane.normal.x * x + plane.normal.y * y + plane.normal.z * z) < (-RADIUS - planes[i].d)) return false;
		}

		return true;
	}
	
	@Override
	public void dispose() {
	
		final FastMesh[] meshes = this.meshes;
		for(int i = 0; i < 4; i++) {
			if(meshes[i] != null) {
				meshes[i].dispose();
				meshes[i] = null;
			}
		}
		
	}
	
	@Override
	public String toString() {
		return getLocalX() + ", " + getLocalY() + ", " + getLocalZ();
	}

}

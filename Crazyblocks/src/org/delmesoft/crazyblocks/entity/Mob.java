package org.delmesoft.crazyblocks.entity;

import org.delmesoft.crazyblocks.math.Vec3i;
import org.delmesoft.crazyblocks.world.blocks.Blocks;

public abstract class Mob extends Entity {

	public static final int RAY_LEN = 5;

	protected float speed;

	protected Vec3i rayPoint = new Vec3i();
	protected Blocks.Block rayBlock = Blocks.AIR;

	public Mob(float x, float y, float z, float width, float height, float depth) {
		super(x, y, z, width, height, depth);
		
	}

}

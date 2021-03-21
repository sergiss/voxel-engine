package org.delmesoft.crazyblocks.entity;

import static org.delmesoft.crazyblocks.math.MathHelper.fastFloor;

import org.delmesoft.crazyblocks.graphics.Renderer;
import org.delmesoft.crazyblocks.world.World;

import com.badlogic.gdx.math.Vector3;

public abstract class Entity {
	
	static final Vector3 tmp = new Vector3();
	
	public World world;
	
	public float x, y, z;
	public float xa, ya, za;
	
	public float width, height, depth; // bounds
	
	public float restitution;
	
	public boolean onGround;
	public short swimData;

	public Entity(float x, float y, float z, float width, float height, float depth) {

		this.x = x;
		this.y = y;
		this.z = z;

		this.width  = width;
		this.height = height;
		this.depth  = depth;

		restitution = 0.2F;

	}

	public abstract void render(Renderer renderer); // TODO : modelbatch

	public abstract void tick(float delta);

	protected void tryMove(float xa, float ya, float za) {
		
		// TODO : if (y < 0) remove this from world

		if(xa != 0F) {

			if (world.isFree(x + xa, y, z, width, height, depth)) {
				x += xa;
			} else {

				hitWall(xa, 0F, 0F);

				// Position correction
				if (xa < 0F) {
					xa = -(x - fastFloor(x));
				} else {
					float xx = x + width;
					xa = 1 - (xx - fastFloor(xx));
				}

				if (world.isFree(x + xa, y, z, width, height, depth)) {
					x += xa;
				}

				// Apply restitution
				//this.xa *= -restitution;
				this.xa *= 0F;
			}
		}

		if(za != 0F) {

			if (world.isFree(x, y, z + za, width, height, depth)) {

				z += za;

			} else {

				hitWall(0F, 0F, za);

				// Position correction
				if (za < 0F) {
					za = -(z - fastFloor(z));
				} else {
					float zz = z + depth;
					za = 1 - (zz - fastFloor(zz));
				}

				if (world.isFree(x, y, z + za, width, height, depth)) {
					z += za;
				}

				// Apply restitution
				//this.za *= -restitution;
				this.za *= 0F;
			}

		}

		onGround = false;

		if (world.isFree(x, y + ya, z, width, height, depth)) {

			y += ya;

		} else {

			if (ya < 0F)
				onGround = true;

			hitWall(0F, ya, 0F);

			// Position correction
			if (ya < 0F) {
				ya = -(y - (int) y);
			} else {
				float yy = y + height;
				ya = 1 - (yy - (int) yy);
			}

			if (world.isFree(x, y + ya, z, width, height, depth)) {
				y += ya;
			}
			
			// Apply restitution
			this.ya *= -restitution;

		}

	}

	protected void hitWall(float xa, float ya, float za) {}
	
	public boolean intersects(Entity entity) {
		return intersects(entity.x, entity.y, entity.z, entity.width, entity.height, entity.depth);
	}
	
	public boolean intersects(float x, float y, float z, float width, float height, float depth) {
		return  (Math.abs(x - this.x) * 2f < (width  + this.width )) &&
				(Math.abs(y - this.y) * 2f < (height + this.height)) &&
				(Math.abs(z - this.z) * 2f < (depth  + this.depth ));
	}

}

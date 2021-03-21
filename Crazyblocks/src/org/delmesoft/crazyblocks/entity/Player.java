package org.delmesoft.crazyblocks.entity;

import org.delmesoft.crazyblocks.graphics.g2d.layer.Layer;
import org.delmesoft.crazyblocks.math.MathHelper;
import org.delmesoft.crazyblocks.screen.GameScreen;
import org.delmesoft.crazyblocks.world.World;
import org.delmesoft.crazyblocks.world.blocks.Blocks;
import org.delmesoft.crazyblocks.world.blocks.utils.ChunkData;
import org.delmesoft.crazyblocks.world.blocks.utils.generators.FluidPoint;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public abstract class Player extends Mob implements Layer {

	public static final int UP = 0;
	public static final int DOWN = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;
	public static final int RISE = 4;
	public static final int DESCENT = 5;
	public static final int ADD = 6;
	public static final int REMOVE = 7;
	public static final int ACTION1 = 8;
	public static final int ACTION2 = 9;
	public static final int ACTION3 = 10;
	public static final int ACTION4 = 11;

	protected GameScreen screen;

	protected boolean flying;

	protected float iDeltaX, iDeltaY;
	
	public float dh, dv;
	protected boolean rise, descent;
	
	protected int dx, dy;
	protected int lx, ly;
	
	protected Blocks.Block block = Blocks.SAND;
	
	private float swing;

	public Player(float width, float height, float depth, GameScreen screen) {
		super(0, 0, 0, width, height, depth);
		this.screen = screen;
		speed = 0.025F;
	}

	public boolean isFlying() {
		return flying;
	}

	public void setFlying(boolean flying) {
		this.flying = flying;
	}

	protected void update() {

		final Camera camera = screen.camera;
		final Vector3 direction = camera.direction;
		final Vector3 cameraPosition = camera.position;

		direction.rotate(camera.up, iDeltaX);

		final float pitch = (float) (Math.atan2(Math.sqrt(direction.x * direction.x + direction.z * direction.z), direction.y) * MathUtils.radiansToDegrees);

		if (pitch - iDeltaY > 179F) {
			iDeltaY = -(179 - pitch);
		} else if (pitch - iDeltaY < 1F) {
			iDeltaY = pitch - 1F;
		}
		tmp.set(direction).crs(camera.up).nor();
		direction.rotate(tmp, iDeltaY);

		float posX = x + width  * 0.5F;
		float posY = y + height - 0.18F;
		float posZ = z + depth  * 0.5F;

		if(onGround && swing != 0F) { // swing

			posY += MathUtils.sin(MathHelper.HALF_PI - swing * 2F) * 0.05F;

			float bobOscillate = MathUtils.sin(MathHelper.HALF_PI + swing) * 0.05F;

			tmp.set(camera.direction).crs(camera.up).nor().scl(bobOscillate);
			posX += tmp.x; posZ += tmp.z;

			camera.up.rotate(camera.direction, MathUtils.cos(MathHelper.HALF_PI - swing) * 0.04F); // angle
			//cameraPosition.set(posX, posY, posZ);
		} else {
			camera.up.set(0F, 1F, 0F);
		}
		cameraPosition.set(posX, posY, posZ);
		//MathHelper.interpolateLinear(0.5F, cameraPosition, posX, posY, posZ);

		swimData = world.isContactWith(x, y, z, width, height, depth, Blocks.WATER.id);

		tryJump();

		dx = dy = 0;
		
	}

	private void tryJump() {
		if(onGround) {
			if(xa != 0F) {
				if (world.isFree(x + xa, y, z, width, height, depth) == false &&
					world.isFree(x + xa, y + 1F, z, width, height, depth)) {
					jump(xa * xa + za * za); onGround = false;
				}
			}
			if(za != 0F) {
				if (world.isFree(x, y, z + za, width, height, depth) == false &&
					world.isFree(x, y + 1F, z + za, width, height, depth)) {
					jump(xa * xa + za * za); onGround = false;
				}
			}
		}
	}

	private void jump(float len2) {
        float a = (float) Math.sqrt(2F * World.GRAVITY);
		ya += a + Math.sqrt(len2) * 0.5F;
	}

	@Override
	public void tick(float delta) {

		// update position
		tryMove(xa, ya, za);

		// update velocity
		xa *= 0.7F;
		za *= 0.7F;

		float speed;

		if(flying) {

			speed = this.speed * 2F;

			ya *= World.FRICTION * 0.9F;

			if(onGround) {
				flying = false;
			} else {

				if(rise) {
					ya += 0.02F;
				} else if(descent) {
					ya -= 0.02F;
				}

			}

		} else {

			speed = this.speed;

			if (ya < 0F && rise) {
				ya *= World.FRICTION * 1.00202F;
				ya -= World.GRAVITY * 0.5F;
			} else {
				ya *= World.FRICTION;
				ya -= World.GRAVITY;
			}

			if(ChunkData.getType(swimData) == Blocks.WATER.id) {

				xa *= 0.5F;
				ya *= 0.75F;
				za *= 0.5F;

				byte data = ChunkData.getData(swimData);

				int direction = FluidPoint.getDirection(data);
				if(direction != Blocks.Side.BOTTOM.ordinal()) {
					Blocks.Side side = Blocks.Side.values()[direction];

					float s = (speed / (FluidPoint.getDepth(data) + 1F)) * 1.5F;

					xa += side.x * s * 1F;
					ya += side.y * s * 1F;
					za += side.z * s * 1F;
				}

				if(rise) {
					ya += 0.02F;
				}

			}

			final float speed2 = xa * xa + za * za;

			// camera swing
			if (speed2 > 0.001F) {
				swing += delta * 9.375F;
			} else {
				swing = 0F;
			}

			// rise
			if(onGround && rise) {
				jump(speed2);
			}

		}

		final Camera camera = screen.camera;
		if(dv != 0) {
			tmp.set(camera.direction).y = 0;
			tmp.nor().scl(speed * dv);
			xa += tmp.x;
			za += tmp.z;
		}

		if(dh != 0) {
			tmp.set(camera.direction).crs(camera.up).nor().scl(speed * dh).y = 0;
			xa += tmp.x;
			za += tmp.z;
		}
		
		delta *= 20F;
		
		// Update camera delta		
		this.iDeltaX = MathHelper.interpolateLinear(delta, this.iDeltaX, dx);
		this.iDeltaY = MathHelper.interpolateLinear(delta, this.iDeltaY, dy);

	}

	protected void hitWall(float xa, float ya, float za) {}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

}

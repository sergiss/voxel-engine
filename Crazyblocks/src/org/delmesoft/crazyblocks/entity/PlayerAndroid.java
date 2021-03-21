package org.delmesoft.crazyblocks.entity;

import org.delmesoft.crazyblocks.graphics.Renderer;
import org.delmesoft.crazyblocks.screen.GameScreen;
import org.delmesoft.crazyblocks.world.blocks.Blocks;
import org.delmesoft.crazyblocks.world.blocks.utils.BlockIntersector;
import org.delmesoft.crazyblocks.world.blocks.utils.generators.FluidPoint;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class PlayerAndroid extends Player {

	public static final int MODE_DEFAULT = 0;
	public static final int MODE_VIEW = 1;
	public static final int MODE_REMOVE = 2;

	private int screenX, screenY;
	private boolean add;

	private long lastTouch = Long.MAX_VALUE;

	private int mode;

	private int sensitivity = 5;
	private int touchId = -1;

	public PlayerAndroid(float width, float height, float depth, GameScreen screen) {
		super(width, height, depth, screen);
	}

	@Override
	public void render(Renderer renderer) {
		super.update();

		Camera camera = super.screen.camera;
		Vector3 cameraPosition = camera.position;

		if(touchId > -1 && mode != MODE_VIEW) {

			long time = System.currentTimeMillis();

			if(time - lastTouch > 500) {

				if(mode == MODE_DEFAULT) {
					mode = MODE_REMOVE;
				}

				if (mode == MODE_REMOVE) { // REMOVE
					lastTouch = System.currentTimeMillis();
					Vector3 d = camera.unproject(tmp.set(screenX, screenY, 1f)).sub(cameraPosition).nor();
					rayBlock = world.ray(cameraPosition.x, cameraPosition.y, cameraPosition.z, d.x, d.y, d.z, RAY_LEN, rayPoint);
					if (rayBlock.renderMode > 0) {
						world.chunkGenerator.removeBlocksAt(true, rayPoint.x, rayPoint.y, rayPoint.z);
						/*int i = 0;
						int[] points = new int[8 * 8 * 8 * 3];
						for(int x = -4; x < 4; x++) {
							for(int z = -4; z <4; z++) {
								for(int y = -4; y < 4; y++) {
									points[i++] = x + rayPoint.x;
									points[i++] = y + rayPoint.y;
									points[i++] = z + rayPoint.z;
								}
							}
						}

						world.chunkGenerator.removeBlocksAt(points);*/
					}
				}

			}

		} else if (add) { // ADD
			add = false;

			Vector3 d = camera.unproject(tmp.set(screenX, screenY, 1f)).sub(cameraPosition).nor();
			rayBlock = world.ray(cameraPosition.x, cameraPosition.y, cameraPosition.z, d.x, d.y, d.z, RAY_LEN, rayPoint);
			if (rayBlock.renderMode > 0) {

				Blocks.Side side = BlockIntersector.intersectRayBounds(cameraPosition.x, cameraPosition.y, cameraPosition.z, d.x, d.y, d.z, rayPoint.x, rayPoint.y, rayPoint.z);
				if (side != null) {
					int x = rayPoint.x + side.x;
					int y = rayPoint.y + side.y;
					int z = rayPoint.z + side.z;
					if (intersects(x, y, z, 1F, 1F, 1F) == false) {
						//world.chunkGenerator.addBlocksAt(block, x, y, z);
						world.addFluidPoint(new FluidPoint(x, y, z, Blocks.WATER.id, (byte) 8, world));
					}
				}

			}

		}

	}

	@Override
	public void tick(float delta) {

		if(touchId > -1 && mode == MODE_DEFAULT && (Math.abs(dx) + Math.abs(dy) > sensitivity)) {
			mode = MODE_VIEW;
		}

		super.tick(delta);

	}

	@Override
	public boolean touchDown(int x, int y, int id) {
		if(touchId == -1) {
			mode = MODE_DEFAULT;
			lx = x;
			ly = y;
			screenX = x;
			screenY = y;
			lastTouch = System.currentTimeMillis();
			touchId = id;
			return true;
		}
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int id) {
		if(touchId == id) {
			if (mode == MODE_DEFAULT) {
				add = true;
			}
			lx = x;
			ly = y;
			screenX = x;
			screenY = y;
			touchId = -1;
			return true;
		}
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int id) {
		if(touchId == id) {
			dx = MathUtils.clamp(lx - x, -16, 16);
			lx = x;
			dy = MathUtils.clamp(ly - y, -16, 16);
			ly = y;
			screenX = x;
			screenY = y;
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int x, int y) {
		return false;
	}

	@Override
	public void render(Batch batch) {
		// Ignore
	}

}

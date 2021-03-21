package org.delmesoft.crazyblocks.entity;

import org.delmesoft.crazyblocks.graphics.Renderer;
import org.delmesoft.crazyblocks.input.InputHandler;
import org.delmesoft.crazyblocks.math.Vec3i;
import org.delmesoft.crazyblocks.screen.GameScreen;
import org.delmesoft.crazyblocks.world.blocks.Blocks;
import org.delmesoft.crazyblocks.world.blocks.Chunk;
import org.delmesoft.crazyblocks.world.blocks.utils.BlockIntersector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

public class PlayerDesktop extends Player {

	private ShapeRenderer shapeRenderer;
	
	private boolean remove, add;
	private long lastRise;

	public PlayerDesktop(float width, float height, float depth, GameScreen screen) {
		super(width, height, depth, screen);

		shapeRenderer = new ShapeRenderer();

		lx = Gdx.input.getX();
		ly = Gdx.input.getY();

	}

	Texture texture = new Texture(Gdx.files.internal("data/resources/tab.png"));

	@Override
	public void render(Renderer renderer) {
		super.update();
		
		Camera camera = screen.camera;
		Vector3 cameraPosition = camera.position;
		Vector3 direction = camera.direction;

		rayBlock = world.ray(cameraPosition.x, cameraPosition.y, cameraPosition.z, direction.x, direction.y, direction.z, RAY_LEN, rayPoint);

		renderer.getRenderContext().setBlending(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		final ShapeRenderer shapeRenderer = this.shapeRenderer;

		shapeRenderer.setColor(0.9F, 0.9F, 0.9F, 0.9F);
		shapeRenderer.getColor().a = 0.7F;

		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		shapeRenderer.updateMatrices();

		float hw = Gdx.graphics.getWidth()  >> 1;
		float hh = Gdx.graphics.getHeight() >> 1;

		shapeRenderer.line(hw - 10F, hh      , hw + 10F, hh      );
		shapeRenderer.line(hw      , hh - 10F, hw      , hh + 10F);

		if(rayBlock.renderMode > 0) {

			if (remove) { // REMOVE

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

			} else if(add) { // ADD
				Blocks.Side side = BlockIntersector.intersectRayBounds(cameraPosition.x, cameraPosition.y, cameraPosition.z, direction.x, direction.y, direction.z, rayPoint.x, rayPoint.y, rayPoint.z);

				if(side != null) {
					int x = rayPoint.x + side.x;
					int y = rayPoint.y + side.y;
					int z = rayPoint.z + side.z;

					if (intersects(x, y, z, 1F, 1F, 1F) == false) {
						world.addBlocksAt(block.id, x, y, z);
						//world.addFluidPoint(new FluidPoint(x, y, z, Blocks.WATER.id, (byte) 7, world));
					}
				}

				//screen.world.addLiquidSource();

			}

			// info
			//printInfo(rayPoint);

			/*shapeRenderer.setColor(0F, 0F, 0F, 1F);
			shapeRenderer.setProjectionMatrix(camera.combined);

			final float x1 = rayPoint.x - 0.005F, x2 = x1 + 1.01F;
			final float y1 = rayPoint.y - 0.005F, y2 = y1 + 1.01F;
			final float z1 = rayPoint.z - 0.005F, z2 = z1 + 1.01F;

			shapeRenderer.line(x1, y1, z1, x1, y2, z1);
			shapeRenderer.line(x2, y1, z1, x2, y2, z1);
			shapeRenderer.line(x2, y1, z2, x2, y2, z2);
			shapeRenderer.line(x1, y1, z2, x1, y2, z2);

			shapeRenderer.line(x1, y1, z1, x2, y1, z1);
			shapeRenderer.line(x1, y2, z1, x2, y2, z1);

			shapeRenderer.line(x1, y1, z1, x1, y1, z2);
			shapeRenderer.line(x1, y2, z1, x1, y2, z2);

			shapeRenderer.line(x1, y1, z2, x2, y1, z2);
			shapeRenderer.line(x1, y2, z2, x2, y2, z2);

			shapeRenderer.line(x2, y1, z1, x2, y1, z2);
			shapeRenderer.line(x2, y2, z1, x2, y2, z2);*/

		}
		add = remove = false;
		shapeRenderer.end();

	}

	@Override
	public boolean touchDown(int x, int y, int id) {
		switch (id) {
		case ACTION1:
			remove = true;
			break;
		case ACTION2:
			add = true;
			break;
		}
		mouseMoved(x, y);
		return true;
	}

	@Override
	public boolean touchUp(int x, int y, int id) {
		mouseMoved(x, y);
		return true;
	}

	@Override
	public boolean touchDragged(int x, int y, int id) {
		mouseMoved(x, y);
		return true;
	}

	@Override
	public boolean mouseMoved(int x, int y) {
		dx = lx - x; lx = x;
		dy = ly - y; ly = y;
		return true;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
		case UP:
			dv = 1;
			break;
		case DOWN:
			dv = -1;
			break;
		case RIGHT:
			dh = 1;
			break;
		case LEFT:
			dh = -1;
			break;
		case RISE:
			rise = true;
			long time = System.currentTimeMillis();
			if(time - lastRise < InputHandler.DOUBLE_CLICK_TIME) {
				flying = !flying;
			}
			lastRise = time;
			break;
		case DESCENT:
			descent = true;
			break;
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {
		case UP:
			if (dv == 1)
				dv = 0;
			break;
		case DOWN:
			if (dv == -1)
				dv = 0;
			break;
		case RIGHT:
			if (dh == 1)
				dh = 0;
			break;
		case LEFT:
			if (dh == -1)
				dh = 0;
			break;
		case RISE:
			rise = false;
			break;
		case DESCENT:
			descent = false;
			break;
		}
		return true;
	}

	@Override
	public void render(Batch batch) {
		// Ignore
	}

	Vec3i tmpPoint = new Vec3i();

	private void printInfo(Vec3i rayPoint) {

		if(rayPoint.equals(tmpPoint) == false) {

			tmpPoint.set(rayPoint);

			Vector3 cameraPosition = screen.camera.position;
			Vector3 direction = screen.camera.direction;

			Blocks.Side side = BlockIntersector.intersectRayBounds(cameraPosition.x, cameraPosition.y, cameraPosition.z, direction.x, direction.y, direction.z, rayPoint.x, rayPoint.y, rayPoint.z);

			if (side != null) {

				Chunk chunk = world.getChunkAbsolute(rayPoint.x, rayPoint.z);

				System.out.println("Ray point: " + rayPoint);
				System.out.println("Ray Light data Sky: "   + chunk.chunkData.getSkyLight  (rayPoint.x - chunk.worldX, rayPoint.y, rayPoint.z - chunk.worldZ));
				System.out.println("Ray Light data Block: " + chunk.chunkData.getBlockLight(rayPoint.x - chunk.worldX, rayPoint.y, rayPoint.z - chunk.worldZ));
				System.out.println("Ray Chunk: " + chunk.toString());

				int x = rayPoint.x + side.x;
				int y = rayPoint.y + side.y;
				int z = rayPoint.z + side.z;

				chunk = world.getChunkAbsolute(x, z);

				System.out.println("Side Point: " + x + ", " + y + ", " + z);
				System.out.println("Side Light data Sky: "   + chunk.chunkData.getSkyLight  (x - chunk.worldX, y, z - chunk.worldZ));
				System.out.println("Side Light data Block: " + chunk.chunkData.getBlockLight(x - chunk.worldX, y, z - chunk.worldZ));
				System.out.println("Side Chunk: " + chunk.toString());
				System.out.println();

			}

		}

	}

}

package org.delmesoft.crazyblocks.screen;

import org.delmesoft.crazyblocks.CrazyBlocks;
import org.delmesoft.crazyblocks.entity.Player;
import org.delmesoft.crazyblocks.entity.PlayerAndroid;
import org.delmesoft.crazyblocks.entity.PlayerDesktop;
import org.delmesoft.crazyblocks.graphics.g2d.layer.MovementLayer;
import org.delmesoft.crazyblocks.math.MathHelper;
import org.delmesoft.crazyblocks.utils.DebugHelper;
import org.delmesoft.crazyblocks.world.Settings;
import org.delmesoft.crazyblocks.world.World;
import org.delmesoft.crazyblocks.world.blocks.Blocks;
import org.delmesoft.crazyblocks.world.blocks.Plot;
import org.delmesoft.crazyblocks.world.blocks.utils.WorldIO;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GameScreen extends AbstractScreen {
	
	public static GameScreen instance;
	
	public World world;

	public Player player;

	public PerspectiveCamera camera;
		
	@Override
	public void create() {
		super.create();

		instance = this;

		createCamera();

		loadSettings();
		
		createWorld();

	}

	private void loadSettings() {
		
		if(Settings.android) {
			player = new PlayerAndroid(0.4F, 1.8F, 0.4F, this);
		} else {
			player = new PlayerDesktop(0.4F, 1.8F, 0.4F, this);
		}
				
		WorldIO.getInstance().loadWorldData(Settings.levelName);

		Blocks.setBlocks("data/resources/blocks.xml", fileManager);
		camera.far = Settings.chunkVisibility << Plot.BIT_OFFSET;

		inputHandler.addInputLayer(new MovementLayer(player, new TextureRegion(new Texture(Gdx.files.internal("data/resources/tab.png")))));

		if(Settings.android == false) {
			inputHandler.mapKey(Keys.W, Player.UP);
			inputHandler.mapKey(Keys.S, Player.DOWN);
			inputHandler.mapKey(Keys.A, Player.LEFT);
			inputHandler.mapKey(Keys.D, Player.RIGHT);
			inputHandler.mapKey(Keys.SHIFT_LEFT, Player.DESCENT);
			inputHandler.mapKey(Keys.SPACE, Player.RISE);

			inputHandler.mapPointer(Buttons.LEFT,  Player.ACTION1);
			inputHandler.mapPointer(Buttons.RIGHT, Player.ACTION2);
		} else {
			inputHandler.mapPointer(0, 0);
			inputHandler.mapPointer(1, 1);
			inputHandler.mapPointer(2, 2);
			inputHandler.mapPointer(3, 3);
		}

		inputHandler.addInputLayer(player);
		
	}
	
	private void createCamera() {
		camera = new PerspectiveCamera();
		camera.near = 0.10f;

	}

	private void createWorld() {
		world = new World(this);
		world.getEnvironment().setRotation(Settings.worldRotation);
		world.addEntity(player);
	}

	@Override
	public void render() {
		
		world.render(camera);

		//spriteBatch.enableBlending();
		//spriteBatch.setBlendFunction(GL20.GL_DST_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);

		/*spriteBatch.begin();

		for(Layer layer : inputHandler.getLayers()) {
			layer.render(spriteBatch);
		}
		spriteBatch.end();*/

		DebugHelper.drawText(String.format("Heap Use: %.2f MB", MathHelper.bytesToMagaBytes(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())), 15, 5);
		DebugHelper.drawText(String.format("Heap Size: %.2f MB", MathHelper.bytesToMagaBytes(Runtime.getRuntime().totalMemory())), 15, 20);
		DebugHelper.drawText("Task count: " + world.chunkGenerator.getThreadPool().getPendingTasks(), 15, 35);
		DebugHelper.drawText("FPS: " + Gdx.graphics.getFramesPerSecond(), 15, 50);
		DebugHelper.drawText("--------------------", 15, 65);
		DebugHelper.drawText(String.format("| CrazyBlocks v%s |", CrazyBlocks.VERSION), 15, 80);
		DebugHelper.drawText("--------------------", 15, 95);
		
	}

	@Override
	public void update(float delta) {
	//	if(!paused) {
			world.update(delta);
			camera.update(true);
	//	}
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportWidth =  width;
		camera.viewportHeight = height;
		
		camera.update(true);
	}

	@Override
	public void dispose() {
		super.dispose();
		world.dispose();
	}

}

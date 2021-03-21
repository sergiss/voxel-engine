package org.delmesoft.crazyblocks;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;

import org.delmesoft.crazyblocks.screen.AbstractScreen;
import org.delmesoft.crazyblocks.screen.GameScreen;

public class CrazyBlocks implements ApplicationListener {

	public static final String VERSION = "0.2.5";
	
	private static CrazyBlocks instance;

	public static CrazyBlocks getInstance() {

		if(instance == null) {
			instance = new CrazyBlocks();
		}

		return instance;
	}

	public AbstractScreen screen;
	
	private CrazyBlocks() {}

	@Override
	public void create() {

		System.out.printf("CrazyBlocks v%s\n", VERSION);
				
		screen = new GameScreen();
		screen.create();	
	}

	@Override
	public void render() {

		final Graphics graphics = Gdx.graphics;
		
		graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		screen.update(graphics.getDeltaTime());

		screen.render();

		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			Gdx.app.exit();
		}

	}

	@Override
	public void resize(int width, int height) {
		screen.resize(width, height);
	}

	@Override
	public void pause() {
		
		screen.setPaused(true);
		
		if(screen instanceof GameScreen) { // TODO 
			GameScreen gScreen = (GameScreen) screen;
			gScreen.world.save();
		}		
		
	}

	@Override
	public void resume() {
		screen.setPaused(false);
	}

	@Override
	public void dispose() {
		screen.dispose();
	}

}

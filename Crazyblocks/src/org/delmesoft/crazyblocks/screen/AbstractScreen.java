package org.delmesoft.crazyblocks.screen;

import org.delmesoft.crazyblocks.assets.FileManager;
import org.delmesoft.crazyblocks.graphics.g3d.FastMesh;
import org.delmesoft.crazyblocks.input.InputHandler;
import org.delmesoft.crazyblocks.input.InputHandlerDefault;
import org.delmesoft.crazyblocks.world.Settings;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;

public abstract class AbstractScreen implements Disposable {

	public FileManager fileManager;

	public InputHandler inputHandler;
	
	public boolean paused;
	
	public void create() {
		
		FastMesh.locationMap.clear();
		
		Gdx.input.setCursorCatched(true);
		fileManager = new FileManager();
		
		inputHandler = new InputHandlerDefault();
		
		if(Gdx.app.getType() == ApplicationType.Android) {
			Settings.android = true;
		}		

		Gdx.input.setInputProcessor(inputHandler);

	}
	
	public abstract void render();
	
	public abstract void update(float delta);
	
	public abstract void resize(int width, int height);
	
	public void setPaused(boolean paused){
		this.paused = paused;
	}

    @Override
	public void dispose() {
		Gdx.input.setCursorCatched(false);
		fileManager.dispose();
	}
	
}

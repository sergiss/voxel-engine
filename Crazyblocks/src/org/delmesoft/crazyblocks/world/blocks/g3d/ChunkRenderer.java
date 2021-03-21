package org.delmesoft.crazyblocks.world.blocks.g3d;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

import org.delmesoft.crazyblocks.world.blocks.Plot;

public interface ChunkRenderer extends Disposable {
	
	void render(Plot plot);

	void begin(float sunLight, Camera camera);
	
	void setTexture(Texture texture);
	
	void end();

}

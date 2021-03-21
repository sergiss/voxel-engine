package org.delmesoft.crazyblocks.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class TextureLoader implements FileLoader {

	@Override
	public Object load(String path, FileManager fileManager) {
		return new Texture(Gdx.files.internal(path), true);
	}

}

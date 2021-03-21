package org.delmesoft.crazyblocks.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class TextureAtlasLoader implements FileLoader {

	@Override
	public Object load(String path, FileManager fileManager) {
		return new TextureAtlas(Gdx.files.internal(path));
	}

}

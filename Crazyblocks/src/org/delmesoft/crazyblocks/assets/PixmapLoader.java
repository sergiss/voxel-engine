package org.delmesoft.crazyblocks.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;

public class PixmapLoader implements FileLoader {

	@Override
	public Object load(String path, FileManager fileManager) {
		return new Pixmap(Gdx.files.internal(path));
	}

}

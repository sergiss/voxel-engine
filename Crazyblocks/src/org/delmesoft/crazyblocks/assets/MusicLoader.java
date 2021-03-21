package org.delmesoft.crazyblocks.assets;

import com.badlogic.gdx.Gdx;

public class MusicLoader implements FileLoader {

	@Override
	public Object load(String path, FileManager fileManager) {
		return Gdx.audio.newMusic(Gdx.files.internal(path));
	}

}

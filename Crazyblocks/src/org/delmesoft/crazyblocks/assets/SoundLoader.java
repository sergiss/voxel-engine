package org.delmesoft.crazyblocks.assets;

import com.badlogic.gdx.Gdx;

public class SoundLoader implements FileLoader{

	@Override
	public Object load(String path, FileManager fileManager) {
		return Gdx.audio.newSound(Gdx.files.internal(path));
	}

}

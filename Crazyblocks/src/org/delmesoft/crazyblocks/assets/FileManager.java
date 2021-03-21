package org.delmesoft.crazyblocks.assets;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class FileManager implements Disposable {
	
	private final HashMap<String, Object> files;
	private final HashMap<Class<?>, FileLoader> loaders;

	public FileManager() {
		
		files   = new HashMap<String, Object>();
		loaders = new HashMap<Class<?>, FileLoader>();
		
		loaders.put(Pixmap.class, new PixmapLoader());
		loaders.put(Texture.class, new TextureLoader());
		loaders.put(TextureAtlas.class, new TextureAtlasLoader());
		loaders.put(Music.class, new MusicLoader());
		loaders.put(Sound.class, new SoundLoader());
				
	}
		
	@SuppressWarnings("unchecked")
	public <T> T get(String path, Class<T> type) {
		
		T file = (T) files.get(path);
		
		if(file == null) {
			
			final FileLoader loader = loaders.get(type);
			
			if(loader != null) {
				file = (T) loader.load(path, this);
				files.put(path, file);
			}
			
		}
		
		return file;
	}
	
	@SuppressWarnings("unchecked")
	public <T> void getAll(Class<T> type, Array<T> store) {

		final Collection<Object> values = files.values();

		for (final Object object : values) {
			if (type.isAssignableFrom(object.getClass())) {
				store.add((T) object);
			}
		}

	}
	
	public boolean isLoaded(String path) {
		return files.containsKey(path);
	}
	
	public void unload(String fileName) {

		final Object obj = files.remove(fileName);
		if (obj instanceof Disposable) {
			((Disposable) obj).dispose();
		}

	}
		
	public int size() {
		return files.size();
	}

	@Override
	public void dispose() {

		final Iterator<Object> it = files.values().iterator();

		while (it.hasNext()) {
			final Object obj = it.next();
			
			if (obj instanceof Disposable) {
				((Disposable) obj).dispose();
			}
			
			it.remove();
		}

		loaders.clear();
	}		
	
}

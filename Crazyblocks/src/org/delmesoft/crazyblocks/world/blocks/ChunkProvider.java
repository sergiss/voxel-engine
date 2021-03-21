package org.delmesoft.crazyblocks.world.blocks;

import com.badlogic.gdx.utils.Disposable;

import org.delmesoft.crazyblocks.utils.datastructure.ChunkMap;
import org.delmesoft.crazyblocks.utils.datastructure.ChunkMap.Iterator;

public interface ChunkProvider extends Disposable {
	
	Chunk getChunkAbsolute(int worldX, int worldZ);
	
	Chunk getChunkRelative(int localX, int localZ);
	
	void removeChunk(Chunk chunk);

	ChunkMap getChunkMap();
	
	void save();

	void load(Chunk chunk);

	void iterate(Iterator it);
		
}

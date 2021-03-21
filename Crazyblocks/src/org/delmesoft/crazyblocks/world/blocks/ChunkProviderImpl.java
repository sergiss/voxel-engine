package org.delmesoft.crazyblocks.world.blocks;

import org.delmesoft.crazyblocks.utils.datastructure.ChunkMap;
import org.delmesoft.crazyblocks.utils.datastructure.ChunkMap.Iterator;
import org.delmesoft.crazyblocks.utils.datastructure.Pool;
import org.delmesoft.crazyblocks.world.World;
import org.delmesoft.crazyblocks.world.blocks.utils.WorldIO;
import org.delmesoft.crazyblocks.world.blocks.utils.generators.ChunkGenerator;
import org.delmesoft.crazyblocks.world.blocks.utils.generators.ChunkGeneratorImpl;

import java.util.concurrent.locks.ReentrantLock;

import static org.delmesoft.crazyblocks.world.blocks.Plot.BIT_OFFSET;

public class ChunkProviderImpl implements ChunkProvider {
	
	private final ChunkMap map;
	private final ReentrantLock reentrantLock;
	private final Pool<Chunk> pool;

	private final WorldIO worldIO;

	public ChunkProviderImpl() {
		map   = new ChunkMap();
		reentrantLock = new ReentrantLock(true);
		pool = new Pool<Chunk>() {
			@Override
			protected Chunk newObject() {
				return new Chunk(ChunkProviderImpl.this);
			}
		};

		worldIO = WorldIO.getInstance();
	}

	@Override
	public Chunk getChunkAbsolute(int x, int z) {
		return getChunkRelative(x >> BIT_OFFSET, z >> BIT_OFFSET);
	}

	@Override
	public Chunk getChunkRelative(int x, int z) {

		final int hashCode = Chunk.hashCode(x, z);
		try {
			reentrantLock.lock();
			Chunk chunk = map.get(hashCode);
			if (chunk == null) {
				chunk = pool.obtain();
				chunk.initialize(x, z);
				map.put(hashCode, chunk);
			}
			return chunk;
		} finally {
			reentrantLock.unlock();
		}
	}

	@Override
	public void removeChunk(final Chunk chunk) {
				
		try {
			reentrantLock.lock();
			worldIO.saveChangedData(chunk);
			map.removeKey(chunk.hashCode());
			chunk.clear();
			pool.free(chunk);
		} finally {
			reentrantLock.unlock();
		}

	}
	
	public void load(Chunk chunk) {
		worldIO.loadChangedData(chunk);
	}
	
	@Override
	public void save() {

		worldIO.saveWorldData();
		
		Iterator it = new Iterator() {
			@Override
			public void next(Chunk chunk) {
				worldIO.saveChangedData(chunk);
			}
		};
		
		iterate(it);
				
	}
	
	@Override
	public void iterate(Iterator it) {
		try {
			reentrantLock.lock();
			map.iterate(it);
		} finally {
			reentrantLock.unlock();
		}
	}

	@Override
	public void dispose() {
		iterate(new Iterator() {
			@Override
			public void next(Chunk chunk) {
				chunk.dispose();
			}
		});
		map.clear();
	}

	@Override
	public ChunkMap getChunkMap() {
		return map;
	}

}
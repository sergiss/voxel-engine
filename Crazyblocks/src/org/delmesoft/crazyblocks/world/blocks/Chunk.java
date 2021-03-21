package org.delmesoft.crazyblocks.world.blocks;

import static org.delmesoft.crazyblocks.world.blocks.Plot.BIT_OFFSET;
import static org.delmesoft.crazyblocks.world.blocks.Plot.SIZE;

import java.util.concurrent.locks.ReentrantLock;

import org.delmesoft.crazyblocks.utils.datastructure.HashMap;
import org.delmesoft.crazyblocks.world.blocks.utils.ChunkData;

import com.badlogic.gdx.utils.Disposable;
public class Chunk implements Disposable {
		
	public static final int VERTICAL_BIT_OFFSET = 7;
	
	public static final int VERTICAL_SIZE = 1 << VERTICAL_BIT_OFFSET; // (1 << 7) = 128;
	
	public static final int PLOT_COUNT = VERTICAL_SIZE >> Plot.BIT_OFFSET;

	public static final int HD = VERTICAL_SIZE * SIZE;
	
	public static final int HD_BIT_OFFSET = VERTICAL_BIT_OFFSET + Plot.BIT_OFFSET;
	
	public static final int BLOCK_COUNT = SIZE << HD_BIT_OFFSET;

	/* -------------------------------------------------------------------------- */

	public final ReentrantLock lock = new ReentrantLock(true);
	
	public HashMap changeMap;

	public ChunkProvider chunkProvider;
	
	public final ChunkData chunkData;
	
	public final Chunk[] neighbors;
		
	// Local position
	public int localX, localZ;
	
	// World position
	public int worldX, worldZ;
	
	public final Plot[] plots;

    public final  boolean[] state; // iteration, data, update, light, spread, generated

	public int maxHeight;

	public Chunk(ChunkProvider chunkProvider) {
		
		this.chunkProvider = chunkProvider;
		
		chunkData = new ChunkData(this);
		
		neighbors = new Chunk[4];
		
		final Plot[] plots = new Plot[PLOT_COUNT];

		for(int i = 0, n = PLOT_COUNT; i < n; i++) {
			plots[i] = new Plot(i, this);
		}
		
		this.plots = plots;
		
		changeMap = new HashMap();

        state = new boolean[6];

	}

	void initialize(int localX, int localZ) {
				
		this.localX = localX;
		this.localZ = localZ;
		
		worldX = localX << BIT_OFFSET;
		worldZ = localZ << BIT_OFFSET;

	}

	public boolean isReady() {
		for(int x = -1; x < 2; ++x) {
			for(int z = -1; z < 2; ++z) {
				if(getChunkRelative(localX + x, localZ + z).state[4] == false) {
					return false;
				}
			}
		}
		return true;
	}

	public Chunk getChunkRelative(int x, int z) {
				
		if(x != localX) {
						
			Chunk chunk;
			
			if (x > localX) { // right
				
				chunk = neighbors[0];
				if(chunk == null) {
					chunk = chunkProvider.getChunkRelative(localX + 1, localZ);
					neighbors[0] = chunk;
					chunk.neighbors[1] = this;
				}
			
			} else { // left

				chunk = neighbors[1];
				if(chunk == null) {
					chunk = chunkProvider.getChunkRelative(localX - 1, localZ);
					neighbors[1] = chunk;
					chunk.neighbors[0] = this;
				}
				
			}
			
			return chunk.getChunkRelative(x, z);
			
		} else if(z != localZ) {
			
			Chunk chunk;
						
			if (z > localZ) { // front

				chunk = neighbors[2];
				if(chunk == null) {
					chunk = chunkProvider.getChunkRelative(localX, localZ + 1);
					neighbors[2] = chunk;
					chunk.neighbors[3] = this;
				}
				
			} else { // back

				chunk = neighbors[3];
				if(chunk == null) {
					chunk = chunkProvider.getChunkRelative(localX, localZ - 1);
					neighbors[3] = chunk;
					chunk.neighbors[2] = this;
				}
				
			}
			
			return chunk.getChunkRelative(x, z);
		}
		
		return this;
	}

	public Chunk getChunkAbsolute(int x, int z) {
		return getChunkRelative(x >> BIT_OFFSET, z >> BIT_OFFSET);
	}

	public byte getBlockTypeAbsolute(int x, int y, int z) {
		Chunk chunk = getChunkRelative(x >> BIT_OFFSET, z >> BIT_OFFSET);
		return chunk.chunkData.getBlockType(x - chunk.worldX, y, z - chunk.worldZ);
	}

	public void setBlockTypeAbsolute(int x, int y, int z, byte type) {
		Chunk chunk = getChunkRelative(x >> BIT_OFFSET, z >> BIT_OFFSET);
		chunk.chunkData.setBlockType(x - chunk.worldX, y, z - chunk.worldZ, type);
	}

	public short getRawTypeAbsolute(int x, int y, int z) {
		Chunk chunk = getChunkRelative(x >> BIT_OFFSET, z >> BIT_OFFSET);
		return chunk.chunkData.getRawType(x - chunk.worldX, y, z - chunk.worldZ);
	}

	public void setRawTypeAbsolute(int x, int y, int z, short rawType) {
		Chunk chunk = getChunkRelative(x >> BIT_OFFSET, z >> BIT_OFFSET);
		chunk.chunkData.setRawType(x - chunk.worldX, y, z - chunk.worldZ, rawType);
	}

	@Override
	public String toString() {
		return localX + ", " + localZ;
	}

	@Override
	public int hashCode() {
		return hashCode(localX, localZ);
	}

	public static int hashCode(int a, int b) {
		return (a << 16) ^ b;
	}

	public void updateHeights() { // TODO : optimize

		try {

			int maxHeight = 1;
			int i1, i2, i3, y;

			lock.lock();

			for (int x = 0; x < SIZE; ++x) {
				i1 = x << HD_BIT_OFFSET;
				for (int z = 0; z < SIZE; ++z) {
					i2 = i1 + z;
					for(y = VERTICAL_SIZE - 1, i3 = y << BIT_OFFSET; y > 0; --y, i3 -= SIZE) {
						if((chunkData.getBlockType(i2 + i3) & 0xFF) != Blocks.AIR.id) {
							chunkData.setHeightAt(i2, (byte) y);
							if(y > maxHeight) {
								maxHeight = y;
							}
							break;
						}
					}
				}
			}

			this.maxHeight = maxHeight;

		} finally {
			lock.unlock();
		}

	}

	public void clear() {

        for(int i = 0; i < 4; ++i) {
            if(neighbors[i] != null) {
                neighbors[i].neighbors[i + (1 - 2 * (i % 2))] = null;
                neighbors[i] = null;
            }
        }

        for(int i = 0; i < state.length; i++) {
            state[i] = false;
        }

        changeMap.clear();
        chunkData.clear();

        final Plot[] plots = this.plots;
        for(int i = 0, n = PLOT_COUNT; i < n; ++i) {
            plots[i].clear();
        }

    }

	@Override
	public void dispose() {
		final Plot[] plots = this.plots;
		for(int i = 0, n = PLOT_COUNT; i < n; ++i) {
			plots[i].dispose();
		}
	}

}

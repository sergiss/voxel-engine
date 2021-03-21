package org.delmesoft.crazyblocks.world.blocks.utils;

import org.delmesoft.crazyblocks.world.blocks.Chunk;
import org.delmesoft.crazyblocks.world.blocks.Plot;

public class DataBuffer {
	
	byte[]   data;
	byte[][] light;
	
	int w, h, hd;
	
	int wx, wz;
	
	public DataBuffer() {
		
		w  = Plot.SIZE + 2;
		h  = (Chunk.VERTICAL_SIZE + 2);
		hd = w * h;
		
		data = new byte[w * hd];
		light = new byte[data.length][2];
		
	}
	
	public void buffer(Chunk chunk) {
		
		int wx = chunk.worldX - 1, 
			wz = chunk.worldZ - 1;
		
		int i0, i1;
		
		final ChunkData chunkData = chunk.chunkData;
		ChunkData cData;
		
		byte bType;
		
		for(int x0 = 0, x1 = wx, n = w; x0 < n; x0++, x1++) {
			for(int z0 = 0, z1 = wz; z0 < n; z0++, z1++) {
				
				i0 = x0 * hd + z0;
				
				if(x0 == 0 || x0 == w - 1 || z0 == 0 || z0 == w -1) { // Neighboring chunk
					
					chunk = chunk.getChunkRelative(x1 >> Plot.BIT_OFFSET, z1 >> Plot.BIT_OFFSET);
					cData = chunk.chunkData;
					
					i1 = (x1 - chunk.worldX) * Chunk.HD + (z1 - chunk.worldZ);
					
				} else { // Current chunk
					
					cData = chunkData;
					i1 = (x0 - 1) * Chunk.HD + (z0 - 1);
					
				}				
				
				for(int y0 = 0; y0 < h; y0++) {
					
					if(y0 > 0 && y0 < Chunk.VERTICAL_SIZE) {	
						
						bType = cData.getBlockType(i1 + ((y0 - 1) << Plot.BIT_OFFSET));
						
						
						data[i0 + y0 * w] = bType;
					}
					
				}
				
				
			}
		}						
		
		this.wx = wx;
		this.wz = wz;
		
	}
	
	public void clear() {
				
		final byte[] data = this.data;
		
		for(int i = 0, n = data.length; i < n; i++) {
			data[i] = 0;
		}
		
	}

}

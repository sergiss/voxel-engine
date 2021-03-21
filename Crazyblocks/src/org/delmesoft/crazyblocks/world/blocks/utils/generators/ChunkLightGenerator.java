package org.delmesoft.crazyblocks.world.blocks.utils.generators;

import org.delmesoft.crazyblocks.world.blocks.Chunk;
import org.delmesoft.crazyblocks.world.blocks.utils.ChunkData;
import org.delmesoft.crazyblocks.world.blocks.utils.DataPointArray;

public interface ChunkLightGenerator {

	// ADD LIGHT
		
	void generateSunLight(Chunk chunk);

	void generateSunLightColumn(ChunkData chunkData, int start, int end, int columnIndex);
	
	void spreadSkyLight(Chunk chunk);

    void spreadSkyLightColumn(Chunk chunk, int x, int y, int z, int i, int j, byte light, DataPointArray stack);

	void spreadSkyLight(Chunk chunk, int x, int y, int z, byte light, DataPointArray stack);

	void spreadBlockLight(Chunk chunk, int x, int y, int z, byte light, DataPointArray stack);

	// REMOVE LIGHT

	void clearSunLightColumn(ChunkData chunkData, int start, int end, int columnIndex);

	void unspreadSkyLightColumn(Chunk chunk, int x, int y, int z, int i, int j, byte light, DataPointArray stack, DataPointArray endPoints);

	void unspreadSkyLight(Chunk chunk, int x, int y, int z, byte light, DataPointArray stack, DataPointArray endPoints);

    void unspreadBlockLight(Chunk chunk, int x, int y, int z, byte light, DataPointArray stack, DataPointArray endPoints);
}

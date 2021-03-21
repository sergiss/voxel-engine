package org.delmesoft.crazyblocks.world.blocks.utils.generators;

import com.badlogic.gdx.math.RandomXS128;

import org.delmesoft.crazyblocks.world.Settings;
import org.delmesoft.crazyblocks.world.blocks.Blocks;
import org.delmesoft.crazyblocks.world.blocks.Chunk;

import java.util.Random;

import static org.delmesoft.crazyblocks.world.blocks.Chunk.HD;
import static org.delmesoft.crazyblocks.world.blocks.Chunk.VERTICAL_SIZE;
import static org.delmesoft.crazyblocks.world.blocks.Plot.BIT_OFFSET;
import static org.delmesoft.crazyblocks.world.blocks.Plot.SIZE;

public class ChunkDataGeneratorImpl implements ChunkDataGenerator {

	private TerrainManager terrainManager;
	
	public ChunkDataGeneratorImpl() {
		terrainManager = new TerrainManager(Settings.seed);
	}

	@Override
	public void generate(Chunk chunk) {

		try {
			
			chunk.lock.lock();
			
			if(chunk.state[1]) {
				return;
			}
			
			int height;
			int index;
			
			int minX = chunk.worldX;
			int maxX = minX + SIZE;
			int minZ = chunk.worldZ;
			int maxZ = minZ + SIZE;
			
			Random random = new RandomXS128();
			random.setSeed((long) (terrainManager.getNoiseAt(minX, minZ) * 1000F));
			Biome biome;
			for (int x = 0, x1 = minX; x1 < maxX; ++x, ++x1) {
				index = x * HD;
				for (int z = 0, z1 = minZ; z1 < maxZ; ++z, ++z1) {
					height = terrainManager.getHeightAt(x1, z1);

					if(height >= TerrainManager.WATER_LEVEL) {
						//chunk.chunkData.setHeightAt(index + z, (byte) height);
						biome = terrainManager.getBiomeAt(x1, z1);
						biome.generateVegetation(x1, height + 1, z1, index + z + ((height + 1) << BIT_OFFSET), chunk, random);
						chunk.chunkData.setBlockType(index + (height << BIT_OFFSET) + z, biome.getBlockType(height));
					} else {
						//chunk.chunkData.setHeightAt(index + z, (byte) TerrainManager.WATER_LEVEL);
						for(int y = height; y <= TerrainManager.WATER_LEVEL; y++) {
							chunk.chunkData.setRawType(index + (y << BIT_OFFSET) + z, Blocks.WATER.id);
						}
					}
					// Strats
					float dirtThickness = terrainManager.getNoiseAt(x, z) * 6F - 4F;
					int stoneTransition = (int) (height + dirtThickness);
					for(int y = height - 1; y > stoneTransition; --y) {
						chunk.chunkData.setBlockType(index + (y << BIT_OFFSET) + z, Blocks.DIRT.id);
					}
					if(stoneTransition < height)
					for(int y = stoneTransition; y > 1; --y) {
						if(y <= TerrainManager.WATER_LEVEL || terrainManager.getNoiseAt(x1, y, z1) > -0.9F ) {
							chunk.chunkData.setBlockType(index + (y << BIT_OFFSET) + z, Blocks.STONE.id);
						}
					}
					
				}
			}

			chunk.state[1] = true;

		} finally {
			chunk.lock.unlock();
		}
		
	}

}

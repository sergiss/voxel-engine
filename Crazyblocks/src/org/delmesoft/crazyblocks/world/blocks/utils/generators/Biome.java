package org.delmesoft.crazyblocks.world.blocks.utils.generators;

import org.delmesoft.crazyblocks.world.blocks.Blocks;
import org.delmesoft.crazyblocks.world.blocks.Chunk;
import org.delmesoft.crazyblocks.world.blocks.utils.ChunkData;

import java.util.Random;

import static org.delmesoft.crazyblocks.world.blocks.Blocks.GRASS;
import static org.delmesoft.crazyblocks.world.blocks.Blocks.SAND;

public enum Biome {
	
	DEFAULT(0.002f, 0.008f) {
		
		public byte getBlockType(float height) {

			if(height - TerrainManager.WATER_LEVEL < 2) {
				return SAND.id;
			}
			return GRASS.id;
		}

		@Override
		void generateVegetation(int rootX, int rootY, int rootZ, int index, Chunk chunk, Random rnd) {
			
			ChunkData chunkData = chunk.chunkData;
			
			if(rnd.nextFloat() < treeFrequency) {
				TreeGenerator.createOakTree(rootX, rootY, rootZ, chunk, rnd);
			} else if(rnd.nextFloat() < flowerFrequency) {
				chunkData.setBlockType(index, Blocks.FLOWER1.id);
			} else if(rnd.nextFloat() < flowerFrequency) {
				chunkData.setBlockType(index, Blocks.FLOWER2.id);
			} else if(rnd.nextFloat() < flowerFrequency) {
				chunkData.setBlockType(index, Blocks.BUSH.id);
			}
			
		}
		
	}, SEASONAL_FOREST(0.004f, 0.004f) {
		
		@Override
		byte getBlockType(float height) {
			return GRASS.id;
		}

		@Override
		void generateVegetation(int rootX, int rootY, int rootZ, int index, Chunk chunk, Random rnd) {
			ChunkData chunkData = chunk.chunkData;

			if(rnd.nextFloat() < treeFrequency) {
				TreeGenerator.createSpruceTree(rootX, rootY, rootZ, chunk, rnd);
			} else if(rnd.nextFloat() < flowerFrequency) {
				chunkData.setBlockType(index, Blocks.FLOWER1.id);
			} else if(rnd.nextFloat() < flowerFrequency) {
				chunkData.setBlockType(index, Blocks.FLOWER2.id);
			} else if(rnd.nextFloat() < flowerFrequency) {
				chunkData.setBlockType(index, Blocks.BUSH.id);
			}
		}
		
	}, TAIGA(0.005f, 0.006f) {
		
		@Override
		byte getBlockType(float height) {
			return GRASS.id;
		}

		@Override
		void generateVegetation(int rootX, int rootY, int rootZ, int index, Chunk chunk, Random rnd) {
			
			ChunkData chunkData = chunk.chunkData;
			
			if(rnd.nextFloat() < treeFrequency) {
				TreeGenerator.createSpruceTree(rootX, rootY, rootZ, chunk, rnd);
			} else if(rnd.nextFloat() < flowerFrequency) {
				chunkData.setBlockType(index, Blocks.FLOWER1.id);
			} else if(rnd.nextFloat() < flowerFrequency) {
				chunkData.setBlockType(index, Blocks.FLOWER2.id);
			} else if(rnd.nextFloat() < flowerFrequency) {
				chunkData.setBlockType(index, Blocks.BUSH.id);
			}
			
		}
	};
	
	public final float treeFrequency;
	public final float flowerFrequency;

	Biome(float treeFrequency, float flowerFrequency) {
		this.treeFrequency = treeFrequency;
		this.flowerFrequency = flowerFrequency;
	}
	
	abstract byte getBlockType(float height);
	
	abstract void generateVegetation(int rootX, int rootY, int rootZ, int index, Chunk chunk, Random rnd);

	
}

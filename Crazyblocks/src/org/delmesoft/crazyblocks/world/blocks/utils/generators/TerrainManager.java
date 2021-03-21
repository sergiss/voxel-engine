package org.delmesoft.crazyblocks.world.blocks.utils.generators;

import org.delmesoft.crazyblocks.world.blocks.utils.noise.ImprovedNoise;
import org.delmesoft.crazyblocks.world.blocks.utils.noise.PerlinNoise;

import java.util.Random;

public class TerrainManager {
	
	public static final int WATER_LEVEL = 64;
	
	public PerlinNoise perlinNoise1;
	public PerlinNoise perlinNoise2;
	
	float rainScale = 0.0005f; // humidity
	float tempScale = 0.00025f; // temperature
	
	float waterLevel = 63f;
	
	public Random random;

	public TerrainManager(long seed) {
	
		System.out.println(String.format("Seed: %d", seed));

		this.perlinNoise1 = new ImprovedNoise(seed);
		this.perlinNoise2 = new ImprovedNoise(seed - 1);
				
		random = new Random(seed);
				
	}

	public double getHeightAt(int x, int y, int z) {
		
		//float p1 = perlinNoise1.noise(x * 0.002f , y *.8f , z * 0.002f) * 16f;
		//float p2 = perlinNoise2.noise(x * 0.025f , y * .32f , z * 0.025f) * 65f;
		
		double p1 = perlinNoise1.noise(x * 0.002f , y * .80f , z * 0.002f) * 16f;
		double p2 = perlinNoise2.noise(x * 0.025f , y * .32f , z * 0.025f) * 65f;
			
		return (waterLevel - y) - (p1 + p2);
	}

	public Biome getBiomeAt(int x, int z) {

		double temperature = (perlinNoise1.noise(x * tempScale, 0, z * tempScale) + 0.5f) * 100f;
		double rainfall    = (perlinNoise2.noise(x * rainScale, 0, z * rainScale) + 0.5f) * 100f;
			
		Biome biome = Biome.DEFAULT;
		
		if(rainfall < 25) {
			
			if(temperature > 66) { // Desert
				//biome = Biome.DESERT;
			} else if(temperature > 33) { // Grass desert
				//biome = Biome.GRASS_DESERT;
			} else { // Tundra
				//biome = Biome.TUNDRA;
			}
						
		} else if(rainfall < 50) {
			
			if(temperature > 75) { // Savanna
				//biome = Biome.SAVANA;
			} else if(temperature > 50) { // Woods
				//biome = Biome.WOODS;
			} else if(temperature > 25) { // Taiga
				biome = Biome.TAIGA;
			}else { // Tundra
				
			}			
			
		} else if(rainfall < 75) {
			
			if(temperature > 66) { // Seasonal forest
				biome = Biome.SEASONAL_FOREST;
			} else if(temperature > 33) { // Forest
				//biome = Biome.FOREST;
			} else { // Taiga
				biome = Biome.TAIGA;
			}
			
		} else {
			
			if(temperature > 50) { // RainForest
				//biome = Biome.RAIN_FOREST;
			} else { // Swamp
				//biome = Biome.SWAMP;
			}
			
		}		
		
		return biome;
	}

	public int getHeightAt(int x, int z) {
		//System.out.println(perlinNoise1.noise(x * 0.010, z * 0.010));
		float height = perlinNoise2.fractal(x * 0.0055F, z * 0.0055F, 3) * 35F + 4;

		if(perlinNoise2.fractal(x * 0.02F, z * 0.02F, 2) > 0.1F) {
			height = perlinNoise2.noise(x * 0.008F, height * 0.03F, z * 0.008F) * 25F + 4;
			height *= -perlinNoise1.noise(x * 0.004F, z * 0.004F) * 5F + 2F;
		}

		return (int) ((height < 0 ? height * 0.4F : height * 0.5F) + WATER_LEVEL);
		
		/*float heightLow  = perlinNoise1.noise(x * 0.04F, z * 0.04F) * 10F - 4F;

		float heightResult;
		if(perlinNoise2.noise(x * 0.06F, z * 0.06F) * 16F > 0) {
			heightResult = heightLow;
		} else {
			float heightHigh = perlinNoise1.noise(x * 0.06F, z * 0.06F) * 40F + 6F;
			heightResult = Math.max(heightLow, heightHigh);
		}
				
		if(heightResult < 0F) {
		    heightResult = heightResult * 0.5F * 0.8F; 
		} else {
			heightResult = heightResult * 0.5F;
		}
		
		return (int) (heightResult + WATER_LEVEL);*/
	}

	public float getNoiseAt(int x, int z) {
		return perlinNoise1.noise(x * 0.01F, z * 0.01F) * 2F;
	}

	public float getNoiseAt(int x, int y, int z) {
		return perlinNoise1.noise(x * 0.15F, y * 0.15F, z * 0.15F) * 5F;
	}

}

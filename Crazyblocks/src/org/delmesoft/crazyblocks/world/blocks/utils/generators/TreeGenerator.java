package org.delmesoft.crazyblocks.world.blocks.utils.generators;

import org.delmesoft.crazyblocks.world.blocks.Blocks;
import org.delmesoft.crazyblocks.world.blocks.Chunk;
import org.delmesoft.crazyblocks.world.blocks.utils.ChunkData;

import java.util.Random;

import static org.delmesoft.crazyblocks.world.blocks.Chunk.HD;
import static org.delmesoft.crazyblocks.world.blocks.Plot.BIT_OFFSET;

public class TreeGenerator {
	
	// 30 alto arboles gigantes
	// 15 ancho

	public static void createOakTree(int rootX, int rootY, int rootZ, Chunk chunk, Random rnd) {

		if(rootY + 30 < Chunk.VERTICAL_SIZE) {

			int treeHigh = rnd.nextInt(7) + 4;

			int startLeaves = (int) (treeHigh * 0.85f);
			int diff = treeHigh - startLeaves;

			final int index = (rootX - chunk.worldX) * HD + (rootZ - chunk.worldZ);

			for (int treeY = 0; treeY < treeHigh; ++treeY) {
				chunk.chunkData.setBlockType(index + ((rootY + treeY) << BIT_OFFSET), Blocks.WOOD.id);
			}

			Chunk tmp = chunk;

			int radius = (int) (treeHigh * 0.75f);
			int r2;

			for (int treeY = 0; treeY < treeHigh; ++treeY) {

				int ry = treeY + (rootY + startLeaves);

				if (treeY >= diff && treeY - diff <= radius) {
					chunk.chunkData.setBlockType(index + (ry << BIT_OFFSET), Blocks.LEAVES.id);
				}

				r2 = radius * radius;

				for (int xc = -radius; xc < radius; ++xc) {

					for (int zc = -radius; zc < radius; ++zc) {

						int rx = (xc + rootX);
						int rz = (zc + rootZ);

						if (rx != rootX || rz != rootZ) {

							if (xc * xc + zc * zc < r2) {

								if (rnd.nextInt(1000) > 100) {
									tmp = tmp.getChunkRelative(rx >> BIT_OFFSET, rz >> BIT_OFFSET);
									tmp.chunkData.setBlockType(ChunkData.positionToIndex(rx - tmp.worldX, ry, rz - tmp.worldZ), Blocks.LEAVES.id);
								}

							}

						}

					}
				}

				radius--;
			}

		}

	}
	
	public static void createSpruceTree(int rootX, int rootY, int rootZ, Chunk chunk, Random rnd) {

		if(rootY + 30 < Chunk.VERTICAL_SIZE) {

			int treeHigh = rnd.nextInt(8) + 8;
			int startLeaves = (int) (treeHigh * 0.25f);
			int diff = treeHigh - startLeaves;

			final int index = (rootX - chunk.worldX) * HD + (rootZ - chunk.worldZ);

			for (int treeY = 0; treeY < treeHigh; treeY++) {
				chunk.chunkData.setBlockType(index + ((rootY + treeY) << BIT_OFFSET), Blocks.WOOD.id);
			}

			Chunk tmp = chunk;

			int radius = (int) (treeHigh * 0.5f);
			int r2;

			for (int treeY = 0; treeY < treeHigh; treeY++) {

				int ry = treeY + (rootY + startLeaves);

				if (treeY >= diff && treeY - diff <= radius) {
					chunk.chunkData.setBlockType(index + (ry << BIT_OFFSET), Blocks.LEAVES.id);
				}

				r2 = treeY % 2 == 0 ? (radius - 1) * (radius - 1) : radius * radius;

				for (int xc = -radius; xc <= radius; ++xc) {

					for (int zc = -radius; zc <= radius; ++zc) {

						int rx = (xc + rootX);
						int rz = (zc + rootZ);

						if (rx != rootX || rz != rootZ) {

							if (xc * xc + zc * zc < r2) {

								if (rnd.nextInt(1000) > 150) {

									tmp = tmp.getChunkRelative(rx >> BIT_OFFSET, rz >> BIT_OFFSET);

									tmp.chunkData.setBlockType(ChunkData.positionToIndex(rx - tmp.worldX, ry, rz - tmp.worldZ), Blocks.LEAVES.id);

								}

							}

						}

					}
				}

				if (treeY % 2 == 1)
					radius--;

			}

		}

	}

}

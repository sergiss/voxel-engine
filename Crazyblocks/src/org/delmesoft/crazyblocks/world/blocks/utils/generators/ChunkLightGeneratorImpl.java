package org.delmesoft.crazyblocks.world.blocks.utils.generators;

import org.delmesoft.crazyblocks.math.MathHelper;
import org.delmesoft.crazyblocks.world.blocks.Blocks;
import org.delmesoft.crazyblocks.world.blocks.Blocks.Block;
import org.delmesoft.crazyblocks.world.blocks.Chunk;
import org.delmesoft.crazyblocks.world.blocks.utils.ChunkData;
import org.delmesoft.crazyblocks.world.blocks.utils.DataPoint;
import org.delmesoft.crazyblocks.world.blocks.utils.DataPointArray;

import static org.delmesoft.crazyblocks.world.blocks.Blocks.MAX_LIGHT_RESISTANCE;
import static org.delmesoft.crazyblocks.world.blocks.Blocks.NULL_LIGHT;
import static org.delmesoft.crazyblocks.world.blocks.Blocks.SUN_LIGHT;
import static org.delmesoft.crazyblocks.world.blocks.Blocks.values;
import static org.delmesoft.crazyblocks.world.blocks.Chunk.HD;
import static org.delmesoft.crazyblocks.world.blocks.Chunk.HD_BIT_OFFSET;
import static org.delmesoft.crazyblocks.world.blocks.Chunk.VERTICAL_SIZE;
import static org.delmesoft.crazyblocks.world.blocks.Plot.BIT_OFFSET;
import static org.delmesoft.crazyblocks.world.blocks.Plot.SIZE;

public class ChunkLightGeneratorImpl implements ChunkLightGenerator {
	
	@Override
	public void generateSunLight(Chunk chunk) {

		try {

			chunk.lock.lock();

			if (chunk.state[3] == false) {

				chunk.updateHeights();

				int x, z;

				// SkyLight columns
				final ChunkData chunkData = chunk.chunkData;
				int n = VERTICAL_SIZE - 1, i, index;
				for (x = 0; x < SIZE; ++x) {
					i = x * HD;
					for (z = 0; z < SIZE; ++z) {
						index = i + z;
						generateSunLightColumn(chunkData, n, chunkData.getHeightAt(index), index);
					}
				}

				chunk.state[3] = true;
			}

		} finally {
			chunk.lock.unlock();
		}

	}

	@Override
	public void generateSunLightColumn(ChunkData chunkData, int start, int end, int columnIndex) {
		for (int j = (start << BIT_OFFSET); start > end; --start, j -= SIZE) {
			chunkData.setSkyLight(j + columnIndex, SUN_LIGHT);
		}
	}

	@Override
	public void spreadSkyLight(Chunk chunk) {

		try {

			chunk.lock.lock();

			if (chunk.state[4] == false) {

				DataPointArray stack = new DataPointArray();
				ChunkData chunkData = chunk.chunkData;

				int x, z;

				// SkyLight columns
				int i, j;
				int minX = chunk.worldX;
				int maxX = minX + SIZE;
				int minZ = chunk.worldZ;
				int maxZ = minZ + SIZE;

				// Spread data
				for (x = minX, i = 0; x < maxX; ++x, i += HD) {
					for (z = minZ, j = 0; z < maxZ; ++z, ++j) {
						spreadSkyLightColumn(chunk, x, chunkData.getHeightAt(i + j) + 1, z, i, j, SUN_LIGHT, stack);
					}
				}

				chunk.state[4] = true;
			}

		} finally {
			chunk.lock.unlock();
		}

	}

    @Override
    public void spreadSkyLightColumn(Chunk chunk, int x, int y, int z, int i, int j, byte light, DataPointArray stack) {

        if (y < VERTICAL_SIZE) {

            // chunk local
            int cx = x >> BIT_OFFSET;
            int cz = z >> BIT_OFFSET;

			int lx = x - chunk.worldX;
			int lz = z - chunk.worldZ;

			Chunk tmp;
			// RIGHT *********************************************************
			int i1;
			if(lx == 15) {
				tmp = chunk.getChunkRelative(cx + 1, cz);
				i1 = j;
			} else {
				tmp = chunk;
				i1 = i + HD + j;
			}
			ChunkData d1 = tmp.chunkData;

			// LEFT *********************************************************
			int i2;
			if(lx == 0) {
				tmp = chunk.getChunkRelative(cx - 1, cz);
				i2 = j + ChunkData.MAX_X;
			} else {
				tmp = chunk;
				i2 = i - HD + j;
			}
			ChunkData d2 = tmp.chunkData;

			// FRONT *********************************************************
			int i3;
			if(lz == 15) {
				tmp = chunk.getChunkRelative(cx, cz + 1);
				i3 = i;
			} else {
				tmp = chunk;
				i3 = i + j + 1;
			}
			ChunkData d3 = tmp.chunkData;

			// BACK *********************************************************
			int i4;
			if(lz == 0) {
				tmp = chunk.getChunkRelative(cx, cz - 1);
				i4 = i + ChunkData.MAX_Z;
			} else {
				tmp = chunk;
				i4 = i + j - 1;
			}
			ChunkData d4 = tmp.chunkData;

            // Get the highest height of neighbors
            int maxHeight = MathHelper.max(d1.getHeightAt(i1), d2.getHeightAt(i2), d3.getHeightAt(i3), d4.getHeightAt(i4));

            for (int i0 = y << BIT_OFFSET; y <= maxHeight; ++y, i0 += SIZE) {
                if (values[d1.getBlockType(i1 + i0)].lightResistance < MAX_LIGHT_RESISTANCE ||
                    values[d2.getBlockType(i2 + i0)].lightResistance < MAX_LIGHT_RESISTANCE ||
                    values[d3.getBlockType(i3 + i0)].lightResistance < MAX_LIGHT_RESISTANCE ||
                    values[d4.getBlockType(i4 + i0)].lightResistance < MAX_LIGHT_RESISTANCE) {

                    spreadSkyLight(chunk, x, y, z, light, stack);
                }
            }

        }

    }

    @Override
	public void spreadSkyLight(Chunk chunk, int x, int y, int z, byte light, DataPointArray stack) {

		stack.add(new DataPoint(x, y, z, light));

		byte newLight, currentLight;
		Block block;
		Chunk nChunk;

		int index;

		int yOffset;
		int cx, cz, ix, iz;

		DataPoint lPoint;

		do {

			lPoint = stack.pop();

			light = lPoint.data;

			x = lPoint.x;
			y = lPoint.y;
			z = lPoint.z;

			yOffset = y << BIT_OFFSET;
			cx = x >> BIT_OFFSET;
			cz = z >> BIT_OFFSET;

			chunk = chunk.getChunkRelative(cx, cz);

			ix = (x - chunk.worldX) * HD;
			iz = z - chunk.worldZ;

			nChunk = chunk.getChunkRelative((x + 1) >> BIT_OFFSET, cz);
			index = ((x + 1) - nChunk.worldX) * HD + iz + yOffset;
			currentLight = nChunk.chunkData.getSkyLight(index);
			if (currentLight < SUN_LIGHT) {
				block = values[nChunk.chunkData.getBlockType(index) & 0xFF];
				
				newLight = (byte) (light - block.lightResistance);

				if (currentLight < newLight) {
					nChunk.chunkData.setSkyLight(index, newLight);
					if (newLight > 1)
						stack.add(new DataPoint(x + 1, y, z, newLight));
				}
			}

			nChunk = chunk.getChunkRelative((x - 1) >> BIT_OFFSET, cz);
			index = ((x - 1) - nChunk.worldX) * HD + iz + yOffset;
			currentLight = nChunk.chunkData.getSkyLight(index);
			if (currentLight < SUN_LIGHT) {
				block = values[nChunk.chunkData.getBlockType(index) & 0xFF];

				newLight = (byte) (light - block.lightResistance);

				if (currentLight < newLight) {
					nChunk.chunkData.setSkyLight(index, newLight);
					if (newLight > 1)
						stack.add(new DataPoint(x - 1, y, z, newLight));
				}
			}

			nChunk = chunk.getChunkRelative(cx, (z + 1) >> BIT_OFFSET);
			index = ix + ((z + 1) - nChunk.worldZ) + yOffset;
			currentLight = nChunk.chunkData.getSkyLight(index);
			if (currentLight < SUN_LIGHT) {
				block = values[nChunk.chunkData.getBlockType(index) & 0xFF];

				newLight = (byte) (light - block.lightResistance);

				if (currentLight < newLight) {
					nChunk.chunkData.setSkyLight(index, newLight);
					if (newLight > 1)
						stack.add(new DataPoint(x, y, z + 1, newLight));
				}
			}

			nChunk = chunk.getChunkRelative(cx, (z - 1) >> BIT_OFFSET);
			index = ix + ((z - 1) - nChunk.worldZ) + yOffset;
			currentLight = nChunk.chunkData.getSkyLight(index);
			if (currentLight < SUN_LIGHT) {
				block = values[nChunk.chunkData.getBlockType(index) & 0xFF];

				newLight = (byte) (light - block.lightResistance);

				if (currentLight < newLight) {
					nChunk.chunkData.setSkyLight(index, newLight);
					if (newLight > 1)
						stack.add(new DataPoint(x, y, z - 1, newLight));
				}
			}

			if (y < VERTICAL_SIZE - 1) {
				index = ix + iz + ((y + 1) << BIT_OFFSET);
				currentLight = chunk.chunkData.getSkyLight(index);
				if (currentLight < SUN_LIGHT) {
					block = values[chunk.chunkData.getBlockType(index) & 0xFF];

					newLight = (byte) (light - block.lightResistance);

					if (currentLight < newLight) {
						chunk.chunkData.setSkyLight(index, newLight);
						if (newLight > 1)
							stack.add(new DataPoint(x, y + 1, z, newLight));
					}
				}
			}

			if (y > 1) {
				index = ix + iz + ((y - 1) << BIT_OFFSET);
				currentLight = chunk.chunkData.getSkyLight(index);
				if (currentLight < SUN_LIGHT) {
					block = values[chunk.chunkData.getBlockType(index) & 0xFF];

					newLight = (byte) (light - block.lightResistance);

					if (currentLight < newLight) {
						chunk.chunkData.setSkyLight(index, newLight);
						if (newLight > 1)
							stack.add(new DataPoint(x, y - 1, z, newLight));
					}
				}
			}

		} while (stack.size > 0);

	}

	@Override
	public void spreadBlockLight(Chunk chunk, int x, int y, int z, byte light, DataPointArray stack) {

		stack.add(new DataPoint(x, y, z, light));

		byte newLight, currentLight;
		Block block;
		Chunk nChunk;

		int index;

		int yOffset;
		int cx, cz, ix, iz;

		DataPoint lPoint;

		do {

			lPoint = stack.pop();

			light = lPoint.data;

			x = lPoint.x;
			y = lPoint.y;
			z = lPoint.z;

			yOffset = y << BIT_OFFSET;
			cx = x >> BIT_OFFSET;
			cz = z >> BIT_OFFSET;

			chunk = chunk.getChunkRelative(cx, cz);

			ix = (x - chunk.worldX) * HD;
			iz = z - chunk.worldZ;

			nChunk = chunk.getChunkRelative((x + 1) >> BIT_OFFSET, cz);
			index = ((x + 1) - nChunk.worldX) * HD + iz + yOffset;
			currentLight = nChunk.chunkData.getBlockLight(index);
			//if (currentLight < SUN_LIGHT) {
				block = values[nChunk.chunkData.getBlockType(index) & 0xFF];

				newLight = (byte) (light - block.lightResistance);

				if (currentLight < newLight) {
					nChunk.chunkData.setBlockLight(index, newLight);
					if (newLight > 1)
						stack.add(new DataPoint(x + 1, y, z, newLight));
				}
			//}

			nChunk = chunk.getChunkRelative((x - 1) >> BIT_OFFSET, cz);
			index = ((x - 1) - nChunk.worldX) * HD + iz + yOffset;
			currentLight = nChunk.chunkData.getBlockLight(index);
			//if (currentLight < SUN_LIGHT) {
				block = values[nChunk.chunkData.getBlockType(index) & 0xFF];

				newLight = (byte) (light - block.lightResistance);

				if (currentLight < newLight) {
					nChunk.chunkData.setBlockLight(index, newLight);
					if (newLight > 1)
						stack.add(new DataPoint(x - 1, y, z, newLight));
				}
			//}

			nChunk = chunk.getChunkRelative(cx, (z + 1) >> BIT_OFFSET);
			index = ix + ((z + 1) - nChunk.worldZ) + yOffset;
			currentLight = nChunk.chunkData.getBlockLight(index);
			//if (currentLight < SUN_LIGHT) {
				block = values[nChunk.chunkData.getBlockType(index) & 0xFF];

				newLight = (byte) (light - block.lightResistance);

				if (currentLight < newLight) {
					nChunk.chunkData.setBlockLight(index, newLight);
					if (newLight > 1)
						stack.add(new DataPoint(x, y, z + 1, newLight));
				}
			//}

			nChunk = chunk.getChunkRelative(cx, (z - 1) >> BIT_OFFSET);
			index = ix + ((z - 1) - nChunk.worldZ) + yOffset;
			currentLight = nChunk.chunkData.getBlockLight(index);
			//if (currentLight < SUN_LIGHT) {
				block = values[nChunk.chunkData.getBlockType(index) & 0xFF];

				newLight = (byte) (light - block.lightResistance);

				if (currentLight < newLight) {
					nChunk.chunkData.setBlockLight(index, newLight);
					if (newLight > 1)
						stack.add(new DataPoint(x, y, z - 1, newLight));
				}
			//}

			if (y < VERTICAL_SIZE - 1) {
				index = ix + iz + ((y + 1) << BIT_OFFSET);
				currentLight = chunk.chunkData.getBlockLight(index);
				//if (currentLight < SUN_LIGHT) {
					block = values[chunk.chunkData.getBlockType(index) & 0xFF];

					newLight = (byte) (light - block.lightResistance);

					if (currentLight < newLight) {
						chunk.chunkData.setBlockLight(index, newLight);
						if (newLight > 1)
							stack.add(new DataPoint(x, y + 1, z, newLight));
					}
				//}
			}

			if (y > 1) {
				index = ix + iz + ((y - 1) << BIT_OFFSET);
				currentLight = chunk.chunkData.getBlockLight(index);
				//if (currentLight < SUN_LIGHT) {
					block = values[chunk.chunkData.getBlockType(index) & 0xFF];

					newLight = (byte) (light - block.lightResistance);

					if (currentLight < newLight) {
						chunk.chunkData.setBlockLight(index, newLight);
						if (newLight > 1)
							stack.add(new DataPoint(x, y - 1, z, newLight));
					}
				//}
			}

		} while (stack.size > 0);
		
	}

    @Override
    public void clearSunLightColumn(ChunkData chunkData, int start, int end, int columnIndex) {
        for (int j = (start << BIT_OFFSET); start > end; --start, j -= SIZE) {
            chunkData.setSkyLight(j + columnIndex, NULL_LIGHT);
        }
    }

    @Override
	public void unspreadSkyLightColumn(Chunk chunk, int x, int y, int z, int i, int j, byte light, DataPointArray stack, DataPointArray endPoints) {
        if (y < VERTICAL_SIZE) {

            // chunk local
            int cx = x >> BIT_OFFSET;
            int cz = z >> BIT_OFFSET;

            int lx = x - chunk.worldX;
            int lz = z - chunk.worldZ;

            Chunk tmp;
            // RIGHT *********************************************************
            int i1;
            if(lx == 15) {
                tmp = chunk.getChunkRelative(cx + 1, cz);
                i1 = j;
            } else {
                tmp = chunk;
                i1 = i + HD + j;
            }
            ChunkData d1 = tmp.chunkData;

            // LEFT *********************************************************
            int i2;
            if(lx == 0) {
                tmp = chunk.getChunkRelative(cx - 1, cz);
                i2 = j + ChunkData.MAX_X;
            } else {
                tmp = chunk;
                i2 = i - HD + j;
            }
			ChunkData d2 = tmp.chunkData;

            // FRONT *********************************************************
            int i3;
            if(lz == 15) {
                tmp = chunk.getChunkRelative(cx, cz + 1);
                i3 = i;
            } else {
                tmp = chunk;
                i3 = i + j + 1;
            }
			ChunkData d3 = tmp.chunkData;

            // BACK *********************************************************
            int i4;
            if(lz == 0) {
                tmp = chunk.getChunkRelative(cx, cz - 1);
                i4 = i + ChunkData.MAX_Z;
            } else {
                tmp = chunk;
                i4 = i + j - 1;
            }
			ChunkData d4 = tmp.chunkData;
			
			int h1 = d1.getHeightAt(i1);
			int h2 = d2.getHeightAt(i2);
			int h3 = d3.getHeightAt(i3);
			int h4 = d4.getHeightAt(i4);
			

            // Get the highest height of neighbors
            int maxHeight = MathHelper.max(h1, h2, h3, h4);

            for (int i0 = y << BIT_OFFSET; y <= maxHeight; ++y, i0 += SIZE) {

                if (values[d1.getBlockType(i1 + i0)].lightResistance < MAX_LIGHT_RESISTANCE ||
                    values[d2.getBlockType(i2 + i0)].lightResistance < MAX_LIGHT_RESISTANCE ||
                    values[d3.getBlockType(i3 + i0)].lightResistance < MAX_LIGHT_RESISTANCE ||
                    values[d4.getBlockType(i4 + i0)].lightResistance < MAX_LIGHT_RESISTANCE) {
                    unspreadSkyLight(chunk, x, y, z, light, stack, endPoints);
					
                }

            }

        }
	}

	@Override
	public void unspreadSkyLight(Chunk chunk, int x, int y, int z, byte light, DataPointArray stack, DataPointArray endPoints) {

		stack.add(new DataPoint(x, y, z, light));
		byte currentLight;

		Chunk nChunk;

		int index;

		int yOffset;
		int cx, cz, ix, iz;

		DataPoint lPoint;

		do {

			lPoint = stack.pop();

			light = lPoint.data;

			x = lPoint.x;
			y = lPoint.y;
			z = lPoint.z;

			yOffset = y << BIT_OFFSET;
			cx = x >> BIT_OFFSET;
			cz = z >> BIT_OFFSET;

			chunk = chunk.getChunkRelative(cx, cz);

			ix = (x - chunk.worldX) * HD;
			iz = z - chunk.worldZ;

			nChunk = chunk.getChunkRelative((x + 1) >> BIT_OFFSET, cz);
			index = ((x + 1) - nChunk.worldX) * HD + iz + yOffset;
			currentLight = nChunk.chunkData.getSkyLight(index);
			if (currentLight > 0) {
				if (currentLight < light) {
					nChunk.chunkData.setSkyLight(index, Blocks.NULL_LIGHT);
					if (currentLight > 1)
						stack.add(new DataPoint(x + 1, y, z, currentLight));
				} else {
					endPoints.add(new DataPoint(x + 1, y, z, currentLight));
				}
			}

			nChunk = chunk.getChunkRelative((x - 1) >> BIT_OFFSET, cz);
			index = ((x - 1) - nChunk.worldX) * HD + iz + yOffset;
			currentLight = nChunk.chunkData.getSkyLight(index);
			if (currentLight > 0) {
				if (currentLight < light) {
					nChunk.chunkData.setSkyLight(index, Blocks.NULL_LIGHT);
					if (currentLight > 1)
						stack.add(new DataPoint(x - 1, y, z, currentLight));
				} else {
					endPoints.add(new DataPoint(x - 1, y, z, currentLight));
				}
			}

			nChunk = chunk.getChunkRelative(cx, (z + 1) >> BIT_OFFSET);
			index = ix + ((z + 1) - nChunk.worldZ) + yOffset;
			currentLight = nChunk.chunkData.getSkyLight(index);
			if (currentLight > 0) {
				if (currentLight < light) {
					nChunk.chunkData.setSkyLight(index, Blocks.NULL_LIGHT);
					if (currentLight > 1)
						stack.add(new DataPoint(x, y, z + 1, currentLight));
				} else {
					endPoints.add(new DataPoint(x, y, z + 1, currentLight));
				}
			}

			nChunk = chunk.getChunkRelative(cx, (z - 1) >> BIT_OFFSET);
			index = ix + ((z - 1) - nChunk.worldZ) + yOffset;
			currentLight = nChunk.chunkData.getSkyLight(index);
			if (currentLight > 0) {
				if (currentLight < light) {
					nChunk.chunkData.setSkyLight(index, Blocks.NULL_LIGHT);
					if (currentLight > 1)
						stack.add(new DataPoint(x, y, z - 1, currentLight));
				} else {
					endPoints.add(new DataPoint(x, y, z - 1, currentLight));
				}
			}

			if (y < VERTICAL_SIZE - 1) {
				index = ix + iz + ((y + 1) << BIT_OFFSET);
				currentLight = chunk.chunkData.getSkyLight(index);
				if (currentLight > 0) {
					if (currentLight < light) {
						chunk.chunkData.setSkyLight(index, Blocks.NULL_LIGHT);
						if (currentLight > 1)
							stack.add(new DataPoint(x, y + 1, z, currentLight));
					} else {
						endPoints.add(new DataPoint(x, y + 1, z, currentLight));
					}
				}
			}

			if (y > 1) {
				index = ix + iz + ((y - 1) << BIT_OFFSET);
				currentLight = chunk.chunkData.getSkyLight(index);
				if (currentLight > 0) {
					if (currentLight < light) {
						chunk.chunkData.setSkyLight(index, Blocks.NULL_LIGHT);
						if (currentLight > 1)
							stack.add(new DataPoint(x, y - 1, z, currentLight));
					}
				} else {
					endPoints.add(new DataPoint(x, y - 1, z, currentLight));
				}
			}

		} while (stack.size > 0);

        while(endPoints.size > 0) {
            lPoint = endPoints.pop();
            chunk = chunk.getChunkRelative(lPoint.x >> BIT_OFFSET, lPoint.z >> BIT_OFFSET);
            light = chunk.chunkData.getSkyLight(lPoint.x - chunk.worldX, lPoint.y, lPoint.z - chunk.worldZ);
            spreadSkyLight(chunk, lPoint.x, lPoint.y, lPoint.z, light, stack);
        }

	}

	@Override
	public void unspreadBlockLight(Chunk chunk, int x, int y, int z, byte light, DataPointArray stack, DataPointArray endPoints) {

		stack.add(new DataPoint(x, y, z, light));
		byte currentLight;

		Chunk nChunk;

		int index;

		int yOffset;
		int cx, cz, ix, iz;

		DataPoint lPoint;

		do {

			lPoint = stack.pop();

			light = lPoint.data;

			x = lPoint.x;
			y = lPoint.y;
			z = lPoint.z;

			yOffset = y << BIT_OFFSET;
			cx = x >> BIT_OFFSET;
			cz = z >> BIT_OFFSET;

			chunk = chunk.getChunkRelative(cx, cz);

			ix = (x - chunk.worldX) * HD;
			iz = z - chunk.worldZ;

			nChunk = chunk.getChunkRelative((x + 1) >> BIT_OFFSET, cz);
			index = ((x + 1) - nChunk.worldX) * HD + iz + yOffset;
			currentLight = nChunk.chunkData.getBlockLight(index);
			if (currentLight > 0) {
				if (currentLight < light) {
					nChunk.chunkData.setBlockLight(index, Blocks.NULL_LIGHT);
					if (currentLight > 1)
						stack.add(new DataPoint(x + 1, y, z, currentLight));
				} else {
					endPoints.add(new DataPoint(x + 1, y, z, currentLight));
				}
			}

			nChunk = chunk.getChunkRelative((x - 1) >> BIT_OFFSET, cz);
			index = ((x - 1) - nChunk.worldX) * HD + iz + yOffset;
			currentLight = nChunk.chunkData.getBlockLight(index);
			if (currentLight > 0) {
				if (currentLight < light) {
					nChunk.chunkData.setBlockLight(index, Blocks.NULL_LIGHT);
					if (currentLight > 1)
						stack.add(new DataPoint(x - 1, y, z, currentLight));
				} else {
					endPoints.add(new DataPoint(x - 1, y, z, currentLight));
				}
			}

			nChunk = chunk.getChunkRelative(cx, (z + 1) >> BIT_OFFSET);
			index = ix + ((z + 1) - nChunk.worldZ) + yOffset;
			currentLight = nChunk.chunkData.getBlockLight(index);
			if (currentLight > 0) {
				if (currentLight < light) {
					nChunk.chunkData.setBlockLight(index, Blocks.NULL_LIGHT);
					if (currentLight > 1)
						stack.add(new DataPoint(x, y, z + 1, currentLight));
				} else {
					endPoints.add(new DataPoint(x, y, z + 1, currentLight));
				}
			}

			nChunk = chunk.getChunkRelative(cx, (z - 1) >> BIT_OFFSET);
			index = ix + ((z - 1) - nChunk.worldZ) + yOffset;
			currentLight = nChunk.chunkData.getBlockLight(index);
			if (currentLight > 0) {
				if (currentLight < light) {
					nChunk.chunkData.setBlockLight(index, Blocks.NULL_LIGHT);
					if (currentLight > 1)
						stack.add(new DataPoint(x, y, z - 1, currentLight));
				} else {
					endPoints.add(new DataPoint(x, y, z - 1, currentLight));
				}
			}

			if (y < VERTICAL_SIZE - 1) {
				index = ix + iz + ((y + 1) << BIT_OFFSET);
				currentLight = chunk.chunkData.getBlockLight(index);
				if (currentLight > 0) {
					if (currentLight < light) {
						chunk.chunkData.setBlockLight(index, Blocks.NULL_LIGHT);
						if (currentLight > 1)
							stack.add(new DataPoint(x, y + 1, z, currentLight));
					} else {
						endPoints.add(new DataPoint(x, y + 1, z, currentLight));
					}
				}
			}

			if (y > 1) {
				index = ix + iz + ((y - 1) << BIT_OFFSET);
				currentLight = chunk.chunkData.getBlockLight(index);
				if (currentLight > 0) {
					if (currentLight < light) {
						chunk.chunkData.setBlockLight(index, Blocks.NULL_LIGHT);
						if (currentLight > 1)
							stack.add(new DataPoint(x, y - 1, z, currentLight));
					} else {
                        endPoints.add(new DataPoint(x, y - 1, z, currentLight));
                    }
				}
			}

		} while (stack.size > 0);

		while(endPoints.size > 0) {
			lPoint = endPoints.pop();
			chunk = chunk.getChunkRelative(lPoint.x >> BIT_OFFSET, lPoint.z >> BIT_OFFSET);
			light = chunk.chunkData.getBlockLight(lPoint.x - chunk.worldX, lPoint.y, lPoint.z - chunk.worldZ);
			spreadBlockLight(chunk, lPoint.x, lPoint.y, lPoint.z, light, stack);
		}
		
	}

}



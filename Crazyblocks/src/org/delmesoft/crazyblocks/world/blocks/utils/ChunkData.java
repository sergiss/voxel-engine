package org.delmesoft.crazyblocks.world.blocks.utils;

import static org.delmesoft.crazyblocks.world.blocks.Chunk.BLOCK_COUNT;
import static org.delmesoft.crazyblocks.world.blocks.Chunk.HD;
import static org.delmesoft.crazyblocks.world.blocks.Chunk.HD_BIT_OFFSET;
import static org.delmesoft.crazyblocks.world.blocks.Plot.BIT_OFFSET;
import static org.delmesoft.crazyblocks.world.blocks.Plot.SIZE;

import org.delmesoft.crazyblocks.math.Vec3i;
import org.delmesoft.crazyblocks.world.blocks.Blocks;
import org.delmesoft.crazyblocks.world.blocks.Chunk;
import org.delmesoft.crazyblocks.world.blocks.Plot;

public class ChunkData {
	
	public static final int MAX_Z = (Plot.SIZE - 1);
	public static final int MAX_X = MAX_Z << HD_BIT_OFFSET;
				
	private final short[] data; // unsigned 0 - 255
	private final byte[] light;
	
	private int refX, refY, refZ;
	
	private Chunk chunk;

	public enum LightType {
		SKY, BLOCK
	}

	public ChunkData(Chunk chunk) {
		this.chunk = chunk;
		light = new byte[BLOCK_COUNT];
		data  = new short[BLOCK_COUNT];
		byte bedrock = Blocks.BEDROCK.id;
		int i;
		for(int x = 0; x < SIZE; x++) {
			i = (x << HD_BIT_OFFSET) + 16;
			for(int z = 0; z < SIZE; z++) {
				data[i + z] = bedrock;
			}
		}
	}

	public void setSkyLight(int index, byte val) {
		light[index] &= ~0xF;
		light[index] |= (val & 0xF);
	}

	public void setSkyLight(int x, int y, int z, byte val) {
		setSkyLight((x << HD_BIT_OFFSET) + (y << BIT_OFFSET) + z, val);
	}

	public byte getSkyLight(int index) {
		return (byte) (light[index] & 0xF);
	}

	public byte getSkyLight(int x, int y, int z) {
		return getSkyLight((x << HD_BIT_OFFSET) + (y << BIT_OFFSET) + z);
	}

	public void setBlockLight(int index, byte val) {
		light[index] &= ~0xF0;
		light[index] |= (val & 0xF) << 4;
	}

	public void setBlockLight(int x, int y, int z, byte val) {
		setBlockLight((x << HD_BIT_OFFSET) + (y << BIT_OFFSET) + z, val);
	}

	public byte getBlockLight(int index) {
		return (byte) ((light[index] & 0xF0) >>> 4);
	}

	public byte getBlockLight(int x, int y, int z) {
		return getBlockLight((x << HD_BIT_OFFSET) + (y << BIT_OFFSET) + z);
	}

	public void setBlockType(int index, byte type) {
		data[index] = type;
	}

	public void setBlockType(int x, int y, int z, byte type) {
		setBlockType((x << HD_BIT_OFFSET) + (y << BIT_OFFSET) + z, type);
	}

	public byte getBlockType(int index) {
		return (byte) (data[index] & 0xFF);
	}

	public byte getBlockType(int x, int y, int z) {
		return getBlockType((x << HD_BIT_OFFSET) + (y << BIT_OFFSET) + z);
	}

	public void setBlockData(int index, byte data) {
		this.data[index] &= ~0xFF00;
		this.data[index] |= (data & 0xFF) << 8;
	}

	public void setBlockData(int x, int y, int z, byte data) {
		setBlockData((x << HD_BIT_OFFSET) + (y << BIT_OFFSET) + z, data);
	}

	public byte getBlockData(int index) {
		return (byte) ((data[index] & 0xFF00) >>> 8);
	}

	public byte getBlockData(int x, int y, int z) {
		return getBlockData((x << HD_BIT_OFFSET) + (y << BIT_OFFSET) + z);
	}

	public short getRawType(int index) {
		return data[index];
	}

	public short getRawType(int x, int y, int z) {
		return getRawType((x << HD_BIT_OFFSET) + (y << BIT_OFFSET) + z);
	}

	public void setRawType(int index, short d) {
		data[index] = d;
	}

	public void setRawType(int x, int y, int z, short rawType) {
		setRawType((x << HD_BIT_OFFSET) + (y << BIT_OFFSET) + z, rawType);
	}

	public byte getHeightAt(int index) {
		return (byte) (data[index] & 0xFF);
	}

	public byte getHeightAt(int x, int z) {
		return getHeightAt((x << HD_BIT_OFFSET) + z);
	}

	public void setHeightAt(int index, byte height) {
		data[index] = height;
	}
	
	public void setHeightAt(int x, int z, short height) {
		data[(x << HD_BIT_OFFSET)+ z] = height;
	}

	public void clear() {
		
		byte air = Blocks.AIR.id;
		byte bedrock = Blocks.BEDROCK.id;
		
		int y;
		for (int i = 0; i < BLOCK_COUNT; ++i) {
			y = (i % HD) >> BIT_OFFSET;
			if (y > 1) {
				light[i] = 0;
				data[i]  = air;
			} else if (y == 1) {
				data[i] = bedrock; // bedrock
			}
		}
	}

	public byte getReferenceLight(int x, int y, int z) {
		
		x += refX;
		z += refZ;

		if (x < 0 || x >= SIZE || z < 0 || z >= SIZE) {
			x += chunk.worldX;
			z += chunk.worldZ;
			Chunk cc = chunk.getChunkRelative(x >> BIT_OFFSET, z >> BIT_OFFSET);
			return cc.chunkData.light[((x - cc.worldX) << HD_BIT_OFFSET) + ((y + refY) << BIT_OFFSET) + (z - cc.worldZ)];
		}

		return light[(x << HD_BIT_OFFSET) + ((y + refY) << BIT_OFFSET) + z];
	}

	public short getReferenceRawType(int x, int y, int z) {

		x += refX;
		z += refZ;

		if (x < 0 || x >= SIZE || z < 0 || z >= SIZE) {
			x += chunk.worldX;
			z += chunk.worldZ;
			Chunk cc = chunk.getChunkRelative(x >> BIT_OFFSET, z >> BIT_OFFSET);
			return cc.chunkData.data[((x - cc.worldX) << HD_BIT_OFFSET) + ((y + refY) << BIT_OFFSET) + (z - cc.worldZ)];
		}

		return data[(x << HD_BIT_OFFSET) + ((y + refY) << BIT_OFFSET) + z];
	}
	
	public void setReferencePoint(int x, int y, int z) {
		this.refX = x;
		this.refY = y;
		this.refZ = z;
	}

	public static int positionToIndex(int x, int y, int z) {
		return (x << HD_BIT_OFFSET) + (y << BIT_OFFSET) + z;
	}

	public static Vec3i indexToPosition(int index) {
		final int x = index >> HD_BIT_OFFSET;
		final int y = (index % HD) >> BIT_OFFSET;
		final int z = index % SIZE;
		return new Vec3i(x, y, z);
	}

	public static byte getType(short rawType) {
		return (byte) (rawType & 0xFF);
	}

	public static byte getData(short rawType) {
		return (byte) ((rawType & 0xFF00) >>> 8);
	}

	public static short getRawType(byte type, byte data) {
		return (short) ((type & 0xFF) | ((byte) (data & 0xFF) << 8));
	}

}

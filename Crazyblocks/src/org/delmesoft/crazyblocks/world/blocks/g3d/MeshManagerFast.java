package org.delmesoft.crazyblocks.world.blocks.g3d;

import org.delmesoft.crazyblocks.graphics.g2d.TextureCoordinates;
import org.delmesoft.crazyblocks.world.blocks.Blocks;
import org.delmesoft.crazyblocks.world.blocks.Blocks.Block;
import org.delmesoft.crazyblocks.world.blocks.utils.ChunkData;

public class MeshManagerFast extends MeshManager {

	private TextureCoordinates[] textureRegions;
	
	@Override
	public void begin(float x, float y, float z, short rawType, ChunkData chunkData) {

		textureRegions = Blocks.values[rawType & 0xFF].textureRegions;

		final float px = x + 1f;
		final float py = y + 1f;
		final float pz = z + 1f;

		pv000.set(x, y, z);
		pv001.set(x, y, pz);
		pv101.set(px, y, pz);
		pv100.set(px, y, z);

		pv110.set(px, py, z);
		pv111.set(px, py, pz);
		pv011.set(x, py, pz);
		pv010.set(x, py, z);
		
	}

	public void createTop(ChunkData chunkData){

		final TextureCoordinates tRegion = textureRegions[0];
		
		LightInfo lightInfo = lightInfo0.set(1f, chunkData.getReferenceLight(0, 1, 0));
		
		face(pv010, tRegion.u, tRegion.v, pv110, tRegion.u2, tRegion.v, pv111, tRegion.u2, tRegion.v2, pv011, tRegion.u,  tRegion.v2, lightInfo.blockLight, lightInfo.skyLight);

		index(iOff, (short) (iOff + 3), (short) (iOff + 2), (short) (iOff + 2), (short) (iOff + 1), iOff);
		
		iOff += 4;

	}

	public void createBottom(ChunkData chunkData){

		final TextureCoordinates tRegion = textureRegions[1];

		LightInfo lightInfo = lightInfo0.set(0.76f, chunkData.getReferenceLight( 0, -1,  0));
		
		face(pv100, tRegion.u, tRegion.v, pv000, tRegion.u2, tRegion.v, pv001, tRegion.u2, tRegion.v2, pv101, tRegion.u, tRegion.v2, lightInfo.blockLight, lightInfo.skyLight);

		index(iOff, (short) (iOff + 3), (short) (iOff + 2), (short) (iOff + 2), (short) (iOff + 1), iOff);
		
		iOff += 4;

	}

	public void createFront(ChunkData chunkData){

		final TextureCoordinates tRegion = textureRegions[2];

		LightInfo lightInfo = lightInfo0.set(0.92f, chunkData.getReferenceLight( 0,  0,  1));
		
		face(pv011, tRegion.u, tRegion.v, pv111, tRegion.u2, tRegion.v, pv101, tRegion.u2, tRegion.v2, pv001, tRegion.u, tRegion.v2, lightInfo.blockLight, lightInfo.skyLight);

		index(iOff, (short) (iOff + 3), (short) (iOff + 2), (short) (iOff + 2), (short) (iOff + 1), iOff);
		
		iOff += 4;

	}

	public void createBack(ChunkData chunkData){

		final TextureCoordinates tRegion = textureRegions[3];

		LightInfo lightInfo = lightInfo0.set(0.92f, chunkData.getReferenceLight( 0,  0, -1));
		
		face(pv110, tRegion.u, tRegion.v, pv010, tRegion.u2, tRegion.v, pv000, tRegion.u2, tRegion.v2, pv100, tRegion.u, tRegion.v2, lightInfo.blockLight, lightInfo.skyLight);

		index(iOff, (short) (iOff + 3), (short) (iOff + 2), (short) (iOff + 2), (short) (iOff + 1), iOff);
		
		iOff += 4;

	}

	public void createRight(ChunkData chunkData){

		final TextureCoordinates tRegion = textureRegions[4];

		LightInfo lightInfo = lightInfo0.set(0.84f, chunkData.getReferenceLight( 1,  0,  0));
		
		face(pv111, tRegion.u, tRegion.v, pv110, tRegion.u2, tRegion.v, pv100, tRegion.u2, tRegion.v2, pv101, tRegion.u, tRegion.v2, lightInfo.blockLight, lightInfo.skyLight);

		index(iOff, (short) (iOff + 3), (short) (iOff + 2), (short) (iOff + 2), (short) (iOff + 1), iOff);
		
		iOff += 4;

	}

	public void createLeft(ChunkData chunkData){

		final TextureCoordinates tRegion = textureRegions[5];

		LightInfo lightInfo = lightInfo0.set(0.84f, chunkData.getReferenceLight(-1,  0,  0));
		
		face(pv010, tRegion.u, tRegion.v, pv011, tRegion.u2, tRegion.v, pv001, tRegion.u2, tRegion.v2, pv000, tRegion.u, tRegion.v2, lightInfo.blockLight, lightInfo.skyLight);

		index(iOff, (short) (iOff + 3), (short) (iOff + 2), (short) (iOff + 2), (short) (iOff + 1), iOff);
		
		iOff += 4;

	}

}

package org.delmesoft.crazyblocks.world.blocks.g3d;

import org.delmesoft.crazyblocks.graphics.g2d.TextureCoordinates;
import org.delmesoft.crazyblocks.world.blocks.Blocks;
import org.delmesoft.crazyblocks.world.blocks.Blocks.Block;
import org.delmesoft.crazyblocks.world.blocks.utils.ChunkData;

public class MeshManagerSmoothLighting extends MeshManager {

	protected TextureCoordinates[] textureRegions;

	protected byte tl, tb, tclb, tcbr, tr, tf, tcrf, tcfl;
	protected byte bl, bb, bcbl, bcrb, br, bf, bcfr, bclf;
	protected byte cfl, cfr, cbl, cbr;

	public void begin(float x, float y, float z, short rawType, ChunkData chunkData) {

		textureRegions = Blocks.values[rawType & 0xFF].textureRegions;

		final float px = x + 1;
		final float py = y + 1;
		final float pz = z + 1;

		pv000.set(x, y, z);
		pv001.set(x, y, pz);
		pv101.set(px, y, pz);
		pv100.set(px, y, z);

		pv110.set(px, py, z);
		pv111.set(px, py, pz);
		pv011.set(x, py, pz);
		pv010.set(x, py, z);

		cfl  = chunkData.getReferenceLight(-1, 0, 1);
		cfr  = chunkData.getReferenceLight( 1, 0, 1);
		cbl  = chunkData.getReferenceLight(-1, 0,-1);
		cbr  = chunkData.getReferenceLight( 1, 0,-1);
		
		tl  =  chunkData.getReferenceLight(-1, 1, 0);
		tb  =  chunkData.getReferenceLight( 0, 1,-1);
		tclb = chunkData.getReferenceLight(-1, 1,-1);
		tcbr = chunkData.getReferenceLight( 1, 1,-1);

		tr  =  chunkData.getReferenceLight( 1, 1, 0);
		tf  =  chunkData.getReferenceLight( 0, 1, 1);
		tcrf = chunkData.getReferenceLight( 1, 1, 1);
		tcfl = chunkData.getReferenceLight(-1, 1, 1);
		
		bl  =  chunkData.getReferenceLight(-1,-1, 0); 
		bb  =  chunkData.getReferenceLight( 0,-1,-1);
		bcbl = chunkData.getReferenceLight(-1,-1,-1);
		bcrb = chunkData.getReferenceLight( 1,-1,-1);

		br  =  chunkData.getReferenceLight( 1,-1, 0);
		bf  =  chunkData.getReferenceLight( 0,-1, 1);				
		bcfr = chunkData.getReferenceLight( 1,-1, 1);
		bclf = chunkData.getReferenceLight(-1,-1, 1);

	}

	public void createTop(ChunkData chunkData) {

		final TextureCoordinates tRegion = textureRegions[0];
		
		byte next = chunkData.getReferenceLight(0, 1, 0);

		LightInfo lf00 = lightInfo0.set(1, next, tl, tclb, tb),
			      lf10 = lightInfo1.set(1, next, tb, tcbr, tr),
			      lf11 = lightInfo2.set(1, next, tr, tcrf, tf),
			      lf01 = lightInfo3.set(1, next, tf, tcfl, tl);

		face(pv010, lf00, tRegion.u, tRegion.v, pv110, lf10, tRegion.u2, tRegion.v, pv111, lf11, tRegion.u2, tRegion.v2, pv011, lf01, tRegion.u,  tRegion.v2);

		if (lf00.getMaxLight() + lf11.getMaxLight() < lf01.getMaxLight() + lf10.getMaxLight()) {
			// generate flipped quad
			index((short) (3 + iOff), (short) (2 + iOff), (short) (1 + iOff), (short) (1 + iOff), iOff, (short) (3 + iOff));
		} else {
			// generate normal quad
			index(iOff, (short) (iOff + 3), (short) (iOff + 2), (short) (iOff + 2), (short) (iOff + 1), iOff);
		}

		iOff += 4;

	}

	public void createBottom(ChunkData chunkData) { // TODO

		final TextureCoordinates tRegion = textureRegions[1];
		
		byte next = chunkData.getReferenceLight(0, -1, 0);
	
		LightInfo lf00 = lightInfo0.set(0.76f, next, br, bcrb, bb),
				  lf10 = lightInfo1.set(0.76f, next, bb, bcbl, bl),
				  lf11 = lightInfo2.set(0.76f, next, bl, bclf, bf),
				  lf01 = lightInfo3.set(0.76f, next, bf, bcfr, br);

		face(pv100, lf00, tRegion.u, tRegion.v, pv000, lf10, tRegion.u2, tRegion.v, pv001, lf11, tRegion.u2, tRegion.v2, pv101, lf01, tRegion.u, tRegion.v2);

		if (lf00.getMaxLight() + lf11.getMaxLight() < lf01.getMaxLight() + lf10.getMaxLight()) {
			// generate flipped quad
			index((short) (3 + iOff), (short) (2 + iOff), (short) (1 + iOff), (short) (1 + iOff), iOff, (short) (3 + iOff));
		} else {
			// generate normal quad
			index(iOff, (short) (iOff + 3), (short) (iOff + 2), (short) (iOff + 2), (short) (iOff + 1), iOff);
		}

		iOff += 4;

	}

	public void createFront(ChunkData chunkData){

		final TextureCoordinates tRegion = textureRegions[2];
		
		byte next = chunkData.getReferenceLight(0, 0, 1);
			
		LightInfo lf00 = lightInfo0.set(0.92f, next, cfl, tcfl, tf);
		LightInfo lf10 = lightInfo1.set(0.92f, next, tf , tcrf, cfr);
		LightInfo lf11 = lightInfo2.set(0.92f, next, cfr, bcfr, bf);
		LightInfo lf01 = lightInfo3.set(0.92f, next, bf , bclf, cfl);

		face(pv011, lf00, tRegion.u, tRegion.v, pv111, lf10, tRegion.u2, tRegion.v, pv101, lf11, tRegion.u2, tRegion.v2, pv001, lf01, tRegion.u, tRegion.v2);

		if (lf00.getMaxLight() + lf11.getMaxLight() < lf01.getMaxLight() + lf10.getMaxLight()) {
			// generate flipped quad
			index((short) (3 + iOff), (short) (2 + iOff), (short) (1 + iOff), (short) (1 + iOff), iOff, (short) (3 + iOff));
		} else {
			// generate normal quad
			index(iOff, (short) (iOff + 3), (short) (iOff + 2), (short) (iOff + 2), (short) (iOff + 1), iOff);
		}

		iOff += 4;

	}

	public void createBack(ChunkData chunkData){
		
		final TextureCoordinates tRegion = textureRegions[3];
		byte next = chunkData.getReferenceLight(0, 0, -1);
		
		LightInfo lf00 = lightInfo0.set(0.92f, next, cbr, tcbr, tb);
		LightInfo lf10 = lightInfo1.set(0.92f, next, tb , tclb, cbl);
		LightInfo lf11 = lightInfo2.set(0.92f, next, cbl, bcbl, bb);
		LightInfo lf01 = lightInfo3.set(0.92f, next, bb , bcrb, cbr);

		face(pv110, lf00, tRegion.u, tRegion.v, pv010, lf10, tRegion.u2, tRegion.v, pv000, lf11, tRegion.u2, tRegion.v2, pv100, lf01, tRegion.u, tRegion.v2);

		if (lf00.getMaxLight() + lf11.getMaxLight() < lf01.getMaxLight() + lf10.getMaxLight()) {
			// generate flipped quad
			index((short) (3 + iOff), (short) (2 + iOff), (short) (1 + iOff), (short) (1 + iOff), iOff, (short) (3 + iOff));
		} else {
			// generate normal quad
			index(iOff, (short) (iOff + 3), (short) (iOff + 2), (short) (iOff + 2), (short) (iOff + 1), iOff);
		}

		iOff += 4;

	}

	public void createRight(ChunkData chunkData){

		final TextureCoordinates tRegion = textureRegions[4];
		
		byte next = chunkData.getReferenceLight(1, 0, 0);
		
		LightInfo lf00 = lightInfo0.set(0.84f, next, cfr, tcrf, tr);
		LightInfo lf10 = lightInfo1.set(0.84f, next, tr , tcbr, cbr);
		LightInfo lf11 = lightInfo2.set(0.84f, next, cbr, bcrb, br);
		LightInfo lf01 = lightInfo3.set(0.84f, next, br , bcfr, cfr);

		face(pv111, lf00, tRegion.u, tRegion.v, pv110, lf10, tRegion.u2, tRegion.v, pv100, lf11, tRegion.u2, tRegion.v2, pv101, lf01, tRegion.u, tRegion.v2);

		if (lf00.getMaxLight() + lf11.getMaxLight() < lf01.getMaxLight() + lf10.getMaxLight()) {
			// generate flipped quad
			index((short) (3 + iOff), (short) (2 + iOff), (short) (1 + iOff), (short) (1 + iOff), iOff, (short) (3 + iOff));
		} else {
			// generate normal quad
			index(iOff, (short) (iOff + 3), (short) (iOff + 2), (short) (iOff + 2), (short) (iOff + 1), iOff);
		}

		iOff += 4;

	}

	public void createLeft(ChunkData chunkData){

		final TextureCoordinates tRegion = textureRegions[5];
	
		byte next = chunkData.getReferenceLight(-1, 0, 0);
	
		LightInfo lf00 = lightInfo0.set(0.84f, next, cbl, tclb, tl);
		LightInfo lf10 = lightInfo1.set(0.84f, next, tl , tcfl, cfl);
		LightInfo lf11 = lightInfo2.set(0.84f, next, cfl, bclf, bl);
		LightInfo lf01 = lightInfo3.set(0.84f, next, bl , bcbl, cbl);

		face(pv010, lf00, tRegion.u, tRegion.v, pv011, lf10, tRegion.u2, tRegion.v, pv001, lf11, tRegion.u2, tRegion.v2, pv000, lf01, tRegion.u, tRegion.v2);

		if (lf00.getMaxLight() + lf11.getMaxLight() < lf01.getMaxLight() + lf10.getMaxLight()) {
			// generate flipped quad
			index((short) (3 + iOff), (short) (2 + iOff), (short) (1 + iOff), (short) (1 + iOff), iOff, (short) (3 + iOff));
		} else {
			// generate normal quad
			index(iOff, (short) (iOff + 3), (short) (iOff + 2), (short) (iOff + 2), (short) (iOff + 1), iOff);
		}

		iOff += 4;

	}

}

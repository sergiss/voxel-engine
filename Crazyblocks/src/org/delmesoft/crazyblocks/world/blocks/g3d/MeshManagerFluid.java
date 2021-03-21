package org.delmesoft.crazyblocks.world.blocks.g3d;

import org.delmesoft.crazyblocks.graphics.g2d.TextureCoordinates;
import org.delmesoft.crazyblocks.world.blocks.Blocks;
import org.delmesoft.crazyblocks.world.blocks.Chunk;
import org.delmesoft.crazyblocks.world.blocks.utils.ChunkData;
import org.delmesoft.crazyblocks.world.blocks.utils.generators.FluidPoint;

/**
 * Created by sergi on 25/09/17.
 */

public class MeshManagerFluid extends MeshManagerSmoothLighting {

    public void begin(float x, float y, float z, short rawType, ChunkData chunkData) {
        Blocks.Block block = Blocks.values[rawType & 0xFF];
        textureRegions = block.textureRegions;

        // TODO : dirección

        final float px = x + 1F;
        final float py1 = y + 1F;
        final float py2 = py1 - yOff;
        final float pz = z + 1F;

        pv000.set(x,  y, z);  // back , left
        pv001.set(x,  y, pz); // front, left
        pv101.set(px, y, pz); // front, right
        pv100.set(px, y, z);  // back , right

        if(y == Chunk.VERTICAL_SIZE - 1 || (ChunkData.getType(chunkData.getReferenceRawType( 0, 1, 0))) == block.id) { // up water

            pv010.set(x , py1, z);  // back , left
            pv011.set(x , py1, pz); // front, left
            pv111.set(px, py1, pz); // front, right
            pv110.set(px, py1, z);  // back , right

        } else {

            int height = FluidPoint.getDepth(ChunkData.getData(rawType));

            short d1 = chunkData.getReferenceRawType( 0, 0,-1); // back
            short d2 = chunkData.getReferenceRawType( 0, 0, 1); // front
            short d3 = chunkData.getReferenceRawType(-1, 0, 0); // left
            short d4 = chunkData.getReferenceRawType( 1, 0, 0); // right

            short s1 = chunkData.getReferenceRawType( 0, 1,-1); // back
            short s2 = chunkData.getReferenceRawType( 0, 1, 1); // front
            short s3 = chunkData.getReferenceRawType(-1, 1, 0); // left
            short s4 = chunkData.getReferenceRawType( 1, 1, 0); // right

            if(ChunkData.getType(s1) == block.id || ChunkData.getType(s3) == block.id || ChunkData.getType(chunkData.getReferenceRawType(-1, 1,-1)) == block.id) {
                pv010.set(x, py1, z);  // back, left
            } else {
                pv010.set(x, py2 - yOff * getHeight(d1, d3, chunkData.getReferenceRawType(-1, 0, -1), block.id, height), z); // back, left
            }

            if(ChunkData.getType(s2) == block.id || ChunkData.getType(s3) == block.id || ChunkData.getType(chunkData.getReferenceRawType(-1, 1, 1)) == block.id) {
                pv011.set(x, py1, pz); // front, left
            } else {
                pv011.set(x, py2 - yOff * getHeight(d2, d3, chunkData.getReferenceRawType(-1, 0, 1), block.id, height), pz); // front, left
            }

            if(ChunkData.getType(s2) == block.id || ChunkData.getType(s4) == block.id || ChunkData.getType(chunkData.getReferenceRawType( 1, 1, 1)) == block.id) {
                pv111.set(px, py1, pz); // front, right
            } else {
                pv111.set(px, py2 - yOff * getHeight(d2, d4, chunkData.getReferenceRawType(1, 0, 1), block.id, height), pz); // front, right
            }

            if(ChunkData.getType(s1) == block.id || ChunkData.getType(s4) == block.id || ChunkData.getType(chunkData.getReferenceRawType( 1, 1,-1)) == block.id) {
                pv110.set(px, py1, z); // back, right
            } else {
                pv110.set(px, py2 - yOff * getHeight(d4, d1, chunkData.getReferenceRawType(1, 0, -1), block.id, height), z); // back, right
            }

        }

        tl  =  chunkData.getReferenceLight(-1, 1, 0);
        tb  =  chunkData.getReferenceLight( 0, 1,-1);
        tclb = chunkData.getReferenceLight(-1, 1,-1);
        tcbr = chunkData.getReferenceLight( 1, 1,-1);

        tr  =  chunkData.getReferenceLight( 1, 1, 0);
        tf  =  chunkData.getReferenceLight( 0, 1, 1);
        tcrf = chunkData.getReferenceLight( 1, 1, 1);
        tcfl = chunkData.getReferenceLight(-1, 1, 1);

    }

    private int getHeight(short d1, short d2, short d3, int blockId, int height) {

        int v1 = ChunkData.getType(d1) == blockId ? FluidPoint.getDepth(ChunkData.getData(d1)) : 16;
        int v2 = ChunkData.getType(d2) == blockId ? FluidPoint.getDepth(ChunkData.getData(d2)) : 16;
        int v3 = ChunkData.getType(d3) == blockId ? FluidPoint.getDepth(ChunkData.getData(d3)) : 16;

        int r1 = v1 < v2 ? v1 : v2;
        int r2 = r1 < v3 ? r1 : v3;
        return r2 < height ? r2 : height;
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

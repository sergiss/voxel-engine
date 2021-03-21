package org.delmesoft.crazyblocks.world.blocks.g3d;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;

import org.delmesoft.crazyblocks.graphics.g2d.TextureCoordinates;
import org.delmesoft.crazyblocks.graphics.g3d.MeshBuilder;
import org.delmesoft.crazyblocks.world.blocks.Blocks;
import org.delmesoft.crazyblocks.world.blocks.Blocks.Block;
import org.delmesoft.crazyblocks.world.blocks.utils.ChunkData;

public abstract class MeshManager extends MeshBuilder {
	
	public static final VertexAttributes VERTEX_ATTRIBUTES =  new VertexAttributes(new VertexAttribute(Usage.Position, 		     3, ShaderProgram.POSITION_ATTRIBUTE), 	    // 32 bits * 3
																				   new VertexAttribute(Usage.Generic,  		     2, "a_lighting"),    					    // 32 bits * 2
																				   new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE+"0")); // 32 bits * 2
	
	public static final float ONE   =  (1 / 16f);
	public static final float TWO   =  (1 / 32f);
	public static final float TREE  =  (1 / 48f);
	public static final float FOUR  =  (1 / 64f);
	
	protected final LightInfo lightInfo0 = new LightInfo();
	protected final LightInfo lightInfo1 = new LightInfo();
	protected final LightInfo lightInfo2 = new LightInfo();
	protected final LightInfo lightInfo3 = new LightInfo();
	
	protected final Vector3 pv000 = new Vector3();
	protected final Vector3 pv001 = new Vector3();
	protected final Vector3 pv101 = new Vector3();
	protected final Vector3 pv100 = new Vector3();
	protected final Vector3 pv110 = new Vector3();
	protected final Vector3 pv111 = new Vector3();
	protected final Vector3 pv011 = new Vector3();
	protected final Vector3 pv010 = new Vector3();

	public float yOff;
		
	public MeshManager() {
		super(VERTEX_ATTRIBUTES);		
	}
	
	public abstract void begin(float x, float y, float z, short rawType, ChunkData chunkData);

	public abstract void createTop(ChunkData chunkData);

	public abstract void createBottom(ChunkData chunkData);

	public abstract void createFront(ChunkData chunkData);

	public abstract  void createBack(ChunkData chunkData);

	public abstract  void createRight(ChunkData chunkData);

	public abstract  void createLeft(ChunkData chunkData);
	
	public class LightInfo {
		
		public float blockLight;
		public float skyLight;
		
		public LightInfo() {}
		
		public LightInfo set(float scl, byte light) {
			scl *= ONE;
			skyLight   = ( (light & 0xF )        ) * scl;
			blockLight = (((light & 0xF0) >>> 4) ) * scl;
			return this;
		}
		
		public LightInfo set(float scl, byte light1, byte light2, byte light3, byte light4) {
			scl *= FOUR;
			skyLight   = ( (light1 & 0xF )        +  (light2 & 0xF)         +  (light3 & 0xF)         +  (light4 & 0xF)        ) * scl;
			blockLight = (((light1 & 0xF0) >>> 4) + ((light2 & 0xF0) >>> 4) + ((light3 & 0xF0) >>> 4) + ((light4 & 0xF0) >>> 4)) * scl;

			return this;
		}
		
		public float getMaxLight() {
			return blockLight > skyLight ? blockLight : skyLight;
		}
		
	}
		
	public void crossPlant(int x, int y, int z, short rawType, ChunkData chunkData) {
		
		final TextureCoordinates tRegion = Blocks.values[rawType & 0xFF].textureRegions[2];
		
		LightInfo lightInfo = lightInfo0.set(1, chunkData.getReferenceLight(0, 0, 0));

		face(x    , y + 1, z + 0.5f, tRegion.u,  tRegion.v, 
			 x + 1, y + 1, z + 0.5f, tRegion.u2, tRegion.v, 
			 x + 1, y    , z + 0.5f, tRegion.u2, tRegion.v2, 
			 x    , y    , z + 0.5f, tRegion.u,  tRegion.v2, lightInfo.blockLight, lightInfo.skyLight);
		
		// Generate normal quad
		index(iOff, (short) (iOff + 3), (short) (iOff + 2), (short) (iOff + 2), (short) (iOff + 1), iOff);
		iOff += 4;
		
		face(x + 0.5f, y + 1, z + 1, tRegion.u,  tRegion.v, 
			 x + 0.5f, y + 1, z    , tRegion.u2, tRegion.v, 
			 x + 0.5f, y    , z    , tRegion.u2, tRegion.v2, 
			 x + 0.5f, y    , z + 1, tRegion.u,  tRegion.v2, lightInfo.blockLight, lightInfo.skyLight);
		
		// Generate normal quad
		index(iOff, (short) (iOff + 3), (short) (iOff + 2), (short) (iOff + 2), (short) (iOff + 1), iOff);
		iOff += 4;	
		
	}
	
}

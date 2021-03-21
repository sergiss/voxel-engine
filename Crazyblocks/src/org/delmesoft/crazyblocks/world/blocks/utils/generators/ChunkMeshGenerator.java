package org.delmesoft.crazyblocks.world.blocks.utils.generators;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import org.delmesoft.crazyblocks.world.blocks.Chunk;

public abstract class ChunkMeshGenerator {
	
	protected final float[] vertices;
	protected int vertexCount;
	
	protected final short[] indices;
	protected int indexCount;

	protected VertexAttributes attributes;
	protected int stride;
	
	public short iOff;
	
	public ChunkMeshGenerator() {
		
		VertexAttribute[] attributes = new VertexAttribute[] {
				
				  new VertexAttribute(Usage.Position, 			3, ShaderProgram.POSITION_ATTRIBUTE), 	   // 32 bits * 3
				  new VertexAttribute(Usage.Generic,   			2, "a_lighting"),    					   // 32 bits * 2
				  new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE+"0")   // 32 bits * 2
				  
		};
		
		this.attributes = new VertexAttributes(attributes);
		stride = this.attributes.vertexSize / 4;
		
		vertices = new float[stride * 4 * 6 * Chunk.BLOCK_COUNT];
		indices  = new short[0];
		
	}	

}

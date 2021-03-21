package org.delmesoft.crazyblocks.graphics.g3d.decal;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;

import org.delmesoft.crazyblocks.entity.Entity;
import org.delmesoft.crazyblocks.graphics.Renderer;
import org.delmesoft.crazyblocks.graphics.g2d.TextureCoordinates;
import org.delmesoft.crazyblocks.math.MathHelper;
import org.delmesoft.crazyblocks.utils.FloatColors;
import org.delmesoft.crazyblocks.world.blocks.Chunk;
import org.delmesoft.crazyblocks.world.blocks.utils.ChunkData;

public class Decal extends Entity {

	public static Vector3 tmpVec3 = new Vector3();

	public Texture texture;

	public TextureCoordinates textureCoordinates;

	public float scale;

	public float color = FloatColors.WHITE;


	public Decal(Texture texture, TextureCoordinates textureCoordinates) {
		this(0, 0, 0, texture.getWidth(), texture.getHeight(), texture, textureCoordinates);
	}
	public Decal(float x, float y, float z, Texture texture, TextureCoordinates textureCoordinates) {
		this(x, y, z, texture.getWidth(), texture.getHeight(), texture, textureCoordinates);
	}
	
	public Decal(float x, float y, float z, float width, float height, Texture texture, TextureCoordinates textureCoordinates) {
		super(x, y, z, width, height, width);
		scale = 1f;
		this.texture = texture;
		this.textureCoordinates = textureCoordinates;
	}

	@Override
	public void render(Renderer renderer) {

		float hw = width * 0.5F, hh = height * 0.5F;

		// center
		float x = hw + this.x,
			  y = hh + this.y,
			  z = hw + this.z;

		renderer.getDecalRenderer().setRotation(tmpVec3.set(renderer.getCamera().position).sub(x, y, z).nor(), renderer.getCamera().up);

		float hs = scale * 0.5F;

		renderer.getDecalRenderer().render(texture,
										   x, y, z,
										   (textureCoordinates.u2 - textureCoordinates.u) * hs,
										   (textureCoordinates.v2 - textureCoordinates.v) * hs,
							 			   getLight(x, y, z, renderer.getAmbientLight()),
				                           textureCoordinates.u, textureCoordinates.v, textureCoordinates.u2, textureCoordinates.v2);
		
	}

	protected float getLight(float x, float y, float z, float light) {
		int ix = MathHelper.fastFloor(x);
		int iz = MathHelper.fastFloor(z);
		Chunk chunk = world.getChunkAbsolute(ix, iz);
		if(chunk != null) {
			int index = ChunkData.positionToIndex(ix - chunk.worldX, (int) Math.min(Math.max(1, y), Chunk.VERTICAL_SIZE - 1), iz - chunk.worldZ);
			return Math.max(chunk.chunkData.getSkyLight(index) * light, chunk.chunkData.getBlockLight(index)) / 15F;
		}
		return 0F;
	}

	@Override
	public void tick(float delta) {

	}

	public boolean isVisible(Camera camera) {
		float hs = scale * 0.5f;
		float hw = hs * width;
		float hh = hs * height;
		return camera.frustum.boundsInFrustum(x + hw, y + hh, z + hw, hw, hh, hw);
	}

}
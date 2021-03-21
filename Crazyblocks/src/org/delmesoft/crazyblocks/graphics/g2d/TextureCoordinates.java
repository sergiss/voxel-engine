package org.delmesoft.crazyblocks.graphics.g2d;

import com.badlogic.gdx.graphics.Texture;

import org.delmesoft.crazyblocks.world.blocks.Blocks;

public class TextureCoordinates {
	
	public float u, v, u2, v2;
	
	public TextureCoordinates() {}
	
	public TextureCoordinates(float u, float v, float u2, float v2) {
		this.u  = u;
		this.v  = v;
		this.u2 = u2;
		this.v2 = v2;
	}

	public TextureCoordinates[] split(int cols, int rows) {

		TextureCoordinates[] textureCoordinates = new TextureCoordinates[cols * rows];
		float w = (u2 - u) / cols;
		float h = (v2 - v) / rows;
		float u, v;
		for(int x = 0; x < cols; ++x) {
			for(int y = 0; y < cols; ++y) {
				u = x * w + this.u;
				v = y * h + this.v;
				textureCoordinates[x * rows + y] = new TextureCoordinates(u, v, u + w, v + h);
			}
		}

		return textureCoordinates;
	}

   /* public static TextureCoordinates getTile(Texture texture, int cols, int rows, int padding, int index) {

		float tw = 1F / ((texture.getWidth()  - (padding * (cols << 1))) / cols);
		float th = 1F / ((texture.getHeight() - (padding * (rows << 1))) / rows);

		TextureCoordinates textureCoords = new TextureCoordinates();
		int col = (index / rows);
		textureCoords.u  = tw * col + 1F/(padding * (col << 1));
		textureCoords.u2 = textureCoords.u + tw;
		int row = (index % rows);
		textureCoords.v  = th * row + 1F/(padding * (row << 1));
		textureCoords.v2 = textureCoords.v + th;

		return textureCoords;
    }*/

}

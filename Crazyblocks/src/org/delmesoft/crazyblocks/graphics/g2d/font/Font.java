package org.delmesoft.crazyblocks.graphics.g2d.font;

import org.delmesoft.crazyblocks.graphics.g2d.TextureCoordinates;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

public class Font implements Disposable {
	
	protected Texture texture;
	protected TextureCoordinates[] regions;
	
	protected int charWidth, charHeight;

	public float scale = 1f;
	
	public Font(TextureRegion textureRegion, int cols, int rows) {
	
		final Texture texture = textureRegion.getTexture();
		
		float invTexWidth  = 1f / texture.getWidth();
		float invTexHeight = 1f / texture.getHeight();

		int x = textureRegion.getRegionX();
		int y = textureRegion.getRegionY();

		int tileWidth  = textureRegion.getRegionWidth() / cols;
		int tileHeight = textureRegion.getRegionHeight() / rows;

		int startX = x;

		TextureCoordinates[] regions = new TextureCoordinates[rows * cols];
		
		int index = 0;
		for (int row = 0; row < rows; row++, y += tileHeight) {
			x = startX;
			float ry = y * invTexHeight;
			float rh = (y + tileHeight) * invTexHeight;
			for (int col = 0; col < cols; col++, x += tileWidth) {
				regions[index++] = new TextureCoordinates(x * invTexWidth, ry, (x + tileWidth) * invTexWidth, rh);
			}
		}
		
		charWidth  = tileWidth;
		charHeight = tileHeight;
		
		this.regions = regions;
		this.texture = texture;
		
	}	
		
	public void render(CharSequence text, float x, float y, Batch batch) {
		
		float width  = getCharWidth();
		float height = getCharHeight();

		float dspX = 0;

		Texture texture = this.texture;
		TextureCoordinates[] regions = this.regions;
		
		TextureCoordinates tc;

		for(int i = 0, n = text.length(); i < n; i++) {
		
			tc = regions[text.charAt(i)];	
		
			batch.draw(texture, x + dspX, y, width, height, tc.u, tc.v2, tc.u2, tc.v);
			dspX += width;

		}
		
	}
	
	public float getCharWidth() {
		return charWidth * scale;
	}

	public float getCharHeight() {
		return charHeight * scale;
	}
	
	public Texture getTexture() {
		return texture;
	}

	public TextureCoordinates[] getTextureCoordinates() {
		return regions;
	}	

	@Override
	public void dispose() {
		for(int i = 0, n = regions.length; i < n; i++) {
			regions[i] = null;
		}
		regions = null;
	}

}

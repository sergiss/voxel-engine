package org.delmesoft.crazyblocks.utils;

import org.delmesoft.crazyblocks.graphics.g2d.font.Font;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class DebugHelper {
	
	private static final String defaultFont = "data/resources/font.png";
	private static final Font font;	
	
	private static Batch batch;
	
	static {
		font = new Font(new TextureRegion(new Texture(Gdx.files.internal(defaultFont))), 16, 16);
		font.scale = 0.55f;
		batch = new SpriteBatch();
	}
	
	public static void drawText(CharSequence charSequence, float x, float y) {
	
		batch.begin();
		font.render(charSequence, x, y, batch);
		batch.end();
		
	}
	
	public static Font getFont() {
		return font;
	}

}

package org.delmesoft.crazyblocks.world.blocks;

import java.io.IOException;

import org.delmesoft.crazyblocks.assets.FileManager;
import org.delmesoft.crazyblocks.graphics.g2d.TextureCoordinates;
import org.delmesoft.crazyblocks.utils.PixmapUtils;
import org.delmesoft.crazyblocks.world.Settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class Blocks {

	public static final byte NULL_LIGHT = 0;
	public static final byte SUN_LIGHT = 15;
	public static final byte MAX_LIGHT_RESISTANCE = SUN_LIGHT;
	
	public static final int PADDING = 2;
	
	public static final Block AIR, BEDROCK, WATER, GRASS, DIRT, STONE, SAND, WOOD, LEAVES, GLASS, SNOW, FLOWER1, FLOWER2, BUSH, TORCH, LAVA;

	public static final Block[] values;

	static {

		Block[] blocks = {
				
				AIR      = new Block( (byte) 0,  "Air",     false, 0, (byte) 1),
				BEDROCK  = new Block( (byte) 1,  "Bedrock", false, 1, MAX_LIGHT_RESISTANCE),
				WATER    = new Block1((byte) 2,  "Water",   false, 2, (byte) 3),
				GRASS    = new Block( (byte) 3,  "Grass",   false, 1, MAX_LIGHT_RESISTANCE),
				DIRT     = new Block( (byte) 4,  "Dirt",    false, 1, MAX_LIGHT_RESISTANCE),
				STONE    = new Block( (byte) 5,  "Stone",   false, 1, MAX_LIGHT_RESISTANCE),
				SAND     = new Block( (byte) 6,  "Sand",    false, 1, MAX_LIGHT_RESISTANCE),
				WOOD     = new Block( (byte) 7,  "Wood",    false, 1, MAX_LIGHT_RESISTANCE),
				LEAVES   = new Block2((byte) 8,  "Leaves",  false, 1, (byte) 2),
				FLOWER1  = new Block2((byte) 9,  "Flower1", false, 3, (byte) 1),
				FLOWER2  = new Block2((byte) 10, "Flower2", false, 3, (byte) 1),
				BUSH     = new Block2((byte) 11, "Bush",    false, 3, (byte) 1),
				GLASS    = new Block1((byte) 12, "Glass",   true,  1, (byte) 1),
				SNOW     = new Block( (byte) 13, "Snow",    false, 1, MAX_LIGHT_RESISTANCE),
				TORCH    = new Block2((byte) 14, "Torch",   true,  4, (byte) 1),
				LAVA     = new Block1((byte) 15,  "Lava",   true,  2, (byte) 3)
				
		};

		values = blocks;

	}
		
	/* Generic block */
	public static class Block {

		public final String name;
		public final byte id;
		public final TextureCoordinates[] textureRegions;
		
		public boolean opaque;
		public boolean lightEmmiter;
		
		public boolean fixed;
		public int resistance;

		public final int renderMode;
		
		public final byte lightResistance;
		
		public Block(byte id, String name, boolean lightEmitter, int renderMode, byte lightResistance) {

			this.id = id;
			
			this.name = name;
			
			this.opaque = lightResistance == MAX_LIGHT_RESISTANCE;
			
			this.lightEmmiter = lightEmitter;
			
			this.renderMode = renderMode;
			
			this.lightResistance = lightResistance;
			
			textureRegions = new TextureCoordinates[6];
							
		}
		
		public static int getUpperOf(int id) {
			
			if(id == DIRT.id){
				return GRASS.id;
			}
			
			return id;
		}
		
		public boolean equals(Block block) {
			return block.opaque == opaque;
		}
		
		@Override
		public String toString() {
			return "Block: " + name;
		}
		
	}
		
	static class Block1 extends Block { // liquid, glass

		public Block1(byte id, String name, boolean lightEmitter, int renderMode, byte lightResistance) {
			super(id, name, lightEmitter, renderMode, lightResistance);
		}
		
		@Override
		public boolean equals(Block block) {
			return block.renderMode == renderMode;
		}
		
	}
	
	static class Block2 extends Block { // leaves, torch

		public Block2(byte id, String name, boolean lightEmitter, int renderMode, byte lightResistance) {
			super(id, name, lightEmitter, renderMode, lightResistance);
		}
		
		@Override
		public boolean equals(Block block) {
			return block.id == id;
			//return false;
		}
		
	}
	
	public enum Side {

		BOTTOM(0, -1, 0), TOP(0, 1, 0), LEFT(-1, 0, 0), RIGHT(1, 0, 0), BACK(0, 0, -1), FRONT(0, 0, 1);
		
		public final int x, y, z;

		Side (int x, int y, int z){
			this.x = x;
			this.y = y;
			this.z = z;
		}
				
	}

	//private static int[] filters = {GL20.GL_NEAREST, GL20.GL_LINEAR, GL20.GL_NEAREST_MIPMAP_NEAREST, GL20.GL_LINEAR_MIPMAP_NEAREST, GL20.GL_LINEAR_MIPMAP_LINEAR};

	public static void setBlocks(String path, FileManager fileManager) {

		final XmlReader xmlReader = new XmlReader();

		Element root = null;
		try {
			root = xmlReader.parse(Gdx.files.internal(path));
		} catch (IOException e) {
			e.printStackTrace();
		}

		final Element textureFile = root.getChildByName("textureFile");
		String texturePath = textureFile.get("texture");
		int textureCols = Integer.valueOf(textureFile.get("cols"));
		int textureRows = Integer.valueOf(textureFile.get("rows"));

		Pixmap srcPixmap = fileManager.get(texturePath, Pixmap.class);

		float padding = PADDING;

		Pixmap pixmap = PixmapUtils.copyTiledPixmap(srcPixmap, textureCols, textureRows, 0, PADDING);

		final Texture texture = new Texture(new PixmapTextureData(pixmap, pixmap.getFormat(), false, true));
		//texture.setFilter(TextureFilter.MipMapNearestLinear, TextureFilter.Nearest);
		//Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL30.GL_TEXTURE_BASE_LEVEL, 0);
		//Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAX_LEVEL , 3);
		//filter(texture, 4);

	/*	// http://gregs-blog.com/2008/01/17/opengl-texture-filter-parameters-explained/
		texture.setFilter(TextureFilter.MipMapNearestLinear, TextureFilter.Nearest);

		Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL30.GL_TEXTURE_BASE_LEVEL, 0);
		Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAX_LEVEL, 3);*/

		Settings.worldTexture = texture;

		final float tWidth  = srcPixmap.getWidth()  / textureCols;
		final float tHeight = srcPixmap.getHeight() / textureRows;

		String[] sidesId = new String[]{"top", "bottom", "front", "back", "right", "left"};
		Block[] values = Blocks.values;

		for(int i = 0; i < values.length; i++) {

			Block block = values[i];

			if(block.renderMode < 4) {

				Element xmlBlock = root.getChildByName(block.name.toLowerCase());
				ObjectMap<String, String> attributes = xmlBlock.getAttributes();

				TextureCoordinates[] textureRegions = block.textureRegions;

				TextureCoordinates tRegion;

				if(attributes.containsKey("all")) {

					int index = Integer.valueOf(attributes.get("all"));

					int x = index % textureCols;
					int y = index / textureCols;

					tRegion = new TextureCoordinates();

					tRegion.u =  (x * tWidth  + (padding * 2f * x)) / texture.getWidth()  + padding / texture.getWidth();
					tRegion.v =  (y * tHeight + (padding * 2f * y)) / texture.getHeight() + padding / texture.getHeight();
					tRegion.u2 = tRegion.u + (tWidth  / texture.getWidth());
					tRegion.v2 = tRegion.v + (tHeight / texture.getHeight());

					for(int j = 0; j < textureRegions.length; j++) {
						textureRegions[j] = tRegion;	
					}

				} else {

					for(int j = 0; j < textureRegions.length; j++) {

						tRegion = textureRegions[j];
						if (tRegion == null) {
							tRegion = new TextureCoordinates();
							textureRegions[j] = tRegion;
						}

						int index = Integer.valueOf(attributes.get(sidesId[j]));

						int x = index % textureCols;
						int y = index / textureCols;
						tRegion.u =  (x * tWidth  + (padding * 2f * x )) / texture.getWidth()  + (padding / texture.getWidth());
						tRegion.v =  (y * tHeight + (padding * 2f * y )) / texture.getHeight() + (padding / texture.getHeight());
						tRegion.u2 = tRegion.u + (tWidth  / texture.getWidth());
						tRegion.v2 = tRegion.v + (tHeight / texture.getHeight());

					}

				}
			}
		}
		
	}

}

package org.delmesoft.crazyblocks.world.blocks.g3d.environment;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

import org.delmesoft.crazyblocks.graphics.Renderer;
import org.delmesoft.crazyblocks.graphics.g3d.FastMesh;
import org.delmesoft.crazyblocks.graphics.g3d.MeshBuilder;
import org.delmesoft.crazyblocks.graphics.g3d.ShaderProgram;
import org.delmesoft.crazyblocks.graphics.g3d.decal.Decal;
import org.delmesoft.crazyblocks.graphics.g3d.decal.DecalRenderer;
import org.delmesoft.crazyblocks.math.MathHelper;
import org.delmesoft.crazyblocks.screen.GameScreen;
import org.delmesoft.crazyblocks.utils.datastructure.Array;
import org.delmesoft.crazyblocks.world.Settings;
import org.delmesoft.crazyblocks.world.World;
import org.delmesoft.crazyblocks.world.blocks.Blocks;
import org.delmesoft.crazyblocks.world.blocks.Chunk;
import org.delmesoft.crazyblocks.world.blocks.utils.noise.ImprovedNoise;
import org.delmesoft.crazyblocks.world.blocks.utils.noise.PerlinNoise;

import static org.delmesoft.crazyblocks.math.MathHelper.fastFloor;

public class Environment implements Disposable {

	private int u_worldView;

	private Matrix4 transform;
	private Matrix4 worldView1, worldView2;

	private ShaderProgram sunMoonProgram;
	private Texture sunMoonTexture;
	private FastMesh sunMoonMesh;
	
	private ShaderProgram skyProgram;
	private Texture skyTexture;
	private FastMesh skyMesh;

	private ShaderProgram horizonProgram;
	private FastMesh horizonMesh;
		
	public float sunLight = 1f;
	
	private ShaderProgram cloudProgram;
	private Array<Cloud> clouds;

	private Weather rainDecal, snowDecal;

	private float sv, sv2;
	private double time;
	
	public Environment(World world) {

		VertexAttributes vertexAttributes = new VertexAttributes(VertexAttribute.Position(), VertexAttribute.TexCoords(0));

		sunMoonProgram = new ShaderProgram(vsSunMoon, fsSunMoon);

		u_worldView = sunMoonProgram.getUniformLocation("u_worldView");

		transform  = new Matrix4();
		worldView1 = new Matrix4();
		worldView2 = new Matrix4();

		sunMoonTexture = new Texture(Gdx.files.internal("data/resources/sky2.png"));
		
		MeshBuilder meshBuilder = new MeshBuilder(vertexAttributes);
		
		short iOff = 0;
		
		meshBuilder.vertex(-0.5f, 1f, -0.5f,  0.5f, 0.0f,
						    0.5f, 1f, -0.5f,  1.0f, 0.0f,
						    0.5f, 1f,  0.5f,  1.0f, 1.0f,
						   -0.5f, 1f,  0.5f,  0.5f, 1.0f);
				
		meshBuilder.index(iOff, (short) (iOff + 3), (short) (iOff + 2), (short) (iOff + 2), (short) (iOff + 1), iOff);
		
		iOff += 4;
		
		meshBuilder.vertex(
						  0.5f, -1f, -0.5f, 0.5f, 0.0f,
						 -0.5f, -1f, -0.5f, 0.0f, 0.0f,
						 -0.5f, -1f,  0.5f, 0.0f, 1.0f,
						  0.5f, -1f,  0.5f, 0.5f, 1.0f);
		
		meshBuilder.index(iOff, (short) (iOff + 3), (short) (iOff + 2), (short) (iOff + 2), (short) (iOff + 1), iOff);

		skyProgram = new ShaderProgram(vsSky, fsSky);

		// Stars
		Pixmap pixmap = new Pixmap(400, 400, Format.RGB888);
		pixmap.setColor(Color.WHITE);
		for(int x = 0; x < 400; x++) {
			for(int z = 0; z < 400; z++) {
				if(MathUtils.random(0, 1000) < 0.1f)
					pixmap.drawPixel(x, z);
			}
		}		
		
		skyTexture = new Texture(pixmap);

		sunMoonMesh = meshBuilder.end();
		
		iOff = 0;
		
		meshBuilder.vertex(-2f, 2f, -2f,  0.0f, 0.0f,
						    2f, 2f, -2f,  1.0f, 0.0f,
						    2f, 2f,  2f,  1.0f, 1.0f,
						   -2f, 2f,  2f,  0.0f, 1.0f);
				
		meshBuilder.index(iOff, (short) (iOff + 3), (short) (iOff + 2), (short) (iOff + 2), (short) (iOff + 1), iOff);
		
		iOff += 4;
		
		meshBuilder.vertex( 2f, -2f, -2f, 1.0f, 0.0f,
						   -2f, -2f, -2f, 0.0f, 0.0f,						  
						   -2f, -2f,  2f, 0.0f, 1.0f,
						    2f, -2f,  2f, 1.0f, 1.0f);
		
		meshBuilder.index(iOff, (short) (iOff + 3), (short) (iOff + 2), (short) (iOff + 2), (short) (iOff + 1), iOff);

		iOff += 4;
		
		meshBuilder.vertex(-2f, 2f,  2f,  0.0f, 0.0f,
							2f, 2f,  2f,  1.0f, 0.0f,
							2f,-2f,  2f,  1.0f, 1.0f,
						   -2f,-2f,  2f,  0.0f, 1.0f);
	
		meshBuilder.index(iOff, (short) (iOff + 3), (short) (iOff + 2), (short) (iOff + 2), (short) (iOff + 1), iOff);

		iOff += 4;
		
		meshBuilder.vertex(	2f, 2f, -2f,  1.0f, 0.0f,
						   -2f, 2f, -2f,  0.0f, 0.0f,
						   -2f,-2f, -2f,  0.0f, 1.0f,
						    2f,-2f, -2f,  1.0f, 1.0f);

		meshBuilder.index(iOff, (short) (iOff + 3), (short) (iOff + 2), (short) (iOff + 2), (short) (iOff + 1), iOff);

		iOff += 4;
		
		meshBuilder.vertex(2f, 2f,  2f,  0.0f, 0.0f,
						   2f, 2f, -2f,  1.0f, 0.0f,
						   2f,-2f, -2f,  1.0f, 1.0f,
						   2f,-2f,  2f,  0.0f, 1.0f);

		meshBuilder.index(iOff, (short) (iOff + 3), (short) (iOff + 2), (short) (iOff + 2), (short) (iOff + 1), iOff);

		iOff += 4;
		
		meshBuilder.vertex(-2f, 2f, -2f,  1.0f, 0.0f,
				   		   -2f, 2f,  2f,  0.0f, 0.0f,
				   		   -2f,-2f,  2f,  0.0f, 1.0f,
				   		   -2f,-2f, -2f,  1.0f, 1.0f);

		meshBuilder.index(iOff, (short) (iOff + 3), (short) (iOff + 2), (short) (iOff + 2), (short) (iOff + 1), iOff);

		skyMesh = meshBuilder.end();

		meshBuilder = new MeshBuilder(new VertexAttributes(VertexAttribute.Position()));

		horizonProgram = new ShaderProgram(vsHori, fsHori);

		iOff = 0;

		meshBuilder.vertex( 1.9f, -0.1f, -1.9f,
				           -1.9f, -0.1f, -1.9f,
				           -1.9f, -0.1f,  1.9f,
							1.9f, -0.1f,  1.9f);

		meshBuilder.index(iOff, (short) (iOff + 3), (short) (iOff + 2), (short) (iOff + 2), (short) (iOff + 1), iOff);

		iOff += 4;

		meshBuilder.vertex(-1.9f, 0.9f,  1.9f,
							1.9f, 0.9f,  1.9f,
							1.9f,-0.1f,  1.9f,
				           -1.9f,-0.1f,  1.9f);

		meshBuilder.index(iOff, (short) (iOff + 3), (short) (iOff + 2), (short) (iOff + 2), (short) (iOff + 1), iOff);

		iOff += 4;

		meshBuilder.vertex(	1.9f, 0.9f, -1.9f,
						   -1.9f, 0.9f, -1.9f,
						   -1.9f,-0.1f, -1.9f,
						    1.9f,-0.1f, -1.9f);

		meshBuilder.index(iOff, (short) (iOff + 3), (short) (iOff + 2), (short) (iOff + 2), (short) (iOff + 1), iOff);

		iOff += 4;

		meshBuilder.vertex(1.9f, 0.9f,  1.9f,
						   1.9f, 0.9f, -1.9f,
						   1.9f,-0.1f, -1.9f,
						   1.9f,-0.1f,  1.9f);

		meshBuilder.index(iOff, (short) (iOff + 3), (short) (iOff + 2), (short) (iOff + 2), (short) (iOff + 1), iOff);

		iOff += 4;

		meshBuilder.vertex(-1.9f, 0.9f, -1.9f,
						   -1.9f, 0.9f,  1.9f,
						   -1.9f,-0.1f,  1.9f,
						   -1.9f,-0.1f, -1.9f);

		meshBuilder.index(iOff, (short) (iOff + 3), (short) (iOff + 2), (short) (iOff + 2), (short) (iOff + 1), iOff);

		horizonMesh = meshBuilder.end();

		cloudProgram = new ShaderProgram(Cloud.vertexShader, Cloud.fragmentShader);

		Cloud.lightLocation     = cloudProgram.getUniformLocation("u_light");
		Cloud.alphaLocation     = cloudProgram.getUniformLocation("u_alpha");
		Cloud.projTransLocation = cloudProgram.getUniformLocation("u_projTrans");

		PerlinNoise perlinNoise = new ImprovedNoise(Settings.seed);
					
		clouds = new Array<Cloud>();
		Cloud cloud;
		for(int x = -Settings.chunkVisibility; x < Settings.chunkVisibility; x++) {
			for(int z = -Settings.chunkVisibility; z < Settings.chunkVisibility; z++) {
				cloud = new Cloud(x * (Cloud.CLOUD_PART_SIZE * Cloud.CLOUD_PARTS), z * (Cloud.CLOUD_PART_SIZE * Cloud.CLOUD_PARTS), perlinNoise);
				if(cloud.isEmpty() == false) {
					clouds.add(cloud);
				} else {
					cloud.dispose();
				}
			}
		}

		Texture texture = new Texture(Gdx.files.internal("data/resources/rain.png"));
		texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.Repeat);
		rainDecal = new Weather(texture);
		rainDecal.world = world;
		rainDecal.scale = 1F / rainDecal.width;
		rainDecal.color = new Color(1F, 1F, 1F, 0.8F).toFloatBits();

		texture = new Texture(Gdx.files.internal("data/resources/snow.png"));
		texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.Repeat);
		snowDecal = new Weather(texture);
		snowDecal.world = world;
		snowDecal.scale = 1F / snowDecal.width;
		snowDecal.color = new Color(1F, 1F, 1F, 0.8F).toFloatBits();
		
		sv  = snowDecal.textureCoordinates.v;
		sv2 = snowDecal.textureCoordinates.v2;
		
	}
	
	private float rotation; // 290 nigth

	public void renderSky(Camera camera, RenderContext context) {
		
		if(Gdx.input.isKeyJustPressed(Keys.I)) rotation = 180;
		
		// rotation == 270 = 24h
		
		rotation += 0.005f;
		
		System.arraycopy(camera.view.val, 0, transform.val, 0, 12);

		worldView1.set(camera.projection);
		worldView1.mul(transform);

		worldView2.set(worldView1);
		
		worldView1.rotate(Vector3.X, rotation * 0.5f);
		
		sunLight = MathUtils.clamp(MathHelper.oscillate((int) (rotation * 3f), -180, 360), 0, 180) / 180f;
		
		context.setCullFace(GL20.GL_FRONT);

		skyProgram.begin();
		
		skyProgram.setUniformMatrix("u_worldView", worldView1);

		skyProgram.setUniformi("u_texture", context.textureBinder.bind(skyTexture));
		skyProgram.setUniformf("u_light", sunLight);
		
		skyMesh.render(skyProgram);
		
		skyProgram.end();
		
		context.setBlending(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		horizonProgram.begin();

		horizonProgram.setUniformMatrix("u_worldView", worldView2);
		horizonProgram.setUniformf("u_light", sunLight);
		horizonMesh.render(horizonProgram);
		horizonProgram.end();

		sunMoonProgram.begin();

		worldView1.rotate(Vector3.X, rotation * 0.5f);
		sunMoonProgram.setUniformMatrix(u_worldView, worldView1);
		sunMoonProgram.setUniformi("u_texture", context.textureBinder.bind(sunMoonTexture));
		sunMoonMesh.render(sunMoonProgram);

		sunMoonProgram.end();

		context.setBlending(false, 0, 0);
		
	}
	
	public void renderClouds(Camera camera, RenderContext context) {
		
		context.setCullFace(GL20.GL_NONE);
		context.setBlending(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		
		cloudProgram.begin();
		
		cloudProgram.setUniformf(Cloud.lightLocation, Math.max(sunLight, 0.05f));
		float xa = rotation * 0.00005F;
		for(int i = 0; i < clouds.size; i++) {
			clouds.get(i).render(camera, cloudProgram, xa);
		}
		
		cloudProgram.end();
	}

	public void renderRain(Camera camera, Renderer renderer) { // TODO : mejorar

		time -= Gdx.graphics.getDeltaTime()  * 0.075F;

		Weather decal = rainDecal;

		float decalHeight = decal.height * 1;

		World world = GameScreen.instance.world;

		Vector3 cPos = camera.position;

		int r = 8;

		int x0 = fastFloor(cPos.x - r);
		int x1 = fastFloor(cPos.x + r) + 1;
		int z0 = fastFloor(cPos.z - r);
		int z1 = fastFloor(cPos.z + r) + 1;

		int c = 0;

		for(int x = x0; x < x1; x++) {
			for(int z = z0; z < z1; z++) {
				Chunk chunk = world.getChunkAbsolute(x, z);
				if(chunk != null)  {

					int height = chunk.chunkData.getHeightAt(x - chunk.worldX, z - chunk.worldZ);

					float y0 = (int) Math.max(cPos.y - decalHeight, height + decalHeight * 0.5F);
					float y1 = cPos.y + decalHeight;
					c++;
					for(float i = y0; i < y1; i+= decalHeight) {
						float t;
						if(i < Chunk.VERTICAL_SIZE) {
							t = (float) (time * 30F);
							decal = rainDecal;
						} else {
							t = (float) time;
							decal = snowDecal;
						}

						decal.y = i - decalHeight * 0.5F; // TODO
						decal.x = x;
						decal.z = z;

						if (decal.isVisible(camera)) {
							float sin = MathUtils.sin(c) * 0.1F + t;
							decal.textureCoordinates.v  = (sin + sv);
							decal.textureCoordinates.v2 = (sin + sv2);
							decal.render(renderer);
						}
					}
				}

			}

		}


	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	@Override
	public void dispose() {

		sunMoonProgram.dispose();
		skyProgram.dispose();
		horizonProgram.dispose();
		cloudProgram.dispose();

		if(sunMoonMesh != null) {
			sunMoonMesh.dispose();
			sunMoonMesh = null;
		}

		if(skyMesh != null) {
			skyMesh.dispose();
			skyMesh = null;
		}

		if(horizonMesh != null) {
			horizonMesh.dispose();
			horizonMesh = null;
		}

		for(int i = 0; i < clouds.size; i++) {
			clouds.get(0).dispose();
		}

		clouds.clear();

	}

	private String vsHori =
			"attribute vec3 a_position;\n" +
			"uniform mat4 u_worldView;\n" +
			"uniform float u_light;\n" +
			"varying vec4 v_color;\n" +
			"void main() {\n" +
			"   v_color = vec4(0.60 * u_light, 0.79 * u_light, 0.99 * u_light, 1.0 - (a_position.y + 0.1));\n" +
			"   gl_Position = u_worldView * vec4(a_position, 1.0);\n" +
			"}";

	private String fsHori =
			"#ifdef GL_ES\n" +
			"precision mediump float;\n" +
			"#endif\n" +
			"varying vec4 v_color;\n" +
			"void main() {\n" +
			"  if(v_color.a <= 0.0) discard;\n" +
			"  gl_FragColor = v_color;\n" +
			"}";

	private String vsSky = 
			"attribute vec3 a_position;\n" +
			"attribute vec2 a_texCoord0;\n" +
			"uniform mat4 u_worldView;\n" +
			"varying vec2 v_texCoord;\n" +
			"void main() {\n" +
			"   v_texCoord = a_texCoord0;\n" +
			"   gl_Position = u_worldView * vec4(a_position, 1.0);\n" +
			"}";

	private String fsSky = 
			"#ifdef GL_ES\n" +
			"precision mediump float;\n" +
			"#endif\n" +
			"uniform sampler2D u_texture;\n" +
			"uniform float u_light;\n" +
			"varying vec2 v_texCoord;\n" +
			"const vec4 color = vec4(0.55, 0.68, 1.0, 1.0);\n" +
			"void main() {\n" +
			"   gl_FragColor = mix(texture2D(u_texture, v_texCoord), color, u_light);\n" +
			"}";
	
	private String vsSunMoon = 
			"attribute vec3 a_position;\n" +
			"attribute vec2 a_texCoord0;\n" +
			"uniform mat4 u_worldView;\n" +
			"varying vec2 v_texCoord;\n" +
			"void main() {\n" +
			"   v_texCoord = a_texCoord0;\n" +
			"   gl_Position = u_worldView * vec4(a_position, 1.0);\n" +
			"}";
	
	private String fsSunMoon = 
			"#ifdef GL_ES\n" +
			"precision mediump float;\n" +
			"#endif\n" +
			"uniform sampler2D u_texture;\n" +
			"varying vec2 v_texCoord;\n" +
			"void main() {\n" +
			"  gl_FragColor = texture2D(u_texture, v_texCoord);\n" +
			"}";

}
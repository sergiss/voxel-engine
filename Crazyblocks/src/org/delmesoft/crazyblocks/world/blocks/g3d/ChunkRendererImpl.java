package org.delmesoft.crazyblocks.world.blocks.g3d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import org.delmesoft.crazyblocks.graphics.g3d.FastMesh;
import org.delmesoft.crazyblocks.graphics.g3d.ShaderProgram;
import org.delmesoft.crazyblocks.math.MathHelper;
import org.delmesoft.crazyblocks.utils.datastructure.Array;
import org.delmesoft.crazyblocks.world.blocks.Blocks;
import org.delmesoft.crazyblocks.world.blocks.Plot;

public class ChunkRendererImpl implements ChunkRenderer {

	public Color fogColor = new Color();
	
	private Camera camera;

	private Texture texture;
	private RenderContext context;

	private Array<FastMesh> blendQueue;
	private Array<FastMesh> animationQueue;
	private Array<FastMesh> crossQueue;
		
	private ShaderProgram opaqueProgram;
	
	private int u_texture0;
	private int u_projTrans0;
	private int u_light10;
	private int u_light20;
	private int u_cameraPosition0;
	private int u_fogColor0;
	private int u_fogDst0;
	
	private ShaderProgram blendProgram;
	
	private int u_texture1;
	private int u_projTrans1;
	private int u_light11;
	private int u_light21;
	private int u_cameraPosition1;
	private int u_fogColor1;
	private int u_fogDst1;
	
	private ShaderProgram animationProgram;
	
	private int u_texture2;
	private int u_projTrans2;
	private int u_light12;
	private int u_light22;
	private int u_tOffset2;
	private int u_cameraPosition2;
	private int u_fogColor2;
	private int u_fogDst2;

	private float time;
	private int animIndex;
	private int frameCount = 5;
	
	private Vector2[] frameOffset;
	
	public float sunLight, oldBlockLight, blockLight;
	
	public ChunkRendererImpl(RenderContext context) {
		
		this.context = context;	

		blendQueue  = new Array<FastMesh>();
		animationQueue = new Array<FastMesh>();
		crossQueue  = new Array<FastMesh>();
		// OPAQUE
		String vert = Gdx.files.internal("data/shaders/opaqueShader.vert").readString();
		String frag = Gdx.files.internal("data/shaders/opaqueShader.frag").readString();

		opaqueProgram = new ShaderProgram(vert, frag);

		u_texture0   = opaqueProgram.getUniformLocation("u_texture");
		u_projTrans0 = opaqueProgram.getUniformLocation("u_projTrans");
		u_light10    = opaqueProgram.getUniformLocation("u_light1");
		u_light20    = opaqueProgram.getUniformLocation("u_light2");
		u_cameraPosition0 = opaqueProgram.getUniformLocation("u_cameraPosition");
		u_fogColor0       = opaqueProgram.getUniformLocation("u_fogColor");
		u_fogDst0         = opaqueProgram.getUniformLocation("u_fogDst");
		// BLEND
		vert = Gdx.files.internal("data/shaders/opaqueShader.vert").readString();
		frag = Gdx.files.internal("data/shaders/blendShader.frag").readString();

		blendProgram = new ShaderProgram(vert, frag);

		u_texture1   = blendProgram.getUniformLocation("u_texture");
		u_projTrans1 = blendProgram.getUniformLocation("u_projTrans");
		u_light11    = blendProgram.getUniformLocation("u_light1");
		u_light21    = blendProgram.getUniformLocation("u_light2");
		u_cameraPosition1 = blendProgram.getUniformLocation("u_cameraPosition");
		u_fogColor1       = blendProgram.getUniformLocation("u_fogColor");
		u_fogDst1         = blendProgram.getUniformLocation("u_fogDst");
		// ANIMATION
		vert = Gdx.files.internal("data/shaders/animationShader.vert").readString();
		frag = Gdx.files.internal("data/shaders/opaqueShader.frag").readString();

		animationProgram = new ShaderProgram(vert, frag);

		u_texture2   = animationProgram.getUniformLocation("u_texture");
		u_projTrans2 = animationProgram.getUniformLocation("u_projTrans");
		u_light12 	 = animationProgram.getUniformLocation("u_light1");
		u_light22 	 = animationProgram.getUniformLocation("u_light2");
		u_tOffset2   = animationProgram.getUniformLocation("u_tOffset");
		u_cameraPosition2 = animationProgram.getUniformLocation("u_cameraPosition");
		u_fogColor2       = animationProgram.getUniformLocation("u_fogColor");
		u_fogDst2         = animationProgram.getUniformLocation("u_fogDst");
	}
	
	@Override
	public void begin(float sunLight, Camera camera){	
		
		this.sunLight = Math.max(sunLight, 0.1f); // Luz del sol
		fogColor.r = sunLight * 0.69F;
		fogColor.g = sunLight * 0.79F;
		fogColor.b = sunLight * 0.99F;

		if(Math.abs(oldBlockLight - blockLight) < 0.01f) {
			this.oldBlockLight = MathUtils.random(0.94f, 1f);
		} else {
			blockLight = MathHelper.interpolateLinear(0.5f, blockLight, oldBlockLight);
		}
		
		this.camera = camera;
		
		context.setCullFace(GL20.GL_BACK);
		context.setDepthTest(GL20.GL_LESS, 0f, 1f);
		
		// context.setDepthMask(true);
		// context.setBlending(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		opaqueProgram.begin();
		opaqueProgram.setUniformi(u_texture0, context.textureBinder.bind(texture));
		opaqueProgram.setUniformMatrix(u_projTrans0, camera.combined);
		opaqueProgram.setUniformf(u_light10, blockLight);
		opaqueProgram.setUniformf(u_light20, this.sunLight);
		opaqueProgram.setUniformf(u_cameraPosition0, camera.position);
		opaqueProgram.setUniformf(u_fogColor0, fogColor);
		opaqueProgram.setUniformf(u_fogDst0, camera.far - Plot.SIZE);

	}
	
	@Override
	public void render(Plot plot) {

		final FastMesh[] meshes = plot.meshes;

		if (meshes[0] != null) {
			meshes[0].render(opaqueProgram);
		}

		if (meshes[1] != null) {

			//	if(plot.dst2(camera.position.x, camera.position.z) < 2000) {

			blendQueue.add(meshes[1]);

			//	} else {

			//		meshes[1].render(opaqueProgram);

			//	}

		}

		if (meshes[2] != null) {
			crossQueue.add(meshes[2]);
		}

		if (meshes[3] != null) {
			animationQueue.add(meshes[3]);
		}

	}
	
	@Override
	public void end(){
		
		// OJO : si se renderiza mal es porque se han cambiado las propiedades del contexto

		opaqueProgram.end();

		int textureHandler = context.textureBinder.bind(texture);

        int i;

		final ShaderProgram blendProgram = this.blendProgram;

		blendProgram.begin();

		blendProgram.setUniformi(u_texture1, textureHandler);

		blendProgram.setUniformMatrix(u_projTrans1, camera.combined);
		blendProgram.setUniformf(u_light11, blockLight);
		blendProgram.setUniformf(u_light21, sunLight);
		blendProgram.setUniformf(u_cameraPosition1, camera.position);
		blendProgram.setUniformf(u_fogColor1, fogColor);
		blendProgram.setUniformf(u_fogDst1, camera.far - Plot.SIZE);

		final Array<FastMesh> blendQueue = this.blendQueue;
		if(blendQueue.size > 0) {
			context.setCullFace(GL20.GL_BACK);
			for(i = 0; i < blendQueue.size; ++i) {
				blendQueue.get(i).render(blendProgram);
			}
			blendQueue.clear();
		}

		final Array<FastMesh> crossQueue = this.crossQueue;
		if(crossQueue.size > 0) {
			context.setCullFace(GL20.GL_NONE);
			for(i = 0; i < crossQueue.size; ++i) {
				crossQueue.get(i).render(blendProgram);
			}
			crossQueue.clear();
		}

		blendProgram.end();

		final Array<FastMesh> animationQueue = this.animationQueue;
		if(animationQueue.size > 0) {
						
			context.setCullFace(GL20.GL_BACK);
			context.setBlending(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

			final ShaderProgram animationProgram = this.animationProgram;

			animationProgram.begin();
			
			animationProgram.setUniformi(u_texture2, textureHandler);
			
			animationProgram.setUniformMatrix(u_projTrans2, camera.combined);
			animationProgram.setUniformf(u_light12, blockLight);
			animationProgram.setUniformf(u_light22, sunLight);

			time += Gdx.graphics.getDeltaTime();

			if (time > 1F) {
				time = 0F;
				animIndex = (animIndex + 1) % frameCount;
			}						

			animationProgram.setUniformf(u_tOffset2, frameOffset[animIndex]);
			animationProgram.setUniformf(u_cameraPosition2, camera.position);
			animationProgram.setUniformf(u_fogColor2, fogColor);
			animationProgram.setUniformf(u_fogDst2, camera.far - Plot.SIZE);

			for(i = 0; i < animationQueue.size; ++i) {
				animationQueue.get(i).render(animationProgram);
			}

			animationProgram.end();
			animationQueue.clear();
		}

	}
	
	@Override
	public void setTexture(Texture texture) {
		this.texture = texture;
		
		int padding = Blocks.PADDING << 1;

		float tileWidth  = 1F / (texture.getWidth()  / (16F + padding));
		float tileHeight = 1F / (texture.getHeight() / (16F + padding));
		
		frameOffset = new Vector2[]{
				new Vector2(), 
				new Vector2(tileWidth, 0F),
				new Vector2(tileWidth * 2F, 0F),
				new Vector2(tileWidth, tileHeight), 
				new Vector2(tileWidth * 2F, tileHeight)
			};
	}

	@Override
	public void dispose() {

		opaqueProgram.dispose();
		blendProgram.dispose();
		animationProgram.dispose();

		blendQueue.clear();
		blendQueue = null;

		animationQueue.clear();
		animationQueue = null;

		texture = null;

	}

}

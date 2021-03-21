package org.delmesoft.crazyblocks.graphics.g3d.decal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class DecalRenderer implements Disposable {

	private final Mesh mesh;

	final float[] vertices;
    int idx = 0;
	Texture lastTexture = null;

	private final ShaderProgram decalShader;

	private RenderContext context;

	private int u_texture;
	private int u_projTrans;
	//private int u_light;

	private Quaternion rotation = new Quaternion();
	private Quaternion tmp1 = new Quaternion();
	private Vector3 tmp2 = new Vector3();

	public DecalRenderer(RenderContext context) {
		this(1000, context);
	}

	public DecalRenderer(int size, RenderContext context) {
		
		this.context = context;

		mesh = new Mesh(Mesh.VertexDataType.VertexArray, false, size * 4, size * 6, new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
																					new VertexAttribute(VertexAttributes.Usage.Generic, 1, "a_light"),
																				    new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE));
		vertices = new float[size * 6 * 4]; // size == n� models, 6 == 3(position) + 1(color) + 2(texture coordinates), 4 == 4 vertices

		int len = size * 6;
		short[] indices = new short[len];
		short j = 0;
		for (int i = 0; i < len; i += 6, j += 4) {
			indices[i] = j;
			indices[i + 1] = (short)(j + 3);
			indices[i + 2] = (short)(j + 2);
			indices[i + 3] = (short)(j + 2);
			indices[i + 4] = (short)(j + 1);
			indices[i + 5] = j;
		}

		mesh.setIndices(indices, 0, len);

		String vert = Gdx.files.internal("data/shaders/dShader.vert").readString();
		String frag = Gdx.files.internal("data/shaders/dShader.frag").readString();

		decalShader = new ShaderProgram(vert, frag);

		if (!decalShader.isCompiled())
			throw new GdxRuntimeException(decalShader.getLog());

		u_texture   = decalShader.getUniformLocation("u_texture");
		u_projTrans = decalShader.getUniformLocation("u_projTrans");
		//u_light     = decalShader.getUniformLocation("u_light");

	}

	public void render(Texture texture, float x, float y, float z, float halfWidth, float halfHeight, float light, float u, float v, float u2, float v2) {

		float[] vertices = this.vertices;

		if (texture != lastTexture) {
			switchTexture(texture);
		} else if (idx == vertices.length) {
			flush();
		}

		int idx = this.idx;
		
		tmp1.set(rotation);
		tmp1.conjugate();
		tmp1.mulLeft(halfWidth, -halfHeight, halfWidth, 0).mulLeft(rotation);

		vertices[idx++] = x + tmp1.x;
		vertices[idx++] = y + tmp1.y;
		vertices[idx++] = z + tmp1.z;
		vertices[idx++] = light;
		vertices[idx++] = u2;
		vertices[idx++] = v2;

		tmp1.set(rotation);
		tmp1.conjugate();
		tmp1.mulLeft(-halfWidth, -halfHeight, -halfWidth, 0).mulLeft(rotation);

		vertices[idx++] = x + tmp1.x;
		vertices[idx++] = y + tmp1.y;
		vertices[idx++] = z + tmp1.z;
		vertices[idx++] = light;
		vertices[idx++] = u;
		vertices[idx++] = v2;

		tmp1.set(rotation);
		tmp1.conjugate();
		tmp1.mulLeft(-halfWidth, halfHeight, -halfWidth, 0).mulLeft(rotation);

		vertices[idx++] = x + tmp1.x;
		vertices[idx++] = y + tmp1.y;
		vertices[idx++] = z + tmp1.z;
		vertices[idx++] = light;
		vertices[idx++] = u;
		vertices[idx++] = v;

		tmp1.set(rotation);
		tmp1.conjugate();
		tmp1.mulLeft(halfWidth,  halfHeight,  halfWidth, 0).mulLeft(rotation);

		vertices[idx++] = x + tmp1.x;
		vertices[idx++] = y + tmp1.y;
		vertices[idx++] = z + tmp1.z;
		vertices[idx++] = light;
		vertices[idx++] = u2;
		vertices[idx++] = v;

		this.idx = idx;
	}

	private void switchTexture(Texture texture) {
		flush();
		lastTexture = texture;
	}

	public void begin(Camera camera) {
		
		decalShader.begin();

		//decalShader.setUniformf(u_light, sunLight);
		
		decalShader.setUniformMatrix(u_projTrans, camera.combined);

		context.setCullFace(GL20.GL_NONE);
		context.setBlending(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

	}

	public void setRotation (Vector3 dir, Vector3 up) {
		tmp2.set(up).crs(dir).nor();
		float x = tmp2.x, y = tmp2.y, z = tmp2.z;
		tmp2.set(dir).crs(x, y, z).nor();
		rotation.setFromAxes(x, tmp2.x, dir.x, y, tmp2.y, dir.y, z, tmp2.z, dir.z);
	}

	public void end() {
		flush();
		decalShader.end();
	}

	public void flush() {

		if (idx == 0) return;

		int spritesInBatch = idx / 24;
		int count = spritesInBatch * 6;

		Mesh mesh = this.mesh;
		mesh.setVertices(vertices, 0, idx);
		mesh.getIndicesBuffer().position(0).limit(count);

		decalShader.setUniformi(u_texture, context.textureBinder.bind(lastTexture));
		mesh.render(decalShader, GL20.GL_TRIANGLES, 0, count, true);

		idx = 0;

	}

	@Override
	public void dispose() {
		decalShader.dispose();
		mesh.dispose();
	}

}
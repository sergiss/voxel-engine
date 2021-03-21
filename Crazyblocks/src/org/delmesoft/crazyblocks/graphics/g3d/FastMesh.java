package org.delmesoft.crazyblocks.graphics.g3d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

public class FastMesh {

	static final Array<Integer> disposeList = new Array<Integer>();

	public static IntMap<int[]> locationMap = new IntMap<int[]>(11);

	private final VertexAttributes attributes;
	private VertexBufferObject vertices;
	private IndexBufferObject indices;

	public FastMesh(float[] vertices, int vertexCount, int stride, short[] indices, int indexCount, VertexAttributes attributes) {
		this.vertices = new VertexBufferObject(vertices, vertexCount, stride, attributes.vertexSize);
		this.indices  = new IndexBufferObject(indices, indexCount);
		this.attributes = attributes;
	}

	public void render(ShaderProgram shader) {

		vertices.bind();

		int location;
		VertexAttribute attribute;

		final int numAttributes = attributes.size();

		int[] locations = locationMap.get(shader.getProgram());
		if (locations == null) {
			locations = new int[numAttributes];
			for (int i = 0; i < numAttributes; i++) {
				attribute = attributes.get(i);
				location = shader.getAttributeLocation(attribute.alias);
				locations[i] = location;
				shader.enableVertexAttribute(location);
				shader.setVertexAttribute(location, attribute.numComponents, attribute.type, attribute.normalized, attributes.vertexSize, attribute.offset);
			}
			locationMap.put(shader.getProgram(), locations);
		} else {
			for (int i = 0; i < numAttributes; i++) {
				attribute = attributes.get(i);
				location = locations[i];
				shader.enableVertexAttribute(location);
				shader.setVertexAttribute(location, attribute.numComponents, attribute.type, attribute.normalized, attributes.vertexSize, attribute.offset);
			}
		}

		indices.bind();

		Gdx.gl20.glDrawElements(GL20.GL_TRIANGLES, indices.indexCount(), GL20.GL_UNSIGNED_SHORT, 0);

		for (int i = 0; i < numAttributes; i++) {
			shader.disableVertexAttribute(locations[i]);
		}

		vertices.unbind();
		indices.unbind();

	}

	public void dispose() {
		vertices.dispose();
		indices.dispose();
	}

	public boolean isEmpty() {
		return indices.indexCount() == 0;
	}
}

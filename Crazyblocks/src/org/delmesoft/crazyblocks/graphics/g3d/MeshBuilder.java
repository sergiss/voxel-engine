package org.delmesoft.crazyblocks.graphics.g3d;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.math.Vector3;

import org.delmesoft.crazyblocks.world.blocks.g3d.MeshManager.LightInfo;

public class MeshBuilder {

	private float[] vertices;
	private int vertexCount;
	private short[] indices;
	private int indexCount;

	private VertexAttributes attributes;
	private int stride;

	public short iOff;

	public MeshBuilder(VertexAttributes attributes) {

		this.attributes = attributes;

		this.stride = attributes.vertexSize >> 2;

		vertices = new float[32];
		indices = new short[8];

	}

	public FastMesh end(){

		FastMesh fastMesh = new FastMesh(vertices, vertexCount, stride, indices, indexCount, attributes);

		vertexCount = 0;
		indexCount = 0;
		iOff = 0;

		return fastMesh;
	}

	public void vertex(float... values) {

		final int aCount = values.length;

		float[] vertices = this.vertices;
		final int vCount = vertexCount;

		int sizeNeeded = vCount + aCount;
		if (sizeNeeded > vertices.length) {

			float[] tmp = new float[(int) (sizeNeeded * 1.75f)];
			System.arraycopy(vertices, 0, tmp, 0, vCount);

			vertices = tmp;
			this.vertices = vertices;

		}

		for (int i = 0; i < aCount; i++) {
			vertices[vertexCount++] = values[i];
		}

	}

	public void index(short... values) {

		final int aCount = values.length;

		short[] indices = this.indices;
		final int iCount = indexCount;

		int sizeNeeded = iCount + aCount;
		if (sizeNeeded > indices.length) {

			short[] tmp = new short[(int) (sizeNeeded * 1.75f)];
			System.arraycopy(indices, 0, tmp, 0, iCount);

			indices = tmp;
			this.indices = indices;

		}

		for (int i = 0; i < aCount; i++) {
			indices[indexCount++] = values[i];
		}

	}

	public void face(float x1, float y1, float z1, float u1, float v1,
					 float x2, float y2, float z2, float u2, float v2,
					 float x3, float y3, float z3, float u3, float v3,
					 float x4, float y4, float z4, float u4, float v4, float light1, float light2) {

		float[] vertices = this.vertices;
		final int vCount = vertexCount;

		int sizeNeeded = vCount + 28;
		if(sizeNeeded > vertices.length) {

			float[] tmp = new float[(int) (sizeNeeded * 1.75f)];
			System.arraycopy(vertices, 0, tmp, 0, vCount);

			vertices = tmp;
			this.vertices = vertices;

		}

		vertices[vCount]     = x1;
		vertices[vCount + 1] = y1;
		vertices[vCount + 2] = z1;
		vertices[vCount + 3] = light1;
		vertices[vCount + 4] = light2;
		vertices[vCount + 5] = u1;
		vertices[vCount + 6] = v1;

		vertices[vCount + 7] = x2;
		vertices[vCount + 8] = y2;
		vertices[vCount + 9] = z2;
		vertices[vCount + 10] = light1;
		vertices[vCount + 11] = light2;
		vertices[vCount + 12] = u2;
		vertices[vCount + 13] = v2;

		vertices[vCount + 14] = x3;
		vertices[vCount + 15] = y3;
		vertices[vCount + 16] = z3;
		vertices[vCount + 17] = light1;
		vertices[vCount + 18] = light2;
		vertices[vCount + 19] = u3;
		vertices[vCount + 20] = v3;

		vertices[vCount + 21] = x4;
		vertices[vCount + 22] = y4;
		vertices[vCount + 23] = z4;
		vertices[vCount + 24] = light1;
		vertices[vCount + 25] = light2;
		vertices[vCount + 26] = u4;
		vertices[vCount + 27] = v4;

		vertexCount += 28;

	}

	public void face(Vector3 vertex1, float u1, float v1,
					 Vector3 vertex2, float u2, float v2,
					 Vector3 vertex3, float u3, float v3,
					 Vector3 vertex4, float u4, float v4, float light1, float light2) {

		float[] vertices = this.vertices;
		final int vCount = vertexCount;

		int sizeNeeded = vCount + 28;
		if(sizeNeeded > vertices.length) {
			float[] tmp = new float[(int) (sizeNeeded * 1.75f)];
			System.arraycopy(vertices, 0, tmp, 0, vCount);
			vertices = tmp;
			this.vertices = vertices;

		}

		vertices[vCount]     = vertex1.x;
		vertices[vCount + 1] = vertex1.y;
		vertices[vCount + 2] = vertex1.z;
		vertices[vCount + 3] = light1;
		vertices[vCount + 4] = light2;
		vertices[vCount + 5] = u1;
		vertices[vCount + 6] = v1;

		vertices[vCount + 7] = vertex2.x;
		vertices[vCount + 8] = vertex2.y;
		vertices[vCount + 9] = vertex2.z;
		vertices[vCount + 10] = light1;
		vertices[vCount + 11] = light2;
		vertices[vCount + 12] = u2;
		vertices[vCount + 13] = v2;

		vertices[vCount + 14] = vertex3.x;
		vertices[vCount + 15] = vertex3.y;
		vertices[vCount + 16] = vertex3.z;
		vertices[vCount + 17] = light1;
		vertices[vCount + 18] = light2;
		vertices[vCount + 19] = u3;
		vertices[vCount + 20] = v3;

		vertices[vCount + 21] = vertex4.x;
		vertices[vCount + 22] = vertex4.y;
		vertices[vCount + 23] = vertex4.z;
		vertices[vCount + 24] = light1;
		vertices[vCount + 25] = light2;
		vertices[vCount + 26] = u4;
		vertices[vCount + 27] = v4;

		vertexCount += 28;

	}

	public void face(Vector3 vertex1, LightInfo lf1, float u1, float v1,
					 Vector3 vertex2, LightInfo lf2, float u2, float v2,
					 Vector3 vertex3, LightInfo lf3, float u3, float v3,
					 Vector3 vertex4, LightInfo lf4, float u4, float v4) {

		float[] vertices = this.vertices;
		final int vCount = vertexCount;

		int sizeNeeded = vCount + 28;
		if(sizeNeeded > vertices.length) {

			float[] tmp = new float[(int) (sizeNeeded * 1.75f)];
			System.arraycopy(vertices, 0, tmp, 0, vCount);

			vertices = tmp;
			this.vertices = vertices;

		}

		vertices[vCount]     = vertex1.x;
		vertices[vCount + 1] = vertex1.y;
		vertices[vCount + 2] = vertex1.z;
		vertices[vCount + 3] = lf1.blockLight;
		vertices[vCount + 4] = lf1.skyLight;
		vertices[vCount + 5] = u1;
		vertices[vCount + 6] = v1;

		vertices[vCount + 7]  = vertex2.x;
		vertices[vCount + 8]  = vertex2.y;
		vertices[vCount + 9]  = vertex2.z;
		vertices[vCount + 10] = lf2.blockLight;
		vertices[vCount + 11] = lf2.skyLight;
		vertices[vCount + 12] = u2;
		vertices[vCount + 13] = v2;

		vertices[vCount + 14] = vertex3.x;
		vertices[vCount + 15] = vertex3.y;
		vertices[vCount + 16] = vertex3.z;
		vertices[vCount + 17] = lf3.blockLight;
		vertices[vCount + 18] = lf3.skyLight;
		vertices[vCount + 19] = u3;
		vertices[vCount + 20] = v3;

		vertices[vCount + 21] = vertex4.x;
		vertices[vCount + 22] = vertex4.y;
		vertices[vCount + 23] = vertex4.z;
		vertices[vCount + 24] = lf4.blockLight;
		vertices[vCount + 25] = lf4.skyLight;
		vertices[vCount + 26] = u4;
		vertices[vCount + 27] = v4;

		vertexCount += 28;

	}

	public void face(Vector3 vertex1, float color1, float u1, float v1,
					 Vector3 vertex2, float color2, float u2, float v2,
					 Vector3 vertex3, float color3, float u3, float v3,
					 Vector3 vertex4, float color4, float u4, float v4, float type) {

		float[] vertices = this.vertices;
		final int vCount = vertexCount;

		int sizeNeeded = vCount + 28;
		if(sizeNeeded > vertices.length) {

			float[] tmp = new float[(int) (sizeNeeded * 1.75f)];
			System.arraycopy(vertices, 0, tmp, 0, vCount);

			vertices = tmp;
			this.vertices = vertices;

		}

		vertices[vCount]     = vertex1.x;
		vertices[vCount + 1] = vertex1.y;
		vertices[vCount + 2] = vertex1.z;
		vertices[vCount + 3] = color1;
		vertices[vCount + 4] = type;
		vertices[vCount + 5] = u1;
		vertices[vCount + 6] = v1;

		vertices[vCount + 7]  = vertex2.x;
		vertices[vCount + 8]  = vertex2.y;
		vertices[vCount + 9]  = vertex2.z;
		vertices[vCount + 10] = color2;
		vertices[vCount + 11] = type;
		vertices[vCount + 12] = u2;
		vertices[vCount + 13] = v2;

		vertices[vCount + 14] = vertex3.x;
		vertices[vCount + 15] = vertex3.y;
		vertices[vCount + 16] = vertex3.z;
		vertices[vCount + 17] = color3;
		vertices[vCount + 18] = type;
		vertices[vCount + 19] = u3;
		vertices[vCount + 20] = v3;

		vertices[vCount + 21] = vertex4.x;
		vertices[vCount + 22] = vertex4.y;
		vertices[vCount + 23] = vertex4.z;
		vertices[vCount + 24] = color4;
		vertices[vCount + 25] = type;
		vertices[vCount + 26] = u4;
		vertices[vCount + 27] = v4;

		vertexCount += 28;

	}

	public void vertex(Vector3 vertex, float color, float u, float v) {

		float[] vertices = this.vertices;
		final int vCount = vertexCount;

		int sizeNeeded = vCount + 6;
		if(sizeNeeded > vertices.length) {

			float[] tmp = new float[(int) (sizeNeeded * 1.75f)];
			System.arraycopy(vertices, 0, tmp, 0, vCount);

			vertices = tmp;
			this.vertices = vertices;

		}

		vertices[vCount] = vertex.x;
		vertices[vCount + 1] = vertex.y;
		vertices[vCount + 2] = vertex.z;
		vertices[vCount + 3] = color;
		vertices[vCount + 4] = u;
		vertices[vCount + 5] = v;

		vertexCount += 6;

	}

	public void vertex(float x, float y, float z, float color, float u, float v) {

		float[] vertices = this.vertices;
		final int vCount = vertexCount;

		int sizeNeeded = vCount + 6;
		if(sizeNeeded > vertices.length) {

			float[] tmp = new float[(int) (sizeNeeded * 1.75f)];
			System.arraycopy(vertices, 0, tmp, 0, vCount);

			vertices = tmp;
			this.vertices = vertices;

		}

		vertices[vCount] = x;
		vertices[vCount + 1] = y;
		vertices[vCount + 2] = z;
		vertices[vCount + 3] = color;
		vertices[vCount + 4] = u;
		vertices[vCount + 5] = v;

		vertexCount += 6;

	}

	public void index(short i0, short i1, short i2, short i3, short i4, short i5) {

		short[] indices = this.indices;
		final int iCount = indexCount;

		if(iCount + 6 > indices.length) {

			short[] tmp = new short[(int) ((iCount + 6) * 1.75f)];
			System.arraycopy(indices, 0, tmp, 0, iCount);

			indices = tmp;
			this.indices = indices;

		}

		indices[iCount] = i0;
		indices[iCount + 1] = i1;
		indices[iCount + 2] = i2;
		indices[iCount + 3] = i3;
		indices[iCount + 4] = i4;
		indices[iCount + 5] = i5;

		indexCount += 6;

	}

	public int getIndexCount() {
		return indexCount;
	}

}
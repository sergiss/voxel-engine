package org.delmesoft.crazyblocks.graphics.g3d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;

import java.nio.ByteBuffer;

/**
 * Created by sergi on 29/08/17.
 */

public class VertexBufferObject {

    private static final GL20 gl = Gdx.gl20;

    private ByteBuffer byteBuffer;

    private int bufferHandle;

    public VertexBufferObject(float[] vertices, int count, int stride, int vertexSize) {

        int l = (count / stride) * vertexSize;
        byteBuffer = BufferUtils.newUnsafeByteBuffer(l);
        BufferUtils.copy(vertices, byteBuffer, count, 0);

    }

    public void bind() {

        if(bufferHandle > 0) {
            gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, bufferHandle);
        } else {
            bufferHandle = gl.glGenBuffer();
            gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, bufferHandle);
            gl.glBufferData(GL20.GL_ARRAY_BUFFER, byteBuffer.limit(), byteBuffer, GL20.GL_STATIC_DRAW);
            byteBuffer = null;
        }

    }

    public void unbind() {
        gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
    }

    public void dispose() {
        if(bufferHandle > 0) {
            gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, 0);
            gl.glDeleteBuffer(bufferHandle);
            bufferHandle = 0;
        }
    }

}

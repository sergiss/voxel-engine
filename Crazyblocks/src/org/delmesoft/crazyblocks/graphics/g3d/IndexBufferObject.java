package org.delmesoft.crazyblocks.graphics.g3d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.BufferUtils;

import java.nio.ShortBuffer;

/**
 * Created by sergi on 29/08/17.
 */

public class IndexBufferObject {

    private static final GL20 gl = Gdx.gl20;

    private final int indexCount;

    private ShortBuffer buffer;

    private int bufferHandle;

    public IndexBufferObject(short[] indices, int count) {
        buffer = BufferUtils.newUnsafeByteBuffer(count << 1).asShortBuffer();
        buffer.put(indices, 0, count);
        buffer.flip();
        indexCount = count;
    }

    public void bind() {
        if(bufferHandle > 0) {
            gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, bufferHandle);
        } else {
            bufferHandle = gl.glGenBuffer();
            gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, bufferHandle);
            gl.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, indexCount << 1, buffer, GL20.GL_STATIC_DRAW);
            buffer = null;
        }
    }

    public void unbind() {
        gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void dispose() {
        if(bufferHandle > 0) {
            gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, 0);
            gl.glDeleteBuffer(bufferHandle);
            bufferHandle = 0;
        }
    }

    public int indexCount() {
        return indexCount;
    }

}

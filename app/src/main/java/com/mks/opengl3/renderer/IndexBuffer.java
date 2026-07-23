package com.mks.opengl3.renderer;

import android.opengl.GLES32;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class IndexBuffer implements Buffer {

    private final int[] eboId = new int[1];
    private int count = 0;

    public IndexBuffer(IntBuffer indices, int byteCount) {

        count = byteCount / 4;

        GLES32.glGenBuffers(1, eboId, 0);
        GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, eboId[0]);

        GLES32.glBufferData(
                GLES32.GL_ELEMENT_ARRAY_BUFFER,
                byteCount,
                indices,
                GLES32.GL_DYNAMIC_DRAW
        );
    }

    public IndexBuffer(int[] indices) {

        count = indices.length;

        IntBuffer buffer = ByteBuffer
                .allocateDirect(indices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer();

        buffer.put(indices);
        buffer.position(0);

        GLES32.glGenBuffers(1, eboId, 0);
        GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, eboId[0]);

        GLES32.glBufferData(
                GLES32.GL_ELEMENT_ARRAY_BUFFER,
                indices.length * 4,
                buffer,
                GLES32.GL_DYNAMIC_DRAW
        );
    }

    /**
     * آپدیت اندیس‌ها بدون ساختن IBO جدید
     */
    public void setData(int[] indices) {

        count = indices.length;

        IntBuffer buffer = ByteBuffer
                .allocateDirect(indices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer();

        buffer.put(indices);
        buffer.position(0);

        bind();

        GLES32.glBufferData(
                GLES32.GL_ELEMENT_ARRAY_BUFFER,
                indices.length * 4,
                buffer,
                GLES32.GL_DYNAMIC_DRAW
        );
    }

    public int getCount() {
        return count;
    }

    public int getId() {
        return eboId[0];
    }

    @Override
    public void bind() {
        GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, eboId[0]);
    }

    @Override
    public void unbind() {
        GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    @Override
    public void delete() {
        GLES32.glDeleteBuffers(1, eboId, 0);
    }
}
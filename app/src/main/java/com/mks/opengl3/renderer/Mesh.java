package com.mks.opengl3.renderer;

import android.opengl.GLES32;
import java.util.List;

public abstract class Mesh {

    protected float[] vertices;
    protected int[] indices;
    protected VertexArray vertexArray;
    protected List<BufferElement> elements;

    private IndexBuffer triangleIBO;
    private IndexBuffer lineIBO;
    private IndexBuffer currentIBO;
    private int currentMode = GLES32.GL_TRIANGLES;

    public Mesh(float[] vertices,int[] indices,List<BufferElement> elements){
        this.vertices=vertices;
        this.indices=indices;
        this.elements=elements;

        vertexArray=new VertexArray();
        vertexArray.addVertexBuffer(new VertexBuffer(vertices,new BufferLayout(elements)));

        triangleIBO=new IndexBuffer(indices);
        currentIBO=triangleIBO;

        vertexArray.setIndexBuffer(currentIBO);
    }

    public void setTriangleIndices(int[] indices){

        triangleIBO = new IndexBuffer(indices);

        if(currentMode == GLES32.GL_TRIANGLES){
            currentIBO = triangleIBO;
        }
    }

    public void setLineIndices(int[] indices){

        lineIBO = new IndexBuffer(indices);

        if(currentMode == GLES32.GL_LINES){
            currentIBO = lineIBO;
        }
    }

    public void useTriangles(){
        currentIBO=triangleIBO;
        currentMode=GLES32.GL_TRIANGLES;
    }

    public void useLines(){
        if(lineIBO==null)return;
        currentIBO=lineIBO;
        currentMode=GLES32.GL_LINES;
    }

    public void onRender(){
        if(currentIBO==null)return;
        vertexArray.bind();
        currentIBO.bind();
        GLES32.glDrawElements(currentMode,currentIBO.getCount(),GLES32.GL_UNSIGNED_INT,0);
        vertexArray.unbind();
    }

    public float[] getVertices(){return vertices;}
    public int[] getIndices(){return indices;}
    public VertexArray getVertexArray(){return vertexArray;}
}

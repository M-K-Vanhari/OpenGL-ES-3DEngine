package com.mks.opengl3.renderer;

import android.opengl.GLES32;

import com.mks.opengl3.math.Vec4;
import com.mks.opengl3.scene.object3d.Object3D;

import java.util.List;

public abstract class Mesh {

    protected float[] vertices;
    protected int[] indices;
    protected VertexArray vertexArray;
    protected List<BufferElement> elements;

    private IndexBuffer triangleIBO;
    private IndexBuffer lineIBO;
    private IndexBuffer olineIBO;
    private IndexBuffer pointIBO;
    private IndexBuffer linepointIBO;
    private IndexBuffer currentIBO;
    private VertexBuffer vertexBuffer;
    private int currentMode = GLES32.GL_TRIANGLES;

    public Mesh(float[] vertices,int[] indices,List<BufferElement> elements){
        this.vertices=vertices;
        this.indices=indices;
        this.elements=elements;
        vertexBuffer = new VertexBuffer(vertices, new BufferLayout(elements));

        vertexArray = new VertexArray();
        vertexArray.addVertexBuffer(vertexBuffer);
        triangleIBO=new IndexBuffer(indices);
        currentIBO=triangleIBO;

        vertexArray.setIndexBuffer(currentIBO);
    }

    public void setTriangleIndices(int[] indices){
        triangleIBO = new IndexBuffer(indices);
        currentMode = GLES32.GL_TRIANGLES;
        currentIBO = triangleIBO;

    }

    public void setLineIndices(int[] indices){
        lineIBO = new IndexBuffer(indices);
        currentMode = GLES32.GL_LINES;
        currentIBO = lineIBO;
    }

    public void setVertices(float[] vertices) {
        this.vertices = vertices;
        vertexBuffer.setData(vertices);
    }

    public void setOLineIndices(int[] indices){
        olineIBO = new IndexBuffer(indices);
        currentMode = GLES32.GL_LINES;
        currentIBO = olineIBO;
    }


    public void setPointIndices(int[] indices){
        pointIBO = new IndexBuffer(indices);
        currentMode = GLES32.GL_POINTS;
        currentIBO = pointIBO;
    }
    public void setLinePointsIndices(int[] indices){
        linepointIBO = new IndexBuffer(indices);
        currentMode = GLES32.GL_LINES;
        currentIBO = linepointIBO;
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

    public void useOLines(){
        if(olineIBO==null)return;
        currentIBO=olineIBO;
        currentMode=GLES32.GL_LINES;
    }
    public void usePoints(){
        currentMode=GLES32.GL_POINTS;
    }
    public void useLinePoints(){
        if(linepointIBO==null)return;
        currentIBO=linepointIBO;
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
    public void drawTriangles() {

        if (triangleIBO == null)
            return;

        vertexArray.bind();
        triangleIBO.bind();

        GLES32.glDrawElements(
                GLES32.GL_TRIANGLES,
                triangleIBO.getCount(),
                GLES32.GL_UNSIGNED_INT,
                0);
    }

    public void drawLines() {

        if (lineIBO == null)
            return;

        vertexArray.bind();
        lineIBO.bind();

        GLES32.glDrawElements(
                GLES32.GL_LINES,
                lineIBO.getCount(),
                GLES32.GL_UNSIGNED_INT,
                0);
    }
    public void drawPoints() {


        vertexArray.bind();

        GLES32.glDrawElements(
                GLES32.GL_POINTS,
                lineIBO.getCount(),
                GLES32.GL_UNSIGNED_INT,
                0);
    }

}

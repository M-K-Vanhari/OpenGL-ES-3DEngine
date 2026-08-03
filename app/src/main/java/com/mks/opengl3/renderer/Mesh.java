package com.mks.opengl3.renderer;

import android.opengl.GLES32;

import com.mks.opengl3.math.Vec4;
import com.mks.opengl3.scene.object3d.MeshData;
import com.mks.opengl3.scene.object3d.Object3D;
import com.mks.opengl3.scene.object3d.Vertex;

import java.util.List;

public abstract class Mesh {

    protected float[] vertices;
    protected int[] indices;
    protected VertexArray vertexArray;
    protected List<BufferElement> elements;
    private int[] lineIndices;
    private int[] triangleIndices;
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
    }

    public void setLineIndices(int[] indices, int offset){

        int[] newIndices = new int[indices.length];

        for(int i = 0; i < indices.length; i++){

            newIndices[i] = indices[i] + offset;

        }

        lineIBO = new IndexBuffer(newIndices);
    }

    public void setVertices(float[] vertices) {
        this.vertices = vertices;
        vertexBuffer.setData(vertices);
    }
    public void setMeshData(MeshData m){
        this.vertices = m.vertices;
        this.lineIndices = m.lineIndices;
        this.triangleIndices = m.triangleIndices;

        vertexBuffer.setData(vertices);

        lineIBO = new IndexBuffer(lineIndices);
        triangleIBO = new IndexBuffer(triangleIndices);
    }

    public void addMeshData(MeshData m){

        int vertexOffset =
                vertices.length / Vertex.STRIDE;


        float[] newVertices =
                new float[vertices.length + m.vertices.length];


        System.arraycopy(
                vertices,
                0,
                newVertices,
                0,
                vertices.length
        );


        System.arraycopy(
                m.vertices,
                0,
                newVertices,
                vertices.length,
                m.vertices.length
        );


        int[] newLines =
                new int[lineIndices.length + m.lineIndices.length];


        System.arraycopy(
                lineIndices,
                0,
                newLines,
                0,
                lineIndices.length
        );


        for(int i = 0; i < m.lineIndices.length; i++){

            newLines[lineIndices.length+i] =
                    m.lineIndices[i] + vertexOffset;
        }


        int[] newTriangles =
                new int[triangleIndices.length + m.triangleIndices.length];


        System.arraycopy(
                triangleIndices,
                0,
                newTriangles,
                0,
                triangleIndices.length
        );


        for(int i = 0; i < m.triangleIndices.length; i++){

            newTriangles[triangleIndices.length+i] =
                    m.triangleIndices[i] + vertexOffset;
        }


        this.vertices = newVertices;
        this.lineIndices = newLines;
        this.triangleIndices = newTriangles;


        vertexBuffer.setData(vertices);

        lineIBO = new IndexBuffer(lineIndices);

        triangleIBO = new IndexBuffer(triangleIndices);
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
        GLES32.glLineWidth(1.0f);
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
        GLES32.glLineWidth(1.0f);
        GLES32.glDrawElements(
                GLES32.GL_LINES,
                lineIBO.getCount(),
                GLES32.GL_UNSIGNED_INT,
                0);
    }
    public void drawLinePoints() {
        if (linepointIBO == null)
            return;
        vertexArray.bind();
        linepointIBO.bind();
        GLES32.glLineWidth(3.0f);
        GLES32.glDrawElements(
                GLES32.GL_LINES,
                linepointIBO.getCount(),
                GLES32.GL_UNSIGNED_INT,
                0);
    }
    public void drawOLine() {
        if (olineIBO == null)
            return;
        vertexArray.bind();
        olineIBO.bind();
        GLES32.glLineWidth(3.0f);
        GLES32.glDrawElements(
                GLES32.GL_LINES,
                olineIBO.getCount(),
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
    public int getVertexCount() {

        if (vertices == null)
            return 0;

        // هر ورتکس 15 float دارد:
        // 3 Position
        // 2 UV
        // 3 Normal
        // 3 Tangent
        // 4 Color

        return vertices.length / 15;
    }
    public int getTriangleCount() {

        if (triangleIBO == null)
            return 0;

        return triangleIBO.getCount() / 3;
    }
    public int getLineCount() {

        if (lineIBO == null)
            return 0;

        return lineIBO.getCount() / 2;
    }
    public int getOLineCount() {

        if (olineIBO == null)
            return 0;

        return olineIBO.getCount() / 2;
    }
    public int getPointCount() {

        if (pointIBO == null)
            return 0;

        return pointIBO.getCount();
    }
}

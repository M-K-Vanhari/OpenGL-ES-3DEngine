package com.mks.opengl3.scene.object3d;

import java.util.ArrayList;

public class GreedyMesher {

    private Vertex[][] zMatrix;

    private int width;
    private int height;
    private static final float EPSILON = 0.001f;
    private boolean[][] visited;

    private ArrayList<Float> vertices;
    private ArrayList<Integer> triangleIndices;
    private ArrayList<Integer> lineIndices;

    private int currentTriangleVertexIndex;
    private int currentLineVertexIndex;

    public class MeshData {

        public float[] vertices;
        public int[] triangleIndices;
        public int[] lineIndices;

        public MeshData(float[] vertices, int[] triangleIndices,int[]  lineIndices) {
            this.vertices = vertices;
            this.triangleIndices = triangleIndices;
            this.lineIndices = lineIndices;

        }

    }

    private float[] toFloatArray(ArrayList<Float> list) {

        float[] array = new float[list.size()];

        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }

        return array;
    }
    private int[] toIntArray(ArrayList<Integer> list) {

        int[] array = new int[list.size()];

        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }

        return array;
    }
    public MeshData build(){
        for(int y=0;y<height;y++){
            for(int x=0;x<width;x++){
                if(visited[y][x])
                    continue;
                Rectangle rect= findLargestRectangle(x,y);
                addRectangle(rect);
            }

        }

        return new MeshData(
                toFloatArray(vertices),
                toIntArray(triangleIndices),
                toIntArray(lineIndices));

    }
    private boolean equalHeight(float a, float b){
        return Math.abs(a - b) < EPSILON;
    }
    public GreedyMesher(Vertex[][] Vertex){

        this.zMatrix = Vertex;

        height = Vertex.length;
        width = Vertex[0].length;

        visited = new boolean[height][width];

        vertices = new ArrayList<>();
        triangleIndices = new ArrayList<>();
        lineIndices = new ArrayList<>();

        currentTriangleVertexIndex = 0;
    }
    private void addVertex(
            float px,
            float py,
            float pz,
            float u,
            float v,
            float r,
            float g,
            float b,
            float a
            ) {

        // Position
        vertices.add(px);
        vertices.add(py);
        vertices.add(pz);

        // UV
        vertices.add(u);
        vertices.add(v);

        // Normal
        vertices.add(0f);
        vertices.add(0f);
        vertices.add(1f);

        // Tangent
        vertices.add(1f);
        vertices.add(0f);
        vertices.add(0f);

        // Color
        vertices.add(r);
        vertices.add(g);
        vertices.add(b);
        vertices.add(a);
    }
    private void addRectangle(Rectangle rect) {

        //---------------------------------------
        // Scale
        //---------------------------------------

        float sx = 3.0f / (width - 1);
        float sy = 3.0f / (height - 1);

        //---------------------------------------
        // مختصات چهار گوشه
        //---------------------------------------

        float x0 = rect.x * sx - 1.5f;
        float y0 = rect.y * sy - 1.5f;

        float x1 = (rect.x + rect.width - 1) * sx - 1.5f;
        float y1 = (rect.y + rect.height - 1) * sy - 1.5f;

        float z = rect.z;

        //---------------------------------------
        // Vertex 0
        //---------------------------------------

        addVertex(
                x0, y0, z,
                (float) rect.x / (width - 1),
                (float) rect.y / (height - 1),
                rect.r,rect.g,rect.b,rect.a);

        //---------------------------------------
        // Vertex 1
        //---------------------------------------

        addVertex(
                x1, y0, z,
                (float) (rect.x + rect.width - 1) / (width - 1),
                (float) rect.y / (height - 1),
                rect.r,rect.g,rect.b,rect.a);

        //---------------------------------------
        // Vertex 2
        //---------------------------------------

        addVertex(
                x1, y1, z,
                (float) (rect.x + rect.width - 1) / (width - 1),
                (float) (rect.y + rect.height - 1) / (height - 1),
                rect.r,rect.g,rect.b,rect.a);

        //---------------------------------------
        // Vertex 3
        //---------------------------------------

        addVertex(
                x0, y1, z,
                (float) rect.x / (width - 1),
                (float) (rect.y + rect.height - 1) / (height - 1),
                rect.r,rect.g,rect.b,rect.a);

        //---------------------------------------
        // دو مثلث
        //---------------------------------------

        triangleIndices.add(currentTriangleVertexIndex);
        triangleIndices.add(currentTriangleVertexIndex + 1);
        triangleIndices.add(currentTriangleVertexIndex + 2);

        triangleIndices.add(currentTriangleVertexIndex);
        triangleIndices.add(currentTriangleVertexIndex + 2);
        triangleIndices.add(currentTriangleVertexIndex + 3);

        currentTriangleVertexIndex += 4;


        lineIndices.add(currentLineVertexIndex);
        lineIndices.add(currentLineVertexIndex + 1);
        lineIndices.add(currentLineVertexIndex);
        lineIndices.add(currentLineVertexIndex + 3);
        lineIndices.add(currentLineVertexIndex + 3);
        lineIndices.add(currentLineVertexIndex + 2);
        lineIndices.add(currentLineVertexIndex + 1);
        lineIndices.add(currentLineVertexIndex + 2);



        currentLineVertexIndex += 4;



    }
    private Rectangle findLargestRectangle(int startX,int startY){

        float z = zMatrix[startY][startX].pz;

        //---------------------------------
        // پیدا کردن عرض
        //---------------------------------

        int width = 0;

        while(startX + width < this.width){

            if(visited[startY][startX+width])
                break;

            if(!equalHeight (zMatrix[startY][startX+width].pz , z))
                break;

            width++;

        }

        //---------------------------------
        // پیدا کردن ارتفاع
        //---------------------------------

        int height = 1;

        boolean expand = true;

        while(expand){

            if(startY + height >= this.height)
                break;

            for(int x=0;x<width;x++){

                if(visited[startY+height][startX+x]){

                    expand=false;
                    break;

                }

                if(!equalHeight(zMatrix[startY+height][startX+x].pz,z)){

                    expand=false;
                    break;

                }

            }

            if(expand)
                height++;

        }

        //---------------------------------
        // علامت گذاری
        //---------------------------------

        for(int y=0;y<height;y++){

            for(int x=0;x<width;x++){

                visited[startY+y][startX+x]=true;

            }

        }

        return new Rectangle(
                startX,
                startY,
                width,
                height,
                z,
                zMatrix[startY][startX].r,
                zMatrix[startY][startX].g,
                zMatrix[startY][startX].b,
                zMatrix[startY][startX].a
        );

    }
    public static class Rectangle{

        public int x;

        public int y;

        public int width;

        public int height;

        public float z;
        public float r;
        public float g;
        public float b;
        public float a;

        public Rectangle(
                int x,
                int y,
                int width,
                int height,
                float z,
                float r,
                float g,
                float b,
                float a){

            this.x=x;
            this.y=y;
            this.width=width;
            this.height=height;
            this.z=z;
            this.r=r;
            this.g=g;
            this.b=b;
            this.a=a;


        }

    }

}

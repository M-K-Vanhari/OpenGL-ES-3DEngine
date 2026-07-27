package com.mks.opengl3.scene.object3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class GreedyMesher {


    public enum Mode{
        SQUARE,
        RECTANGLE
    }
    private final float worldSize = 3.0f;
    private float sx;
    private float sy;

    private float originX;
    private float originY;
    private int width;
    private int height;

    //----------------------------------------------------
    // State
    //----------------------------------------------------

    private boolean[][] visited;

    //----------------------------------------------------
    // Output
    //----------------------------------------------------

    private ArrayList<Float> vertices = new ArrayList<>();

    private ArrayList<Integer> triangleIndices = new ArrayList<>();

    private ArrayList<Integer> lineIndices = new ArrayList<>();

    //----------------------------------------------------
    // Sharing
    //----------------------------------------------------

    private HashMap<VertexKey,Integer> vertexMap =
            new HashMap<>();

    private HashSet<EdgeKey> edgeSet =
            new HashSet<>();

    private int currentVertexIndex=0;
    private Vertex[][] source;

    //----------------------------------------------------
    // Constructor
    //----------------------------------------------------


    public GreedyMesher(Vertex[][] source){

        this.source = source;

        height = source.length;
        width = source[0].length;
        sx = worldSize / (width - 1);
        sy = worldSize / (height - 1);

        originX = -worldSize * 0.5f;
        originY = -worldSize * 0.5f;

        visited = new boolean[height][width];

    }

    //----------------------------------------------------
    // Output
    //----------------------------------------------------

    public MeshData build(Mode mode){

        //--------------------------------------
        // Reset
        //--------------------------------------

        vertices.clear();

        triangleIndices.clear();

        lineIndices.clear();

        vertexMap.clear();

        edgeSet.clear();

        currentVertexIndex = 0;

        //--------------------------------------
        // Clear visited
        //--------------------------------------

        for(int y=0;y<height;y++){

            Arrays.fill(visited[y],false);

        }

        //--------------------------------------
        // Greedy Meshing
        //--------------------------------------

        for(int y=0;y<height;y++){

            for(int x=0;x<width;x++){

                if(visited[y][x])
                    continue;

                Rectangle rect;

                if(mode==Mode.SQUARE){

                    rect = findLargestSquare(x,y);

                }else{

                    rect = findLargestRectangle(x,y);

                }

                addRectangle(rect);

            }

        }

        //--------------------------------------
        // Result
        //--------------------------------------

        return new MeshData(

                toFloatArray(vertices),

                toIntArray(triangleIndices),

                toIntArray(lineIndices)

        );

    }

    //----------------------------------------------------
    // MeshData
    //----------------------------------------------------

    public static class MeshData{

        public final float[] vertices;

        public final int[] triangleIndices;

        public final int[] lineIndices;

        public MeshData(
                float[] vertices,
                int[] triangleIndices,
                int[] lineIndices){

            this.vertices=vertices;
            this.triangleIndices=triangleIndices;
            this.lineIndices=lineIndices;

        }

    }

    //----------------------------------------------------
    // Rectangle
    //----------------------------------------------------

    private static class Rectangle{

        int x;
        int y;

        int width;
        int height;

        float z;

        float r;
        float g;
        float b;
        float a;

        Rectangle(
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
    //----------------------------------------------------
    // Vertex Key
    //----------------------------------------------------

    private static class VertexKey {

        float x;
        float y;
        float z;

        float r;
        float g;
        float b;
        float a;

        VertexKey(
                float x,
                float y,
                float z,
                float r,
                float g,
                float b,
                float a){

            this.x=x;
            this.y=y;
            this.z=z;

            this.r=r;
            this.g=g;
            this.b=b;
            this.a=a;
        }

        @Override
        public boolean equals(Object obj){

            if(this==obj)
                return true;

            if(!(obj instanceof VertexKey))
                return false;

            VertexKey v=(VertexKey)obj;

            return Float.compare(x,v.x)==0 &&
                    Float.compare(y,v.y)==0 &&
                    Float.compare(z,v.z)==0 &&
                    Float.compare(r,v.r)==0 &&
                    Float.compare(g,v.g)==0 &&
                    Float.compare(b,v.b)==0 &&
                    Float.compare(a,v.a)==0;
        }

        @Override
        public int hashCode(){

            int h=17;

            h=31*h+Float.floatToIntBits(x);
            h=31*h+Float.floatToIntBits(y);
            h=31*h+Float.floatToIntBits(z);

            h=31*h+Float.floatToIntBits(r);
            h=31*h+Float.floatToIntBits(g);
            h=31*h+Float.floatToIntBits(b);
            h=31*h+Float.floatToIntBits(a);

            return h;

        }

    }

    //----------------------------------------------------
    // Edge Key
    //----------------------------------------------------

    private static class EdgeKey{

        int v0;
        int v1;

        EdgeKey(int a,int b){

            if(a<b){

                v0=a;
                v1=b;

            }else{

                v0=b;
                v1=a;

            }

        }

        @Override
        public boolean equals(Object obj){

            if(this==obj)
                return true;

            if(!(obj instanceof EdgeKey))
                return false;

            EdgeKey e=(EdgeKey)obj;

            return v0==e.v0 &&
                    v1==e.v1;

        }

        @Override
        public int hashCode(){

            return 31*v0+v1;

        }

    }

    //----------------------------------------------------
    // Height Compare
    //----------------------------------------------------

    private boolean equalHeight(float z1,float z2){

        return Math.abs(z1-z2)<0.0001f;

    }
    //----------------------------------------------------
    // Find Largest Rectangle
    //----------------------------------------------------
    private Rectangle findLargestSquare(int startX, int startY) {
        float z = source[startY][startX].pz;
        int size = 1;
        while (true) {

            int newSize = size + 1;

            //---------------------------------
            // خروج از محدوده
            //---------------------------------

            if (startX + newSize > width)
                break;

            if (startY + newSize > height)
                break;

            boolean ok = true;

            //---------------------------------
            // بررسی کل مربع
            //---------------------------------

            for (int y = startY; y < startY + newSize && ok; y++) {

                for (int x = startX; x < startX + newSize; x++) {

                    if (visited[y][x]) {
                        ok = false;
                        break;
                    }

                    if (!equalHeight(source[y][x].pz, z)) {
                        ok = false;
                        break;
                    }

                }

            }

            if (!ok)
                break;

            size = newSize;

        }

        //---------------------------------
        // علامت گذاری
        //---------------------------------

        for (int y = startY; y < startY + size; y++) {

            for (int x = startX; x < startX + size; x++) {

                visited[y][x] = true;

            }

        }

        return new Rectangle(

                startX,
                startY,

                size,
                size,

                source[startY][startX].pz,

                source[startY][startX].r,
                source[startY][startX].g,
                source[startY][startX].b,
                source[startY][startX].a

        );

    }

    private Rectangle findLargestRectangle(int startX, int startY){

        float z = source[startY][startX].pz;

        //---------------------------------
        // پیدا کردن عرض
        //---------------------------------

        int rectWidth = 0;

        while (startX + rectWidth < width) {

            if (visited[startY][startX + rectWidth])
                break;

            if (!equalHeight(
                    source[startY][startX + rectWidth].pz,
                    z))
                break;

            rectWidth++;
        }

        //---------------------------------
        // پیدا کردن ارتفاع
        //---------------------------------

        int rectHeight = 1;

        boolean expand = true;

        while (expand) {

            if (startY + rectHeight >= height)
                break;

            for (int x = 0; x < rectWidth; x++) {

                if (visited[startY + rectHeight][startX + x]) {
                    expand = false;
                    break;
                }

                if (!equalHeight(
                        source[startY + rectHeight][startX + x].pz,
                        z)) {
                    expand = false;
                    break;
                }
            }

            if (expand)
                rectHeight++;
        }

        //---------------------------------
        // علامت‌گذاری
        //---------------------------------

        for (int y = 0; y < rectHeight; y++) {

            for (int x = 0; x < rectWidth; x++) {

                visited[startY + y][startX + x] = true;
            }
        }

        //---------------------------------
        // خروجی
        //---------------------------------

        Vertex v = source[startY][startX];

        return new Rectangle(

                startX,
                startY,

                rectWidth,
                rectHeight,

                v.pz,

                v.r,
                v.g,
                v.b,
                v.a
        );
    }    //----------------------------------------------------
// Array Convert
//----------------------------------------------------

    private float[] toFloatArray(ArrayList<Float> list){

        float[] array = new float[list.size()];

        for(int i=0;i<list.size();i++)
            array[i]=list.get(i);

        return array;
    }

    private int[] toIntArray(ArrayList<Integer> list){

        int[] array = new int[list.size()];

        for(int i=0;i<list.size();i++)
            array[i]=list.get(i);

        return array;
    }
    //----------------------------------------------------
// Add Vertex
//----------------------------------------------------

    private void addVertex(

            float px,
            float py,
            float pz,

            float u,
            float v,

            float r,
            float g,
            float b,
            float a){

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
    //----------------------------------------------------
// Shared Vertex
//----------------------------------------------------

    private int getVertexIndex(

            float px,
            float py,
            float pz,

            float u,
            float v,

            float r,
            float g,
            float b,
            float a){

        VertexKey key = new VertexKey(

                px,
                py,
                pz,

                r,
                g,
                b,
                a);

        Integer index = vertexMap.get(key);

        if(index != null)
            return index;

        addVertex(

                px,
                py,
                pz,

                u,
                v,

                r,
                g,
                b,
                a);

        index = currentVertexIndex;

        vertexMap.put(key,index);

        currentVertexIndex++;

        return index;

    }
    //----------------------------------------------------
// Shared Edge
//----------------------------------------------------

    private void addEdge(int v0,int v1){

        EdgeKey key = new EdgeKey(v0,v1);

        if(edgeSet.contains(key))
            return;

        edgeSet.add(key);

        lineIndices.add(v0);
        lineIndices.add(v1);

    }
    //----------------------------------------------------
// Add Rectangle
//----------------------------------------------------

    private void addRectangle(Rectangle rect){

        float worldSize = 3.0f;

        float sx = worldSize / (width - 1);
        float sy = worldSize / (height - 1);

        float originX = -worldSize * 0.5f;
        float originY = -worldSize * 0.5f;

        float x0 = rect.x * sx + originX;
        float y0 = rect.y * sy + originY;

        float x1 = (rect.x + rect.width) * sx + originX;
        float y1 = (rect.y + rect.height) * sy + originY;

        float u0 = (float)rect.x/(width-1);
        float v0 = (float)rect.y/(height-1);

        float u1 = (float)(rect.x+rect.width)/(width-1);
        float v1 = (float)(rect.y+rect.height)/(height-1);

        float z = rect.z;

        int i0 = getVertexIndex(
                x0,y0,z,
                u0,v0,
                rect.r,rect.g,rect.b,rect.a);

        int i1 = getVertexIndex(
                x1,y0,z,
                u1,v0,
                rect.r,rect.g,rect.b,rect.a);

        int i2 = getVertexIndex(
                x1,y1,z,
                u1,v1,
                rect.r,rect.g,rect.b,rect.a);

        int i3 = getVertexIndex(
                x0,y1,z,
                u0,v1,
                rect.r,rect.g,rect.b,rect.a);

        //------------------------------------
        // Triangles
        //------------------------------------

        triangleIndices.add(i0);
        triangleIndices.add(i1);
        triangleIndices.add(i2);

        triangleIndices.add(i0);
        triangleIndices.add(i2);
        triangleIndices.add(i3);

        //------------------------------------
        // Lines
        //------------------------------------

        addEdge(i0,i1);
        addEdge(i1,i2);
        addEdge(i2,i3);
        addEdge(i3,i0);

        //------------------------------------
        // مرحله بعد:
        //------------------------------------

        buildLeftWalls(rect);
        buildRightWalls(rect);
        buildTopWalls(rect);
        buildBottomWalls(rect);

    }
    private void buildLeftWalls(Rectangle rect){

        int x = rect.x;

        float zTop = rect.z;

        for(int y = rect.y; y < rect.y + rect.height; y++){

            float zNeighbour;

            if(x == 0)
                zNeighbour = 0.0f;
            else
                zNeighbour = source[y][x-1].pz;

            if(equalHeight(zTop,zNeighbour))
                continue;

            float xx = originX + x * sx;

            float yy0 = originY + y * sy;
            float yy1 = originY + (y+1) * sy;

            addVerticalFace(
                    xx,yy0,
                    xx,yy1,
                    zNeighbour,
                    zTop,
                    rect.r,
                    rect.g,
                    rect.b,
                    rect.a);
        }
    }
    private void buildRightWalls(Rectangle rect){

        int x = rect.x + rect.width;

        float zTop = rect.z;

        for(int y = rect.y; y < rect.y + rect.height; y++){

            float zNeighbour;

            if(x >= width)
                zNeighbour = 0.0f;
            else
                zNeighbour = source[y][x].pz;

            if(equalHeight(zTop,zNeighbour))
                continue;

            float xx = originX + x * sx;

            float yy0 = originY + y * sy;
            float yy1 = originY + (y+1) * sy;

            addVerticalFace(
                    xx,yy1,
                    xx,yy0,
                    zNeighbour,
                    zTop,
                    rect.r,
                    rect.g,
                    rect.b,
                    rect.a);
        }
    }
    private void buildTopWalls(Rectangle rect){

        int y = rect.y;

        float zTop = rect.z;

        for(int x = rect.x; x < rect.x + rect.width; x++){

            float zNeighbour;

            if(y == 0)
                zNeighbour = 0.0f;
            else
                zNeighbour = source[y-1][x].pz;

            if(equalHeight(zTop,zNeighbour))
                continue;

            float yy = originY + y * sy;

            float xx0 = originX + x * sx;
            float xx1 = originX + (x+1) * sx;

            addVerticalFace(
                    xx1,yy,
                    xx0,yy,
                    zNeighbour,
                    zTop,
                    rect.r,
                    rect.g,
                    rect.b,
                    rect.a);
        }
    }

    private void buildBottomWalls(Rectangle rect){

        int y = rect.y + rect.height;

        float zTop = rect.z;

        for(int x = rect.x; x < rect.x + rect.width; x++){

            float zNeighbour;

            if(y >= height)
                zNeighbour = 0.0f;
            else
                zNeighbour = source[y][x].pz;

            if(equalHeight(zTop,zNeighbour))
                continue;

            float yy = originY + y * sy;

            float xx0 = originX + x * sx;
            float xx1 = originX + (x+1) * sx;

            addVerticalFace(
                    xx0,yy,
                    xx1,yy,
                    zNeighbour,
                    zTop,
                    rect.r,
                    rect.g,
                    rect.b,
                    rect.a);
        }
    }
    private void addVerticalFace(

            float x0,float y0,
            float x1,float y1,

            float zBottom,
            float zTop,

            float r,float g,float b,float a){

        int i0 = getVertexIndex(x0,y0,zBottom,0,0,r,g,b,a);
        int i1 = getVertexIndex(x1,y1,zBottom,1,0,r,g,b,a);
        int i2 = getVertexIndex(x1,y1,zTop,1,1,r,g,b,a);
        int i3 = getVertexIndex(x0,y0,zTop,0,1,r,g,b,a);

        triangleIndices.add(i0);
        triangleIndices.add(i1);
        triangleIndices.add(i2);

        triangleIndices.add(i0);
        triangleIndices.add(i2);
        triangleIndices.add(i3);

        addEdge(i0,i1);
        addEdge(i1,i2);
        addEdge(i2,i3);
        addEdge(i3,i0);
    }

}
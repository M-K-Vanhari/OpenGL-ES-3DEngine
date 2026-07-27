package com.mks.opengl3.scene.object3d;

import java.util.ArrayList;
import java.util.HashMap;

public class ContourGenerator {

    //---------------------------------------
    // Input
    //---------------------------------------
    private ArrayList<ContourPoint> currentContour =
            new ArrayList<>();
    private Vertex[][] vertices;
    private boolean buildRoof;
    private boolean buildWalls;
    private boolean buildLines;
    private boolean[][] levelMask;
    private float interval;

    private int width;
    private int height;
    private float minHeight;
    private float maxHeight;

    //---------------------------------------
    // Output
    //---------------------------------------

    private ArrayList<Float> lineVertices =
            new ArrayList<>();

    private ArrayList<Integer> lineIndices =
            new ArrayList<>();
    private ArrayList<Integer> triangleIndices =
            new ArrayList<>();

    //---------------------------------------
    // Vertex Sharing
    //---------------------------------------

    private HashMap<VertexKey,Integer> vertexMap =
            new HashMap<>();

    private int currentIndex=0;

    //---------------------------------------
    // Constructor
    //---------------------------------------

    public ContourGenerator(Vertex[][] vertices){

        this.vertices=vertices;

        height=vertices.length;
        width=vertices[0].length;

    }

    //---------------------------------------
    // MeshData
    //---------------------------------------

    public static class MeshData{

        public final float[] vertices;



        public int[] lineIndices;

        public int[] triangleIndices;

        public MeshData(
                float[] vertices,
                int[] triangleIndices,
                int[] lineIndices){

            this.vertices=vertices;
            this.lineIndices=lineIndices;
            this.triangleIndices=triangleIndices;

        }

    }
    private static class ContourPoint {

        float x;
        float y;
        float z;

        float r;
        float g;
        float b;
        float a;
        int vertexIndex = -1;

    }
    private  static class VertexKey{

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
        public boolean equals(Object o){

            if(this==o)
                return true;

            if(!(o instanceof VertexKey))
                return false;

            VertexKey k=(VertexKey)o;

            return Float.compare(x,k.x)==0 &&
                    Float.compare(y,k.y)==0 &&
                    Float.compare(z,k.z)==0 &&
                    Float.compare(r,k.r)==0 &&
                    Float.compare(g,k.g)==0 &&
                    Float.compare(b,k.b)==0 &&
                    Float.compare(a,k.a)==0;
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
// Build Contour Lines
//----------------------------------------------------

    public MeshData build( float interval,

                           boolean buildRoof,

                           boolean buildWalls,

                           boolean buildLines){

        //---------------------------------------
        // پاک کردن داده‌های قبلی
        //---------------------------------------

        lineVertices.clear();
        lineIndices.clear();
        vertexMap.clear();
        this.interval = interval;
        this.buildRoof = buildRoof;
        this.buildWalls = buildWalls;
        this.buildLines = buildLines;
        currentIndex = 0;

        //---------------------------------------
        // پیدا کردن کمترین و بیشترین ارتفاع
        //---------------------------------------

        minHeight = Float.MAX_VALUE;
        maxHeight = -Float.MAX_VALUE;

        for(int y = 0; y < height; y++){

            for(int x = 0; x < width; x++){

                float z = vertices[y][x].pz;

                if(z < minHeight)
                    minHeight = z;

                if(z > maxHeight)
                    maxHeight = z;

            }

        }

        //---------------------------------------
        // اگر همه ارتفاع‌ها برابر باشند
        //---------------------------------------

        if(maxHeight <= minHeight){

            return new MeshData(
                    new float[0],
                    new int[0],
                    new int[0]
            );

        }

        //---------------------------------------
        // شروع اولین Level
        //---------------------------------------

        float firstLevel =
                (float)Math.ceil(minHeight / interval) * interval;

        //---------------------------------------
        // تولید خطوط میزان
        //---------------------------------------

        for(float level = firstLevel;
            level <= maxHeight;
            level += interval){

            processLevel(level);

        }
        if(buildRoof){

            buildRoofs();

        }
        //---------------------------------------
        // خروجی
        //---------------------------------------

        return new MeshData(

                toFloatArray(lineVertices),
                toIntArray(triangleIndices),
                toIntArray(lineIndices)

        );

    }
    //----------------------------------------------------
// ArrayList<Float> -> float[]
//----------------------------------------------------

    private float[] toFloatArray(ArrayList<Float> list){

        float[] array = new float[list.size()];

        for(int i = 0; i < list.size(); i++){
            array[i] = list.get(i);
        }

        return array;
    }
    //----------------------------------------------------
// ArrayList<Integer> -> int[]
//----------------------------------------------------

    private int[] toIntArray(ArrayList<Integer> list){

        int[] array = new int[list.size()];

        for(int i = 0; i < list.size(); i++){
            array[i] = list.get(i);
        }

        return array;
    }
//    private ArrayList<ContourPoint> sortContour(
//            ArrayList<ContourPoint> points){
//
//        ArrayList<ContourPoint> polygon =
//                new ArrayList<>();
//
//        if(points.isEmpty())
//            return polygon;
//
//        polygon.add(points.get(0));
//
//        points.remove(0);
//
//        while(!points.isEmpty()){
//
//            ContourPoint last =
//                    polygon.get(polygon.size()-1);
//
//            int found = -1;
//
//            for(int i=0;i<points.size();i++){
//
//                ContourPoint p = points.get(i);
//
//                if(equal(last.x,p.x) &&
//                        equal(last.y,p.y))
//                    continue;
//
//                float dx = last.x-p.x;
//                float dy = last.y-p.y;
//
//                if(dx*dx+dy*dy<0.000001f){
//
//                    found=i;
//                    break;
//
//                }
//
//            }
//
//            if(found==-1)
//                break;
//
//            polygon.add(points.remove(found));
//
//        }
//
//        return polygon;
//
//    }
//    private void buildRoof(){
//
//        ArrayList<Integer> polygon =
//                sortContour();
//
//        if(polygon.size()<3)
//            return;
//
//        int root = polygon.get(0);
//
//        for(int i=1;i<polygon.size()-1;i++){
//
//            triangleIndices.add(root);
//            triangleIndices.add(polygon.get(i));
//            triangleIndices.add(polygon.get(i+1));
//
//        }
//
//    }
    private void processLevel(float level){
        currentContour.clear();
        for(int y=0;y<height-1;y++){

            for(int x=0;x<width-1;x++){

                processCell(
                        x,
                        y,
                        level);

            }

        }

//            if(buildRoof){
//
//                buildRoof(currentContour);
//
//
//
//        }

    }
    //------------------------------------------------------
// Marching Squares Table
//------------------------------------------------------

    private static final int[][] CASES = {

            {},             //0
            {3,0},          //1
            {0,1},          //2
            {3,1},          //3
            {1,2},          //4
            {3,2,0,1},      //5
            {0,2},          //6
            {3,2},          //7
            {2,3},          //8
            {0,2},          //9
            {0,3,1,2},      //10
            {1,2},          //11
            {1,3},          //12
            {0,1},          //13
            {3,0},          //14
            {}              //15

    };
    private void processCell(
            int x,
            int y,
            float level){

        Vertex v0 = vertices[y][x];
        Vertex v1 = vertices[y][x + 1];
        Vertex v2 = vertices[y + 1][x + 1];
        Vertex v3 = vertices[y + 1][x];

        //------------------------------------
        // ساخت Case
        //------------------------------------

        int index = 0;

        if (v0.pz >= level) index |= 1;
        if (v1.pz >= level) index |= 2;
        if (v2.pz >= level) index |= 4;
        if (v3.pz >= level) index |= 8;

        if (index == 0 || index == 15)
            return;

        int[] edges = CASES[index];

        //------------------------------------
        // تولید Segmentها
        //------------------------------------

        for (int i = 0; i < edges.length; i += 2) {

            ContourPoint p0 =
                    interpolate(
                            edges[i],
                            x,
                            y,
                            level);

            ContourPoint p1 =
                    interpolate(
                            edges[i + 1],
                            x,
                            y,
                            level);

            if(buildLines){

                addLine(
                        p0,
                        p1,
                        level);
                currentContour.add(p0);
         currentContour.add(p1);
            }

            if(buildWalls){

                addWall(
                        p0,
                        p1,
                        level,
                        level-interval);

            }

        }


    }
//------------------------------------------------------
// Interpolate
//------------------------------------------------------

    private ContourPoint interpolate(
            int edge,
            int x,
            int y,
            float level){

        Vertex a;
        Vertex b;

        switch(edge){

            case 0: // Top

                a = vertices[y][x];
                b = vertices[y][x+1];
                break;

            case 1: // Right

                a = vertices[y][x+1];
                b = vertices[y+1][x+1];
                break;

            case 2: // Bottom

                a = vertices[y+1][x+1];
                b = vertices[y+1][x];
                break;

            default: // Left

                a = vertices[y+1][x];
                b = vertices[y][x];
                break;
        }

        //------------------------------------
        // جلوگیری از تقسیم بر صفر
        //------------------------------------

        float dz = b.pz - a.pz;

        float t;

        if(Math.abs(dz) < 0.000001f)
            t = 0.5f;
        else
            t = (level - a.pz) / dz;

        //------------------------------------
        // خروجی
        //------------------------------------

        ContourPoint p = new ContourPoint();

        p.x = a.px + t * (b.px - a.px);
        p.y = a.py + t * (b.py - a.py);
        p.z = level + 0.001f;   // کمی بالاتر از سطح

        //------------------------------------
        // رنگ
        //------------------------------------

        p.r = a.r + t * (b.r - a.r);
        p.g = a.g + t * (b.g - a.g);
        p.b = a.b + t * (b.b - a.b);
        p.a = a.a + t * (b.a - a.a);

        return p;

    }    //----------------------------------------------------
// Get Vertex Index
//----------------------------------------------------

    //----------------------------------------------------
// Shared Vertex
//----------------------------------------------------

    private int getVertexIndex(

            float px,
            float py,
            float pz,

            float r,
            float g,
            float b,
            float a){

        VertexKey key = new VertexKey(
                px,py,pz,
                r,g,b,a);

        Integer index = vertexMap.get(key);

        if(index != null)
            return index;

        //---------------------------------------
        // Position
        //---------------------------------------

        lineVertices.add(px);
        lineVertices.add(py);
        lineVertices.add(pz);

        //---------------------------------------
        // UV
        //---------------------------------------

        lineVertices.add(0f);
        lineVertices.add(0f);

        //---------------------------------------
        // Normal
        //---------------------------------------

        lineVertices.add(0f);
        lineVertices.add(0f);
        lineVertices.add(1f);

        //---------------------------------------
        // Tangent
        //---------------------------------------

        lineVertices.add(1f);
        lineVertices.add(0f);
        lineVertices.add(0f);

        //---------------------------------------
        // Color
        //---------------------------------------

        lineVertices.add(r);
        lineVertices.add(g);
        lineVertices.add(b);
        lineVertices.add(a);

        index = currentIndex;

        vertexMap.put(key,index);

        currentIndex++;

        return index;

    }
    //------------------------------------------------------
// Add Line
//------------------------------------------------------

    private void addLine(
            ContourPoint p0,
            ContourPoint p1,
            float level){

        int i0 = getVertexIndex(
                p0.x,p0.y,p0.z,
                p0.r,p0.g,p0.b,p0.a);

        int i1 = getVertexIndex(
                p1.x,p1.y,p1.z,
                p1.r,p1.g,p1.b,p1.a);

        lineIndices.add(i0);
        lineIndices.add(i1);

//        addWall(
//                p0,
//                p1,
//                level,
//                level-interval);

    }// Build Wall
//------------------------------------------------------

    private void addWall(
            ContourPoint p0,
            ContourPoint p1,
            float top,
            float bottom){

        int i0 = getVertexIndex(
                p0.x,p0.y,top,
                p0.r,p0.g,p0.b,p0.a);

        int i1 = getVertexIndex(
                p1.x,p1.y,top,
                p1.r,p1.g,p1.b,p1.a);

        int i2 = getVertexIndex(
                p0.x,p0.y,bottom,
                p0.r,p0.g,p0.b,p0.a);

        int i3 = getVertexIndex(
                p1.x,p1.y,bottom,
                p1.r,p1.g,p1.b,p1.a);

        //----------------------------------
        // Triangle 1
        //----------------------------------

        triangleIndices.add(i0);
        triangleIndices.add(i1);
        triangleIndices.add(i2);

        //----------------------------------
        // Triangle 2
        //----------------------------------

        triangleIndices.add(i2);
        triangleIndices.add(i1);
        triangleIndices.add(i3);

    }
    //------------------------------------------------------
// Build Roofs
//------------------------------------------------------

    private void buildRoofs(){

        for(float level = minHeight;
            level <= maxHeight;
            level += interval){

            //--------------------------------------
            // ساخت Mask
            //--------------------------------------

            createLevelMask(level);

            //--------------------------------------
            // Greedy Meshing
            //--------------------------------------

            // meshLevel(level);

        }

    }
    //------------------------------------------------------
// Create Mask
//------------------------------------------------------

    private void createLevelMask(float level){

        levelMask = new boolean[height][width];

        for(int y=0;y<height;y++){

            for(int x=0;x<width;x++){

                levelMask[y][x] =
                        vertices[y][x].pz >= level;

            }

        }

    }
    //------------------------------------------------------
// Append Mesh
//------------------------------------------------------

}
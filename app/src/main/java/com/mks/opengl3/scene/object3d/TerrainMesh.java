package com.mks.opengl3.scene.object3d;


import android.opengl.GLES32;

import com.mks.opengl3.renderer.BufferElement;
import com.mks.opengl3.renderer.Mesh;
import com.mks.opengl3.renderer.ShaderDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TerrainMesh extends Mesh {

    private int[] triangleIndices;
    private int[] lineIndices;

    public TerrainMesh(int width, int height) {

        super(
                createVertices(width,height),
                createTriangleIndices(width,height),
                createLayout()
        );

        setLineIndices(createLineIndicesOptimized(createVertices(width,height),width,height));
    }


    private static int[] createLineIndicesOptimized(float[] vertices, int w, int h) {
        ArrayList<Integer> ind = new ArrayList<>();
        int stride = 15;

        // ساخت ماتریس Z
        float[][][] zMatrix = new float[h][w][3];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                zMatrix[y][x][0] = vertices[(y * w + x) * stride + 2];
                zMatrix[y][x][1] = 0.0f;
                zMatrix[y][x][2] = y * w + x;
            }
        }
        boolean[] near =new boolean[4];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if ((x==0)&&(y==0)) zMatrix[y][x][1] = 1.0f;
                else if ((x==w-1)&&(y==0)) zMatrix[y][x][1] = 1.0f;
                else if ((x==w-1)&&(y==h-1)) zMatrix[y][x][1] = 1.0f;
                else if ((x==0)&&(y==h-1)) zMatrix[y][x][1] = 1.0f;
                else if ((x>0)&&(y==0)&&(x<w-1)
                        &&(zMatrix[y][x][0]!=zMatrix[y][x-1][0])
                        &&(zMatrix[y][x][0]!=zMatrix[y][x+1][0])
                        &&(zMatrix[y][x][0]!=zMatrix[y+1][x][0])
                ) zMatrix[y][x][1] = 1.0f;
                else if ((x>0)&&(y==h-1)&&(x<w-1)
                        &&(zMatrix[y][x][0]!=zMatrix[y][x-1][0])
                        &&(zMatrix[y][x][0]!=zMatrix[y][x+1][0])
                        &&(zMatrix[y][x][0]!=zMatrix[y-1][x][0])
                ) zMatrix[y][x][1] = 1.0f;
                else if ((y>0)&&(x==0)&&(y<h-1)
                        &&(zMatrix[y][x][0]!=zMatrix[y-1][x][0])
                        &&(zMatrix[y][x][0]!=zMatrix[y+1][x][0])
                        &&(zMatrix[y][x][0]!=zMatrix[y][x+1][0])
                ) zMatrix[y][x][1] = 1.0f;
                else if ((y>0)&&(x==w-1)&&(y<h-1)
                        &&(zMatrix[y][x][0]!=zMatrix[y-1][x][0])
                        &&(zMatrix[y][x][0]!=zMatrix[y+1][x][0])
                        &&(zMatrix[y][x][0]!=zMatrix[y][x-1][0])
                ) zMatrix[y][x][1] = 1.0f;
                else if ((y>0)&&(y<h-1)&&(x>0)&&(x<w-1)
                        &&(zMatrix[y][x][0]!=zMatrix[y-1][x][0])
                        &&(zMatrix[y][x][0]!=zMatrix[y+1][x][0])
                        &&(zMatrix[y][x][0]!=zMatrix[y][x-1][0])
                        &&(zMatrix[y][x][0]!=zMatrix[y][x+1][0])
                ) zMatrix[y][x][1] = 1.0f;
            }
        }

        // Horizontal

        ArrayList<Integer> ind = new ArrayList<>();

        // Horizontal
        for(int y=0;y<h;y++){

            for(int x=0;x<w-1;x++){

                int i0=y*w+x;
                int i1=i0+1;

                ind.add(i0);
                ind.add(i1);
            }
        }

        // Vertical
        for(int x=0;x<w;x++){

            for(int y=0;y<h-1;y++){

                int i0=y*w+x;
                int i1=i0+w;

                ind.add(i0);
                ind.add(i1);
            }
        }

        int[] r=new int[ind.size()];

        for(int i=0;i<r.length;i++)
            r[i]=ind.get(i);

        return r;

    }

    // تابع کمکی برای شمارش تعداد متوالی در یک ردیف
    private static int getConsecutiveCount(float[] row, int start, float value) {
        int count = 0;
        for (int i = start; i < row.length && row[i] == value; i++) {
            count++;
        }
        return count;
    }

    // تابع کمکی برای شمارش تعداد متوالی در یک ستون
    private static int getConsecutiveCountVertical(float[][] zMatrix, int startY, int x, float value) {
        int count = 0;
        for (int y = startY; y < zMatrix.length && zMatrix[y][x] == value; y++) {
            count++;
        }
        return count;
    }
    private static int[] createTriangleIndices(int w,int h){

        ArrayList<Integer> ind = new ArrayList<>();

        for(int y=0;y<h-1;y++){

            for(int x=0;x<w-1;x++){

                int i0 = y*w+x;
                int i1 = i0+1;
                int i2 = i0+w;
                int i3 = i2+1;

                ind.add(i2);
                ind.add(i0);
                ind.add(i1);

                ind.add(i2);
                ind.add(i1);
                ind.add(i3);
            }
        }

        int[] r=new int[ind.size()];

        for(int i=0;i<r.length;i++)
            r[i]=ind.get(i);

        return r;
    }

    private static int[] createLineIndices(int w,int h){

        ArrayList<Integer> ind = new ArrayList<>();

        // Horizontal
        for(int y=0;y<h;y++){

            for(int x=0;x<w-1;x++){

                int i0=y*w+x;
                int i1=i0+1;

                ind.add(i0);
                ind.add(i1);
            }
        }

        // Vertical
        for(int x=0;x<w;x++){

            for(int y=0;y<h-1;y++){

                int i0=y*w+x;
                int i1=i0+w;

                ind.add(i0);
                ind.add(i1);
            }
        }

        int[] r=new int[ind.size()];

        for(int i=0;i<r.length;i++)
            r[i]=ind.get(i);

        return r;
    }
    private static float[] createVertices(int w, int h) {

        ArrayList<Float> data = new ArrayList<>();

        float sx = 3.0f / (w - 1);
        float sy = 3.0f / (h - 1);

        for (int y = 0; y < h; y++) {

            for (int x = 0; x < w; x++) {

                float px = x * sx - 1.5f;
                float py = y * sy - 1.5f;
                float pz = 0;

                // position
                data.add(px);
                data.add(py);
                data.add(pz);

                // uv
                data.add((float)x/(w-1));
                data.add((float)y/(h-1));

                // normal
                data.add(0f);
                data.add(0f);
                data.add(1f);

                // tangent
                data.add(1f);
                data.add(0f);
                data.add(0f);

                // color
                data.add(0f);
                data.add(1f);
                data.add(0f);
                data.add(1f);
            }
        }

        float[] v = new float[data.size()];

        for(int i=0;i<data.size();i++)
            v[i]=data.get(i);

        return v;
    }

    private static List<BufferElement> createLayout() {

        ArrayList<BufferElement> layout = new ArrayList<>();

        layout.add(new BufferElement(ShaderDataType.Vec3, "aPosition", false));
        layout.add(new BufferElement(ShaderDataType.Vec2, "aTexCoord", false));
        layout.add(new BufferElement(ShaderDataType.Vec3, "aNormal", false));
        layout.add(new BufferElement(ShaderDataType.Vec3, "aTangent", false));
        layout.add(new BufferElement(ShaderDataType.Vec4, "aColor", false));

        return layout;
    }
}


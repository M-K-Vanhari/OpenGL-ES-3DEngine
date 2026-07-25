package com.mks.opengl3.scene.object3d;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES32;

import com.mks.opengl3.MainActivity;
import com.mks.opengl3.R;
import com.mks.opengl3.renderer.BufferElement;
import com.mks.opengl3.renderer.Mesh;
import com.mks.opengl3.renderer.ShaderDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TerrainMesh extends Mesh {

    private int[] triangleIndices;
    private int[] lineIndices;

    public TerrainMesh(Bitmap bitmap,int mode,int width, int height) {
        super(
                bitmap == null
                        ? createVertices(width, height)
                        : createVerticesFromBitmap(bitmap),
                createTriangleIndices(width,height),
                createLayout()
        );

            setTriangleIndices(createTriangleIndices(width,height));
            setLineIndices(createLineIndices(width,height));
            if ((mode == Object3D.OLINES)) {
                setOLineIndices(createLineIndicesOptimized(getVertices(), width, height));
            }

            if (mode == Object3D.LINEPOINTS)
            {
                setVertices(createLinePointVertices(getVertices()));
                setLinePointsIndices(createLinePointIndices(getVertices()));
            }

    }


    public static float[][][] medianFilter(float[][][] zMatrix) {

        int h = zMatrix.length;
        int w = zMatrix[0].length;

        float[][] filtered = new float[h][w];

        float[] values = new float[9];

        for (int y = 1; y < h - 1; y++) {

            for (int x = 1; x < w - 1; x++) {

                int k = 0;

                for (int j = -1; j <= 1; j++) {

                    for (int i = -1; i <= 1; i++) {

                        values[k++] = zMatrix[y + j][x + i][0];

                    }
                }

                Arrays.sort(values);

                filtered[y][x] = values[4];
            }
        }

        // اعمال نتیجه روی ماتریس اصلی
        for (int y = 1; y < h - 1; y++) {

            for (int x = 1; x < w - 1; x++) {

                zMatrix[y][x][0] = filtered[y][x];

            }
        }
        return zMatrix;
    }

    public static void removeFlatArea(float[][][] zMatrix, float tolerance) {

        int h = zMatrix.length;
        int w = zMatrix[0].length;

        for (int y = 1; y < h - 1; y++) {

            for (int x = 1; x < w - 1; x++) {

                float z = zMatrix[y][x][0];

                boolean flat = true;

                for (int j = -1; j <= 1 && flat; j++) {

                    for (int i = -1; i <= 1; i++) {

                        if (i == 0 && j == 0)
                            continue;

                        if (Math.abs(zMatrix[y + j][x + i][0] - z) > tolerance) {
                            flat = false;
                            break;
                        }
                    }
                }

                if (flat) {

                    // این ورتکس حذف شود
                    zMatrix[y][x][1] = 1.0f;

                }
            }
        }
    }
    public static void averageFilter(float[][][] zMatrix) {

        int h = zMatrix.length;
        int w = zMatrix[0].length;

        float[][] filtered = new float[h][w];

        for (int y = 1; y < h - 1; y++) {

            for (int x = 1; x < w - 1; x++) {

                float sum = 0.0f;

                for (int j = -1; j <= 1; j++) {

                    for (int i = -1; i <= 1; i++) {

                        sum += zMatrix[y + j][x + i][0];

                    }
                }

                filtered[y][x] = sum / 9.0f;
            }
        }

        // کپی نتیجه به ماتریس اصلی
        for (int y = 1; y < h - 1; y++) {

            for (int x = 1; x < w - 1; x++) {

                zMatrix[y][x][0] = filtered[y][x];

            }
        }
    }
    public static void gaussianFilter(float[][][] zMatrix) {

        int h = zMatrix.length;
        int w = zMatrix[0].length;

        float[][] filtered = new float[h][w];

        int[][] kernel = {
                {1,2,1},
                {2,4,2},
                {1,2,1}
        };

        for (int y = 1; y < h - 1; y++) {

            for (int x = 1; x < w - 1; x++) {

                float sum = 0.0f;

                for (int j = -1; j <= 1; j++) {

                    for (int i = -1; i <= 1; i++) {

                        sum += zMatrix[y + j][x + i][0] *
                                kernel[j + 1][i + 1];

                    }

                }

                filtered[y][x] = sum / 16.0f;

            }
        }

        // کپی نتیجه
        for (int y = 1; y < h - 1; y++) {

            for (int x = 1; x < w - 1; x++) {

                zMatrix[y][x][0] = filtered[y][x];

            }
        }
    }
    public static float[][][] removeFlatSlope(float[][][] zMatrix, float tolerance) {

        int h = zMatrix.length;
        int w = zMatrix[0].length;

        for (int y = 1; y < h - 1; y++) {

            for (int x = 1; x < w - 1; x++) {

                float center = zMatrix[y][x][0];

                float dzLeft  = Math.abs(center - zMatrix[y][x - 1][0]);
                float dzRight = Math.abs(center - zMatrix[y][x + 1][0]);
                float dzUp    = Math.abs(center - zMatrix[y - 1][x][0]);
                float dzDown  = Math.abs(center - zMatrix[y + 1][x][0]);

                float maxSlope = Math.max(
                        Math.max(dzLeft, dzRight),
                        Math.max(dzUp, dzDown));

                if (maxSlope < tolerance) {

                    // این نقطه شیب مؤثری ندارد
                    zMatrix[y][x][1] = 1.0f;

                }
            }
        }
        return zMatrix;
    }
    public static float[][][] laplacianFilter(float[][][] zMatrix) {

        int h = zMatrix.length;
        int w = zMatrix[0].length;

        float[][] filtered = new float[h][w];

        for (int y = 1; y < h - 1; y++) {

            for (int x = 1; x < w - 1; x++) {

                float center = zMatrix[y][x][0];

                float value =
                        4.0f * center
                                - zMatrix[y - 1][x][0]
                                - zMatrix[y + 1][x][0]
                                - zMatrix[y][x - 1][0]
                                - zMatrix[y][x + 1][0];

                filtered[y][x] = value;
            }
        }

        for (int y = 1; y < h - 1; y++) {

            for (int x = 1; x < w - 1; x++) {

                zMatrix[y][x][0] = filtered[y][x];

            }
        }
        return zMatrix;
    }
    public static void bilateralFilter(float[][][] zMatrix,
                                       float sigmaSpace,
                                       float sigmaRange) {

        int h = zMatrix.length;
        int w = zMatrix[0].length;

        float[][] filtered = new float[h][w];

        int radius = 1;   // فیلتر 3×3

        for (int y = radius; y < h - radius; y++) {

            for (int x = radius; x < w - radius; x++) {

                float center = zMatrix[y][x][0];

                float sum = 0.0f;
                float weightSum = 0.0f;

                for (int j = -radius; j <= radius; j++) {

                    for (int i = -radius; i <= radius; i++) {

                        float neighbor = zMatrix[y + j][x + i][0];

                        // فاصله مکانی
                        float ds = i * i + j * j;

                        // اختلاف ارتفاع
                        float dr = neighbor - center;

                        float spatialWeight =
                                (float)Math.exp(-ds / (2.0f * sigmaSpace * sigmaSpace));

                        float rangeWeight =
                                (float)Math.exp(-(dr * dr) /
                                        (2.0f * sigmaRange * sigmaRange));

                        float weight = spatialWeight * rangeWeight;

                        sum += neighbor * weight;
                        weightSum += weight;
                    }
                }

                filtered[y][x] = sum / weightSum;
            }
        }

        // کپی نتیجه
        for (int y = radius; y < h - radius; y++) {

            for (int x = radius; x < w - radius; x++) {

                zMatrix[y][x][0] = filtered[y][x];

            }
        }
    }



    public static float[] vertexClustering(float[] vertices,
                                           float cellSize){

        int stride = 15;

        HashMap<String,Cluster> clusters = new HashMap<>();

        for(int i=0;i<vertices.length;i+=stride){

            float x = vertices[i];
            float y = vertices[i+1];
            float z = vertices[i+2];

            int cx = (int)Math.floor(x/cellSize);
            int cy = (int)Math.floor(y/cellSize);
            int cz = (int)Math.floor(z/cellSize);

            String key = cx+"_"+cy+"_"+cz;

            Cluster c = clusters.get(key);

            if(c==null){

                c=new Cluster();
                clusters.put(key,c);

            }

            c.x+=vertices[i];
            c.y+=vertices[i+1];
            c.z+=vertices[i+2];

            c.u+=vertices[i+3];
            c.v+=vertices[i+4];

            c.nx+=vertices[i+5];
            c.ny+=vertices[i+6];
            c.nz+=vertices[i+7];

            c.tx+=vertices[i+8];
            c.ty+=vertices[i+9];
            c.tz+=vertices[i+10];

            c.r+=vertices[i+11];
            c.g+=vertices[i+12];
            c.b+=vertices[i+13];
            c.a+=vertices[i+14];

            c.count++;

        }

        float[] result=new float[clusters.size()*stride];

        int p=0;

        for(Cluster c:clusters.values()){

            result[p++]=c.x/c.count;
            result[p++]=c.y/c.count;
            result[p++]=c.z/c.count;

            result[p++]=c.u/c.count;
            result[p++]=c.v/c.count;

            result[p++]=c.nx/c.count;
            result[p++]=c.ny/c.count;
            result[p++]=c.nz/c.count;

            result[p++]=c.tx/c.count;
            result[p++]=c.ty/c.count;
            result[p++]=c.tz/c.count;

            result[p++]=c.r/c.count;
            result[p++]=c.g/c.count;
            result[p++]=c.b/c.count;
            result[p++]=c.a/c.count;

        }

        return result;

    }
    private static int[] createLineIndicesOptimized(float[] vertices, int w, int h) {
        ArrayList<Integer> ind = new ArrayList<>();
        int stride = 15;
        float tolerance = 0.1f;
        float[][][] zMatrix = new float[h][w][3];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                float z = vertices[(y * w + x) * stride + 2];
                z = ((int)(z / tolerance)) * tolerance;

              //  zMatrix[y][x][0] = z;
                zMatrix[y][x][0] = vertices[(y * w + x) * stride + 2];
                zMatrix[y][x][1] = 0.0f;
                zMatrix[y][x][2] = y * w + x;
            }
        }
        zMatrix =removeFlatSlope(zMatrix,1.0f);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if ((x == 0) && (y == 0)) zMatrix[y][x][1] = 3.0f;
                if ((x == w - 1) && (y == 0)) zMatrix[y][x][1] = 3.0f;
                if ((x == w - 1) && (y == h - 1)) zMatrix[y][x][1] = 3.0f;
                if ((x == 0) && (y == h - 1)) zMatrix[y][x][1] = 3.0f;
                if ((x > 0) && (y == 0) && (x < w - 1)) {
                    if ((zMatrix[y][x][0] != zMatrix[y][x - 1][0])
                            || (zMatrix[y][x][0] != zMatrix[y][x + 1][0])
                    ) {
                        if ((zMatrix[y][x][1] == 2.0f) || (zMatrix[y][x][1] == 1.0f))
                            zMatrix[y][x][1] = 3.0f;
                        else
                            zMatrix[y][x][1] = 1.0f;
                    }
                }
                if ((x > 0) && (y == 0) && (x < w - 1)
                        && (zMatrix[y][x][0] != zMatrix[y + 1][x][0])
                ) {
                    if ((zMatrix[y][x][1] == 2.0f) || (zMatrix[y][x][1] == 1.0f))
                        zMatrix[y][x][1] = 3.0f;
                    else
                        zMatrix[y][x][1] = 2.0f;
                }
                if ((x > 0) && (y == h - 1) && (x < w - 1)) {
                    if ((zMatrix[y][x][0] != zMatrix[y][x - 1][0])
                            || (zMatrix[y][x][0] != zMatrix[y][x + 1][0])
                    ) {
                        if ((zMatrix[y][x][1] == 2.0f) || (zMatrix[y][x][1] == 1.0f))
                            zMatrix[y][x][1] = 3.0f;
                        else
                            zMatrix[y][x][1] = 1.0f;
                    }
                }
                if ((x > 0) && (y == h - 1) && (x < w - 1)
                        && (zMatrix[y][x][0] != zMatrix[y - 1][x][0])
                ) {
                    if ((zMatrix[y][x][1] == 2.0f) || (zMatrix[y][x][1] == 1.0f))
                        zMatrix[y][x][1] = 3.0f;
                    else
                        zMatrix[y][x][1] = 2.0f;
                }
                if ((y > 0) && (x == 0) && (y < h - 1)) {
                    if ((zMatrix[y][x][0] != zMatrix[y - 1][x][0])
                            || (zMatrix[y][x][0] != zMatrix[y + 1][x][0])
                    ) {
                        if ((zMatrix[y][x][1] == 2.0f) || (zMatrix[y][x][1] == 1.0f))
                            zMatrix[y][x][1] = 3.0f;
                        else
                            zMatrix[y][x][1] = 2.0f;
                    }
                }
                if ((y > 0) && (x == 0) && (y < h - 1)
                        && (zMatrix[y][x][0] != zMatrix[y][x + 1][0])
                ) {
                    if ((zMatrix[y][x][1] == 2.0f) || (zMatrix[y][x][1] == 1.0f))
                        zMatrix[y][x][1] = 3.0f;
                    else
                        zMatrix[y][x][1] = 1.0f;
                }
                if ((y > 0) && (x == w - 1) && (y < h - 1)) {
                    if ((zMatrix[y][x][0] != zMatrix[y - 1][x][0])
                            || (zMatrix[y][x][0] != zMatrix[y + 1][x][0])
                    ) if ((zMatrix[y][x][1] == 2.0f) || (zMatrix[y][x][1] == 1.0f))
                        zMatrix[y][x][1] = 3.0f;
                    else
                        zMatrix[y][x][1] = 2.0f;
                }
                if ((y > 0) && (x == w - 1) && (y < h - 1)
                        && (zMatrix[y][x][0] != zMatrix[y][x - 1][0])
                ){ if ((zMatrix[y][x][1] == 2.0f) || (zMatrix[y][x][1] == 1.0f))
                    zMatrix[y][x][1] = 3.0f;
                else
                    zMatrix[y][x][1] = 1.0f;
                }
                if ((y > 0) && (y < h - 1) && (x > 0) && (x < w - 1)) {
                    if ((zMatrix[y][x][0] != zMatrix[y][x - 1][0]) ||
                            (zMatrix[y][x][0] != zMatrix[y][x + 1][0])
                    ) {
                        if ((zMatrix[y][x][1] == 2.0f)||(zMatrix[y][x][1] == 1.0f))
                            zMatrix[y][x][1] = 3.0f;
                        else
                            zMatrix[y][x][1] = 1.0f;
                    }
                    if ((zMatrix[y][x][0] != zMatrix[y - 1][x][0]) ||
                            (zMatrix[y][x][0] != zMatrix[y + 1][x][0])
                    ) {
                        if ((zMatrix[y][x][1] == 2.0f)||(zMatrix[y][x][1] == 1.0f))
                            zMatrix[y][x][1] = 3.0f;
                        else
                            zMatrix[y][x][1] = 2.0f;
                    }

                }

            }
        }

        for (int y = 0; y < h; y++) {
            int start=0;
            int start_set=0;

            for (int x = 0; x < w; x++) {
                if ((zMatrix[y][x][1] == 1.0f)||(zMatrix[y][x][1] == 3.0f))
                {
                    if(start_set==0){
                        ind.add((int)zMatrix[y][x][2]);
                        start_set=1;
                    }
                    else if (x== w-1){
                        if (start_set==2)
                            ind.add(start);
                        ind.add((int)zMatrix[y][x][2]);
                    }
                    else if (start_set==1){
                        ind.add((int)zMatrix[y][x][2]);
                        start=(int)zMatrix[y][x][2];
                        start_set=2;
                    }
                    else {
                        ind.add(start);
                        ind.add((int)zMatrix[y][x][2]);
                        start=(int)zMatrix[y][x][2];
                    }
                }
            }
        }

        for (int x = 0; x < w; x++) {
            int start=0;
            int start_set=0;

            for (int y = 0; y < h; y++) {
                if ((zMatrix[y][x][1] == 2.0f)||(zMatrix[y][x][1] == 3.0f))
                {
                    if(start_set==0){
                        ind.add((int)zMatrix[y][x][2]);
                        start_set=1;
                    }
                    else if (y== h-1){
                        if (start_set==2)
                            ind.add(start);
                        ind.add((int)zMatrix[y][x][2]);
                    }
                    else if (start_set==1){
                        ind.add((int)zMatrix[y][x][2]);
                        start=(int)zMatrix[y][x][2];
                        start_set=2;
                    }
                    else {
                        ind.add(start);
                        ind.add((int)zMatrix[y][x][2]);
                        start=(int)zMatrix[y][x][2];
                    }
                }
            }
        }

        int[] r=new int[ind.size()];

        for(int i=0;i<r.length;i++)
            r[i]=ind.get(i);

        return r;

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


    private static float[] createVerticesFromBitmap(Bitmap bitmap) {

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        ArrayList<Float> data = new ArrayList<>();

        float sx = 3.0f / (w - 1);
        float sy = 3.0f / (h - 1);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int pixel = bitmap.getPixel(x, y);

                // استخراج مقادیر RGB
                float r = ((pixel >> 16) & 0xFF) / 255.0f;
                float g = ((pixel >> 8) & 0xFF) / 255.0f;
                float b = (pixel & 0xFF) / 255.0f;

                // موقعیت در فضای 3D (می‌توانید بر اساس رنگ ارتفاع دهید)
                float px = x * sx - 1.5f;
                float py = y * sy - 1.5f;
             //   float pz = (r + g + b) / 3.0f * 2.0f - 1.0f; // ارتفاع بر اساس روشنایی
                float pz = (float)Math.sqrt(
                        (r - 0f) * (r - 0f) +
                                (g - 1f) * (g - 1f) +
                                (b - 0f) * (b - 0f)
                );
                // position
                data.add(px);
                data.add(py);
                data.add(pz);

                // uv (مختصات بافت)
                data.add((float) x / (w - 1));
                data.add((float) y / (h - 1));

                // normal (محاسبه شده بر اساس ارتفاع)
                float[] normal = calculateNormal(x, y, bitmap);
                data.add(normal[0]);
                data.add(normal[1]);
                data.add(normal[2]);

                // tangent
                data.add(1f);
                data.add(0f);
                data.add(0f);

                // color (رنگ پیکسل)
                data.add(r);
                data.add(g);
                data.add(b);
                data.add(1f); // alpha
            }
        }

        float[] v = new float[data.size()];
        for (int i = 0; i < data.size(); i++) {
            v[i] = data.get(i);
        }

        return v;
    }
    private static float[] createLinePointVertices(float[] vertices) {

        final int STRIDE = 15;

        int vertexCount = vertices.length / STRIDE;

        float[] result = new float[vertices.length * 2];

        int dst = 0;

        for (int i = 0; i < vertexCount; i++) {

            int src = i * STRIDE;

            // ورتکس اصلی
            System.arraycopy(vertices, src, result, dst, STRIDE);
            dst += STRIDE;

            // ورتکس جدید
            System.arraycopy(vertices, src, result, dst, STRIDE);

            // فقط Z را صفر کن
            result[dst + 2] = 0.0f;

            dst += STRIDE;
        }

        return result;
    }

    private static int[] createLinePointIndices(float[] vertices) {

        final int STRIDE = 15;

        int vertexCount = vertices.length / STRIDE;

        int[] indices = new int[vertexCount];

        int k = 0;

        for (int i = 0; i < vertexCount; i += 2) {

            indices[k++] = i;
            indices[k++] = i + 1;
        }

        return indices;
    }
    private static float[] calculateNormal(int x, int y, Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        float height = getHeightFromPixel(bitmap.getPixel(x, y));
        float heightX1 = (x < w - 1) ? getHeightFromPixel(bitmap.getPixel(x + 1, y)) : height;
        float heightX2 = (x > 0) ? getHeightFromPixel(bitmap.getPixel(x - 1, y)) : height;
        float heightY1 = (y < h - 1) ? getHeightFromPixel(bitmap.getPixel(x, y + 1)) : height;
        float heightY2 = (y > 0) ? getHeightFromPixel(bitmap.getPixel(x, y - 1)) : height;

        float gradX = (heightX1 - heightX2) / 2.0f;
        float gradY = (heightY1 - heightY2) / 2.0f;

        float nx = -gradX;
        float ny = -gradY;
        float nz = 1.0f;

        float length = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (length > 0) {
            nx /= length;
            ny /= length;
            nz /= length;
        }

        return new float[]{nx, ny, nz};
    }

    private static float getHeightFromPixel(int pixel) {
        float r = ((pixel >> 16) & 0xFF) / 255.0f;
        float g = ((pixel >> 8) & 0xFF) / 255.0f;
        float b = (pixel & 0xFF) / 255.0f;
        return (r + g + b) / 3.0f;
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

//    Edge edge = new Edge(10,11);
//
//    CollapseResult result =
//            EdgeCollapse.edgeCollapse(
//                    vertices,
//                    triangleIndices,
//                    edge);
//
//mesh.setVertices(result.vertices);
//mesh.setTriangleIndices(result.indices);
    public static CollapseResult edgeCollapse(
            float[] vertices,
            int[] indices,
            Edge edge)
    {

        final int stride = 15;

        int keep = edge.v1;
        int remove = edge.v2;

        // ---------- انتقال ورتکس به وسط ----------

        int a = keep * stride;
        int b = remove * stride;

        for(int i=0;i<stride;i++){

            vertices[a+i] =
                    (vertices[a+i]+vertices[b+i])*0.5f;

        }

        // ---------- جایگزینی اندیس ----------

        for(int i=0;i<indices.length;i++){

            if(indices[i]==remove)
                indices[i]=keep;

        }

        // ---------- حذف مثلث های خراب ----------

        ArrayList<Integer> newIndices =
                new ArrayList<>();

        for(int i=0;i<indices.length;i+=3){

            int i0=indices[i];
            int i1=indices[i+1];
            int i2=indices[i+2];

            if(i0==i1 || i1==i2 || i0==i2)
                continue;

            newIndices.add(i0);
            newIndices.add(i1);
            newIndices.add(i2);

        }

        int[] outIndices =
                new int[newIndices.size()];

        for(int i=0;i<newIndices.size();i++)
            outIndices[i]=newIndices.get(i);

        CollapseResult result =
                new CollapseResult();

        result.vertices = vertices;
        result.indices = outIndices;

        return result;

    }
}


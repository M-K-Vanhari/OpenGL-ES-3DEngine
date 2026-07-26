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
        if (bitmap != null)
            setOLineIndices(createLineIndicesOptimized(createVerticesFromBitmap(bitmap),width,height));
        if (mode == Object3D.LINEPOINTS)
        {
            setVertices(createLinePointVertices(getVertices()));
            setLinePointsIndices(createLinePointIndices(getVertices()));
        }

        Vertex[][] vr=Vertex.fromFloatArray(getVertices(),width,height);
        GreedyMesher m =new GreedyMesher(vr);
        GreedyMesher.MeshData md = m.build();
        setVertices(md.vertices);
        setTriangleIndices(md.triangleIndices);
        setLineIndices(md.lineIndices);
    }

    static class ZData {
        float value;
        int direction;
        int index;

        ZData(float value, int direction, int index) {
            this.value = value;
            this.direction = direction;
            this.index = index;
        }
    }
    private static ZData[][] getZMatrix(float[] vertices, int w, int h){
        int stride = 15;
        ZData[][] zMatrix = new ZData[h][w];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int index = y * w + x;
                zMatrix[y][x] = new ZData(
                        vertices[index * stride + 2],
                        0,
                        index
                );
            }
        }
        return zMatrix;
    }
    private static int calculateDirection(ZData current, ZData left, ZData right, ZData up, ZData down, int x, int y, int w, int h) {
        // بررسی همسایه‌ها
        boolean diffLeft = (x > 0) && (current.value != left.value);
        boolean diffRight = (x < w-1) && (current.value != right.value);
        boolean diffUp = (y > 0) && (current.value != up.value);
        boolean diffDown = (y < h-1) && (current.value != down.value);

        // تشخیص لبه‌ها
        boolean horizontalEdge = diffLeft || diffRight;
        boolean verticalEdge = diffUp || diffDown;

        // گوشه‌ها
        boolean isCorner = (x == 0 && y == 0) || (x == w-1 && y == 0) ||
                (x == w-1 && y == h-1) || (x == 0 && y == h-1);

        if (isCorner) return 3;
        if (horizontalEdge && verticalEdge) return 3;
        if (horizontalEdge) return 1;
        if (verticalEdge) return 2;
        return 0;
    }


    private static int[] createLineIndicesOptimized(float[] vertices, int w, int h) {
        ArrayList<Integer> ind = new ArrayList<>();
//            int stride = 15;
//
//            float[][][] zMatrix = new float[h][w][3];
//            for (int y = 0; y < h; y++) {
//                for (int x = 0; x < w; x++) {
//                    zMatrix[y][x][0] = vertices[(y * w + x) * stride + 2];
//                    zMatrix[y][x].direction = 0.0f;
//                    zMatrix[y][x][2] = y * w + x;
//                }
//            }
        ZData[][] zMatrix=getZMatrix(vertices,w,h);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                ZData left = (x > 0) ? zMatrix[y][x-1] : null;
                ZData right = (x < w-1) ? zMatrix[y][x+1] : null;
                ZData up = (y > 0) ? zMatrix[y-1][x] : null;
                ZData down = (y < h-1) ? zMatrix[y+1][x] : null;

                zMatrix[y][x].direction = calculateDirection(
                        zMatrix[y][x], left, right, up, down, x, y, w, h
                );
            }
        }

// پردازش لبه‌های افقی
        for (int y = 0; y < h; y++) {
            // پیدا کردن اولین نقطه لبه
            int firstEdge = -1;
            for (int x = 0; x < w; x++) {
                if (zMatrix[y][x].direction == 1 || zMatrix[y][x].direction == 3) {
                    firstEdge = x;
                    break;
                }
            }

            if (firstEdge == -1) continue;

            int prevEdge = firstEdge;
            int currentEdge = firstEdge;

            // پیدا کردن نقاط لبه و ایجاد بردارها
            for (int x = firstEdge + 1; x < w; x++) {
                if (zMatrix[y][x].direction == 1 || zMatrix[y][x].direction == 3) {
                    // نقطه قبلی
                    ind.add(zMatrix[y][prevEdge].index);
                    // نقطه فعلی
                    ind.add(zMatrix[y][currentEdge].index);
                    // نقطه بعدی
                    ind.add(zMatrix[y][x].index);

                    prevEdge = currentEdge;
                    currentEdge = x;
                }
            }
        }

// پردازش لبه‌های عمودی (مشابه)
        for (int x = 0; x < w; x++) {
            int firstEdge = -1;
            for (int y = 0; y < h; y++) {
                if (zMatrix[y][x].direction == 2 || zMatrix[y][x].direction == 3) {
                    firstEdge = y;
                    break;
                }
            }

            if (firstEdge == -1) continue;

            int prevEdge = firstEdge;
            int currentEdge = firstEdge;

            for (int y = firstEdge + 1; y < h; y++) {
                if (zMatrix[y][x].direction == 2 || zMatrix[y][x].direction == 3) {
                    ind.add(zMatrix[prevEdge][x].index);
                    ind.add(zMatrix[currentEdge][x].index);
                    ind.add(zMatrix[y][x].index);

                    prevEdge = currentEdge;
                    currentEdge = y;
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
}
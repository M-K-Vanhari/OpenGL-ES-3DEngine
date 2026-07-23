package com.mks.opengl3.scene.object3d;



import android.opengl.GLES32;

import com.mks.opengl3.renderer.BufferElement;
import com.mks.opengl3.renderer.Mesh;
import com.mks.opengl3.renderer.ShaderDataType;

import java.util.ArrayList;
import java.util.List;



public class Plane extends Mesh {

    public Plane(int widthSegments, int heightSegments) {
        super(
                createVertices(widthSegments, heightSegments),
                createIndices(widthSegments, heightSegments),
                createLayout()
        );

    }

    private static float[] createVertices(int wSeg, int hSeg) {

        int vertexCount = (wSeg + 1) * (hSeg + 1);

        // position3 + uv2 + normal3 + tangent3 + color4 = 15 float
        float[] vertices = new float[vertexCount * 15];

        int p = 0;

        for (int y = 0; y <= hSeg; y++) {

            float fy = (float) y / hSeg - 0.5f;

            for (int x = 0; x <= wSeg; x++) {

                float fx = (float) x / wSeg - 0.5f;

                // position
                vertices[p++] = fx;
                vertices[p++] = fy;
                vertices[p++] = 0.0f;

                // uv
                vertices[p++] = (float) x / wSeg;
                vertices[p++] = (float) y / hSeg;

                // normal
                vertices[p++] = 0;
                vertices[p++] = 0;
                vertices[p++] = 1;

                // tangent
                vertices[p++] = 1;
                vertices[p++] = 0;
                vertices[p++] = 0;

                // color
                vertices[p++] = 1;
                vertices[p++] = 1;
                vertices[p++] = 1;
                vertices[p++] = 1;
            }
        }

        return vertices;
    }

    private static int[] createIndices(int wSeg, int hSeg) {

        int horizontalLines = (hSeg + 1) * wSeg;
        int verticalLines   = (wSeg + 1) * hSeg;

        int[] indices = new int[(horizontalLines + verticalLines) * 2];

        int k = 0;

        // خطوط افقی
        for (int y = 0; y <= hSeg; y++) {
            for (int x = 0; x < wSeg; x++) {

                int v0 = y * (wSeg + 1) + x;
                int v1 = v0 + 1;

                indices[k++] = v0;
                indices[k++] = v1;
            }
        }

        // خطوط عمودی
        for (int x = 0; x <= wSeg; x++) {
            for (int y = 0; y < hSeg; y++) {

                int v0 = y * (wSeg + 1) + x;
                int v1 = v0 + (wSeg + 1);

                indices[k++] = v0;
                indices[k++] = v1;
            }
        }

        return indices;
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
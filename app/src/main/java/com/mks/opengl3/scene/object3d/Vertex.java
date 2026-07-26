package com.mks.opengl3.scene.object3d;

public class Vertex {
    public static final int STRIDE = 15;
    // position
    float px = 0.0f;
    float py = 0.0f;
    float pz = 0.0f;
    // uv
    float u = 0.0f;
    float v = 0.0f;
    // normal
    float nx = 0.0f;
    float ny = 0.0f;
    float nz = 0.0f;
    // tangent
    float tx = 0.0f;
    float ty = 0.0f;
    float tz = 0.0f;
    // color
    float r = 0.0f;
    float g = 0.0f;
    float b = 0.0f;
    float a = 0.0f;


    public Vertex()
    {
    }
    public Vertex(float px, float py, float pz,
                  float u, float v,
                  float nx, float ny, float nz,
                  float tx, float ty, float tz, float r, float g, float b, float a)
    {
        this.px = px;
        this.py = py;
        this.pz = pz;
        this.u = u;
        this.v = v;
        this.nx = nx;
        this.ny = ny;
        this.nz = nz;
        this.tx = tx;
        this.ty = ty;
        this.tz = tz;
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }
    public static Vertex[][] fromFloatArray(float[] data, int width, int height) {

        if (width * height != data.length / STRIDE) {
            throw new IllegalArgumentException(
                    "Width * Height does not match vertex count.");
        }
        Vertex[][] vertices = new Vertex[height][width];
        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                vertices[y][x] = new Vertex(
                        data[index],
                        data[index + 1],
                        data[index + 2],
                        data[index + 3],
                        data[index + 4],
                        data[index + 5],
                        data[index + 6],
                        data[index + 7],
                        data[index + 8],
                        data[index + 9],
                        data[index + 10],
                        data[index + 11],
                        data[index + 12],
                        data[index + 13],
                        data[index + 14]
                );
                index += STRIDE;
            }
        }
        return vertices;
    }
    public static Vertex[] fromFloatArray(float[] data) {
        Vertex[] vertices = new Vertex[data.length / STRIDE];
        for (int i = 0; i < vertices.length; i++) {
            int index = i * STRIDE;
            vertices[i] = new Vertex(
                    data[index],
                    data[index + 1],
                    data[index + 2],
                    data[index + 3],
                    data[index + 4],
                    data[index + 5],
                    data[index + 6],
                    data[index + 7],
                    data[index + 8],
                    data[index + 9],
                    data[index + 10],
                    data[index + 11],
                    data[index + 12],
                    data[index + 13],
                    data[index + 14]
            );
        }
        return vertices;
    }
    public static float[] toFloatArray(Vertex[] vertices) {
        float[] data = new float[vertices.length * STRIDE];

        for (int i = 0; i < vertices.length; i++) {

            int index = i * STRIDE;

            Vertex v = vertices[i];

            data[index] = v.px;
            data[index + 1] = v.py;
            data[index + 2] = v.pz;

            data[index + 3] = v.u;
            data[index + 4] = v.v;

            data[index + 5] = v.nx;
            data[index + 6] = v.ny;
            data[index + 7] = v.nz;

            data[index + 8] = v.tx;
            data[index + 9] = v.ty;
            data[index + 10] = v.tz;

            data[index + 11] = v.r;
            data[index + 12] = v.g;
            data[index + 13] = v.b;
            data[index + 14] = v.a;
        }

        return data;
    }
}

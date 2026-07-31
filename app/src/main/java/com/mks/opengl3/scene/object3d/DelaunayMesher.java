package com.mks.opengl3.scene.object3d;

import java.util.ArrayList;
import java.util.HashSet;

public class DelaunayMesher {

    private static Vertex[] createSuperTriangle(Vertex[] vertices) {

        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;

        float maxX = -Float.MAX_VALUE;
        float maxY = -Float.MAX_VALUE;

        for (Vertex v : vertices) {

            if (v.px < minX) minX = v.px;
            if (v.py < minY) minY = v.py;

            if (v.px > maxX) maxX = v.px;
            if (v.py > maxY) maxY = v.py;

        }

        float dx = maxX - minX;
        float dy = maxY - minY;

        float delta = Math.max(dx, dy);

        float midX = (minX + maxX) * 0.5f;
        float midY = (minY + maxY) * 0.5f;

        Vertex p1 = new Vertex(
                midX - 20 * delta,
                midY - delta,
                0,
                0,0,
                0,0,1,
                1,0,0,
                1,1,1,1);

        Vertex p2 = new Vertex(
                midX,
                midY + 20 * delta,
                0,
                0,0,
                0,0,1,
                1,0,0,
                1,1,1,1);

        Vertex p3 = new Vertex(
                midX + 20 * delta,
                midY - delta,
                0,
                0,0,
                0,0,1,
                1,0,0,
                1,1,1,1);

        return new Vertex[]{
                p1,
                p2,
                p3
        };

    }
    public static MeshData triangulate(Vertex[] inputVertices) {

        Vertex[] superTriangle = createSuperTriangle(inputVertices);

        Vertex[] vertices =
                new Vertex[inputVertices.length + 3];

        System.arraycopy(
                inputVertices,
                0,
                vertices,
                0,
                inputVertices.length);

        vertices[inputVertices.length]     = superTriangle[0];
        vertices[inputVertices.length + 1] = superTriangle[1];
        vertices[inputVertices.length + 2] = superTriangle[2];

        ArrayList<Triangle> triangles =
                new ArrayList<>();

        Triangle first = new Triangle(
                inputVertices.length,
                inputVertices.length + 1,
                inputVertices.length + 2);

        first.makeCCW(vertices);

        triangles.add(first);

        //--------------------------------------------------
// Insert vertices one by one
//--------------------------------------------------

        for (int vertexIndex = 0;
             vertexIndex < inputVertices.length;
             vertexIndex++) {

            Vertex point = vertices[vertexIndex];

            //--------------------------------------
            // Find Bad Triangles
            //--------------------------------------

            ArrayList<Triangle> badTriangles =
                    new ArrayList<>();

            for (Triangle triangle : triangles) {

                if (triangle.circumCircleContains(
                        vertices,
                        point)) {

                    badTriangles.add(triangle);

                }

            }

            //--------------------------------------
            // Find Polygon Boundary
            //--------------------------------------

            ArrayList<Edge> polygon =
                    new ArrayList<>();

            for (Triangle bad : badTriangles) {

                for (Edge edge : bad.getEdges()) {

                    boolean shared = false;

                    for (Triangle other : badTriangles) {

                        if (other == bad)
                            continue;

                        if (other.getEdges().contains(edge)) {

                            shared = true;
                            break;

                        }

                    }

                    if (!shared) {

                        polygon.add(edge);

                    }

                }

            }

            //--------------------------------------
            // Remove bad triangles
            //--------------------------------------

            triangles.removeAll(badTriangles);

            //--------------------------------------
            // Create new triangles
            //--------------------------------------

            for (Edge edge : polygon) {

                Triangle t = new Triangle(
                        edge.v1,
                        edge.v2,
                        vertexIndex);

                t.makeCCW(vertices);

                triangles.add(t);

            }

        }

        //--------------------------------------------------
// Remove Super Triangle
//--------------------------------------------------

        ArrayList<Triangle> finalTriangles =
                new ArrayList<>();

        for (Triangle triangle : triangles) {

            if (triangle.a >= inputVertices.length)
                continue;

            if (triangle.b >= inputVertices.length)
                continue;

            if (triangle.c >= inputVertices.length)
                continue;

            finalTriangles.add(triangle);

        }
        //--------------------------------------------------
// Triangle Indices
//--------------------------------------------------

        int[] triangleIndices =
                new int[finalTriangles.size() * 3];

        int index = 0;

        for (Triangle triangle : finalTriangles) {

            triangleIndices[index++] = triangle.a;
            triangleIndices[index++] = triangle.b;
            triangleIndices[index++] = triangle.c;

        }
        //--------------------------------------------------
// Line Indices
//--------------------------------------------------

        HashSet<Edge> edgeSet =
                new HashSet<>();

        for (Triangle triangle : finalTriangles) {

            edgeSet.add(new Edge(triangle.a, triangle.b));
            edgeSet.add(new Edge(triangle.b, triangle.c));
            edgeSet.add(new Edge(triangle.c, triangle.a));

        }

        int[] lineIndices =
                new int[edgeSet.size() * 2];

        index = 0;

        for (Edge edge : edgeSet) {

            lineIndices[index++] = edge.v1;
            lineIndices[index++] = edge.v2;

        }
        return new MeshData(
                Vertex.toFloatArray(inputVertices),
                triangleIndices,
                lineIndices
        );
    }
}


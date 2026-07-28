package com.mks.opengl3.scene.object3d;

public class Edge {

    public int v1;
    public int v2;

    public Edge(int a, int b) {

        if (a < b) {

            v1 = a;
            v2 = b;

        } else {

            v1 = b;
            v2 = a;

        }

    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof Edge))
            return false;

        Edge e = (Edge) obj;

        return v1 == e.v1 &&
                v2 == e.v2;

    }

    @Override
    public int hashCode() {

        return 31 * v1 + v2;

    }

}

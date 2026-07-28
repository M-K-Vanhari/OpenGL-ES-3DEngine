package com.mks.opengl3.scene.object3d;


import java.util.ArrayList;
import java.util.List;

    public class Triangle {

        public int a;
        public int b;
        public int c;

        public Triangle(int a, int b, int c) {

            this.a = a;
            this.b = b;
            this.c = c;

        }

        public List<Edge> getEdges() {

            ArrayList<Edge> edges = new ArrayList<>(3);

            edges.add(new Edge(a, b));
            edges.add(new Edge(b, c));
            edges.add(new Edge(c, a));

            return edges;

        }
        public void makeCCW(Vertex[] vertices) {

            Vertex A = vertices[a];
            Vertex B = vertices[b];
            Vertex C = vertices[c];

            float cross =
                    (B.px - A.px) * (C.py - A.py)
                            - (B.py - A.py) * (C.px - A.px);

            if (cross < 0) {

                int t = b;
                b = c;
                c = t;

            }

        }
        public boolean circumCircleContains(Vertex[] vertices, Vertex p) {

            Vertex A = vertices[a];
            Vertex B = vertices[b];
            Vertex C = vertices[c];

            double ax = A.px - p.px;
            double ay = A.py - p.py;

            double bx = B.px - p.px;
            double by = B.py - p.py;

            double cx = C.px - p.px;
            double cy = C.py - p.py;

            double det =
                    (ax * ax + ay * ay) * (bx * cy - by * cx)
                            - (bx * bx + by * by) * (ax * cy - ay * cx)
                            + (cx * cx + cy * cy) * (ax * by - ay * bx);

            float cross =
                    (B.px - A.px) * (C.py - A.py)
                            - (B.py - A.py) * (C.px - A.px);

            if (cross > 0)
                return det > 0;
            else
                return det < 0;
        }
    }

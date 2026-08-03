package com.mks.opengl3.scene.object3d;

public class TerrainColor {

        public float r;
        public float g;
        public float b;
        public float a;

        public TerrainColor() {
            this(0f, 0f, 0f, 1f);
        }

        public TerrainColor(float r, float g, float b) {
            this(r, g, b, 1f);
        }

        public TerrainColor(float r, float g, float b, float a) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }

        public TerrainColor copy() {
            return new TerrainColor(r, g, b, a);
        }

        public float distanceSquared(float r, float g, float b) {

            float dr = Math.abs(this.r - r);
            float dg = Math.abs(this.g - g);
            float db = Math.abs(this.b - b);

            return dr + dg + db;
        }
    }

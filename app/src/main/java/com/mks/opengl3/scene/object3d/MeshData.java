package com.mks.opengl3.scene.object3d;

public  class MeshData {
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

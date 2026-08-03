package com.mks.opengl3.scene.object3d;

import static com.mks.opengl3.utils.Utility.toFloatArray;
import static com.mks.opengl3.utils.Utility.toIntArray;

import com.mks.opengl3.utils.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class TerracedMeshGenerator {
    public  MeshData build(
            Vertex[][] grid,
            float levelHeight)
    {
        int height = grid.length;
        int width = grid[0].length;



        Utility utility =new Utility();
        TerrainColor[] palette= utility.createPalette(1024);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x].pz = (float)Math.floor(grid[y][x].pz / levelHeight) * levelHeight;
                int index =utility.findNearestColor(palette,grid[y][x].r,grid[y][x].g,grid[y][x].b);
                index = (int)Math.floor(index / 200) * 200;
                grid[y][x].r=palette[index].r;
                grid[y][x].g=palette[index].g;
                grid[y][x].b=palette[index].b;
            }
        }
     
        ArrayList<Integer> triangles = new ArrayList<>();
        ArrayList<Integer> lines = new ArrayList<>();

        // ---------- Build Roof ----------
        for (int y = 0; y < height - 1; y++) {
            for (int x = 0; x < width - 1; x++) {
                int v00 = y * width + x;
                int v10 = y * width + x + 1;
                int v01 = (y + 1) * width + x;
                int v11 = (y + 1) * width + x + 1;

                triangles.add(v00);
                triangles.add(v10);
                triangles.add(v01);

                triangles.add(v10);
                triangles.add(v11);
                triangles.add(v01);

                lines.add(v00);
                lines.add(v10);

                lines.add(v10);
                lines.add(v11);

                lines.add(v11);
                lines.add(v01);

                lines.add(v01);
                lines.add(v00);
            }
        }

        return new MeshData(
                Vertex.toFloatArray(grid),
                toIntArray(triangles),
                toIntArray(lines)
        );
    }

}
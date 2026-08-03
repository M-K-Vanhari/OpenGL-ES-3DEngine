package com.mks.opengl3.utils;

import com.mks.opengl3.scene.object3d.TerrainColor;

import java.util.ArrayList;

public class Utility {
    public Utility() {
        // جلوگیری از نمونه‌سازی
    }
    //----------------------------------------------------
// ArrayList<Float> -> float[]
//----------------------------------------------------

    public static float[] toFloatArray(ArrayList<Float> list){

        float[] array = new float[list.size()];

        for(int i = 0; i < list.size(); i++){
            array[i] = list.get(i);
        }

        return array;
    }
    //----------------------------------------------------
// ArrayList<Integer> -> int[]
//----------------------------------------------------

    public static int[] toIntArray(ArrayList<Integer> list){

        int[] array = new int[list.size()];

        for(int i = 0; i < list.size(); i++){
            array[i] = list.get(i);
        }

        return array;
    }
    public TerrainColor[] createPalette(int count)
    {
        TerrainColor[] palette = new TerrainColor[count];

        for(int i = 0; i < count; i++)
        {
            float t = (float)i / (count - 1);

            float r;
            float g;
            float b;

            if(t < 0.25f)
            {
                // Blue -> Cyan
                float k = t / 0.25f;
                r = 0;
                g = k;
                b = 1;
            }
            else if(t < 0.5f)
            {
                // Cyan -> Green
                float k = (t - 0.25f) / 0.25f;
                r = 0;
                g = 1;
                b = 1 - k;
            }
            else if(t < 0.75f)
            {
                // Green -> Yellow
                float k = (t - 0.5f) / 0.25f;
                r = k;
                g = 1;
                b = 0;
            }
            else
            {
                // Yellow -> Red
                float k = (t - 0.75f) / 0.25f;
                r = 1;
                g = 1 - k;
                b = 0;
            }

            palette[i] = new TerrainColor(r,g,b,1);
        }

        return palette;
    }
    public int findNearestColor(TerrainColor[] palette,float r, float g, float b) {

        int bestIndex = 0;
        float bestDistance = Float.MAX_VALUE;

        for (int i = 0; i < palette.length; i++) {

            float distance =
                    palette[i].distanceSquared(r, g, b);

            if (distance < bestDistance) {

                bestDistance = distance;
                bestIndex = i;

            }
        }

        return bestIndex;
    }
}

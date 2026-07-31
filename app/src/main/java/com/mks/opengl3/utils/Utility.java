package com.mks.opengl3.utils;

import java.util.ArrayList;

public class Utility {
    private Utility() {
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

}

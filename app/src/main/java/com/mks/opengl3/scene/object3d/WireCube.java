package com.mks.opengl3.scene.object3d;

import android.opengl.GLES32;

import com.mks.opengl3.renderer.BufferElement;
import com.mks.opengl3.renderer.Mesh;
import com.mks.opengl3.renderer.ShaderDataType;

import java.util.Arrays;

public class WireCube extends Mesh {

    public WireCube() {

        super(
                new float[]{
                        // Position           UV      Normal          Tangent
                        -0.5f, -0.5f, -0.5f, 0,0, 0,0,1, 1,0,0,1.0f,1.0f,1.0f,1.0f, //0
                        0.5f, -0.5f, -0.5f, 0,0, 0,0,1, 1,0,0,1.0f,1.0f,1.0f,1.0f, //1
                        0.5f,  0.5f, -0.5f, 0,0, 0,0,1, 1,0,0,1.0f,1.0f,1.0f,1.0f, //2
                        -0.5f,  0.5f, -0.5f, 0,0, 0,0,1, 1,0,0,1.0f,1.0f,1.0f,1.0f, //3

                        -0.5f, -0.5f,  0.5f, 0,0, 0,0,1, 1,0,0,1.0f,1.0f,1.0f,1.0f, //4
                        0.5f, -0.5f,  0.5f, 0,0, 0,0,1, 1,0,0,1.0f,1.0f,1.0f,1.0f, //5
                        0.5f,  0.5f,  0.5f, 0,0, 0,0,1, 1,0,0,1.0f,1.0f,1.0f,1.0f, //6
                        -0.5f,  0.5f,  0.5f, 0,0, 0,0,1, 1,0,0 ,1.0f,1.0f,1.0f,1.0f //7
                },

                new int[]{
                        // پایین
                        0,1,
                        1,2,
                        2,3,
                        3,0,

                        // بالا
                        4,5,
                        5,6,
                        6,7,
                        7,4,

                        // عمودی
                        0,4,
                        1,5,
                        2,6,
                        3,7
                },

                Arrays.asList(
                        new BufferElement(ShaderDataType.Vec3,"aPosition",false),
                        new BufferElement(ShaderDataType.Vec2,"aTexCoord",false),
                        new BufferElement(ShaderDataType.Vec3,"aNormal",false),
                        new BufferElement(ShaderDataType.Vec3,"aTangent",false),
                        new BufferElement(ShaderDataType.Vec4, "aColor", false)
                )
        );

    }
}

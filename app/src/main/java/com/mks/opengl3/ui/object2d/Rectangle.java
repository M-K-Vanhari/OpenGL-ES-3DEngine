package com.mks.opengl3.ui.object2d;

import com.mks.opengl3.renderer.BufferElement;
import com.mks.opengl3.renderer.Mesh;
import com.mks.opengl3.renderer.ShaderDataType;

import java.util.Arrays;

public class Rectangle extends Mesh {
    public Rectangle(){
        super(new float[]{ -1.0f, -1.0f, 0.0f, 0.0f,
                           -1.0f, 1.0f, 0.0f, 1.0f,
                            1.0f, 1.0f, 1.0f, 1.0f,
                            1.0f, -1.0f, 1.0f, 0.0f }, new int[]{ 0, 1, 2, 0, 2, 3},
                Arrays.asList(new BufferElement[] { new BufferElement(ShaderDataType.Vec2, "aPosition", false),
                                                 new BufferElement(ShaderDataType.Vec2, "aTexCoord", false)}));
    }
}

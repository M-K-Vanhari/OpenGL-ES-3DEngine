package com.mks.opengl3.ui.object2d;

import android.content.Context;

import com.mks.opengl3.math.Vec4;
import com.mks.opengl3.renderer.Uniform;

public class BasicObject2DShader extends Object2DShader{
    Uniform color;
    public BasicObject2DShader(Context context){
        super(context, "assets/ui/BasicShader.vs", "assets/ui/BasicShader.fs");
        color = new Uniform("uColor", shaderProgramId);
    }

    public void setColor(Vec4 color){
        this.color.setObject(color);
        this.color.sendToShader();
    }
}

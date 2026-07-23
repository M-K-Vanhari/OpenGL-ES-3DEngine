package com.mks.opengl3.events;

public class WindowResizeEvent extends Event{
    float x;
    float y;
    public WindowResizeEvent(float x, float y){
        super(Type.WINDOW_RESIZE);
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}

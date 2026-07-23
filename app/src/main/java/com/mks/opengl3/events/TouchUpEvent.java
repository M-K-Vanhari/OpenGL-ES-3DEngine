package com.mks.opengl3.events;

public class TouchUpEvent extends TouchEvent {
    public TouchUpEvent(float x, float y){
        super(x, y, Type.TOUCH_UP);
    }
}

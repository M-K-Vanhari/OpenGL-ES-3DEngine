package com.mks.opengl3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES32;
import android.view.ScaleGestureDetector;
import android.widget.TextView;

import com.mks.opengl3.events.Event;
import com.mks.opengl3.events.WindowResizeEvent;
import com.mks.opengl3.layers.Layer;
import com.mks.opengl3.scene.Scene;
import com.mks.opengl3.ui.UI;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AppRenderer extends ScaleGestureDetector.SimpleOnScaleGestureListener implements GLSurfaceView.Renderer{

    int width;
    int height;

    Context context;
    List<Layer> layerStack = new ArrayList<Layer>();
    Timer timer;
    Scene scene;

    private TextView txtDebug;
    private long lastTime = System.currentTimeMillis();
    private int frameCount = 0;
    private int fps = 0;

    public AppRenderer(Context context){
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig){
        scene = new Scene(context);
        layerStack.add(scene);
        // Creating UI test
      //  UI ui = new UI(context);
      //  ui.addToLayerStack(layerStack);
        GLES32.glEnable(GLES32.GL_DEPTH_TEST);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask(){ public void run(){onUpdate();}}, 0, 16);
    }
    public void setDebugTextView(TextView txtDebug) {
        this.txtDebug = txtDebug;
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height){
        this.width = width;
        this.height = height;
        onEvent(new WindowResizeEvent(width, height));
        // Set Viewport
        GLES32.glViewport(0,0, width, height);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDrawFrame(GL10 glUnused){
        GLES32.glViewport(0,0,width, height);
        // Rendering background
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT | GLES32.GL_DEPTH_BUFFER_BIT);
        GLES32.glClearColor(0.02f, 0.02f, 0.02f, 1.0f);
        // Rendering frame layer by layer
        for(int i = 0; i < layerStack.size(); i++) {
            layerStack.get(i).onRender();
            GLES32.glClear( GLES32.GL_DEPTH_BUFFER_BIT);
        }
        frameCount++;

        long currentTime = System.currentTimeMillis();

        if (currentTime - lastTime >= 1000) {
            fps = frameCount;
            frameCount = 0;
            lastTime = currentTime;
        }
        if (txtDebug != null) {

            ((Activity) context).runOnUiThread(() -> {

                txtDebug.setText(
                        "FPS       : " +fps + "\n" +
                                "Vertices  : " + scene.getVertexCount() + "\n" +
                                "Triangles : " + scene.getTriangleCount() + "\n" +
                                "Lines     : " + scene.getLineCount()+ "\n" +
                            "OLines     : " + scene.getOLineCount()
                );

            });

        }

    }

    public void onEvent(Event event){
        for(int i = layerStack.size() - 1; i >= 0; i--) {
            layerStack.get(i).onEvent(event);
        }
    }

    public void onUpdate(){
        for(int i = layerStack.size() - 1; i >= 0; i--) {
            layerStack.get(i).onUpdate();
        }
    }

    public void addLayer(Layer layer){
        layerStack.add(layer);
    }
}

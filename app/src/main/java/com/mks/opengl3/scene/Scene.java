package com.mks.opengl3.scene;

import android.content.Context;

import com.mks.opengl3.events.Event;
import com.mks.opengl3.events.EventDispatcher;
import com.mks.opengl3.events.ScaleEvent;
import com.mks.opengl3.events.TouchDownEvent;
import com.mks.opengl3.events.TouchMoveEvent;
import com.mks.opengl3.events.TouchUpEvent;
import com.mks.opengl3.events.WindowResizeEvent;
import com.mks.opengl3.layers.Layer;
import com.mks.opengl3.math.Vec3;
import com.mks.opengl3.math.Vec4;
import com.mks.opengl3.renderer.Mesh;
import com.mks.opengl3.renderer.Texture;
import com.mks.opengl3.scene.object3d.Cube;
import com.mks.opengl3.scene.object3d.Object3D;
import com.mks.opengl3.scene.object3d.Plane;
import com.mks.opengl3.scene.object3d.StandardMaterial3D;
import com.mks.opengl3.scene.object3d.StandardObject3DShader;
import com.mks.opengl3.scene.object3d.TerrainMesh;
import com.mks.opengl3.scene.object3d.WireCube;

import java.util.Timer;
import java.util.TimerTask;

public class Scene extends Layer {
    float aspectRatio;
    Camera camera;
    PointLight[] pointLights;
    Vec3 ambientLight;
    Object3D object;
    Object3D object1;
    Object3D obj;
    Object3D plt;

    boolean blockRotating;
    Timer timer;
    Object3D root = new Object3D(null);
    SceneNode sceneNode = new SceneNode(root);

    public Scene(Context context){
        // Creating camera and lights
        camera = new Camera(new Vec3( 0.0f, 0.0f, -5.0f), 60.0f, 1.0f);
        pointLights = new PointLight[1];
        pointLights[0] = new PointLight(new Vec3(1.0f, 0.0f, 1.0f), new Vec3(1.0f, 1.0f, 1.0f), 1.25f);
        ambientLight = new Vec3(0.25f, 0.25f, 0.25f);

        // Creating a cube
        // Creating mesh
        Mesh mesh = new WireCube();
        StandardMaterial3D material = new StandardMaterial3D(new StandardObject3DShader(context));
        material.setTextured(false);

        material.setColor(new Vec4(1.0f, 0.0f, 0.0f,1.0f)); // قرمز
        // Creating Object
        object = new Object3D(mesh,material);

        Mesh mesh1 = new Cube();
        StandardMaterial3D material1 = new StandardMaterial3D(new StandardObject3DShader(context));
      //  material1.setTextured(false);

       // material1.setColor(new Vec4(1.0f, 0.0f, 0.0f,1.0f)); // قرمز
        // Creating Object
        object1 = new Object3D(mesh1,material1);


        TerrainMesh me = new TerrainMesh(32,32);
        StandardMaterial3D ml = new StandardMaterial3D(new StandardObject3DShader(context));
        ml.setTextured(false);

        ml.setColor(new Vec4(1.0f, 1.0f, 0.0f,1.0f)); // قرمز

        obj = new Object3D(me, ml);

        obj.setRenderMode(Object3D.LINES);
        Plane pl = new Plane(32,32);
        StandardMaterial3D pm = new StandardMaterial3D(new StandardObject3DShader(context));
        pm.setTextured(false);

        pm.setColor(new Vec4(1.0f, 0.0f, 1.0f,1.0f)); // قرمز

        plt = new Object3D(pl, pm);
        root.addChild(plt);
        root.addChild(obj);
        root.addChild(object);
        root.addChild(object1);

        blockRotating = false;
        timer = new Timer();
    }

    public float getAspectRatio(){
        return aspectRatio;
    }

    public void setAspectRatio(float aspectRatio){
        this.aspectRatio = aspectRatio;
    }

    public void onEvent(Event event){
        EventDispatcher dispatcher = new EventDispatcher(event);
        dispatcher.dispatch(Event.Type.TOUCH_DOWN, (Event e) -> (onTouchDownEvent((TouchDownEvent) e)));
        dispatcher.dispatch(Event.Type.TOUCH_MOVE, (Event e) -> (onTouchMoveEvent((TouchMoveEvent) e)));
        dispatcher.dispatch(Event.Type.TOUCH_UP, (Event e) -> (onTouchUpEvent((TouchUpEvent) e)));
        dispatcher.dispatch(Event.Type.SCALE_GESTURE, (Event e) -> (onScaleEvent((ScaleEvent) e)));
        dispatcher.dispatch(Event.Type.WINDOW_RESIZE, (Event e) -> (onWindowResizeEvent((WindowResizeEvent) e)));
    }

    public void onRender(){
        sceneNode.update();
      //  object.onRender(this);
      //  object1.onRender(this);
       // obj.onRender(this);


        obj.onRender(this);
    }

    public void onUpdate(){
        root.onUpdate();
      //  object1.onUpdate();
        camera.onUpdate();
    }

    // Returns true if blocking
    public boolean onTouchDownEvent(TouchDownEvent e){
        return true;
    }
    public boolean onTouchUpEvent(TouchUpEvent e){
        root.setRotating(false);
        root.startDeceleration();
       // object1.setRotating(false);
       // object1.startDeceleration();
        camera.startDeceleration();
        // Blocking rotation for 0.05 seconds
        timer.schedule(new TimerTask(){ public void run(){ blockRotating = false; }}, 50);
        return true;
    }
    public boolean onTouchMoveEvent(TouchMoveEvent e){
        if(!blockRotating) {
            root.setRotating(true);
//            object1.setRotating(true);
            rotateObject(e.getDX(), e.getDY());
        }
        return true;
    }
    public boolean onScaleEvent(ScaleEvent e){
        zoom(e.getScaleFactor());
        blockRotating = true;
        return true;
    }

    public boolean onWindowResizeEvent(WindowResizeEvent e){
        aspectRatio = e.getX() / e.getY();
        camera.setAspectRatio(aspectRatio);
        return false;
    }

    public void rotateObject(float dX, float dY){

        float rotationSpeed = 2.5f * camera.getFov();
        float degToRad = (float) (Math.PI / 180.0);
        float rotX = dX * rotationSpeed * degToRad;
        float rotY = -dY * rotationSpeed * degToRad;
        root.rotate(new Vec4(0.0f, 1.0f, 0.0f, rotX));
        root.rotate(new Vec4(1.0f, 0.0f, 0.0f, rotY));
        root.setRotationVelocity(new Vec3(rotX, rotY, 0.0f));
//        object1.rotate(new Vec4(0.0f, 1.0f, 0.0f, rotX));
//        object1.rotate(new Vec4(1.0f, 0.0f, 0.0f, rotY));
//        object1.setRotationVelocity(new Vec3(rotX, rotY, 0.0f));
    }

    public void zoom(float scaleFactor){
        float zoomSpeed = 1.0f;
        float fov = camera.getFov();
        if(scaleFactor != 0.0f)
            fov /= scaleFactor * zoomSpeed;
        camera.setFovVelocity(fov - camera.getFov());
        camera.setFov(fov);
    }

    public Camera getCamera(){
        return camera;
    }

    public void setCamera(Camera camera){
        this.camera = camera;
    }

    public PointLight[] getPointLights(){
        return pointLights;
    }

    public Vec3[] getPointLightPositions(){
        Vec3[] lightPositions = new Vec3[1];
        for(int i = 0; i < pointLights.length; i++){
            lightPositions[i] = pointLights[i].getPosition();
        }
        return lightPositions;
    }

    public Vec3[] getPointLightColors(){
        Vec3[] lightColors = new Vec3[1];
        for(int i = 0; i < pointLights.length; i++){
            if(pointLights[i] != null) {
                lightColors[i] = pointLights[i].getColor();
            }
        }
        return lightColors;
    }

    public void setPointLight(PointLight pointLight, int index){
        pointLights[index] = pointLight;
    }

    public Vec3 getAmbientLight(){
        return ambientLight;
    }

    public Object3D getObject(){
        return object;
    }

    public void setObject(Object3D object){
        this.object = object;
    }


}

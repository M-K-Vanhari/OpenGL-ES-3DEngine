package com.mks.opengl3.scene.object3d;

import android.opengl.GLES32;

import com.mks.opengl3.inertia.ExponentialFunction;
import com.mks.opengl3.inertia.Inertia;
import com.mks.opengl3.math.Mat4;
import com.mks.opengl3.math.Vec3;
import com.mks.opengl3.math.Vec4;
import com.mks.opengl3.renderer.Mesh;
import com.mks.opengl3.scene.Scene;

import java.util.ArrayList;


public class Object3D {
    Mesh mesh;
    public static StandardMaterial3D material;

    Vec3 position;
    Vec3 scale;
    Vec4 rotation;
    private Object3D parent;
    private ArrayList<Object3D> children = new ArrayList<>();

    private Mat4 localModel = new Mat4();
    private Mat4 worldModel = new Mat4();

    Vec3 rotationVelocity;
    boolean rotating;
    Inertia inertia;
    public static final int TRIANGLES = 1;
    public static final int LINES = 2;
    public static final int OLINES = 4;
    public static final int POINTS = 16;
    public static final int LINEPOINTS = 32;


    public static int renderMode = TRIANGLES;


    public void setRenderMode(int mode){
        renderMode = mode;
        if (renderMode == TRIANGLES)
            showTriangles();
        else if (renderMode == LINES)
            showLine();
        else if (renderMode == OLINES)
            showOLine();
        else if (renderMode == POINTS)
            showPoint();
        else if (renderMode == LINEPOINTS)
            showLinePoint();


    }
    // If no material is given to the constructor, the standard material is assigned to the object.
    public Object3D(Mesh mesh, StandardObject3DShader shader){
        this(mesh, new StandardMaterial3D(shader));
    }

    public Object3D(Mesh mesh, StandardMaterial3D material){
        this.mesh = mesh;
        Object3D.material = material;

        position = new Vec3();
        scale = new Vec3(1.0f);
        rotation = new Vec4();
        localModel = new Mat4();
        worldModel = new Mat4();
        rotationVelocity = new Vec3();

        inertia = new Inertia();

        // Setting up model matrix. Only changes when object moves
        Object3D.material.getShader().setModel(worldModel);
        updateWorldMatrix();
    }

    public Object3D(Mesh mesh){
        this.mesh = mesh;

        position = new Vec3();
        scale = new Vec3(1.0f);
        rotation = new Vec4();
        localModel = new Mat4();
        worldModel = new Mat4();
        rotationVelocity = new Vec3();

        inertia = new Inertia();
        updateWorldMatrix();
    }

    // Draws object to the screen
    public void onRender(Scene scene){
        material.getShader().bind();

// ارسال Uniform ها
        material.getShader().setTextured(material.isTextured());
        material.getShader().setNormalIntensity(material.getNormalIntensity());
        material.getShader().setRoughnessIntensity(material.getRoughnessIntensity());

        material.getShader().setModel(worldModel);
        material.getShader().setView(scene.getCamera().getView());
        material.getShader().setProjection(scene.getCamera().getProjection());

        material.getShader().setPointLightPositions(scene.getPointLightPositions());
        material.getShader().setPointLightColors(scene.getPointLightColors());
        material.getShader().setAmbientLight(scene.getAmbientLight());

// ---------- Triangles ----------
        if ((Object3D.renderMode & Object3D.TRIANGLES) != 0) {

            GLES32.glEnable(GLES32.GL_POLYGON_OFFSET_FILL);
            GLES32.glPolygonOffset(1.0f, 1.0f);

            material.getShader().setTextured(material.isTextured());
            material.getShader().setUseVertexColor(true);
            material.getShader().setColor(material.getColor());

            mesh.drawTriangles();

            GLES32.glDisable(GLES32.GL_POLYGON_OFFSET_FILL);
        }

// ---------- Lines ----------
        if ((Object3D.renderMode & Object3D.LINES) != 0) {

            material.getShader().setTextured(false);
            material.getShader().setUseVertexColor(false);
            if ((Object3D.renderMode & Object3D.TRIANGLES) != 0)
                material.getShader().setColor(new Vec4(0, 0, 0, 1));
            else
            {
                material.getShader().setUseVertexColor(true);
                material.getShader().setColor(material.getColor());
            }

            mesh.drawLines();
        }

// ---------- Points ----------
        if ((Object3D.renderMode & Object3D.POINTS) != 0) {

            material.getShader().setTextured(false);
            material.getShader().setUseVertexColor(false);
            material.getShader().setColor(new Vec4(1, 0, 0, 1));

            mesh.drawPoints();
        }

// بازگرداندن وضعیت Material
        material.getShader().setTextured(material.isTextured());
        mesh.onRender();

        material.getShader().unbind();
    }

    public void onUpdate(){

        rotate(new Vec4(0.0f, 1.0f, 0.0f, rotationVelocity.x));
        rotate(new Vec4(1.0f, 0.0f, 0.0f, rotationVelocity.y));

        rotationVelocity.multiply(inertia.getValue());

    }

    public void startDeceleration(){
        long duration = (long) (Math.pow(10 * rotationVelocity.length(), 1.4) * 800);
        inertia.setDuration(duration);
        inertia.setFunction(new ExponentialFunction(2, duration));
        inertia.start();
    }
    public void setModel(Mat4 model){
        this.localModel = model;
    }
    public void rotate(Vec4 rotation){

        localModel = Mat4.multiply(
                Mat4.rotation(rotation.getVec3(), rotation.w),
                localModel);

        updateWorldMatrix();

    }

    public void translate(Vec3 translation){
        position.add(translation);
        localModel = Mat4.multiply(
                Mat4.translation(translation),
                localModel
        );
        updateWorldMatrix();
    }
/*
    public void scale(Vec3 scale){
        scale.multiply(scale);
        model.multiply(Mat4.scale(scale));
        shader.setModel(model);
    }
*/

    public void updateWorldMatrix()
    {
        if(parent == null)
            worldModel = localModel;
        else
            worldModel = Mat4.multiply(parent.worldModel, localModel);

        for(Object3D child : children)
            child.updateWorldMatrix();
    }
    public Mesh getMesh(){
        return mesh;
    }
    public void showLine() {
        mesh.useLines();
    }
    public void showOLine() {
        mesh.useOLines();
    }
    public void showPoint() {
        mesh.usePoints();
    }public void showLinePoint() {
        mesh.useLinePoints();
    }
    public void showTriangles() {
        mesh.useTriangles();
    }

    public void setMesh(Mesh mesh){
        this.mesh = mesh;
    }

    public StandardMaterial3D getMaterial(){
        return material;
    }


    public Vec3 getPosition(){
        return position;
    }
    /*
        public void setPosition(Vec3 position){
            this.position = position;
            model.x4 = position.x;
            model.y4 = position.y;
            model.z4 = position.z;
            shader.setModel(model);
        }

        public Vec3 getScale() {
            return scale;
        }

        public void setScale(Vec3 scale) {
            this.scale = scale;
            model.x1 = scale.x;
            model.y2 = scale.y;
            model.z3 = scale.z;
            shader.setModel(model);
        }
    */
    public Vec4 getRotation(){
        return rotation;
    }

    public void setRotation(Vec4 rotation){
        this.rotation = rotation;
    }

    public Mat4 getLocalModel()
    {
        return localModel;
    }

    public Mat4 getWorldModel()
    {
        return worldModel;
    }
    public Vec3 getRotationVelocity(){
        return rotationVelocity;
    }

    public void setRotationVelocity(Vec3 rotationVelocity){
        this.rotationVelocity = rotationVelocity;
    }

    public boolean isRotating() {
        return rotating;
    }

    public void setRotating(boolean rotating) {
        this.rotating = rotating;
    }


    public void removeChild(Object3D child)
    {
        children.remove(child);
        child.parent = null;
    }

    public Object3D getParent()
    {
        return parent;
    }
    public ArrayList<Object3D> getChildren()
    {
        return children;
    }
    public void addChild(Object3D child)
    {
        child.parent = this;
        children.add(child);
        child.updateWorldMatrix();
    }
}

package com.mks.opengl3.scene;

import com.mks.opengl3.math.Mat4;
import com.mks.opengl3.math.Vec4;
import com.mks.opengl3.scene.object3d.Object3D;

import java.util.ArrayList;

public class SceneNode {
    private Object3D root;

    public SceneNode(Object3D root) {
        this.root = root;
    }

    public Object3D getRoot() {
        return root;
    }

    public void update() {
        root.updateWorldMatrix();
    }
}
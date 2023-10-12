package com.github.raspopov.states;

import com.github.raspopov.nodes.MinesNode;
import com.jme3.app.Application;
import com.jme3.app.ChaseCameraAppState;
import com.jme3.app.state.BaseAppState;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MinesState extends BaseAppState {
    @Getter
    private final MinesNode minesNode;
    private final ChaseCameraAppState chaseCameraAppState;

    @Override
    protected void initialize(Application app) {
        Geometry targetSpatial = new Geometry("", new Box(Vector3f.ZERO, Vector3f.ZERO));
        targetSpatial.setLocalTranslation(Vector3f.ZERO);
        chaseCameraAppState.setTarget(targetSpatial);
        chaseCameraAppState.setMinDistance(10);
        chaseCameraAppState.setDefaultVerticalRotation(FastMath.PI / 6);
        app.getStateManager().attach(chaseCameraAppState);


        Box b = new Box(5, 0.1f, 5); // create cube shape
        Geometry geom = new Geometry("Box", b);  // create cube geometry from the shape
        Material mat = new Material(app.getAssetManager(),
                "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        mat.setColor("Color", ColorRGBA.Red);   // set color of material to blue
        geom.setMaterial(mat);                   // set the cube's material

//        geom.setLocalTranslation(0,2,0);
        geom.setLocalTranslation(0, 0, 0);

        minesNode.attachChild(geom);              // make the cube appear in the scene
    }

    @Override
    protected void cleanup(Application app) {

    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}

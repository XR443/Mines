package com.github.raspopov.application;

import com.github.raspopov.states.MinesState;
import com.jme3.app.DebugKeysAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.app.state.ConstantVerifierState;
import com.jme3.audio.AudioListenerState;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import org.springframework.stereotype.Component;

@Component
public class MinesApplication extends SimpleApplication {

    private final MinesState minesState;

    public MinesApplication(MinesState minesState) {
        super(new StatsAppState(), new FlyCamAppState(), new AudioListenerState(), new DebugKeysAppState(),
                new ConstantVerifierState());

        this.minesState = minesState;
    }

    @Override
    public void simpleInitApp() {
        stateManager.getState(FlyCamAppState.class).setEnabled(false);

        Box b = new Box(1, 1, 1); // create cube shape
        Geometry geom = new Geometry("Box", b);  // create cube geometry from the shape
        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        mat.setColor("Color", ColorRGBA.Blue);   // set color of material to blue
        geom.setMaterial(mat);                   // set the cube's material

//        geom.setLocalTranslation(0,2,0);
        geom.setLocalTranslation(0, 0, 0);

        rootNode.attachChild(geom);              // make the cube appear in the scene


        stateManager.attach(minesState);
        rootNode.attachChild(minesState.getMinesNode());
    }
}

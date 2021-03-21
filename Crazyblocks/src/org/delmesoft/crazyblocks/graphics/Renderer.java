package org.delmesoft.crazyblocks.graphics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;

import org.delmesoft.crazyblocks.graphics.g3d.decal.DecalRenderer;

/**
 * Created by sergi on 22/09/17.
 */

public interface Renderer {

    ModelBatch getModelBatch();
    DecalRenderer getDecalRenderer();
    RenderContext getRenderContext();
    Camera getCamera();

    float getAmbientLight();

}

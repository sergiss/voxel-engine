package org.delmesoft.crazyblocks.graphics.g2d.layer;

import com.badlogic.gdx.graphics.g2d.Batch;

import org.delmesoft.crazyblocks.input.InputLayer;

/**
 * Created by sergi on 13/09/17.
 */

public interface Layer extends InputLayer {

    void render(Batch batch);


}

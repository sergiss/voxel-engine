package org.delmesoft.crazyblocks.world.blocks.g3d.environment;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;

import org.delmesoft.crazyblocks.graphics.Renderer;
import org.delmesoft.crazyblocks.graphics.g2d.TextureCoordinates;
import org.delmesoft.crazyblocks.graphics.g3d.decal.Decal;

/**
 * Created by sergi on 23/09/17.
 */

public class Weather extends Decal {

    public Weather(Texture texture) {
        super(texture, new TextureCoordinates(0F, 0F, 1F, 1F));
    }

    public void render(Renderer renderer) {
        float hs = this.scale * 0.5F;

        float hw = width  * hs, hh = height * hs;

        float x = hw + this.x, y = hh + this.y, z = this.z + hw;

        Camera camera = renderer.getCamera();
        Vector3 cPos = camera.position;

        renderer.getDecalRenderer().setRotation(tmpVec3.set(cPos.x - x, 0, cPos.z - z).nor(), camera.up);

        renderer.getDecalRenderer().render(texture,
                x, y, z,
                hw, hh,
                getLight(x, y, z, renderer.getAmbientLight()),
                textureCoordinates.u, textureCoordinates.v, textureCoordinates.u2, textureCoordinates.v2);
    }

}

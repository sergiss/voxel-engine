package org.delmesoft.crazyblocks.entity.particle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

import org.delmesoft.crazyblocks.graphics.Renderer;
import org.delmesoft.crazyblocks.graphics.g2d.TextureCoordinates;
import org.delmesoft.crazyblocks.graphics.g3d.decal.Decal;
import org.delmesoft.crazyblocks.math.MathHelper;
import org.delmesoft.crazyblocks.screen.GameScreen;
import org.delmesoft.crazyblocks.world.World;
import org.delmesoft.crazyblocks.world.blocks.Chunk;
import org.delmesoft.crazyblocks.world.blocks.utils.ChunkData;

import static org.delmesoft.crazyblocks.math.MathHelper.fastFloor;

/**
 * Created by sergi on 22/09/17.
 */

public class ParticleTrash extends Decal {

    public static final float REMOVE_TIME = 0.5F;

    private float removeTime;

    public ParticleTrash(float x, float y, float z, float width, float height, Texture texture, TextureCoordinates textureCoordinates) {
        super(x, y, z, width, height, texture, textureCoordinates);

        ya = MathUtils.random(0.0F , 0.2F );
        xa = MathUtils.random(-0.1F, 0.1F);
        za = MathUtils.random(-0.1F, 0.1F);

        removeTime = REMOVE_TIME * MathUtils.random(0.75F, 1F);

        restitution = 0.25F;

    }

    @Override
    public void tick(float delta) {

        if(onGround && (removeTime -= delta) < 0F) {

            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    world.removeEntity(ParticleTrash.this);
                }
            });

        } else {

            tryMove(xa, ya, za);

            xa *= 0.90f;
            za *= 0.90f;
            ya *= 0.95f;

            ya -= World.GRAVITY * 0.6F;

        }

    }

}

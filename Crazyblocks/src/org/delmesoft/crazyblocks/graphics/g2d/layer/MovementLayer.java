package org.delmesoft.crazyblocks.graphics.g2d.layer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

import org.delmesoft.crazyblocks.entity.Player;
import org.delmesoft.crazyblocks.math.MathHelper;

/**
 * Created by sergi on 16/09/17.
 */

public class MovementLayer implements Layer {

    private static final float VIRTUAL_DST = 1027.771F;

    private final TextureRegion region;

    private final Player player;

    private final Rectangle bounds;

    private final float d;

    private int lx, ly;

    private int touchId = -1;

    public MovementLayer(Player player, TextureRegion region) {

        this.player = player;
        this.region = region;

        bounds = new Rectangle();

        bounds.setSize(Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 4);
        bounds.setPosition(0F, Gdx.graphics.getHeight() - bounds.height);
        float dst = MathHelper.len(0F, 0F, bounds.getWidth(), bounds.getHeight());
        d = 150F * (dst / VIRTUAL_DST);
    }

    @Override
    public boolean touchDown(int x, int y, int id) {
        if(touchId == -1 && bounds.contains(x, y)) {
            lx = x;
            ly = y;
            touchId = id;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int x, int y, int id) {
        if(touchId == id) {
            player.dv = player.dh = 0;
            touchId = -1;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int x, int y, int id) {

        if(touchId == id) {

            int dx = x  - lx;
            int dy = ly - y;

            int len2 = dx * dx + dy * dy;

            if (len2 != 0) {

                float dst = (float) Math.sqrt(len2);

                float nx = dx / dst;
                float ny = dy / dst;

                player.dh = MathUtils.clamp((dst * nx) / d, -1, 1);
                player.dv = MathUtils.clamp((dst * ny) / d, -1, 1);

                if(dst > d) {
                    dst -= d;
                    lx += (int) (dst * nx);
                    ly -= (int) (dst * ny);
                }

            }

            return true;
        }

        return false;
    }

    @Override
    public boolean mouseMoved(int x, int y) {
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public void render(Batch batch) {
        batch.draw(region, 0, 0, bounds.width, bounds.height);
    }

}

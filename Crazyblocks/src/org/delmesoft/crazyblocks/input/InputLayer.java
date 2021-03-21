package org.delmesoft.crazyblocks.input;

/**
 * Created by sergi on 13/09/17.
 */

public interface InputLayer {

    boolean touchDown(int x, int y, int id);

    boolean touchUp(int x, int y, int id);

    boolean touchDragged(int x, int y, int id);

	boolean mouseMoved(int x, int y);

	boolean keyDown(int keycode);

	boolean keyUp(int keycode);

}

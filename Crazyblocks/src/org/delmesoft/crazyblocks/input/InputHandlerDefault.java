package org.delmesoft.crazyblocks.input;

/**
 * Created by sergi on 13/09/17.
 */

public class InputHandlerDefault extends InputHandler {

	public InputHandlerDefault() {
	}

	@Override
	public boolean keyDown(int keycode) {
		Integer usercode = keyMap.get(keycode);
		if(usercode != null) {
			for (InputLayer layer : layers) {
				if (layer.keyDown(usercode)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		Integer usercode = keyMap.get(keycode);
		if(usercode != null) {
			for (InputLayer layer : layers) {
				if (layer.keyUp(usercode)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		pointer += button;
		Integer usercode = pointerMap.get(pointer);
		if (usercode != null) {
			for (InputLayer layer : layers) {
				if (layer.touchDown(screenX, screenY, usercode)) {
					layerMap.put(usercode, layer);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		pointer += button;
		Integer usercode = pointerMap.get(pointer);
		if(usercode != null) {
			InputLayer layer = layerMap.remove(usercode);
			if (layer != null) {
				layer.touchUp(screenX, screenY, usercode);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		for (InputLayer layer : layers) {
			if (layer.touchDragged(screenX, screenY, pointer)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		for (InputLayer layer : layers) {
			if (layer.mouseMoved(screenX, screenY)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

}

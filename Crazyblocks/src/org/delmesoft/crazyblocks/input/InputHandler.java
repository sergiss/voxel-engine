package org.delmesoft.crazyblocks.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.InputProcessor;

import org.delmesoft.crazyblocks.graphics.g2d.layer.Layer;

/**
 * Created by sergi on 13/09/17.
 */

public abstract class InputHandler implements InputProcessor {
	
	public static final long DOUBLE_CLICK_TIME = 500;
	
	protected Map<Integer, Integer> keyMap;
	protected Map<Integer, Integer> pointerMap;

    protected Map<Integer, InputLayer> layerMap;

    protected List<Layer> layers;

    public InputHandler() {
    	keyMap    = new HashMap<Integer, Integer>();
    	pointerMap = new HashMap<Integer, Integer>();
        layerMap  = new HashMap<Integer, InputLayer>();        
        layers    = new ArrayList<Layer>();
    }

    public void addInputLayer(Layer layer) {
        if(layers.contains(layer) == false)
            layers.add(layer);
    }

    public boolean removeInputLayer(Layer layer) {
        return layers.remove(layer);
    }
    
    public void mapKey(int code, int usercode) {
    	if(keyMap.containsValue(usercode) == false)
    		keyMap.put(code, usercode);
    }
    
    public boolean unmapKey(int code) {
    	return keyMap.remove(code) != null;
    }
    
    public boolean containsKeycode(int code) {
    	return keyMap.containsKey(code);
    }
    
    public void mapPointer(int code, int usercode) {
    	if(pointerMap.containsValue(usercode) == false)
    		pointerMap.put(code, usercode);
    }
    
    public boolean unmapPointer(int code) {
    	return pointerMap.remove(code) != null;
    }
    
    public boolean containsPointercode(int code) {
    	return pointerMap.containsKey(code);
    }

    public List<Layer> getLayers() {
        return layers;
    }
}

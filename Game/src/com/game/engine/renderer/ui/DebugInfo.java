package com.game.engine.renderer.ui;

import org.lwjgl.glfw.GLFW;
import com.spinyowl.legui.component.Layer;
import com.game.engine.tools.input.*;

/**
 * @author laptop
 * @date Dec. 14, 2021
 * 
 */
public class DebugInfo implements IKeyState {

	private Layer layer;
	
	public DebugInfo() {
		layer = new Layer();
	}
	
	@Override
	public void onKeyPressed(int keys) {
		if (keys == GLFW.GLFW_KEY_F3) {
			
		}
	}

	@Override
	public void onKeyReleased(int keys) {
		
	}
	
}

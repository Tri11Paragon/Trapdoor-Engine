package com.game.engine.renderer.ui;

import org.lwjgl.glfw.GLFW;

import com.game.engine.display.DisplayManager;
import com.game.engine.tools.input.IKeyState;
import com.spinyowl.legui.component.Layer;
import com.spinyowl.legui.style.Style.DisplayType;

/**
 * @author brett
 * @date Jan. 25, 2022
 * 
 */
public class Console implements IKeyState {
	
	private Layer layer;
	private boolean enabled;
	private DebugInfo info;
	
	public Console(DebugInfo info) {
		this.info = info;
		this.layer = new Layer();
		layer.setSize(DisplayManager.WIDTH, DisplayManager.HEIGHT);
		
		
		
		UIMaster.getMasterFrame().addLayer(layer);
	}

	@Override
	public void onKeyPressed(int keys) {
	}

	@Override
	public void onKeyReleased(int keys) {
		if (keys == GLFW.GLFW_KEY_GRAVE_ACCENT) {
			if (info.isEnabled())
				info.setEnabled(false);
			enabled = !enabled;
			layer.setEnabled(enabled);
			layer.getStyle().setDisplay(enabled == true ? DisplayType.MANUAL : DisplayType.NONE);
		}
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	
	
}

package com.trapdoor.engine.renderer.ui;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;

import com.spinyowl.legui.component.Layer;
import com.spinyowl.legui.style.Style.DisplayType;
import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.registry.annotations.AnnotatedClass;
import com.trapdoor.engine.registry.annotations.AnnotationHandler;
import com.trapdoor.engine.registry.annotations.ClearScreenEventSubscriber;
import com.trapdoor.engine.tools.input.IKeyState;

/**
 * @author brett
 * @date Jan. 25, 2022
 * this doesn't really need to be an object
 */
public class Console implements IKeyState, AnnotatedClass {
	
	private static ArrayList<Console> createdConsoles = new ArrayList<Console>();
	
	private Layer layer;
	private boolean enabled;
	
	public Console() {
		this.layer = new Layer();
		layer.setSize(DisplayManager.WIDTH, DisplayManager.HEIGHT);
		createdConsoles.add(this);
		
		
		UIMaster.getMasterFrame().addLayer(layer);
	}
	
	@ClearScreenEventSubscriber
	public static void clearScreen() {
		for (Console c : createdConsoles)
			c.enabled = false;
	}

	@Override
	public void onKeyPressed(int keys) {
	}

	@Override
	public void onKeyReleased(int keys) {
		if (keys == GLFW.GLFW_KEY_GRAVE_ACCENT) {
			AnnotationHandler.cleanScreen();
			
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

package com.trapdoor.engine.renderer.ui;

import org.lwjgl.glfw.GLFW;

import com.spinyowl.legui.component.Layer;
import com.spinyowl.legui.component.ScrollablePanel;
import com.spinyowl.legui.style.Style.DisplayType;
import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.registry.annotations.AnnotatedClass;
import com.trapdoor.engine.registry.annotations.AnnotationHandler;
import com.trapdoor.engine.registry.annotations.ClearScreenEventSubscriber;
import com.trapdoor.engine.tools.input.IKeyState;

/**
 * @author brett
 * @date Feb. 13, 2022
 * 
 */
public class CommandBox implements IKeyState, AnnotatedClass {

	private static CommandBox box;
	
	private Layer layer;
	private boolean enabled;
	
	private String commandOutputBuffer;
	private ScrollablePanel panel;
	
	protected CommandBox() {
		this.layer = new Layer();
		layer.setSize(DisplayManager.WIDTH, DisplayManager.HEIGHT);
		
		panel = new ScrollablePanel(30, 60, DisplayManager.WIDTH - 30, DisplayManager.HEIGHT - 120);
		
		UIMaster.getMasterFrame().addLayer(layer);
	}
	
	@Override
	public void onKeyPressed(int keys) {
		
	}

	@Override
	public void onKeyReleased(int keys) {
		if (keys == GLFW.GLFW_KEY_ENTER) {
			AnnotationHandler.cleanScreen();
			
			enabled = !enabled;
			layer.setEnabled(enabled);
			layer.getStyle().setDisplay(enabled == true ? DisplayType.MANUAL : DisplayType.NONE);
		}
	}
	
	@ClearScreenEventSubscriber
	public static void clearScreen() {
		box.enabled = false;
	}
	
	public static void init() {
		box = new CommandBox();
	}
	
	public static CommandBox getInstance() {
		return box;
	}
	
}

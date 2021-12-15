package com.game.engine.renderer.ui;

import org.lwjgl.glfw.GLFW;

import com.game.engine.TextureLoader;
import com.game.engine.VAOLoader;
import com.game.engine.display.DisplayManager;
import com.game.engine.tools.input.IKeyState;
import com.game.engine.world.World;
import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.component.Layer;
import com.spinyowl.legui.style.Style.DisplayType;

/**
 * @author laptop
 * @date Dec. 14, 2021
 * 
 */
public class DebugInfo implements IKeyState {

	private Layer layer;
	
	private Label gameVersion;
	
	private Label mainRenderings;
	private Label mainThreadFPS;
	private Label entityCount;
	private Label renderCount;
	
	private Label physics;
	private Label physicsThreadFPS;
	
	private boolean enabled = false;
	private StringBuilder builder;
	
	public DebugInfo() {
		builder = new StringBuilder();
		
		layer = new Layer();
		layer.setSize(1200, 512);
		
		gameVersion = new Label(DisplayManager.title);
		
		mainRenderings = new Label("Renderer Info:");
		mainThreadFPS = new Label("FPS " + DisplayManager.getFPS() + " Frametime (ms): " + DisplayManager.getFrameTimeMilis());
		entityCount = new Label("E: 0");
		renderCount = new Label("VAO: " + VAOLoader.getVAOS() + " VBO: " + VAOLoader.getVBOS() + " Textures: " + TextureLoader.getTextures());
		
		physics = new Label("Physics Info:");
		physicsThreadFPS = new Label("UPS " + World.getFPS() + " Updatetime (ms): " + World.getFrameTimeMilis());
		
		final float size = 16;
		final float padding = 5;
		final float offsetY = 25;
		
		gameVersion.setPosition(0, calcPosY(size * 1.3f, padding, 10));
		gameVersion.getStyle().setFontSize(size * 1.3f);
		gameVersion.getStyle().setFont("orbitron-light");
		
		mainRenderings.setPosition(0, calcPosY(size * 1.2f, padding , offsetY));
		mainRenderings.getStyle().setFontSize(size * 1.2f);
		mainRenderings.getStyle().setFont("orbitron-light");
		mainThreadFPS.setPosition(0, calcPosY(size, padding, offsetY));
		mainThreadFPS.getStyle().setFontSize(size);
		mainThreadFPS.getStyle().setFont("orbitron-light");
		entityCount.setPosition(0, calcPosY(size, padding, offsetY));
		entityCount.getStyle().setFontSize(size);
		entityCount.getStyle().setFont("orbitron-light");
		renderCount.setPosition(0, calcPosY(size, padding, offsetY));
		renderCount.getStyle().setFontSize(size);
		renderCount.getStyle().setFont("orbitron-light");
		
		physics.setPosition(0, calcPosY(size * 1.2f, padding, offsetY));
		physics.getStyle().setFontSize(size * 1.2f);
		physics.getStyle().setFont("orbitron-light");
		physicsThreadFPS.setPosition(0, calcPosY(size * 1.2f, padding, offsetY));
		physicsThreadFPS.getStyle().setFontSize(size);
		physicsThreadFPS.getStyle().setFont("orbitron-light");
		
		layer.add(gameVersion);
		layer.add(mainRenderings);
		layer.add(mainThreadFPS);
		layer.add(entityCount);
		layer.add(renderCount);
		layer.add(physics);
		layer.add(physicsThreadFPS);
		
		UIMaster.getMasterFrame().addLayer(layer);
		layer.setEnabled(enabled);
		layer.getStyle().setDisplay(enabled == true ? DisplayType.MANUAL : DisplayType.NONE);
	}
	
	private int last = 0;
	private float calcPosY(float size, float padding, float offsetY) {
		if (last == 0) {
			last++;
			return offsetY;
		}
		// I AM BIG BRAIN
		return offsetY + size * last + padding * last++;
	}
	
	private double round(double d) {
		int i = (int) (d * 1000);
		return i / 1000d;
	}
	
	/**
	 * called every second
	 */
	public void updateInfo() {
		if (!enabled)
			return;
		builder = new StringBuilder();
		builder.append("FPS  ");
		builder.append((int)DisplayManager.getFPS());
		builder.append("  Frametime (ms):  ");
		builder.append(round(DisplayManager.getFrameTimeMilis()));
		mainThreadFPS.getTextState().setText(builder.toString());
		
		builder = new StringBuilder();
		builder.append("Entities:  ");
		builder.append(World.entityCount);
		entityCount.getTextState().setText(builder.toString());
		
		builder = new StringBuilder();
		builder.append("VAO:  ");
		builder.append(VAOLoader.getVAOS());
		builder.append("  VBO:  ");
		builder.append(VAOLoader.getVBOS());
		builder.append("  Textures:  ");
		builder.append(TextureLoader.getTextures());
		renderCount.getTextState().setText(builder.toString());
		
		builder = new StringBuilder();
		builder.append("UPS  ");
		builder.append((int)World.getFPS());
		builder.append("  Updatetime (ms):  ");
		builder.append(round(World.getFrameTimeMilis()));
		physicsThreadFPS.getTextState().setText(builder.toString());
	}
	
	/**
	 * called every frame
	 */
	public void updateFrame() {
		if (!enabled)
			return;
		
	}
	
	/**
	 * called every 100ms
	 */
	public void update() {
		if (!enabled)
			return;
		
	}
	
	@Override
	public void onKeyPressed(int keys) {
		if (keys == GLFW.GLFW_KEY_F3) {
			enabled = !enabled;
			layer.setEnabled(enabled);
			layer.getStyle().setDisplay(enabled == true ? DisplayType.MANUAL : DisplayType.NONE);
		}
	}

	@Override
	public void onKeyReleased(int keys) {
		
	}
	
}

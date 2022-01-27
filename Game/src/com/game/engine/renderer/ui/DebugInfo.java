package com.game.engine.renderer.ui;

import java.text.DecimalFormat;

import org.lwjgl.glfw.GLFW;

import com.game.engine.TextureLoader;
import com.game.engine.VAOLoader;
import com.game.engine.display.DisplayManager;
import com.game.engine.registry.Threading;
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
	
	public static float x,y,z;

	private Layer layer;
	
	private Label gameVersion;
	
	private Label mainRenderings;
	private Label mainThreadFPS;
	private Label entityCount;
	private Label renderCount;
	
	private Label physics;
	private Label physicsThreadFPS;
	
	private Label playerPosition;
	
	private boolean enabled = false;
	private StringBuilder builder;
	private Console console;
	
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
		physicsThreadFPS = new Label("UPS " + Threading.getFPS() + " Updatetime (ms): " + Threading.getFrameTimeMilis());
		
		playerPosition = new Label("X: 0 | Y: 0 | Z: 0");
		
		final float size = 16;
		final float padding = 5;
		final float offsetY = 25;
		
		gameVersion.setPosition(0, calcPosY(size * 1.3f, padding, 10));
		gameVersion.getStyle().setFontSize(size * 1.3f);
		gameVersion.getStyle().setFont("mono");
		
		calcPosY(size, padding, offsetY);
		mainRenderings.setPosition(0, calcPosY(size, padding , offsetY));
		mainRenderings.getStyle().setFontSize(size * 1.2f);
		mainRenderings.getStyle().setFont("mono");
		mainThreadFPS.setPosition(0, calcPosY(size, padding, offsetY));
		mainThreadFPS.getStyle().setFontSize(size);
		mainThreadFPS.getStyle().setFont("mono");
		entityCount.setPosition(0, calcPosY(size, padding, offsetY));
		entityCount.getStyle().setFontSize(size);
		entityCount.getStyle().setFont("mono");
		renderCount.setPosition(0, calcPosY(size, padding, offsetY));
		renderCount.getStyle().setFontSize(size);
		renderCount.getStyle().setFont("mono");
		
		calcPosY(size, padding, offsetY);
		physics.setPosition(0, calcPosY(size, padding, offsetY));
		physics.getStyle().setFontSize(size * 1.2f);
		physics.getStyle().setFont("mono");
		physicsThreadFPS.setPosition(0, calcPosY(size, padding, offsetY));
		physicsThreadFPS.getStyle().setFontSize(size);
		physicsThreadFPS.getStyle().setFont("mono");
		
		calcPosY(size, padding, offsetY);
		playerPosition.setPosition(0, calcPosY(size, padding, offsetY));
		playerPosition.getStyle().setFontSize(size);
		playerPosition.getStyle().setFont("mono");
		
		layer.add(gameVersion);
		layer.add(mainRenderings);
		layer.add(mainThreadFPS);
		layer.add(entityCount);
		layer.add(renderCount);
		layer.add(physics);
		layer.add(physicsThreadFPS);
		layer.add(playerPosition);
		
		UIMaster.getMasterFrame().addLayer(layer);
		
		//UIMaster.getMasterFrame().getContainer().add(layer);
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
	
	private final DecimalFormat df = new DecimalFormat("#.###");
	private String round(double d) {
		return df.format(d);
	}
	
	private void includeSpace(StringBuilder sb, String x) {
		sb.append(x);
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
		builder.append((int)Threading.getFPS());
		builder.append("  Updatetime (ms):  ");
		builder.append(round(Threading.getFrameTimeMilis()));
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
	 * called every 250ms
	 */
	public void update() {
		if (!enabled)
			return;
		
		builder = new StringBuilder();
		builder.append("X:  ");
		includeSpace(builder, round(x));
		builder.append(" | Y: ");
		includeSpace(builder, round(y));
		builder.append(" | Z: ");
		includeSpace(builder, round(z));
		playerPosition.getTextState().setText(builder.toString());
	}
	
	@Override
	public void onKeyPressed(int keys) {
		if (keys == GLFW.GLFW_KEY_F3) {
			if (console.isEnabled())
				console.setEnabled(false);
			enabled = !enabled;
			layer.setEnabled(enabled);
			layer.getStyle().setDisplay(enabled == true ? DisplayType.MANUAL : DisplayType.NONE);
		}
	}

	@Override
	public void onKeyReleased(int keys) {
		
	}

	public void setConsole(Console console) {
		this.console = console;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
}

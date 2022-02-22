package com.trapdoor.engine.display;

import org.joml.Vector2f;
import org.joml.Vector4f;

import com.game.displays.MainMenuDisplay;
import com.spinyowl.legui.component.Button;
import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.component.Layer;
import com.spinyowl.legui.component.Slider;
import com.spinyowl.legui.event.MouseClickEvent;
import com.spinyowl.legui.event.WindowSizeEvent;
import com.spinyowl.legui.listener.MouseClickEventListener;
import com.spinyowl.legui.style.Style.DisplayType;
import com.trapdoor.engine.ProjectionMatrix;
import com.trapdoor.engine.datatypes.ui.XButton;
import com.trapdoor.engine.renderer.ui.UIMaster;
import com.trapdoor.engine.tools.SettingsLoader;

/**
 * @author brett
 * @date Feb. 13, 2022
 * 
 */
public class OptionsDisplay extends IDisplay {
	
	private MainMenuDisplay mainMenu;
	private Layer layer;
	
	public OptionsDisplay(MainMenuDisplay mainMenu) {
		this.mainMenu = mainMenu;
	}
	
	@SuppressWarnings("unused")
	@Override
	public void onCreate() {
		layer = new Layer();
		layer.setSize(DisplayManager.WIDTH, DisplayManager.HEIGHT);
		layer.getStyle().setBackground(MainMenuDisplay.rixieBackground);
		
		layer.getListenerMap().addListener(WindowSizeEvent.class, (e) -> {
			MainMenuDisplay.rixie.setSize(new Vector2f(e.getWidth(), e.getHeight()));
		});
		
		
		float left = 69;
		float leftLabel = 200 + 69 + 10;
		
		float left2 = 200 + 69 + 20 + 20;
		float leftLabel2 = 400 + 69 + 20 + 20 + 20;
		
		Label fovLabel = new Label("FOV: " + (int) ProjectionMatrix.FOV);
		fovLabel.setPosition(leftLabel, 69);
		fovLabel.getStyle().setFont("mono");
		fovLabel.getStyle().setFontSize(20f);
		fovLabel.getStyle().setTextColor(new Vector4f(0.0f, 0.0f, 0.0f, 1.0f));
		Slider fov = new Slider();
		fov.setMinValue(50);
		fov.setMaxValue(130);
		fov.setValue(ProjectionMatrix.FOV);
		fov.addSliderChangeValueEventListener((e) -> {
			fovLabel.getTextState().setText("FOV: " + (int)e.getNewValue());
		});
		fov.setPosition(left, 69);
		fov.setSize(200, 20);
		layer.add(fov);
		layer.add(fovLabel);
		
		Label fpsLabel = new Label("FPS: " + (int) DisplayManager.FPS_MAX);
		fpsLabel.setPosition(leftLabel, 69 + 30);
		fpsLabel.getStyle().setFont("mono");
		fpsLabel.getStyle().setFontSize(20f);
		fpsLabel.getStyle().setTextColor(new Vector4f(0.0f, 0.0f, 0.0f, 1.0f));
		Slider fps = new Slider();
		fps.setMinValue(0);
		fps.setMaxValue(120);
		fps.setValue(DisplayManager.FPS_MAX);
		fps.addSliderChangeValueEventListener((e) -> {
			if (e.getNewValue() < 0.1f)
				fpsLabel.getTextState().setText("FPS: Unlimited");
			else 
				fpsLabel.getTextState().setText("FPS: " + (int)e.getNewValue());
		});
		fps.setPosition(left, 69 + 30);
		fps.setSize(200, 20);
		layer.add(fps);
		layer.add(fpsLabel);
		
		Label graphicsLabel = new Label("Graphics: " + decodeLevel(SettingsLoader.GRAPHICS_LEVEL));
		graphicsLabel.setPosition(leftLabel, 69 + 60);
		graphicsLabel.getStyle().setFont("mono");
		graphicsLabel.getStyle().setFontSize(20f);
		graphicsLabel.getStyle().setTextColor(new Vector4f(0.0f, 0.0f, 0.0f, 1.0f));
		Slider graphics = new Slider();
		graphics.setMinValue(0);
		graphics.setMaxValue(3);
		graphics.setValue(SettingsLoader.GRAPHICS_LEVEL);
		graphics.addSliderChangeValueEventListener((e) -> {
			graphicsLabel.getTextState().setText("Graphics: " + decodeLevel((int)e.getNewValue()));
		});
		graphics.setPosition(left, 69 + 60);
		graphics.setSize(200, 20);
		layer.add(graphics);
		layer.add(graphicsLabel);
		
		
		Button back = new XButton("Back", true, false, 1.02f, 0, DisplayManager.HEIGHT - 48 - 20, 456, 48);
		//sp.getStyle().setBorder(new SimpleLineBorder(ColorConstants.red(), 5));
		back.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {
			if (event.getAction() != MouseClickEvent.MouseClickAction.RELEASE)
				return;
			DisplayManager.changeDisplay(mainMenu);
		});
		back.getListenerMap().addListener(WindowSizeEvent.class, (e) -> {
			back.setPosition(back.getPosition().x, e.getHeight() - 48 - 20);
		});
		layer.add(back);
		
		Button apply = new XButton("Apply", true, false, 1.02f, 0, DisplayManager.HEIGHT - 48*2 - 10 - 20, 456, 48);
		apply.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {
			if (event.getAction() != MouseClickEvent.MouseClickAction.RELEASE)
				return;
			
			ProjectionMatrix.FOV = Float.parseFloat(fovLabel.getTextState().getText().substring(4));
			if (fpsLabel.getTextState().getText().contentEquals("FPS: Unlimited"))
				DisplayManager.FPS_MAX = 0;
			else 
				DisplayManager.FPS_MAX = (int) Float.parseFloat(fpsLabel.getTextState().getText().substring(4));
			SettingsLoader.GRAPHICS_LEVEL = decodeLevel(graphicsLabel.getTextState().getText());
			
		});
		apply.getListenerMap().addListener(WindowSizeEvent.class, (e) -> {
			apply.setPosition(apply.getPosition().x, e.getHeight() - 48*2 - 10 - 20);
		});
		layer.add(apply);
		
		layer.setEnabled(false);
		layer.getStyle().setDisplay(layer.isEnabled() == true ? DisplayType.MANUAL : DisplayType.NONE);
		UIMaster.getMasterFrame().addLayer(layer);
	}
	
	private String decodeLevel(int l) {
		if (l == 0)
			return "MAX";
		else if (l == 1)
			return "HIGH";
		else if (l == 2)
			return "MEDIUM";
		else if (l == 3)
			return "LOW";
		else
			return "LOW";
	}
	
	private int decodeLevel(String l) {
		if (l.contains("MAX"))
			return 0;
		else if (l.contains("HIGH"))
			return 1;
		else if (l.contains("MEDIUM"))
			return 2;
		else if (l.contains("LOW"))
			return 3;
		else
			return 3;
	}

	@Override
	public void onSwitch() {
		layer.setEnabled(true);
		layer.getStyle().setDisplay(layer.isEnabled() == true ? DisplayType.MANUAL : DisplayType.NONE);
	}

	@Override
	public void render() {
	}

	@Override
	public void update() {
	}

	@Override
	public void onLeave() {
		layer.setEnabled(false);
		layer.getStyle().setDisplay(layer.isEnabled() == true ? DisplayType.MANUAL : DisplayType.NONE);
	}

	@Override
	public void onDestory() {
	}

}

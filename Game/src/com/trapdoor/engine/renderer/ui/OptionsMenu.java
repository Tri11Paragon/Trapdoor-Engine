package com.trapdoor.engine.renderer.ui;

import com.trapdoor.engine.registry.GameRegistry;
import com.trapdoor.engine.tools.Logging;
import com.trapdoor.engine.tools.SettingsLoader;

import imgui.ImGui;
import imgui.type.ImBoolean;

/**
 * @author laptop
 * @date Mar. 20, 2022
 * 
 */
public class OptionsMenu {

	public static OptionsMenu menu;
	
	private boolean enabled = false;
	
	private ImBoolean enableAutoExposure = new ImBoolean(SettingsLoader.enableAutoExposure);
	private float[] exposure = new float[1];
	
	public OptionsMenu() {
		exposure[0] = SettingsLoader.exposureDefault;
	}
	
	public void render() {
		try {
			if (enabled)
				return;
			ImGui.pushFont(GameRegistry.getFont("roboto-regular"));
			ImGui.begin("Options");
			
			ImGui.text("Graphics");
			ImGui.checkbox("Auto Exposure ", enableAutoExposure);
			ImGui.sliderFloat("Exposure ", exposure, 0, 2.5f);
			
			ImGui.end();
			ImGui.popFont();
			
			SettingsLoader.enableAutoExposure = enableAutoExposure.get();
			SettingsLoader.exposureDefault = exposure[0];
		} catch (Exception e) {
			Logging.logger.fatal(e.getMessage(), e);
		}
	}
	
	public void enable() {
		this.enabled = true;
	}
	
	public void disable() {
		this.enabled = false;
	}
	
	public void toggle() {
		this.enabled = !this.enabled;
	}
	
	public void set(boolean enabled) {
		this.enabled = enabled;
	}
	
	public static void create() {
		menu = new OptionsMenu();
	}
	
}

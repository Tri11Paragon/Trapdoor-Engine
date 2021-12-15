package com.game.engine.display;

import com.game.engine.renderer.ui.UIMaster;
import com.game.engine.threading.GameRegistry;
import com.game.engine.threading.Threading;
import com.spinyowl.legui.component.Layer;
import com.spinyowl.legui.component.ProgressBar;
import com.spinyowl.legui.component.TextInput;
import com.spinyowl.legui.component.optional.align.HorizontalAlign;
import com.spinyowl.legui.component.optional.align.VerticalAlign;
import com.spinyowl.legui.style.Style.DisplayType;

/**
 * @author brett
 * @date Nov. 28, 2021
 * 
 */
public class LoadingScreenDisplay extends IDisplay {

	public static final int TIME = 50;
	
	
	public TestDisplay test;
	
	private long time;
	
	public Layer layer;
	public ProgressBar bar;
	
	@Override
	public void onCreate() {
		GameRegistry.registerMaterialTexture("resources/textures/512.png");
		GameRegistry.registerMaterialTexture("resources/textures/yes.png");
		//GameRegistry.registerMaterialTextureFolder("resources/textures/materials/");
		
		layer = new Layer();
		layer.setSize(DisplayManager.WIDTH, DisplayManager.HEIGHT);
		
		bar = new ProgressBar();
		bar.setSize(512, 60);
		bar.getStyle().setHorizontalAlign(HorizontalAlign.RIGHT);
		bar.getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
		
		layer.add(bar);
		
		TextInput textInput = new TextInput(250, 130, 100, 30);
	    textInput.getStyle().setHorizontalAlign(HorizontalAlign.RIGHT);
	    layer.add(textInput);
		
		UIMaster.getMasterFrame().addLayer(layer);
		
		test = new TestDisplay();
		time = System.currentTimeMillis();
	}

	@Override
	public void onSwitch() {
		layer.setEnabled(true);
		layer.getStyle().setDisplay(layer.isEnabled() == true ? DisplayType.MANUAL : DisplayType.NONE);
	}
	
	@Override
	public void render() {
		// TODO: splash screen / logo
		
		// make sure we have loaded all assets and the splash screen has existed for some time.
		if ((Threading.isEmpty() && System.currentTimeMillis() - time > TIME)) {
			//DisplayManager.createDisplay(test);
			//DisplayManager.changeDisplay(test);
		}
		if (System.currentTimeMillis() - time > (TIME+50) * 5)
			Threading.d();
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

package com.game.engine.display;

import com.game.engine.renderer.ui.UIMaster;
import com.game.engine.threading.GameRegistry;
import com.game.engine.threading.Threading;
import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.component.Layer;
import com.spinyowl.legui.component.ProgressBar;
import com.spinyowl.legui.component.event.label.LabelContentChangeEvent;
import com.spinyowl.legui.component.event.label.LabelContentChangeEventListener;
import com.spinyowl.legui.component.event.label.LabelWidthChangeEvent;
import com.spinyowl.legui.component.event.label.LabelWidthChangeEventListener;
import com.spinyowl.legui.component.optional.align.HorizontalAlign;
import com.spinyowl.legui.component.optional.align.VerticalAlign;
import com.spinyowl.legui.event.WindowSizeEvent;
import com.spinyowl.legui.listener.WindowSizeEventListener;
import com.spinyowl.legui.style.Style.DisplayType;

/**
 * @author brett
 * @date Nov. 28, 2021
 * 
 */
public class LoadingScreenDisplay extends IDisplay {

	public static final int TIME = 50;
	public static volatile float MAX = 1;
	public static volatile float PROGRESS = 1;
	
	public static synchronized void progress() {
		PROGRESS++;
	}
	
	public static synchronized void max() {
		MAX++;
	}
	
	public TestDisplay test;
	
	private long time;
	
	public Layer layer;
	public static ProgressBar bar;
	public static Label info;
	
	public static void updateBar() {
		bar.setValue((PROGRESS/MAX) * 100);
	}
	
	@Override
	public void onCreate() {		
		createUI();
		
		test = new TestDisplay();
		time = System.currentTimeMillis();
		
		GameRegistry.registerTexture("resources/textures/512.png");
		GameRegistry.registerTexture("resources/textures/yes.png");
		GameRegistry.registerTexture(GameRegistry.DEFAULT_EMPTY_NORMAL_MAP);
		GameRegistry.registerModel("resources/models/depression.obj");
		GameRegistry.registerModel("resources/models/lll3.obj");
		GameRegistry.registerModel("resources/models/power model.obj");
		GameRegistry.registerModel("resources/models/hellolosers.obj");

		
	}

	@Override
	public void onSwitch() {
		layer.setEnabled(true);
		layer.getStyle().setDisplay(layer.isEnabled() == true ? DisplayType.MANUAL : DisplayType.NONE);
	}
	
	@Override
	public void render() {
		// TODO: splash screen / logo
		//bar.setValue((PROGRESS/MAX) * 100);
		updateBar();
		
		// make sure we have loaded all assets and the splash screen has existed for some time.
		if ((Threading.isEmpty() && System.currentTimeMillis() - time > TIME) && bar.getValue() >= 98) {
			DisplayManager.createDisplay(test);
			DisplayManager.changeDisplay(test);
		}
		if (System.currentTimeMillis() - time > (TIME+50) * 5)
			Threading.d();
	}

	@Override
	public void onLeave() {
		layer.setEnabled(false);
		layer.getStyle().setDisplay(layer.isEnabled() == true ? DisplayType.MANUAL : DisplayType.NONE);
		GameRegistry.onLoadingComplete();
	}

	@Override
	public void onDestory() {
		
	}
	
	public void createUI() {
		layer = new Layer();
		layer.setSize(DisplayManager.WIDTH, DisplayManager.HEIGHT);
		
		bar = new ProgressBar(DisplayManager.WIDTH/2 - 256, DisplayManager.HEIGHT/2, 512, 60);
		bar.getStyle().setHorizontalAlign(HorizontalAlign.RIGHT);
		bar.getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
		bar.setFocusable(false);
		
		layer.add(bar);
		
		info = new Label();
		info.getStyle().setDisplay(DisplayType.NONE);
		info.setPosition(DisplayManager.WIDTH/2 - info.getTextState().getTextWidth()/2, DisplayManager.HEIGHT/2 + 96);
		info.getListenerMap().addListener(LabelWidthChangeEvent.class, (LabelWidthChangeEventListener) event -> {
			info.setPosition(DisplayManager.WIDTH/2 - event.getWidth()/2, DisplayManager.HEIGHT/2 + 96);
		});
		info.getListenerMap().addListener(LabelContentChangeEvent.class, (LabelContentChangeEventListener) event -> {
			info.getStyle().setDisplay(DisplayType.MANUAL);
		});
		
		layer.add(info);
		
	    layer.getListenerMap().addListener(WindowSizeEvent.class,
	            (WindowSizeEventListener) event -> {
	            	int w2 = event.getWidth()/2;
	            	int h2 = event.getHeight()/2;
	            	bar.setPosition(w2 - bar.getSize().x/2, h2);
	            	info.setPosition(w2 - info.getTextState().getTextWidth()/2, h2 + 96);
	            });
	    
		UIMaster.getMasterFrame().addLayer(layer);
	}
	
}

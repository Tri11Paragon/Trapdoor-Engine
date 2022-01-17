package com.game.engine.display;

import com.game.engine.renderer.ui.UIMaster;
import com.game.engine.threading.GameRegistry;
import com.game.engine.threading.Threading;
import com.game.engine.tools.Logger;
import com.game.game.displays.MainMenuDisplay;
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
	
	public IDisplay singlePlayer;
	
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
		
		singlePlayer = new MainMenuDisplay();
		time = System.currentTimeMillis();
		
		GameRegistry.registerTexture("resources/textures/character Texture.png");
		
		GameRegistry.registerModel("resources/models/depression.dae");
		GameRegistry.registerModel("resources/models/model.dae");
		GameRegistry.registerModel("resources/models/test object.dae");
		GameRegistry.registerModel("resources/models/supercube.dae");
		GameRegistry.registerModel("resources/models/floor.dae");

		GameRegistry.registerModel("resources/models/poop.dae");
		GameRegistry.registerModel("resources/models/chess/w_king.dae");

		GameRegistry.registerModel("resources/models/chess/w_king_bt.dae");
		GameRegistry.registerModel("resources/models/chess/w_king_bt2.dae");
		GameRegistry.registerModel("resources/models/chess/w_king_bt3.dae");

		GameRegistry.registerModel("resources/models/chess/w_queen.dae");
		GameRegistry.registerModel("resources/models/chess/w_bishop.dae");
		GameRegistry.registerModel("resources/models/chess/w_knight.dae");
		GameRegistry.registerModel("resources/models/chess/w_rook.dae");

		
		
		GameRegistry.registerFont("bettergrade", "resources/fonts/bettergrade/BetterGrade-519DV.ttf");
		
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
		
		if ((Threading.isEmpty() && System.currentTimeMillis() - time > TIME)) {
		// make sure we have loaded all assets and the splash screen has existed for some time.
			if (bar.getValue() >= 98) {
				Logger.writeln("Loading finished!");
				DisplayManager.createDisplay(singlePlayer);
				DisplayManager.changeDisplay(singlePlayer);
			} else {
				LoadingScreenDisplay.progress();
			}
		}
		if (System.currentTimeMillis() - time > (TIME+50) * 5)
			Threading.d();
	}
	
	@Override
	public void update() {
		
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
	    
	    layer.setEnabled(false);
		layer.getStyle().setDisplay(layer.isEnabled() == true ? DisplayType.MANUAL : DisplayType.NONE);
		UIMaster.getMasterFrame().addLayer(layer);
	}
	
}

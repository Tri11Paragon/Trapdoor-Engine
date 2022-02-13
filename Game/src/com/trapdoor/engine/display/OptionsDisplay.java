package com.trapdoor.engine.display;

import com.game.displays.MainMenuDisplay;
import com.spinyowl.legui.component.Button;
import com.spinyowl.legui.component.Layer;
import com.spinyowl.legui.event.MouseClickEvent;
import com.spinyowl.legui.listener.MouseClickEventListener;
import com.spinyowl.legui.style.Style.DisplayType;
import com.trapdoor.engine.datatypes.ui.XButton;
import com.trapdoor.engine.renderer.ui.UIMaster;

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
	
	@Override
	public void onCreate() {
		layer = new Layer();
		layer.setSize(DisplayManager.WIDTH, DisplayManager.HEIGHT);
		
		Button sp = new XButton("Back", true, false, 1.02f, 0, 210, 456, 48);
		//sp.getStyle().setBorder(new SimpleLineBorder(ColorConstants.red(), 5));
		sp.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {
			if (event.getAction() != MouseClickEvent.MouseClickAction.RELEASE)
				return;
			DisplayManager.changeDisplay(mainMenu);
		});
		layer.add(sp);
		
		layer.setEnabled(false);
		layer.getStyle().setDisplay(layer.isEnabled() == true ? DisplayType.MANUAL : DisplayType.NONE);
		UIMaster.getMasterFrame().addLayer(layer);
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

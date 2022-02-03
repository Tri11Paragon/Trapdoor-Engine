package com.game.displays;

import org.joml.Vector4f;

import com.spinyowl.legui.component.Button;
import com.spinyowl.legui.component.Component;
import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.component.Layer;
import com.spinyowl.legui.component.Slider;
import com.spinyowl.legui.component.Widget;
import com.spinyowl.legui.component.optional.align.HorizontalAlign;
import com.spinyowl.legui.component.optional.align.VerticalAlign;
import com.spinyowl.legui.event.MouseClickEvent;
import com.spinyowl.legui.listener.MouseClickEventListener;
import com.spinyowl.legui.style.Background;
import com.spinyowl.legui.style.Style.DisplayType;
import com.spinyowl.legui.style.shadow.Shadow;
import com.trapdoor.Main;
import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.display.IDisplay;
import com.trapdoor.engine.display.ModelEditorDisplay;
import com.trapdoor.engine.display.TestDisplay;
import com.trapdoor.engine.renderer.ui.UIMaster;

public class MainMenuDisplay extends IDisplay {

	private Layer layer;

	@Override
	public void onCreate() {

		layer = new Layer();
		layer.setSize(DisplayManager.WIDTH, DisplayManager.HEIGHT);

		Background bg = new Background();
		bg.setColor(new Vector4f(125 / 255f, 125 / 255f, 125 / 255f, 1));
		layer.getStyle().setBackground(bg);
		
		Label title = new Label();
		title.getTextState().setText(DisplayManager.gameName);
		title.getStyle().setFont("bettergrade");
		title.getStyle().setFontSize(144f);
		title.getStyle().setTextColor(37 / 255f, 37 / 255f, 37 / 255f, 1);
		title.getStyle().setVerticalAlign(VerticalAlign.TOP);
		title.getStyle().setHorizontalAlign(HorizontalAlign.LEFT);
		title.setPosition(25, 25);
		layer.add(title);

		//IDisplay display = new SinglePlayerDisplay();
		IDisplay display = new TheAmazingWorldOfHentaiDisplay();
		DisplayManager.createDisplay(display);

		Button sp = new Button();
		sp.getTextState().setText("Start Game");
		//sp.getStyle().setBorder(new SimpleLineBorder(ColorConstants.red(), 5));
		sp.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {
			if (event.getAction() != MouseClickEvent.MouseClickAction.RELEASE)
				return;
			DisplayManager.changeDisplay(display);
		});
		sp.setSize(456, 48);
		setButtonPosition(sp, 69, 210, 48, 10, 0);
		layer.add(sp);
		
		Button options = new Button();
		options.getTextState().setText("Options");
		options.setSize(456, 48);
		setButtonPosition(options, 69, 210, 48, 10, 1);
		layer.add(options);
		
		if (Main.devMode) {
			Label lb = new Label("Dev Zone");
			lb.getStyle().setFontSize(52f);
			setButtonPosition(lb, 69, 210, 48, 15, 2);
			layer.add(lb);
			
			IDisplay display2 = new TestDisplay();
			DisplayManager.createDisplay(display2);
			
			Button tester = new Button();
			tester.getStyle().setShadow(new Shadow());
			tester.getTextState().setText("Engine Test");
			tester.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {
				if (event.getAction() != MouseClickEvent.MouseClickAction.RELEASE)
					return;
				DisplayManager.changeDisplay(display2);
			});
			tester.setSize(456, 48);
			setButtonPosition(tester, 69, 210, 48, 10, 1);
			layer.add(tester);
			
			IDisplay display3 = new ModelEditorDisplay();
			DisplayManager.createDisplay(display3);
			
			Button materials = new Button();
			materials.getStyle().setShadow(new Shadow());
			materials.getTextState().setText("Model viewer / Material Editor");
			materials.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {
				if (event.getAction() != MouseClickEvent.MouseClickAction.RELEASE)
					return;
				DisplayManager.changeDisplay(display3);
			});
			materials.setSize(456, 48);
			setButtonPosition(materials, 69, 210, 48, 10, 1);
			layer.add(materials);
		}
		
		Widget shadowWidget = new Widget(20, 310, 200, 120);
		Slider blurSlider = new Slider(110, 5 + 20 * 2, 80, 10);
		shadowWidget.getContainer().add(blurSlider);
		blurSlider.addSliderChangeValueEventListener(
				(e) -> shadowWidget.getStyle().getShadow().setBlur(blurSlider.getValue()));
		//layer.add(shadowWidget);

		// make sure the layer is disabled until this screen is switched it
		layer.setEnabled(false);
		layer.getStyle().setDisplay(layer.isEnabled() == true ? DisplayType.MANUAL : DisplayType.NONE);
		UIMaster.getMasterFrame().addLayer(layer);
	}
	
	
	private int i = 0;
	private void setButtonPosition(Component button, float x, float startY, float height, float padding, int order) {
		i += order;
		button.setPosition(x, startY + height * i + padding * i);
	}

	@Override
	public void onSwitch() {
		
		layer.setEnabled(true);
		layer.getStyle().setDisplay(layer.isEnabled() == true ? DisplayType.MANUAL : DisplayType.NONE);

	}

	@Override
	public void render() {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

}

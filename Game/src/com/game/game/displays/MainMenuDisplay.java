package com.game.game.displays;

import org.joml.Vector4f;

import com.game.engine.display.DisplayManager;
import com.game.engine.display.IDisplay;
import com.game.engine.renderer.ui.UIMaster;
import com.spinyowl.legui.component.Button;
import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.component.Layer;
import com.spinyowl.legui.component.Slider;
import com.spinyowl.legui.component.Widget;
import com.spinyowl.legui.component.event.label.LabelWidthChangeEvent;
import com.spinyowl.legui.component.event.label.LabelWidthChangeEventListener;
import com.spinyowl.legui.event.CursorEnterEvent;
import com.spinyowl.legui.event.MouseClickEvent;
import com.spinyowl.legui.listener.CursorEnterEventListener;
import com.spinyowl.legui.listener.MouseClickEventListener;
import com.spinyowl.legui.style.Style.DisplayType;
import com.spinyowl.legui.style.border.SimpleLineBorder;
import com.spinyowl.legui.style.color.ColorConstants;
import com.spinyowl.legui.style.shadow.Shadow;

public class MainMenuDisplay extends IDisplay {

	private Layer layer;

	@Override
	public void onCreate() {

		layer = new Layer();
		layer.setSize(DisplayManager.WIDTH, DisplayManager.HEIGHT);

		Label title = new Label();
		title.getTextState().setText(DisplayManager.title);
		title.getStyle().setFont("mono");
		title.getStyle().setFontSize(24f);
		title.getStyle().setTextColor(1 / 255, 22 / 255, 137 / 255, 1);
		title.setPosition(DisplayManager.WIDTH / 2 - title.getTextState().getTextWidth() / 2,
				DisplayManager.HEIGHT / 2 - 100);
		title.getListenerMap().addListener(LabelWidthChangeEvent.class, (LabelWidthChangeEventListener) event -> {
			title.setPosition(DisplayManager.WIDTH / 2 - event.getWidth() / 2, DisplayManager.HEIGHT / 2 - 100);
		});
		layer.add(title);

		IDisplay display = new SinglePlayerDisplay();

		Button sp = new Button();
		sp.getStyle().setShadow(new Shadow());
		sp.getTextState().setText("Single Player");
		//sp.getStyle().setBorder(new SimpleLineBorder(ColorConstants.red(), 5));
		sp.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {
			if (event.getAction() != MouseClickEvent.MouseClickAction.RELEASE)
				return;
			DisplayManager.createDisplay(display);
			DisplayManager.changeDisplay(display);
		});
		sp.getListenerMap().addListener(CursorEnterEvent.class, (CursorEnterEventListener) event -> {
			//sp.getStyle().getShadow().setColor(ColorConstants.black());
			sp.getStyle().getShadow().setBlur(100);
		});
		sp.setSize(100, 100);
		sp.setPosition(69, 420);
		layer.add(sp);
		
		Widget shadowWidget = new Widget(20, 310, 200, 120);
		Slider blurSlider = new Slider(110, 5 + 20 * 2, 80, 10);
		shadowWidget.getContainer().add(blurSlider);
		blurSlider.addSliderChangeValueEventListener(
				(e) -> shadowWidget.getStyle().getShadow().setBlur(blurSlider.getValue()));
		layer.add(shadowWidget);

		UIMaster.getMasterFrame().addLayer(layer);
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
	public void onLeave() {
		layer.setEnabled(false);
		layer.getStyle().setDisplay(layer.isEnabled() == true ? DisplayType.MANUAL : DisplayType.NONE);
	}

	@Override
	public void onDestory() {
		// TODO Auto-generated method stub

	}

}

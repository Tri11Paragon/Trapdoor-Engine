package com.game.displays;

import org.joml.Vector2f;
import org.joml.Vector4f;

import com.spinyowl.legui.component.Button;
import com.spinyowl.legui.component.Component;
import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.component.Layer;
import com.spinyowl.legui.component.Slider;
import com.spinyowl.legui.component.Widget;
import com.spinyowl.legui.component.event.component.ChangeSizeEvent;
import com.spinyowl.legui.component.optional.align.HorizontalAlign;
import com.spinyowl.legui.component.optional.align.VerticalAlign;
import com.spinyowl.legui.event.MouseClickEvent;
import com.spinyowl.legui.event.WindowSizeEvent;
import com.spinyowl.legui.icon.Icon;
import com.spinyowl.legui.icon.ImageIcon;
import com.spinyowl.legui.image.loader.ImageLoader;
import com.spinyowl.legui.listener.MouseClickEventListener;
import com.spinyowl.legui.style.Background;
import com.spinyowl.legui.style.Style.DisplayType;
import com.spinyowl.legui.style.border.SimpleLineBorder;
import com.spinyowl.legui.style.shadow.Shadow;
import com.trapdoor.Main;
import com.trapdoor.engine.datatypes.ui.XButton;
import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.display.IDisplay;
import com.trapdoor.engine.display.ModelEditorDisplay;
import com.trapdoor.engine.display.OptionsDisplay;
import com.trapdoor.engine.display.TestDisplay;
import com.trapdoor.engine.registry.GameRegistry;
import com.trapdoor.engine.registry.annotations.MainMenuLoadEvent;
import com.trapdoor.engine.renderer.ui.UIMaster;

import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;

public class MainMenuDisplay extends IDisplay {

	private Layer layers;
	public static Icon rixie;
	public static Background rixieBackground;
	public static SinglePlayerDisplay sp;

	@MainMenuLoadEvent
	public static void onMenu() {
		MainMenuDisplay theDisplay;
		DisplayManager.createDisplay((theDisplay = new MainMenuDisplay()));
		DisplayManager.changeDisplay(theDisplay);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {

		Layer layer = new Layer();
		layer.setPosition(69/2, 69/2);
		if (Main.devMode)
			layer.setSize(456 + 69, 575);
		else
			layer.setSize(456 + 69, 345);
		// evil hack (doesn't work)
		layer.getListenerMap().addListener(ChangeSizeEvent.class, (e) -> {
			if (Main.devMode)
				layer.setSize(456 + 69, 575);
			else
				layer.setSize(456 + 69, 345);
		});
		
		SimpleLineBorder border = new SimpleLineBorder();
		border.setColor(new Vector4f(0 / 255f, 0 / 255f, 0 / 255f, 200 / 255f));
		border.setThickness(2);
		layer.getStyle().setBorder(border);
		layer.getStyle().setBorderRadius(25);
		
		layers = new Layer();
		layers.setSize(DisplayManager.WIDTH, DisplayManager.HEIGHT);
		layers.add(layer);
		
		int left = 69/2;
		
		Background bg = new Background();
		bg.setColor(new Vector4f(125 / 255f, 125 / 255f, 125 / 255f, 1));
		rixie = new ImageIcon(ImageLoader.loadImage("resources/textures/icon/rixie.jpg"));
		layer.getListenerMap().addListener(WindowSizeEvent.class, (e) -> {
			rixie.setSize(new Vector2f(e.getWidth(), e.getHeight()));
		});
		bg.setIcon(rixie);
		rixieBackground = bg;
		layers.getStyle().setBackground(bg);
		
		Background bg2 = new Background();
		bg2.setColor(new Vector4f(50 / 255f, 50 / 255f, 50 / 255f, 0.75f));
		layer.getStyle().setBackground(bg2);
		
		Label title = new Label();
		title.getTextState().setText(DisplayManager.gameName);
		title.getStyle().setFont("bettergrade");
		title.getStyle().setFontSize(184f);
		title.getStyle().setTextColor(240 / 255f, 240 / 255f, 240 / 255f, 1);
		title.getStyle().setVerticalAlign(VerticalAlign.TOP);
		title.getStyle().setHorizontalAlign(HorizontalAlign.LEFT);
		title.setPosition(left, 25);
		layer.add(title);

		IDisplay display = new JacobDisplay();
//		IDisplay display = new TheAmazingWorldOfHentaiDisplay();
		DisplayManager.createDisplay(display);

		Button sp = new XButton("Jacob's Fun Land", false, false, 1.02f, left, 210, 456, 48);
		//sp.getStyle().setBorder(new SimpleLineBorder(ColorConstants.red(), 5));
		sp.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {
			if (event.getAction() != MouseClickEvent.MouseClickAction.RELEASE)
				return;
			DisplayManager.changeDisplay(display);
		});
		setButtonPosition(sp, left, 210, 48, 10, 0);
		layer.add(sp);
		
		IDisplay optionsDis = new OptionsDisplay(this);
		DisplayManager.createDisplay(optionsDis);
		
		Button options = new XButton("Options", false, false, 1.02f, left, 210 + 48 + 10, 456, 48);
		setButtonPosition(options, left, 210, 48, 10, 1);
		options.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {
			if (event.getAction() != MouseClickEvent.MouseClickAction.RELEASE)
				return;
			DisplayManager.changeDisplay(optionsDis);
		});
		layer.add(options);
		
		if (Main.devMode) {
			Label lb = new Label("Dev Zone");
			lb.getStyle().setFontSize(52f);
			setButtonPosition(lb, left, 210, 48, 15, 2);
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
			setButtonPosition(tester, left, 210, 48, 10, 1);
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
			setButtonPosition(materials, left, 210, 48, 10, 1);
			layer.add(materials);
		}
		
		Widget shadowWidget = new Widget(20, 310, 200, 120);
		Slider blurSlider = new Slider(110, 5 + 20 * 2, 80, 10);
		shadowWidget.getContainer().add(blurSlider);
		blurSlider.addSliderChangeValueEventListener(
				(e) -> shadowWidget.getStyle().getShadow().setBlur(blurSlider.getValue()));
		//layer.add(shadowWidget);

		MainMenuDisplay.sp = new SinglePlayerDisplay();
		DisplayManager.createDisplay(MainMenuDisplay.sp);
		
		// make sure the layer is disabled until this screen is switched it
		layers.setEnabled(false);
		layers.getStyle().setDisplay(layer.isEnabled() == true ? DisplayType.MANUAL : DisplayType.NONE);
		UIMaster.getMasterFrame().addLayer(layers);
	}
	
	
	private int i = 0;
	private void setButtonPosition(Component button, float x, float startY, float height, float padding, int order) {
		i += order;
		button.setPosition(x, startY + height * i + padding * i);
	}

	@Override
	public void onSwitch() {
		
		layers.setEnabled(true);
		layers.getStyle().setDisplay(layers.isEnabled() == true ? DisplayType.MANUAL : DisplayType.NONE);

	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		ImGui.pushFont(GameRegistry.getFont("roboto-regular"));
		
		final int childWidth = 512;
		final int childHeight = 512;
		
		ImGui.setNextWindowPos(DisplayManager.windowPosX + (DisplayManager.WIDTH/2 - childWidth/2), DisplayManager.windowPosY + (DisplayManager.HEIGHT/2 - childHeight/2), ImGuiCond.Appearing);
		ImGui.begin("Debug", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoTitleBar 
				| ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoBackground | ImGuiWindowFlags.NoBringToFrontOnFocus 
				| ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoDecoration);
		
		ImGui.beginChild("Buttons", childWidth, childHeight);
			if (ImGui.button("Start Game", 512, 100)) {
				DisplayManager.changeDisplay(sp);
			}
		ImGui.endChild();
		
		ImGui.end();
		ImGui.popFont();
	}
	
	@Override
	public void update() {
		
	}

	@Override
	public void onLeave() {
		layers.setEnabled(false);
		layers.getStyle().setDisplay(layers.isEnabled() == true ? DisplayType.MANUAL : DisplayType.NONE);
	}

	@Override
	public void onDestory() {
		// TODO Auto-generated method stub

	}

}

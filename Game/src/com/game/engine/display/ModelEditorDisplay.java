package com.game.engine.display;

import java.util.Set;
import java.util.ArrayList;
import java.util.Map.Entry;

import com.game.engine.camera.Camera;
import com.game.engine.camera.RotatingCamera;
import com.game.engine.datatypes.ogl.assimp.Model;
import com.game.engine.renderer.ui.UIMaster;
import com.game.engine.threading.GameRegistry;
import com.game.engine.world.World;
import com.game.engine.world.entities.Entity;
import com.spinyowl.legui.component.Layer;
import com.spinyowl.legui.component.RadioButton;
import com.spinyowl.legui.component.RadioButtonGroup;
import com.spinyowl.legui.component.ScrollablePanel;
import com.spinyowl.legui.component.Widget;
import com.spinyowl.legui.component.event.component.ChangeSizeEvent;
import com.spinyowl.legui.event.MouseClickEvent;
import com.spinyowl.legui.event.WindowSizeEvent;
import com.spinyowl.legui.style.Style.DisplayType;

/**
 * @author brett
 * @date Jan. 1, 2022
 * 
 */
public class ModelEditorDisplay extends IDisplay {

	private Camera camera;
	private World world;
	private Layer layer;
	private Model currentModel;
	private ArrayList<RadioButton> buttons = new ArrayList<RadioButton>();
	private Entity e;
	
	@Override
	public void onCreate() {
		this.camera = new RotatingCamera();
		this.world = new World(camera);
		
		//this.world.addEntityToWorld(new EntityCamera(this.camera));
		
		Set<Entry<String, Model>> en = GameRegistry.getModelEntries();
		
		currentModel = GameRegistry.getErrorModel();
		e = new Entity();
		e.setModel(currentModel);
		e.setPosition(0, 0, 0);
		this.world.addEntityToWorld(e);
		
		layer = new Layer();
		layer.setSize(DisplayManager.WIDTH, DisplayManager.HEIGHT);
		
		Widget materialsWidget = new Widget("Material Editor");
		materialsWidget.setCloseable(false);
		materialsWidget.setSize(256, 512);
		materialsWidget.setPosition(DisplayManager.WIDTH - 256 - 25, 25);
		materialsWidget.getContainer().setSize(256, 512);
		
		materialsWidget.getListenerMap().addListener((WindowSizeEvent.class), (WindowSizeEventListener) -> {
			materialsWidget.setPosition(WindowSizeEventListener.getWidth() - materialsWidget.getSize().x - 25, 25);
		});
		
		ScrollablePanel matPanel = new ScrollablePanel();
		matPanel.setAutoResize(false);
		matPanel.setSize(256, 512);
		matPanel.getContainer().setSize(256, 512);
		
		
		
		materialsWidget.getContainer().add(matPanel);
		layer.add(materialsWidget);
		
		Widget modelsWidget = new Widget("Model Select");
		modelsWidget.setCloseable(false);
		modelsWidget.setSize(256, 512);
		modelsWidget.setPosition(25, 25);
		modelsWidget.getContainer().setSize(256, 512);
		
		ScrollablePanel panel = new ScrollablePanel();
		panel.setAutoResize(false);
		panel.setSize(256, 512);
		panel.getContainer().setSize(256, 512);
		
		modelsWidget.getContainer().add(panel);
		layer.add(modelsWidget);
		
		RadioButtonGroup radioButtonGroup = new RadioButtonGroup();
		int i = 0;
		for (Entry<String, Model> m : en) {
			RadioButton b = new RadioButton();
			b.setSize(256, 14);
			b.setPosition(5, i * 18 + 2 * i + 8);
			
			b.getStyle().setFontSize(12f);
			b.getStyle().setTextColor(1, 1, 1, 1);
			b.getTextState().setText(m.getKey().replace("resources/models/", ""));
			
			b.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) -> {
				Model associatedModel = m.getValue();
				currentModel = associatedModel;
				e.setModel(currentModel);
			});
			
			b.setRadioButtonGroup(radioButtonGroup);
			panel.getContainer().add(b);
			buttons.add(b);
			i++;
		}
		
		modelsWidget.getListenerMap().addListener((ChangeSizeEvent.class), (ChangeSizeEventListener) -> {
			panel.setSize(modelsWidget.getSize());
			for (RadioButton b : buttons) {
				b.setSize(ChangeSizeEventListener.getNewSize().x(), b.getSize().y);
			}
		});
		
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
		
		this.world.render();
		
	}
	
	@Override
	public void update() {
		this.world.update();
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

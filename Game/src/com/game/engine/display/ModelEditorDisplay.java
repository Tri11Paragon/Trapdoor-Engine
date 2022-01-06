package com.game.engine.display;

import java.util.Set;
import java.util.ArrayList;
import java.util.Map.Entry;

import com.game.Main;
import com.game.engine.camera.Camera;
import com.game.engine.camera.RotatingCamera;
import com.game.engine.datatypes.ogl.assimp.Material;
import com.game.engine.datatypes.ogl.assimp.Mesh;
import com.game.engine.datatypes.ogl.assimp.Model;
import com.game.engine.renderer.ui.UIMaster;
import com.game.engine.threading.GameRegistry;
import com.game.engine.tools.Logger;
import com.game.engine.tools.models.MaterialFSFormater;
import com.game.engine.world.World;
import com.game.engine.world.entities.Entity;
import com.spinyowl.legui.component.Button;
import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.component.Layer;
import com.spinyowl.legui.component.RadioButton;
import com.spinyowl.legui.component.RadioButtonGroup;
import com.spinyowl.legui.component.ScrollablePanel;
import com.spinyowl.legui.component.SelectBox;
import com.spinyowl.legui.component.Slider;
import com.spinyowl.legui.component.TextInput;
import com.spinyowl.legui.component.Widget;
import com.spinyowl.legui.component.event.component.ChangeSizeEvent;
import com.spinyowl.legui.component.event.selectbox.SelectBoxChangeSelectionEventListener;
import com.spinyowl.legui.component.event.slider.SliderChangeValueEvent;
import com.spinyowl.legui.component.event.textinput.TextInputContentChangeEvent;
import com.spinyowl.legui.event.MouseClickEvent;
import com.spinyowl.legui.event.WindowSizeEvent;
import com.spinyowl.legui.event.MouseClickEvent.MouseClickAction;
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
	private Material currentMaterial;
	private ArrayList<RadioButton> buttons = new ArrayList<RadioButton>();
	private Entity e;
	
	@Override
	public void onCreate() {
		this.camera = new RotatingCamera();
		this.world = new World(camera);
		
		//this.world.addEntityToWorld(new EntityCamera(this.camera));
		
		Set<Entry<String, Model>> en = GameRegistry.getModelEntries();
		
		currentModel = GameRegistry.getErrorModel();
		currentMaterial = GameRegistry.getErrorMaterial();
		e = new Entity();
		e.setModel(currentModel);
		e.setPosition(0, 0, 0);
		this.world.addEntityToWorld(e);
		
		layer = new Layer();
		layer.setSize(DisplayManager.WIDTH, DisplayManager.HEIGHT);
		
		Widget materialsWidget = new Widget("Material Editor");
		materialsWidget.setCloseable(false);
		materialsWidget.setResizable(false);
		materialsWidget.setSize(512, 256);
		materialsWidget.setPosition(DisplayManager.WIDTH - 512 - 25, 25);
		materialsWidget.getContainer().setSize(512, 256);
		
		materialsWidget.getListenerMap().addListener((WindowSizeEvent.class), (WindowSizeEventListener) -> {
			materialsWidget.setPosition(WindowSizeEventListener.getWidth() - materialsWidget.getSize().x - 25, 25);
		});
		
		ScrollablePanel matPanel = new ScrollablePanel();
		matPanel.setAutoResize(false);
		matPanel.setSize(512, 256);
		matPanel.getContainer().setSize(512, 512);
		
		
		
		Label diffuseTextureLabel = new Label("Diffuse Texture: ");
		diffuseTextureLabel.setPosition(5, 54);
		matPanel.getContainer().add(diffuseTextureLabel);
		
		TextInput diffuseTexture = new TextInput();
		diffuseTexture.setPosition(120, 50);
		diffuseTexture.setSize(356, diffuseTextureLabel.getSize().y + 5);
		diffuseTexture.getListenerMap().addListener((TextInputContentChangeEvent.class), l -> {
			currentMaterial.setDiffuseTexturePath(l.getNewValue());
			currentMaterial.setDiffuseTexture(GameRegistry.getTexture(l.getNewValue()));
		});
		matPanel.getContainer().add(diffuseTexture);
		
		
		
		Label normalTextureLabel = new Label("Normal Texture: ");
		normalTextureLabel.setPosition(5, 82);
		matPanel.getContainer().add(normalTextureLabel);
		
		TextInput normalTexture = new TextInput();
		normalTexture.setPosition(120, 78);
		normalTexture.setSize(356, normalTextureLabel.getSize().y + 5);
		normalTexture.getListenerMap().addListener((TextInputContentChangeEvent.class), l -> {
			currentMaterial.setNormalTexturePath(l.getNewValue());
			currentMaterial.setNormalTexture(GameRegistry.getTexture(l.getNewValue()));
		});
		matPanel.getContainer().add(normalTexture);
		
		
		
		Slider diffuseSlider = new Slider();
		TextInput diffuseInput = new TextInput();
		Label diffuseSliderLabel = new Label("(Diffuse)");
		diffuseSliderLabel.setPosition(275, 110);
		
		diffuseSlider.setSize(200, 30);
		diffuseSlider.setPosition(5, 100);
		diffuseSlider.getListenerMap().addListener((SliderChangeValueEvent.class), l -> {
			float f = l.getNewValue() / 100f;
			diffuseInput.getTextState().setText(Float.toString(f));
			currentMaterial.getColorInformation().x = f;
		});
		
		diffuseInput.setSize(48, 30);
		diffuseInput.setPosition(210, 100);
		diffuseInput.getListenerMap().addListener((TextInputContentChangeEvent.class), l -> {
			try {
				float f = Float.parseFloat(l.getNewValue());
				currentMaterial.getColorInformation().x = f;
				diffuseSlider.setValue(f);
			} catch (NumberFormatException e) {}
		});
		matPanel.getContainer().add(diffuseSlider);
		matPanel.getContainer().add(diffuseInput);
		matPanel.getContainer().add(diffuseSliderLabel);
		
		
		
		Slider specSlider = new Slider();
		TextInput specInput = new TextInput();
		Label specSliderLabel = new Label("(Specular)");
		specSliderLabel.setPosition(275, 150);
		
		specSlider.setSize(200, 30);
		specSlider.setPosition(5, 140);
		specSlider.getListenerMap().addListener((SliderChangeValueEvent.class), l -> {
			float f = l.getNewValue() / 100f;
			specInput.getTextState().setText(Float.toString(f));
			currentMaterial.getColorInformation().y = f;
		});
		
		specInput.setSize(48, 30);
		specInput.setPosition(210, 140);
		specInput.getListenerMap().addListener((TextInputContentChangeEvent.class), l -> {
			try {
				float f = Float.parseFloat(l.getNewValue());
				currentMaterial.getColorInformation().y = f;
				specSlider.setValue(f);
			} catch (NumberFormatException e) {}
		});
		matPanel.getContainer().add(specSlider);
		matPanel.getContainer().add(specInput);
		matPanel.getContainer().add(specSliderLabel);
		
		
		
		Slider ambientSlider = new Slider();
		TextInput ambientInput = new TextInput();
		Label ambientSliderLabel = new Label("(Ambient)");
		ambientSliderLabel.setPosition(275, 190);
		
		ambientSlider.setSize(200, 30);
		ambientSlider.setPosition(5, 180);
		ambientSlider.getListenerMap().addListener((SliderChangeValueEvent.class), l -> {
			float f = l.getNewValue() / 100f;
			ambientInput.getTextState().setText(Float.toString(f));
			currentMaterial.getColorInformation().z = f;
		});
		
		ambientInput.setSize(48, 30);
		ambientInput.setPosition(210, 180);
		ambientInput.getListenerMap().addListener((TextInputContentChangeEvent.class), l -> {
			try {
				float f = Float.parseFloat(l.getNewValue());
				currentMaterial.getColorInformation().z = f;
				ambientSlider.setValue(f);
			} catch (NumberFormatException e) {}
		});
		matPanel.getContainer().add(ambientSlider);
		matPanel.getContainer().add(ambientInput);
		matPanel.getContainer().add(ambientSliderLabel);
		
		
		
		Button save = new Button("Save Model Materials");
		save.setSize(500 - 12, 32);
		save.setPosition(6, 256);
		save.getListenerMap().addListener(MouseClickEvent.class, e -> {
			if (e.getAction() != MouseClickAction.RELEASE)
				return;
			if (Main.verbose)
				Logger.writeln("Saving " + currentModel + "'s materials!");
			MaterialFSFormater.saveMaterialsToFile(currentModel);
		});
		matPanel.getContainer().add(save);
		
		SelectBox<Object> selectMesh = new SelectBox<Object>(5, 10, 512, 32);
		selectMesh.addSelectBoxChangeSelectionEventListener( (SelectBoxChangeSelectionEventListener<Object>) event -> {
					if (event.getNewValue() instanceof String) 
						return;
					Mesh newObject = (Mesh) event.getNewValue();
					currentMaterial = newObject.getMaterial();
					
					diffuseTexture.getTextState().setText(currentMaterial.getDiffuseTexturePath());
					normalTexture.getTextState().setText(currentMaterial.getNormalTexturePath());
					
					diffuseInput.getTextState().setText(Float.toString(currentMaterial.getColorInformation().x));
					diffuseSlider.setValue(currentMaterial.getColorInformation().x * 100);
					
					specInput.getTextState().setText(Float.toString(currentMaterial.getColorInformation().y));
					specSlider.setValue(currentMaterial.getColorInformation().y * 100);
					
					ambientInput.getTextState().setText(Float.toString(currentMaterial.getColorInformation().z));
					ambientSlider.setValue(currentMaterial.getColorInformation().z * 100);
					
				});
		
		
		matPanel.getContainer().add(selectMesh);
		materialsWidget.getContainer().add(matPanel);
		layer.add(materialsWidget);
		
		Widget modelsWidget = new Widget("Model Select");
		modelsWidget.setResizable(false);
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
				diffuseTexture.getTextState().setText("");
				normalTexture.getTextState().setText("");
				
				Model associatedModel = m.getValue();
				currentModel = associatedModel;
				e.setModel(currentModel);
				// remove old materials
				for (int ir = 0; ir < selectMesh.getSelectBoxElements().size(); ir++) {
					selectMesh.removeElement(ir);
				}
				selectMesh.addElement("Select Material");
				// add the new ones
				for (int ir = 0; ir < currentModel.getMeshes().length; ir++) {
					selectMesh.addElement(currentModel.getMeshes()[ir]);
				}
				currentMaterial = currentModel.getMaterials()[0];
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

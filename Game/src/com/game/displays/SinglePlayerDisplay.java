package com.game.displays;

import org.joml.Vector3f;
import org.joml.Vector4f;

import com.game.entities.EntityBall;
import com.game.entities.EntityGrid;
import com.game.entities.EntityKevin;
import com.game.entities.SloshyEntityCamera;
import com.spinyowl.legui.component.Layer;
import com.spinyowl.legui.style.Background;
import com.spinyowl.legui.style.Style.DisplayType;
import com.spinyowl.legui.style.border.SimpleLineBorder;
import com.trapdoor.engine.camera.CreativeFirstPerson;
import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.display.IDisplay;
import com.trapdoor.engine.registry.GameRegistry;
import com.trapdoor.engine.registry.annotations.RegistrationEventSubscriber;
import com.trapdoor.engine.renderer.ui.UIMaster;
import com.trapdoor.engine.world.World;
import com.trapdoor.engine.world.entities.Entity;
import com.trapdoor.engine.world.entities.components.Transform;

public class SinglePlayerDisplay extends IDisplay{
	
	public CreativeFirstPerson camera;
	public World world;
	private Layer layers, cameraIcon, ballIcon;
	private Entity a, ball;
	private SloshyEntityCamera s;
	private Transform t;
//	private SmoothEntityCamera s;
//	private EntityCamera s;
	
	@RegistrationEventSubscriber
	public static void register() {		
		GameRegistry.registerModel("resources/models/simple/grid.dae");
		GameRegistry.registerModel("resources/models/simple/ball.dae");
		GameRegistry.registerModel("resources/models/simple/ball_b.dae");
		GameRegistry.registerModel("resources/models/kevin.dae");
		GameRegistry.registerModel("resources/models/Table.dae");
	}
	
	@Override
	public void onCreate() {
		
		this.camera = new CreativeFirstPerson();;
		this.world = new World(camera);
		this.setSkyTextures(
				"resources/textures/skyboxes/urban-skyboxes/CNTower/posx.jpg", 	// right
				"resources/textures/skyboxes/urban-skyboxes/CNTower/negx.jpg", 	// left
				"resources/textures/skyboxes/urban-skyboxes/CNTower/posy.jpg", 	// top
				"resources/textures/skyboxes/urban-skyboxes/CNTower/negy.jpg", // bottom
				"resources/textures/skyboxes/urban-skyboxes/CNTower/posz.jpg", 	// front
				"resources/textures/skyboxes/urban-skyboxes/CNTower/negz.jpg"	// back
			);
		
		this.camera.setPosition(new Vector3f(0, 10, 20));
		this.camera.setPitch(22);
//		s = (SloshyEntityCamera) new SloshyEntityCamera(this.camera).setPosition(10, 0, 0);
//		s = new SmoothEntityCamera(this.camera);
//		s = new EntityCamera(this.camera);
//		this.world.addEntityToWorld(s);
		
		a = new Entity().setModel(GameRegistry.getModel("resources/models/simple/grid.dae"))
				.setPosition(0, -5, 0);
		this.world.addEntityToWorld(a);
		
		Entity b = new EntityGrid().setModel(GameRegistry.getModel("resources/models/simple/grid.dae"))
				.setPosition(0, 2, 0);
		t = (Transform) b.getComponent(Transform.class);
		t.setScale(0.01f, 0, 1);
		this.world.addEntityToWorld(b);
				
		ball = new EntityBall().setModel(GameRegistry.getModel("resources/models/simple/ball.dae"));
		this.world.addEntityToWorld(ball);
		
		a = new EntityBall(-2).setModel(GameRegistry.getModel("resources/models/simple/ball_b.dae"));
		this.world.addEntityToWorld(a);
		
		a = new EntityKevin().setModel(GameRegistry.getModel("resources/models/kevin.dae"))
				.setPosition(5, 0, -5);
		t = (Transform) a.getComponent(Transform.class);
		t.setScale(2, 2, 2);
		t.setYaw(-45);
		this.world.addEntityToWorld(a);
		
		for (int j = -10; j <= 10; j += 5) {
			for (int i = -10; i <= 10; i += 5) {
				a = new Entity().setModel(GameRegistry.getModel("resources/models/Table.dae")).setPosition(j, 0, i);
				t = (Transform) a.getComponent(Transform.class);
				t.setScale(4, 4, 4);
				this.world.addEntityToWorld(a);
			}
		}
		
		doGUI();
	}

	@Override
	public void onSwitch() {
		// TODO Auto-generated method stub
		layers.setEnabled(true);
		layers.getStyle().setDisplay(layers.isEnabled() == true ? DisplayType.MANUAL : DisplayType.NONE);
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		this.world.render();
//		Transform t = s.getComponent(Transform.class);
		Transform u = ball.getComponent(Transform.class);
//		cameraIcon.setPosition(150 + 6*t.getX(), 150 + 6*t.getZ());
		ballIcon.setPosition(150 + 6*u.getX(), 150 + 6*u.getZ());
	}
	
	@Override
	public void update() {
		this.world.update();
	}

	@Override
	public void onLeave() {
		// TODO Auto-generated method stub
		layers.setEnabled(false);
		layers.getStyle().setDisplay(layers.isEnabled() == true ? DisplayType.MANUAL : DisplayType.NONE);
	}

	@Override
	public void onDestory() {
		// TODO Auto-generated method stub
		this.world.cleanup();
	}
	
	private void doGUI() {
		// GUI
		Layer layer = new Layer();
		layer.setPosition(0, 0);
		layer.setSize(300, 300);		
		SimpleLineBorder border = new SimpleLineBorder();
		border.setColor(new Vector4f(0 / 255f, 0 / 255f, 0 / 255f, 200 / 255f));
		border.setThickness(2);		
		layer.getStyle().setBorder(border);		
		Background bg2 = new Background();
		bg2.setColor(new Vector4f(50 / 255f, 50 / 255f, 50 / 255f, 0.75f));
		layer.getStyle().setBackground(bg2);
		
		cameraIcon = new Layer();
//				cameraIcon.setPosition(150, 150);
		cameraIcon.setSize(10, 10);
		Background bg3 = new Background();
		bg3.setColor(new Vector4f(1f, 1f, 1f, 1f));
		cameraIcon.getStyle().setBackground(bg3);
		
		ballIcon = new Layer();
//				ballIcon.setPosition(150, 150);
		ballIcon.setSize(10, 10);
		Background bg4 = new Background();
		bg4.setColor(new Vector4f(1f, 0f, 0f, 1f));
		ballIcon.getStyle().setBackground(bg4);
		
		layers = new Layer();
		layers.setSize(DisplayManager.WIDTH, DisplayManager.HEIGHT);
		layers.add(layer);
		layers.add(cameraIcon);
		layers.add(ballIcon);
		UIMaster.getMasterFrame().addLayer(layers);
		
		layers.setEnabled(false);
		layers.getStyle().setDisplay(layers.isEnabled() == true ? DisplayType.MANUAL : DisplayType.NONE);
	}
	
}

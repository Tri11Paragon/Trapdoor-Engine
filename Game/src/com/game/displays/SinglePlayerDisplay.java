package com.game.displays;

import org.joml.Vector4f;

import com.game.entities.EntityGolfBall;
import com.game.entities.EntityKent;
import com.game.entities.Kentipede;
import com.game.entities.SloshyEntityCamera;
import com.game.entities.SmoothEntityCamera;
import com.spinyowl.legui.component.Layer;
import com.spinyowl.legui.component.event.component.ChangeSizeEvent;
import com.spinyowl.legui.intersection.RectangleIntersector;
import com.spinyowl.legui.style.Background;
import com.spinyowl.legui.style.border.SimpleLineBorder;
import com.trapdoor.Main;
import com.trapdoor.engine.camera.CreativeFirstPerson;
import com.trapdoor.engine.datatypes.lighting.Light;
import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.display.IDisplay;
import com.trapdoor.engine.registry.GameRegistry;
import com.trapdoor.engine.registry.annotations.RegistrationEventSubscriber;
import com.trapdoor.engine.renderer.ui.UIMaster;
import com.trapdoor.engine.world.World;
import com.trapdoor.engine.world.entities.Entity;
import com.trapdoor.engine.world.entities.EntityCamera;
import com.trapdoor.engine.world.entities.components.Transform;

public class SinglePlayerDisplay extends IDisplay{
	
	public CreativeFirstPerson camera;
	public World world;
	private Layer cameraIcon, ballIcon;
	private Entity a;
	private SloshyEntityCamera s;
//	private SmoothEntityCamera s;
//	private EntityCamera s;
	
	@RegistrationEventSubscriber
	public static void register() {		
		GameRegistry.registerModel("resources/models/simple/grid.dae");
		GameRegistry.registerModel("resources/models/simple/ball.dae");
	}
	
	@Override
	@SuppressWarnings("unused")
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
		
		s = (SloshyEntityCamera) new SloshyEntityCamera(camera).setPosition(0, 0, 5);
//		s = new SmoothEntityCamera(this.camera);
//		s = new EntityCamera(this.camera);
		this.world.addEntityToWorld(s);
		
		a = new Entity().setModel(GameRegistry.getModel("resources/models/simple/grid.dae"))
				.setPosition(0, -5, 0);
		this.world.addEntityToWorld(a);
				
		a = new EntityGolfBall().setModel(GameRegistry.getModel("resources/models/simple/ball.dae"))
				.setPosition(0, 0, -6);
		this.world.addEntityToWorld(a);
		
		//kent
//		new Kentipede(this.world, 2, 10, s);
		
//		for (float i = 0; i < 2*Math.PI; i+=Math.PI/15) {
//			a = new EntityKent(i).setModel(GameRegistry.getModel("resources/models/kent.dae"));
//			this.world.addEntityToWorld(a);
//		}
		
//		for (float i = 0; i < 2*Math.PI; i+=Math.PI/10) {
//			a = new EntityKent(i, 2).setModel(GameRegistry.getModel("resources/models/kent.dae"));
//			this.world.addEntityToWorld(a);
//		}
		
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
//		cameraIcon.setPosition(150, 150);
		cameraIcon.setSize(10, 10);
		Background bg3 = new Background();
		bg3.setColor(new Vector4f(1f, 1f, 1f, 1f));
		cameraIcon.getStyle().setBackground(bg3);
		
		ballIcon = new Layer();
//		ballIcon.setPosition(150, 150);
		ballIcon.setSize(10, 10);
		Background bg4 = new Background();
		bg4.setColor(new Vector4f(1f, 0f, 0f, 1f));
		ballIcon.getStyle().setBackground(bg4);
		
		Layer layers = new Layer();
		layers.setSize(DisplayManager.WIDTH, DisplayManager.HEIGHT);
		layers.add(layer);
		layers.add(cameraIcon);
		layers.add(ballIcon);
		UIMaster.getMasterFrame().addLayer(layers);
		
	}

	@Override
	public void onSwitch() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		this.world.render();
		Transform t = s.getComponent(Transform.class);
		Transform u = a.getComponent(Transform.class);
		cameraIcon.setPosition(150 + 6*t.getX(), 150 + 6*t.getZ());
		ballIcon.setPosition(150 + 6*u.getX(), 150 + 6*u.getZ());
	}
	
	@Override
	public void update() {
		this.world.update();
	}

	@Override
	public void onLeave() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDestory() {
		// TODO Auto-generated method stub
		this.world.cleanup();
	}
	
}

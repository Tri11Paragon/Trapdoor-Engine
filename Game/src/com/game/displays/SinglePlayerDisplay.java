package com.game.displays;

import org.joml.Vector3d;

import com.game.entities.EntityKent;
import com.game.entities.Kentipede;
import com.game.entities.SmoothEntityCamera;
import com.jme3.math.Vector3f;
import com.trapdoor.engine.camera.CreativeFirstPerson;
import com.trapdoor.engine.datatypes.lighting.Light;
import com.trapdoor.engine.display.IDisplay;
import com.trapdoor.engine.registry.GameRegistry;
import com.trapdoor.engine.registry.annotations.RegistrationEventSubscriber;
import com.trapdoor.engine.world.World;
import com.trapdoor.engine.world.entities.Entity;
import com.trapdoor.engine.world.entities.EntityCamera;
import com.trapdoor.engine.world.entities.components.Transform;

public class SinglePlayerDisplay extends IDisplay{
	
	public CreativeFirstPerson camera;
	public World world;
	
	@RegistrationEventSubscriber
	public static void register() {
		GameRegistry.registerModel("resources/models/supercube.dae");
		GameRegistry.registerModel("resources/models/kent.dae");
//		GameRegistry.registerModel("resources/models/Mackenzie_Hallway_brt.dae");
	}
	
	@Override
	public void onCreate() {
		
		this.camera = new CreativeFirstPerson();;
		this.world = new World(camera);
		this.setSkyTextures(
				"resources/textures/skyboxes/lolzplus2/right.png.jpg", 	// right
				"resources/textures/skyboxes/lolzplus2/left.png.jpg", 	// left
				"resources/textures/skyboxes/lolzplus2/top.png.jpg", 	// top
				"resources/textures/skyboxes/lolzplus2/bottom.png.jpg", // bottom
				"resources/textures/skyboxes/lolzplus2/front.png.jpg", 	// front
				"resources/textures/skyboxes/lolzplus2/back.png.jpg"	// back
			);
		
		SmoothEntityCamera s = new SmoothEntityCamera(this.camera);
		Transform t3 = (Transform) s.getComponent(Transform.class);
//		t3.setPosition(0, 0, 0);
		this.world.addEntityToWorld(s);
		
		Entity h;
		Entity a;
		Transform t, t2;
		/*
		//The hallways
		int dist = 12;
		
		h = new Entity().setModel(GameRegistry.getModel("resources/models/Mackenzie_Hallway_brt.dae"));
		h.setPosition(0 - dist, 0, 10);
		this.world.addEntityToWorld(h);
		
		h = new Entity().setModel(GameRegistry.getModel("resources/models/Mackenzie_Hallway_brt.dae"));
		h.setPosition(0 + dist, 0, 10);
		this.world.addEntityToWorld(h);
		
		h = new Entity().setModel(GameRegistry.getModel("resources/models/Mackenzie_Hallway_brt.dae"));
		h.setPosition(0, 0, 10 - dist);
		t2 = (Transform) h.getComponent(Transform.class);
		t2.setRotation((float) Math.PI/2, 0, 0);
		this.world.addEntityToWorld(h);
		
		h = new Entity().setModel(GameRegistry.getModel("resources/models/Mackenzie_Hallway_brt.dae"));
		h.setPosition(0, 0, 10 + dist);
		t2 = (Transform) h.getComponent(Transform.class);
		t2.setRotation((float) Math.PI/2, 0, 0);
		this.world.addEntityToWorld(h);
		
		
		//The small floors
		a = new Entity().setModel(GameRegistry.getModel("resources/models/supercube.dae"));
		a.setPosition(0, -1, 10);
		
		t = a.getComponent(Transform.class);
		t.setScale(3, 1, 3);
		
		this.world.addEntityToWorld(a);
		
		a = new Entity().setModel(GameRegistry.getModel("resources/models/supercube.dae"));
		a.setPosition(0, 7, 10);
		
		t = a.getComponent(Transform.class);
		t.setScale(3, 1, 3);
		
		this.world.addEntityToWorld(a);
		*/
		
		//The big floor		
		a = new Entity().setModel(GameRegistry.getModel("resources/models/supercube.dae"));
		a.setPosition(0, -5, 0);
		
		Light thefunny = new Light(Light.lightings[6], 1.0f, 1.0f, 1.0f, 0, 10, 0);
		a.addLight(thefunny);
		
		t = a.getComponent(Transform.class);
		t.setScale(100f, 1, 100f);
		
		this.world.addEntityToWorld(a);
		
		
		//kent
		new Kentipede(this.world, 2, 10, t3);
		
//		for (float i = 0; i < 2*Math.PI; i+=Math.PI/15) {
//			a = new EntityKent(i).setModel(GameRegistry.getModel("resources/models/kent.dae"));
//			this.world.addEntityToWorld(a);
//		}
		
//		for (float i = 0; i < 2*Math.PI; i+=Math.PI/10) {
//			a = new EntityKent(i, 1).setModel(GameRegistry.getModel("resources/models/kent.dae"));
//			this.world.addEntityToWorld(a);
//		}
		
	}

	@Override
	public void onSwitch() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		this.world.render();
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

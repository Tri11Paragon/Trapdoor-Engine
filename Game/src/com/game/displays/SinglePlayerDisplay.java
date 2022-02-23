package com.game.displays;

import com.game.entities.SmoothEntityCamera;
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
		
	}
	
	@Override
	public void onCreate() {
		this.camera = new CreativeFirstPerson();
		this.world = new World(camera);
		
		this.world.addEntityToWorld(new SmoothEntityCamera(this.camera));
		
		Entity a = new Entity();
		a.setModel(GameRegistry.getModel("resources/models/supercube.dae"));
		a.setPosition(0, -5, 0);
		
		Light thefunny = new Light(Light.lightings[6], 1.0f, 1.0f, 1.0f, 0, 10, 0);
		a.addLight(thefunny);
		
		Transform t = a.getComponent(Transform.class);
		t.setScale(100f, 1, 100f);
		
		// How to apply t to a ??
		
		this.world.addEntityToWorld(a);
		
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

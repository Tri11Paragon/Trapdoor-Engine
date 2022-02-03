package com.game.displays;

import com.game.entities.EntityPiss;
import com.game.entities.EntityPoop;
import com.trapdoor.engine.camera.CreativeFirstPerson;
import com.trapdoor.engine.datatypes.lighting.Light;
import com.trapdoor.engine.display.IDisplay;
import com.trapdoor.engine.registry.GameRegistry;
import com.trapdoor.engine.registry.annotations.RegistrationEventSubscriber;
import com.trapdoor.engine.tools.Logging;
import com.trapdoor.engine.world.World;
import com.trapdoor.engine.world.entities.Entity;
import com.trapdoor.engine.world.entities.EntityCamera;

public class TheAmazingWorldOfHentaiDisplay extends IDisplay{
	
	public CreativeFirstPerson camera;
	public World world;
	
	@RegistrationEventSubscriber
	public static void register() {
		Logging.logger.fatal("HEY YOU SUCK");
		
		GameRegistry.registerModel("resources/models/poop.dae");
		GameRegistry.registerModel("resources/models/piss.dae");
	}
	
	@Override
	public void onCreate() {
		this.camera = new CreativeFirstPerson();
		this.world = new World(camera);
		
		this.world.addEntityToWorld(new EntityCamera(this.camera));
		
		this.world.addEntityToWorld(new EntityPoop().setModel(
				GameRegistry.getModel("resources/models/poop.dae")).setPosition(0, 0, -15)
				.addLight(new Light(Light.lightings[6], 5.0f, 0.0f, 0.0f, 0, 5, 0)));
		
		this.world.addEntityToWorld(new EntityPiss().setModel(
				GameRegistry.getModel("resources/models/piss.dae")).setPosition(20, 0, -10)
				.addLight(new Light(Light.lightings[6], 3.0f, 0.5f, 0.5f, 0, 5, 0)));
		
		this.setSkyColor(0, 0, 0);
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

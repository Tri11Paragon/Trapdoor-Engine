package com.game.displays;

import com.game.entities.EntityKent;
import com.game.entities.EntityPiss;
import com.trapdoor.engine.camera.CreativeFirstPerson;
import com.trapdoor.engine.datatypes.lighting.Light;
import com.trapdoor.engine.display.IDisplay;
import com.trapdoor.engine.registry.GameRegistry;
import com.trapdoor.engine.registry.annotations.RegistrationEventSubscriber;
import com.trapdoor.engine.tools.input.Keyboard;
import com.trapdoor.engine.world.World;
import com.trapdoor.engine.world.entities.Entity;
import com.trapdoor.engine.world.entities.EntityCamera;

public class TheAmazingWorldOfHentaiDisplay extends IDisplay{
	
	public CreativeFirstPerson camera;
	public World world;
	
	@RegistrationEventSubscriber
	public static void register() {
		
		GameRegistry.registerModel("resources/models/poop.dae");
		GameRegistry.registerModel("resources/models/piss.dae");
		GameRegistry.registerModel("resources/models/cum.dae");
		GameRegistry.registerModel("resources/models/kent.dae");
		GameRegistry.registerModel("resources/models/zucc.dae");
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		this.camera = new CreativeFirstPerson();
		this.world = new World(camera);
		
		Light thefunny = new Light(Light.lightings[6], 1.0f, 1.0f, 1.0f, 0, 0, 0);
		
		this.world.addEntityToWorld(new EntityCamera(this.camera).setModel(GameRegistry.getModel("resources/models/poop.dae")).addLight(thefunny));
		
		this.world.addEntityToWorld(new Entity().setModel(
				GameRegistry.getModel("resources/models/poop.dae")).setPosition(-30, 0, -15));
		
		this.world.addEntityToWorld(new EntityPiss().setModel(
				GameRegistry.getModel("resources/models/piss.dae")).setPosition(20, 0, -10));
		
		this.world.addEntityToWorld(new Entity().setModel(
				GameRegistry.getModel("resources/models/cum.dae")).setPosition(30, 0, -10));
		
		this.world.addEntityToWorld(new EntityKent().setModel(
				GameRegistry.getModel("resources/models/kent.dae")).setPosition(-20, 0, -10));
		
		this.world.addEntityToWorld(new EntityKent().setModel(
				GameRegistry.getModel("resources/models/zucc.dae")).setPosition(0, 0, -10));
		
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
	
	@SuppressWarnings("deprecation")
	@Override
	public void update() {
		this.world.update();
		if (Keyboard.isKeyDown(Keyboard.C) && Keyboard.state()) {
			this.world.addEntityToWorld(new Entity(1).setModel(
					GameRegistry.getModel("resources/models/cum.dae"))
					.setPosition(
							(float) camera.getPosition().x + 10, 
							(float) camera.getPosition().y, 
							(float) camera.getPosition().z)
					.applyCentralForce(0, 200, 0));
		}
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

package com.trapdoor.engine.display;

import com.game.entities.EntityPoop;
import com.trapdoor.engine.camera.CreativeFirstPerson;
import com.trapdoor.engine.datatypes.lighting.Light;
import com.trapdoor.engine.datatypes.ogl.assimp.Model;
import com.trapdoor.engine.registry.GameRegistry;
import com.trapdoor.engine.registry.annotations.PostRegistrationEventSubscriber;
import com.trapdoor.engine.registry.annotations.RegistrationEventSubscriber;
import com.trapdoor.engine.world.World;
import com.trapdoor.engine.world.entities.Entity;
import com.trapdoor.engine.world.entities.EntityCamera;
import com.trapdoor.engine.world.sound.SoundSystem;
import com.trapdoor.engine.world.sound.SoundSystemType;

/**
 * @author brett
 * @date Oct. 18, 2021
 * 
 */
public class TestDisplay extends IDisplay {
	
	
	//private ArrayList<Entity> e = new ArrayList<Entity>();
	private CreativeFirstPerson camera;
	private World world;
	private Model cubeModel;
	
	@RegistrationEventSubscriber
	public static void register() {
		GameRegistry.registerTexture("resources/textures/character Texture.png");
		
		GameRegistry.registerModel("resources/models/depression.dae");
		GameRegistry.registerModel("resources/models/model.dae");
		GameRegistry.registerModel("resources/models/test object.dae");
		GameRegistry.registerModel("resources/models/supercube.dae");
		GameRegistry.registerModel("resources/models/floor.dae");
		GameRegistry.registerModel("resources/models/tuber.dae");
		
		GameRegistry.registerModel("resources/models/poop.dae");
		
		GameRegistry.registerSound("resources/sounds/penis.ogg");
		
		GameRegistry.registerSound("resources/sounds/music/how about some piano.ogg");
		GameRegistry.registerSound("resources/sounds/music/i don't really know eh 9.ogg");
		GameRegistry.registerSound("resources/sounds/music/im sorry.ogg");
		GameRegistry.registerSound("resources/sounds/music/omniworks.ogg");
		GameRegistry.registerSound("resources/sounds/music/rpd.ogg");
		GameRegistry.registerSound("resources/sounds/music/weapons of mass distraction remasted.ogg");
		GameRegistry.registerSound("resources/sounds/music/weapons of mass distraction.ogg");
		
	}
	
	@PostRegistrationEventSubscriber
	public static void postRegister() {
		SoundSystem.registerSoundSystem("basic_music", SoundSystemType.BACKGROUND,
				GameRegistry.getSound("resources/sounds/music/how about some piano.ogg"),
				GameRegistry.getSound("resources/sounds/music/i don't really know eh 9.ogg"),
				GameRegistry.getSound("resources/sounds/music/im sorry.ogg"),
				GameRegistry.getSound("resources/sounds/music/omniworks.ogg"),
				GameRegistry.getSound("resources/sounds/music/rpd.ogg"),
				GameRegistry.getSound("resources/sounds/music/weapons of mass distraction remasted.ogg"),
				GameRegistry.getSound("resources/sounds/music/weapons of mass distraction.ogg"));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		this.setSkyTextures(
					"resources/textures/skyboxes/day/right.jpg", 	// right
					"resources/textures/skyboxes/day/left.jpg", 	// left
					"resources/textures/skyboxes/day/top.jpg", 	// top
					"resources/textures/skyboxes/day/bottom.jpg", // bottom
					"resources/textures/skyboxes/day/front.jpg", 	// front
					"resources/textures/skyboxes/day/back.jpg"	// back
				);
		
		this.camera = new CreativeFirstPerson();
		this.world = new World(camera);
		
		this.world.addEntityToWorld(new EntityCamera(this.camera));
		
		this.world.addEntityToWorld(
				new Entity(0, true, null)
					.setModel(GameRegistry.getModel("resources/models/floor.dae"))
					.setPosition(0, -20, 0)
				);
		this.world.addEntityToWorld(
				new Entity(0, true, null)
					.setModel(GameRegistry.getModel("resources/models/tuber.dae"))
					.setPosition(0, -19.7f, -58)
					.addLight(new Light(Light.lightings[5], 1.0f, 1.0f, 1.0f, 0, 5, -5))
					.addLight(new Light(Light.lightings[5], 1.0f, 1.0f, 1.0f, 0, 5, 0))
					.addLight(new Light(Light.lightings[5], 1.0f, 1.0f, 1.0f, 0, 5, 5))
				);
		
		this.cubeModel = GameRegistry.getModel("resources/models/depression.dae");
		this.world.addEntityToWorld(new Entity().setModel(GameRegistry.getModel("resources/models/test object.dae")).setPosition(0, -15.0f, 0));
		this.world.addEntityToWorld(new Entity().setModel(cubeModel).setPosition(25, 0, 0));
		this.world.addEntityToWorld(new Entity().setModel(cubeModel).setPosition(-25, 0, 0));
		this.world.addEntityToWorld(new Entity().setModel(cubeModel).setPosition(0, 0, 25));
		this.world.addEntityToWorld(new Entity().setModel(cubeModel).setPosition(0, 0, -25));
		this.world.addEntityToWorld(
				new Entity()
					.setModel(GameRegistry.getModel("resources/models/hellolosers.obj"))
					.setPosition(5, 5, 5)
					.addLight(
							new Light(Light.lightings[6], 2.5f, 2.5f, 1.5f, -5, -5, -5)
							 )
					);
		// add entity
		this.world.addEntityToWorld(
				new Entity()
					// set the model
					.setModel(
							GameRegistry.getModel("resources/models/lll3.obj"))
					// change position
					.setPosition(15, 5, 25)); 
		//add poop
		this.world.addEntityToWorld(
				new EntityPoop()
					// set the model
					.setModel(
							GameRegistry.getModel("resources/models/poop.dae"))
					// change position
					.setPosition(-15, -4, -15));
	}

	@Override
	public void onSwitch() {
		
	}
	
	@Override
	public void render() {
		this.world.render();
		SoundSystem.update();
	}
	
	@Override
	public void update() {
		this.world.update();
	}

	@Override
	public void onLeave() {
		//EntityRenderer.deleteAllEntities();
		//World.deleteAllEntities();
	}

	@Override
	public void onDestory() {
		this.world.cleanup();
	}
	
}

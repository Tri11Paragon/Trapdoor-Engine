package com.trapdoor.engine.display;

import org.joml.Vector3f;

import com.game.entities.EntityKent;
import com.game.entities.EntityPoop;
import com.game.entities.Kentipede;
import com.trapdoor.engine.camera.CreativeFirstPerson;
import com.trapdoor.engine.datatypes.commands.FreeMoveCommand;
import com.trapdoor.engine.datatypes.commands.GravityCommand;
import com.trapdoor.engine.datatypes.commands.RayCastCommand;
import com.trapdoor.engine.datatypes.commands.TeleportCommand;
import com.trapdoor.engine.datatypes.lighting.Light;
import com.trapdoor.engine.datatypes.ogl.assimp.Model;
import com.trapdoor.engine.registry.GameRegistry;
import com.trapdoor.engine.registry.annotations.PostRegistrationEventSubscriber;
import com.trapdoor.engine.registry.annotations.RegistrationEventSubscriber;
import com.trapdoor.engine.renderer.particles.ParticleSystem;
import com.trapdoor.engine.renderer.ui.CommandBox;
import com.trapdoor.engine.renderer.ui.DebugInfo;
import com.trapdoor.engine.tools.RayCasting;
import com.trapdoor.engine.tools.input.Mouse;
import com.trapdoor.engine.world.World;
import com.trapdoor.engine.world.entities.BouncingEntity;
import com.trapdoor.engine.world.entities.Entity;
import com.trapdoor.engine.world.entities.EntityCamera;
import com.trapdoor.engine.world.entities.EntitySpawner;
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
	private EntityCamera cameraEnt;
	private RayCasting rayCasting;
	private World world;
	private Model cubeModel;
	private ParticleSystem ps;
	
	@RegistrationEventSubscriber
	public static void register() {
		GameRegistry.registerTexture("resources/textures/character Texture.png");
		
		GameRegistry.registerModel("resources/models/depression.dae");
		GameRegistry.registerModel("resources/models/model.dae");
		GameRegistry.registerModel("resources/models/test object.dae");
		GameRegistry.registerModel("resources/models/supercube.dae");
		GameRegistry.registerModel("resources/models/floor.dae");
		GameRegistry.registerModel("resources/models/tuber.dae");
		GameRegistry.registerModel("resources/models/zucc.dae");
		GameRegistry.registerModel("resources/models/playerblend.dae");
		GameRegistry.registerModel("resources/models/spawner.dae");
		
		GameRegistry.registerModel("resources/models/poop.dae");
		GameRegistry.registerModel("resources/models/Mackenzie_Hallway_brt.dae");
		
		GameRegistry.registerSound("resources/sounds/penis.ogg");
		
		GameRegistry.registerSound("resources/sounds/music/how about some piano.ogg");
		GameRegistry.registerSound("resources/sounds/music/i don't really know eh 9.ogg");
		GameRegistry.registerSound("resources/sounds/music/im sorry.ogg");
		GameRegistry.registerSound("resources/sounds/music/omniworks.ogg");
		GameRegistry.registerSound("resources/sounds/music/rpd.ogg");
		GameRegistry.registerSound("resources/sounds/music/weapons of mass distraction remasted.ogg");
		GameRegistry.registerSound("resources/sounds/music/weapons of mass distraction.ogg");
		
		GameRegistry.registerParticleTexture("resources/textures/particles/atlas/atlas_0.png");
		GameRegistry.registerParticleTexture("resources/textures/particles/atlas/atlas_2.png");
		GameRegistry.registerParticleTexture("resources/textures/particles/atlas/atlas_7.png");
		
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
	
	@Override
	public void onCreate() {
		this.setSkyTextures(
					"resources/textures/skyboxes/lolzplus2/right.png.jpg", 	// right
					"resources/textures/skyboxes/lolzplus2/left.png.jpg", 	// left
					"resources/textures/skyboxes/lolzplus2/top.png.jpg", 	// top
					"resources/textures/skyboxes/lolzplus2/bottom.png.jpg", // bottom
					"resources/textures/skyboxes/lolzplus2/front.png.jpg", 	// front
					"resources/textures/skyboxes/lolzplus2/back.png.jpg"	// back
				);
		
		this.camera = new CreativeFirstPerson();
		this.world = new World(camera);
		this.rayCasting = new RayCasting(camera, world);
		
		// .setModel(GameRegistry.getModel("resources/models/playerblend.dae"))
		this.world.addEntityToWorld((cameraEnt = new EntityCamera(this.camera)));
		
		this.world.addEntityToWorld(
				new Entity(0, true, null)
					.setModel(GameRegistry.getModel("resources/models/floor.dae"))
					.setPosition(0, -20, 0)
				);
		this.world.addEntityToWorld(
				new Entity(0, true, null)
					.setModel(GameRegistry.getModel("resources/models/tuber.dae"))
					.setPosition(0, -19.7f, -58)
					.addLight(new Light(Light.lightings[5], 2.5f, 2.5f, 2.5f, 0, 5, -5))
					.addLight(new Light(Light.lightings[5], 1.0f, 1.0f, 1.0f, 0, 5, 0))
					.addLight(new Light(Light.lightings[5], 1.0f, 1.0f, 1.0f, 0, 5, 5))
				);
		this.world.addEntityToWorld(new Entity()
				.setModel(GameRegistry.getModel("resources/models/Mackenzie_Hallway_brt.dae"))
				.setPosition(-59, -19.6f, 0)
				.addLight(new Light(Light.lightings[7], 0, 2, 0)));
		
		this.cubeModel = GameRegistry.getModel("resources/models/depression.dae");
		this.world.addEntityToWorld(new Entity().setModel(GameRegistry.getModel("resources/models/test object.dae")).setPosition(0, -15.0f, 0));
		this.world.addEntityToWorld(new Entity().setModel(cubeModel).setPosition(25, 0, 0));
		this.world.addEntityToWorld(new Entity().setModel(cubeModel).setPosition(-25, 0, 0));
		this.world.addEntityToWorld(new Entity().setModel(cubeModel).setPosition(0, 0, 25));
		this.world.addEntityToWorld(new Entity().setModel(cubeModel).setPosition(0, 0, -25));
		BouncingEntity rixie = new BouncingEntity(120);
		rixie.setModel(cubeModel);
		rixie.setPosition(-60, 5, 0);
		//rixie.getComponent(Transform.class).setScale(5, 5, 5);
		this.world.addEntityToWorld(rixie);
		this.world.addEntityToWorld(new EntityKent()
											.setModel(GameRegistry.getModel("resources/models/kent.dae"))
											.setPosition(25, -15, -40)
											.addLight(new Light(Light.lightings[5], 0, 1, 0)));
		this.world.addEntityToWorld(new EntityKent().setModel(GameRegistry.getModel("resources/models/zucc.dae")).setPosition(30, -15, -45));
		this.world.addEntityToWorld(
				new Entity()
					.setModel(GameRegistry.getModel("resources/models/hellolosers.obj"))
					.setPosition(5, 5, 5)
					.addLight(
							new Light(Light.lightings[6], 2.5f, 2.5f, 1.5f, -5, -5, -5)
							 )
					);
		this.world.addEntityToWorld(new EntityKent().setModel(GameRegistry.getModel("resources/models/kent.dae")).setPosition(00, 10, -50));
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
		
		new Kentipede(world, 150, 10, 0, 2, 1, 10, rixie);
		new Kentipede(world, -35, 0, -35, 2, 2, 10, rixie);
		new Kentipede(world, 2, 3, 10, rixie);
		new Kentipede(world, 35, 0, -35, 2, 4, 10, rixie);
		
		this.world.addEntityToWorld(new EntitySpawner(
												new EntityKent(0, cameraEnt).setModel(GameRegistry.getModel("resources/models/kent.dae")), 
												cameraEnt,
												12000)
										.setModel(GameRegistry.getModel("resources/models/spawner.dae"))
										.setPosition(75, -8, -25));
		
		ps = new ParticleSystem(100, 20, 0.2f, 1, 1);
		this.world.addParticleSystemToWorld(ps);
		
	}

	@Override
	public void onSwitch() {
		// can be added to any screen, will just overwrite on load
		CommandBox.registerCommand("raycast", new RayCastCommand(rayCasting));
		TeleportCommand tp = new TeleportCommand(cameraEnt, camera);
		CommandBox.registerCommand("teleport", tp);
		CommandBox.registerCommand("tp", tp);
		CommandBox.registerCommand("gravity", new GravityCommand(cameraEnt));
		FreeMoveCommand move = new FreeMoveCommand(camera);
		CommandBox.registerCommand("move", move);
		CommandBox.registerCommand("creative", move);
		
		DebugInfo.assignWorld(world);
	}
	
	@Override
	public void render() {
		this.world.render();
		SoundSystem.update();
		//TextureRenderer.renderTexture(this.world.getSSAOMap().getSSAOBluredTexture(), DisplayManager.WIDTH-512, 0, 512, 512);
		//TextureRenderer.renderTextureArray(this.world.getShadowMap().getDepthMapTexture(), 0, 0, 0, 256, 256);
		//TextureRenderer.renderTextureArray(this.world.getShadowMap().getDepthMapTexture(), 1, 256, 0, 256, 256);
		//TextureRenderer.renderTextureArray(this.world.getShadowMap().getDepthMapTexture(), 2, 0, 256, 256, 256);
		//TextureRenderer.renderTextureArray(this.world.getShadowMap().getDepthMapTexture(), 3, 256, 256, 256, 256);
	}
	
	long last = System.currentTimeMillis();
	long max = 60;
	
	@Override
	public void update() {
		this.world.update();
		this.rayCasting.update();
		if (Mouse.isMiddleClick() && System.currentTimeMillis() - last > max) {
			this.cameraEnt.shoot(rayCasting.getCurrentRay());
			last = System.currentTimeMillis();
		}
		this.cameraEnt.grab(rayCasting.getCurrentRay());
		this.ps.generateParticles(new Vector3f(0, 0, 0));
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

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
import com.trapdoor.engine.renderer.particles.systems.AnimatedParticleSystem;
import com.trapdoor.engine.renderer.ui.Console;
import com.trapdoor.engine.renderer.ui.DebugInfo;
import com.trapdoor.engine.renderer.ui.themes.AppleTheme;
import com.trapdoor.engine.tools.input.Mouse;
import com.trapdoor.engine.world.World;
import com.trapdoor.engine.world.entities.BouncingEntity;
import com.trapdoor.engine.world.entities.Entity;
import com.trapdoor.engine.world.entities.EntityCamera;
import com.trapdoor.engine.world.entities.EntitySpawner;
import com.trapdoor.engine.world.entities.tools.Weapon;
import com.trapdoor.engine.world.entities.tools.ai.EntityKentSpawnType;
import com.trapdoor.engine.world.sound.SoundSystem;
import com.trapdoor.engine.world.sound.SoundSystemType;

import imgui.ImGui;
import imgui.type.ImString;

/**
 * @author brett
 * @date Oct. 18, 2021
 * 
 */
public class TestDisplay extends IDisplay {
	
	
	//private ArrayList<Entity> e = new ArrayList<Entity>();
	private CreativeFirstPerson camera;
	private EntityCamera cameraEnt;
	private World world;
	private Model cubeModel;
	private ParticleSystem ps;
	private ParticleSystem smokey;
	
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
		GameRegistry.registerModel("resources/models/megacube.dae");
		
		GameRegistry.registerModel("resources/models/poop.dae");
		GameRegistry.registerModel("resources/models/greg.dae");
		GameRegistry.registerModel("resources/models/Mackenzie_Hallway_brt.dae");
		
		GameRegistry.registerSound("resources/sounds/penis.ogg");
		
		GameRegistry.registerSound("resources/sounds/music/how about some piano.ogg");
		GameRegistry.registerSound("resources/sounds/music/i don't really know eh 9.ogg");
		GameRegistry.registerSound("resources/sounds/music/im sorry.ogg");
		GameRegistry.registerSound("resources/sounds/music/omniworks.ogg");
		GameRegistry.registerSound("resources/sounds/music/rpd.ogg");
		GameRegistry.registerSound("resources/sounds/music/weapons of mass distraction remasted.ogg");
		GameRegistry.registerSound("resources/sounds/music/weapons of mass distraction.ogg");
		
		GameRegistry.registerParticleTexture("resources/textures/512_64.png",
											 "resources/textures/character Texture.png",
											 "resources/textures/particles/atlas/atlas_2.png",
											 "resources/textures/particles/atlas/atlas_7.png");
		
		GameRegistry.registerParticleTextureFolder("resources/textures/particles/fire/");
		GameRegistry.registerParticleTextureFolder("resources/textures/particles/smoke/");
		
		GameRegistry.registerParticleTextureFolder("resources/textures/particles/kenny/smoke/");
		GameRegistry.registerParticleTextureFolder("resources/textures/particles/kenny/star/");
		GameRegistry.registerParticleTextureFolder("resources/textures/particles/kenny/spark/");
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

		/*this.setSkyTextures(
				"resources/textures/skyboxes/mountain-skyboxes/Ryfjallet/posx.jpg", 	// right
				"resources/textures/skyboxes/mountain-skyboxes/Ryfjallet/negx.jpg", 	// left
				"resources/textures/skyboxes/mountain-skyboxes/Ryfjallet/posy.jpg", 	// top
				"resources/textures/skyboxes/mountain-skyboxes/Ryfjallet/negy.jpg", // bottom
				"resources/textures/skyboxes/mountain-skyboxes/Ryfjallet/posz.jpg", 	// front
				"resources/textures/skyboxes/mountain-skyboxes/Ryfjallet/negz.jpg"	// back
			);*/
		
		this.camera = new CreativeFirstPerson();
		this.world = new World(camera);

		// .setModel(GameRegistry.getModel("resources/models/playerblend.dae"))
		this.world.addEntityToWorld((cameraEnt = new EntityCamera(this.camera)).setModel(GameRegistry.getModel("resources/models/playerblend.dae")));
		
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
		this.world.addEntityToWorld(new Entity(1500).setModel(GameRegistry.getModel("resources/models/test object.dae")).setPosition(0, -15.0f, 0));
		this.world.addEntityToWorld(new Entity().setModel(cubeModel).setPosition(25, 0, 0).generateApproximateCollider());
		this.world.addEntityToWorld(new Entity().setModel(cubeModel).setPosition(-25, 0, 0).generateApproximateCollider());
		this.world.addEntityToWorld(new Entity().setModel(cubeModel).setPosition(0, 0, 25).generateApproximateCollider());
		this.world.addEntityToWorld(new Entity().setModel(cubeModel).setPosition(0, 0, -25).generateApproximateCollider());
		BouncingEntity rixie = new BouncingEntity(120);
		rixie.setModel(cubeModel);
		rixie.setPosition(-60, 5, 0);
		//rixie.getComponent(Transform.class).setScale(5, 5, 5);
		this.world.addEntityToWorld(rixie);
		this.world.addEntityToWorld(new EntityKent()
											.setModel(GameRegistry.getModel("resources/models/kent.dae"))
											.setPosition(25, -15, -40)
											.addLight(new Light(Light.lightings[5], 0, 1, 0)).generateApproximateCollider());
		this.world.addEntityToWorld(new EntityKent().setModel(GameRegistry.getModel("resources/models/zucc.dae")).setPosition(30, -15, -45));
		this.world.addEntityToWorld(
				new Entity()
					.setModel(GameRegistry.getModel("resources/models/hellolosers.obj"))
					.setPosition(5, 5, 5)
					.addLight(
							new Light(Light.lightings[6], 2.5f, 2.5f, 1.5f, -5, -5, -5)
							 )
					);
		this.world.addEntityToWorld(new EntityKent().setModel(GameRegistry.getModel("resources/models/kent.dae")).setPosition(00, 10, -50).generateApproximateCollider());
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
		new Kentipede(world, 35, 0, -35, 2, 4, 10, rixie);
		
//		Entity e = new Entity();
//		e.setModel(GameRegistry.getModel("resources/models/kent.dae"));
//		e.setPosition(0, 50, 0);
//		e.getComponent(Transform.class).setScale(20, 20, 20);
//		this.world.addEntityToWorld(e);
		
		ps = new AnimatedParticleSystem("resources/textures/particles/fire/", 100, 20, 0, 0.5f, 5);
		this.world.addParticleSystemToWorld(ps);
		smokey = new AnimatedParticleSystem("resources/textures/particles/smoke/", 100, 3, 0, 5, 10);
		this.world.addParticleSystemToWorld(smokey);
		
		this.world.addEntityToWorld(new EntitySpawner(
				new EntityKentSpawnType(rixie), 
				smokey,
				ps,
				12000, 15, 6)
					.setModel(GameRegistry.getModel("resources/models/spawner.dae"))
					.setPosition(75, -8, -25));
		
		this.world.addEntityToWorld(new Entity().setModel(GameRegistry.getModel("resources/models/greg.dae")).setPosition(-5, -18, 20));
	}

	@Override
	public void onSwitch() {
		// can be added to any screen, will just overwrite on load
		Console.registerCommand("raycast", new RayCastCommand(this.world.getRaycast()));
		TeleportCommand tp = new TeleportCommand(cameraEnt, camera);
		Console.registerCommand("teleport", tp);
		Console.registerCommand("tp", tp);
		Console.registerCommand("gravity", new GravityCommand(cameraEnt));
		FreeMoveCommand move = new FreeMoveCommand(camera);
		Console.registerCommand("move", move);
		Console.registerCommand("creative", move);
		Console.registerCommand("noclip", move);
		
		DebugInfo.assignWorld(world);
		
		new AppleTheme(true, 1.00f).applyStyle(ImGui.getStyle());
	}
	
	float[] flt = new float[1];
	ImString str = new ImString();
	
	@Override
	public void render() {
		ImGui.pushFont(GameRegistry.getFont("roboto-regular"));
		
		/*ImGui.begin("The Best Debug Menu");
		ImGui.beginChild("The child!", 256, 256);
		ImGui.text("Hello, World! " + FontAwesomeIcons.Angry);
        if (ImGui.button(FontAwesomeIcons.Save + " Save")) {
            System.out.println("hello!");
        }
        ImGui.sameLine();
        ImGui.text(String.valueOf(5));
        ImGui.inputText("string \uf556", str, ImGuiInputTextFlags.CallbackResize);
        ImGui.text("Result: " + str.get());
        ImGui.sliderFloat("float" + FontAwesomeIcons.Smile, flt, 0, 1);
        ImGui.separator();
        ImGui.text("Extra");
        //ImGui.image(this.world.getShadowMap().getDepthMapTexture(), 256, 256);
        ImGui.endChild();
        ImGui.end();*/
        
		
		this.world.render();
		SoundSystem.update();
		this.cameraEnt.getGreg().render();
		//TextureRenderer.renderTexture(this.world.getSSAOMap().getSSAOBluredTexture(), DisplayManager.WIDTH-512, 0, 512, 512);
		//TextureRenderer.renderTextureArray(this.world.getShadowMap().getDepthMapTexture(), 0, 0, 0, 256, 256);
		//TextureRenderer.renderTextureArray(this.world.getShadowMap().getDepthMapTexture(), 1, 256, 0, 256, 256);
		//TextureRenderer.renderTextureArray(this.world.getShadowMap().getDepthMapTexture(), 2, 0, 256, 256, 256);
		//TextureRenderer.renderTextureArray(this.world.getShadowMap().getDepthMapTexture(), 3, 256, 256, 256, 256);
		ImGui.popFont();
	}
	
	long last = System.currentTimeMillis();
	long max = 60;
	
	private boolean rightLastFrame = false;
	
	@Override
	public void update() {
		this.world.update();
		if (Mouse.isMiddleClick() && System.currentTimeMillis() - last > max) {
			this.cameraEnt.shoot(this.world.getRaycast().getCurrentRay());
			last = System.currentTimeMillis();
		}
		//if (Mouse.isLeftClick()) {
		//	this.cameraEnt.grab(rayCasting.getCurrentRay());
		//} else
		//	this.cameraEnt.notgrab();
		Weapon greg = this.cameraEnt.getGreg();
		if (Mouse.isLeftClick()) {
			greg.shoot();
		} else
			greg.shootN();
		
		if (Mouse.isRightClick()) {
			greg.alt();
			rightLastFrame = true;
		} else {
			if (rightLastFrame)
				greg.altRelease();
			rightLastFrame = false;
			greg.altN();
		}
		
		greg.update();
		
		
		this.ps.generateParticles(new Vector3f(0, 0, 0));
		
		this.ps.setDirection(new Vector3f(0.0f, 0.0f, -1.0f), 0.1f);
		
		this.ps.saveState(25, 5, this.ps.getGravityComplient(), 20, this.ps.getAverageScale());
		this.ps.generateParticles(new Vector3f(130, -10, 10));
		this.ps.restoreState();
		
		this.ps.setDirection(null, 0);
		
		this.smokey.generateParticles(new Vector3f(50, -8, 10));
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

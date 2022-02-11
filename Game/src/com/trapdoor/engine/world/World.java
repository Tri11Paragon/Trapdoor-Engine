package com.trapdoor.engine.world;

import java.util.ArrayList;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import com.karl.Engine.skybox.SkyboxRenderer;
import com.trapdoor.engine.camera.Camera;
import com.trapdoor.engine.datatypes.ogl.assimp.Model;
import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.registry.Threading;
import com.trapdoor.engine.renderer.DeferredRenderer;
import com.trapdoor.engine.renderer.EntityRenderer;
import com.trapdoor.engine.tools.Logging;
import com.trapdoor.engine.world.entities.Entity;

/**
 * @author brett
 * @date Dec. 13, 2021
 * Handles physics, world, rendering of world
 * etc.
 */
public class World {
	
	/*
	 * Physics variables 
	 */
	public static final float PLANE_BUFFER = 0.1f;
	
	private final WorldEntityStorage entityStorage;
	
	// Physics container
	private PhysicsSpace physWorld;
	
	/*
	 * everything else
	 */
	public static int entityCount;
	
	private Camera c;
	private EntityRenderer renderer;
	private SkyboxRenderer skyboxRenderer;
	private DeferredRenderer deferredRenderer;

	public World(Camera c) {
		// entitiesinworld is shared memory between the renderer and the world object.
		this.renderer = new EntityRenderer();
		this.deferredRenderer = new DeferredRenderer(c);
		this.skyboxRenderer = new SkyboxRenderer();
		this.c = c;
		this.entityStorage = new WorldEntityStorage(c, this.renderer);
		
		// setup physics
		PhysicsSpace.BroadphaseType bPhase = PhysicsSpace.BroadphaseType.DBVT;
        this.physWorld = new PhysicsSpace(bPhase);
        this.physWorld.setMaxSubSteps(1);
		this.physWorld.setGravity(new Vector3f(0.0f, 0.0f, 0.0f));
		
	}
	
	/**
	 * called by the main thread
	 */
	public void render() {
		this.c.render();
		
		DisplayManager.enableCulling();
		DisplayManager.disableTransparentcy(); 
		
		this.deferredRenderer.startFirstPass();
		this.skyboxRenderer.render(c);
		
		this.deferredRenderer.enableMainShaders();
		this.deferredRenderer.getShader().loadViewPos(this.c.getPosition());
		this.entityStorage.render(this.deferredRenderer);
		
		ArrayList<Entity> ents = this.entityStorage.getAllEntities();
		for (int i = 0; i < ents.size(); i++)
			ents.get(i).render();
		
		this.deferredRenderer.endFirstPass();
		
		this.deferredRenderer.runSecondPass();
		
		DisplayManager.disableCulling();
	}
	
	/**
	 * called by the physics thread
	 */
	public void update() {
		ArrayList<Entity> allEnts = this.entityStorage.getAllEntities();
		entityCount = allEnts.size();
		c.move();
		c.updateViewMatrix();
		// TODO: fix this
		//c.calculateFrustum(ProjectionMatrix.projectionMatrix, c.getViewMatrix());
		
		for (int i = 0; i < entityCount; i++) {
			Entity a = allEnts.get(i);
			a.update();
		}
		
		// calcualte the phys, stepped relative to the game speed
		// faster it is running the smaller the steps.
		try {
			physWorld.update((float) Threading.getFrameTimeSeconds());
		} catch (Exception e) {
			Logging.logger.fatal(e.getLocalizedMessage(), e);
			System.exit(-1);
		}
	}
	
	public void modelChanged(Entity e, Model old, Model n) {
		this.entityStorage.changeModel(e, old, n);
	}
	
	public void removeEntityPhysics(Entity e) {
		this.physWorld.remove(e.getRigidbody());
	}
	
	public void addEntityPhysics(Entity e) {
		this.physWorld.add(e.getRigidbody());
	}
	
	public void addEntityToWorld(Entity e) {
		this.entityStorage.addEntity(e);
		e.setWorld(this);
		this.physWorld.add(e.getRigidbody());
		e.onAddedToWorld();
	}
	
	public void removeEntityFromWorld(Entity e) {
		this.entityStorage.removeEntity(e);
		this.removeEntityPhysics(e);
		e.onRemovedFromWorld();
	}
	
	public Camera getCamera() {
		return c;
	}
	
	public void cleanup() {
		Logging.logger.debug("Destorying world!");
		this.deferredRenderer.cleanup();
	}
	
}

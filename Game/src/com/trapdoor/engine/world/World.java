package com.trapdoor.engine.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.joml.Vector3d;
import org.lwjgl.opengl.GL33;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.math.Vector3f;
import com.karl.Engine.skybox.SkyboxRenderer;
import com.trapdoor.engine.camera.Camera;
import com.trapdoor.engine.datatypes.DynamicArray;
import com.trapdoor.engine.datatypes.ogl.assimp.Model;
import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.registry.Threading;
import com.trapdoor.engine.renderer.DeferredRenderer;
import com.trapdoor.engine.renderer.EntityRenderer;
import com.trapdoor.engine.renderer.ao.SSAORenderer;
import com.trapdoor.engine.renderer.particles.ParticleRenderer;
import com.trapdoor.engine.renderer.particles.ParticleSystem;
import com.trapdoor.engine.renderer.postprocessing.BloomRenderer;
import com.trapdoor.engine.renderer.shadows.ShadowMap;
import com.trapdoor.engine.renderer.shadows.ShadowRenderer;
import com.trapdoor.engine.tools.Logging;
import com.trapdoor.engine.tools.SettingsLoader;
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
	private final Map<PhysicsCollisionObject, Entity> entityPhyiscsMap = Collections.synchronizedMap(new ConcurrentHashMap<PhysicsCollisionObject, Entity>());
	private final DynamicArray<ParticleSystem> particleSystems = new DynamicArray<ParticleSystem>();
	
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
	private ShadowRenderer shadowRenderer;
	private SSAORenderer ssaoRenderer;
	private BloomRenderer bloomRenderer;
	private ParticleRenderer particleRenderer;

	public World(Camera c) {
		// entitiesinworld is shared memory between the renderer and the world object.
		this.renderer = new EntityRenderer();
		this.deferredRenderer = new DeferredRenderer(c);
		this.skyboxRenderer = new SkyboxRenderer();
		this.c = c;
		this.entityStorage = new WorldEntityStorage(c, this.renderer);
		particleRenderer = new ParticleRenderer();
		if (SettingsLoader.GRAPHICS_LEVEL < 2) {
			this.shadowRenderer = new ShadowRenderer(c);
			this.ssaoRenderer = new SSAORenderer();
			this.bloomRenderer = new BloomRenderer();
		}
		
		// setup physics
        this.physWorld = new PhysicsSpace(PhysicsSpace.BroadphaseType.DBVT);
        //this.physWorld.setMaxSubSteps(0);
        this.physWorld.useDeterministicDispatch(false);
        this.physWorld.useScr(false);
        // this helps/
        //this.physWorld.setAccuracy(1f/120f);
        
        this.physWorld.addCollisionListener((PhysicsCollisionEvent event) -> {
        	Entity e1 = entityPhyiscsMap.get(event.getObjectA());
        	Entity e2 = entityPhyiscsMap.get(event.getObjectB());
        	e1.onCollision(e2, event);
        	e2.onCollision(e1, event);
        });
        this.physWorld.addOngoingCollisionListener((PhysicsCollisionEvent event) -> {
        	Entity e1 = entityPhyiscsMap.get(event.getObjectA());
        	Entity e2 = entityPhyiscsMap.get(event.getObjectB());
        	e1.onOngoingCollision(e2, event);
        	e2.onOngoingCollision(e1, event);
        });
		//this.physWorld.setGravity(new Vector3f(0.0f, 0.0f, 0.0f));
		
	}
	
	/**
	 * called by the main thread
	 */
	public void render() {
		this.c.render();
		
		DisplayManager.enableCulling();
		DisplayManager.disableTransparentcy(); 
		
		if (SettingsLoader.GRAPHICS_LEVEL <  2 && DisplayManager.enableShadows) {
			this.shadowRenderer.renderDepthMap(c, entityStorage);
			
			GL33.glViewport(0, 0, DisplayManager.WIDTH, DisplayManager.HEIGHT);
		}
		
		this.deferredRenderer.startFirstPass(this);
		this.deferredRenderer.enableMainShaders();
		this.deferredRenderer.getShader().loadViewPos(this.c.getPosition());
		this.entityStorage.render(this.deferredRenderer);
		
		ArrayList<Entity> ents = this.entityStorage.getAllEntities();
		for (int i = 0; i < ents.size(); i++)
			ents.get(i).render();
		
		this.skyboxRenderer.render(c);
		this.deferredRenderer.endFirstPass();
		
		if (SettingsLoader.GRAPHICS_LEVEL < 2) {
			ssaoRenderer.render(this.deferredRenderer);
		}
		
		// TODO: add bloomy option
		if (SettingsLoader.GRAPHICS_LEVEL < 2) {
			this.bloomRenderer.bindBloom();
		}
		
		this.deferredRenderer.runSecondPass(this.ssaoRenderer);
		for (int i = 0; i < particleSystems.size(); i++) {
			particleSystems.get(i).update();
		}
		particleRenderer.update(this, c);
		this.particleRenderer.render(this, c);
		
		if (SettingsLoader.GRAPHICS_LEVEL < 2) {
			this.bloomRenderer.applyBlur(this.deferredRenderer);
			this.bloomRenderer.render(this.deferredRenderer);
		}
		
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
		
		for (int i = 0; i < allEnts.size(); i++) {
			Entity a = allEnts.get(i);
			a.update();
		}
		
		// calcualte the phys, stepped relative to the game speed
		// faster it is running the smaller the steps.
		try {
			physWorld.update((float) Threading.getFrameTimeSeconds());
			physWorld.distributeEvents();
		} catch (Exception e) {
			Logging.logger.fatal(e.getLocalizedMessage(), e);
			System.exit(-1);
		}
	}
	
	private final Vector3f v1 = new Vector3f();
	private final Vector3f v2 = new Vector3f();
	private final List<PhysicsRayTestResult> otherResults = new ArrayList<PhysicsRayTestResult>();
	public List<PhysicsRayTestResult> raycast(org.joml.Vector3f ray, float distance) {
		Vector3d pos = c.getPosition();
		v1.x = (float) pos.x;
		v1.y = (float) pos.y;
		v1.z = (float) pos.z;
		
		v2.x = v1.x + ray.x * distance;
		v2.y = v1.y + ray.y * distance;
		v2.z = v1.z + ray.z * distance;
		
		return physWorld.rayTestRaw(v1, v2, otherResults);
	}
	
	private final Vector3f s1 = new Vector3f();
	private final Vector3f s2 = new Vector3f();
	private final List<PhysicsRayTestResult> results = new ArrayList<PhysicsRayTestResult>();
	public List<PhysicsRayTestResult> raycastSorted(org.joml.Vector3f ray, float distance) {
		Vector3d pos = c.getPosition();
		s1.x = (float) pos.x;
		s1.y = (float) pos.y;
		s1.z = (float) pos.z;
		
		s2.x = s1.x + ray.x * distance;
		s2.y = s1.y + ray.y * distance;
		s2.z = s1.z + ray.z * distance;
		
		return physWorld.rayTest(s1, s2, results);
	}
	
	public void modelChanged(Entity e, Model old, Model n) {
		this.entityStorage.changeModel(e, old, n);
	}
	
	public void removeEntityPhysics(Entity e) {
		if (e.usingRigidBody())
			this.physWorld.remove(e.getRigidbody());
		else
			this.physWorld.remove(e.getCollisionObject());
		entityPhyiscsMap.remove(e.getCollisionObject());
	}
	
	public void addEntityPhysics(Entity e) {
		//TODO: i think this check can be removed
		if (e.usingRigidBody()) {
			this.physWorld.add(e.getRigidbody());
			entityPhyiscsMap.put(e.getRigidbody(), e);
		} else {
			this.physWorld.add(e.getCollisionObject());
			entityPhyiscsMap.put(e.getCollisionObject(), e);
		}
	}
	
	public void addEntityToWorld(Entity e) {
		e.setWorld(this);
		
		this.addEntityPhysics(e);
		
		this.entityStorage.addEntity(e);
		e.onAddedToWorld();
	}
	
	public void addParticleSystemToWorld(ParticleSystem s) {
		s.create(this.particleRenderer, this);
		particleSystems.add(s);
	}
	
	public void removeParticleSystemFromWorld(ParticleSystem s) {
		particleSystems.remove(s);
	}
	
	public void removeEntityFromWorld(Entity e) {
		this.entityStorage.removeEntity(e);
		this.removeEntityPhysics(e);
		e.onRemovedFromWorld();
	}
	
	public Entity getEntity(PhysicsCollisionObject obj) {
		return entityPhyiscsMap.get(obj);
	}
	
	public Camera getCamera() {
		return c;
	}
	
	public int getParticleCount() {
		return particleRenderer.getStorage().size();
	}
	public int getParticleSize() {
		return particleRenderer.getStorage().size();
	}
	
	public ShadowMap getShadowMap() {
		return shadowRenderer.getShadowMap();
	}
	
	public SSAORenderer getSSAOMap() {
		return ssaoRenderer;
	}
	
	public Vector3f getGravity() {
		return physWorld.getGravity(new Vector3f());
	}
	
	public void cleanup() {
		Logging.logger.debug("Destorying world!");
		this.deferredRenderer.cleanup();
		this.shadowRenderer.cleanup();
		this.ssaoRenderer.cleanup();
		this.bloomRenderer.cleanup();
		this.particleRenderer.cleanUp();
	}
	
}

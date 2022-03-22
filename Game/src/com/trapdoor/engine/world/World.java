package com.trapdoor.engine.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.math.Vector3f;
import com.karl.Engine.skybox.SkyboxRenderer;
import com.trapdoor.engine.ProjectionMatrix;
import com.trapdoor.engine.UBOLoader;
import com.trapdoor.engine.camera.Camera;
import com.trapdoor.engine.datatypes.DynamicArray;
import com.trapdoor.engine.datatypes.ogl.assimp.Model;
import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.registry.Threading;
import com.trapdoor.engine.renderer.DepthPassRenderer;
import com.trapdoor.engine.renderer.MaterialPassRenderer;
import com.trapdoor.engine.renderer.functions.DepthRenderFunction;
import com.trapdoor.engine.renderer.functions.EntityRenderFunction;
import com.trapdoor.engine.renderer.functions.ShadowRenderFunction;
import com.trapdoor.engine.renderer.particles.ParticleRenderer;
import com.trapdoor.engine.renderer.particles.ParticleSystem;
import com.trapdoor.engine.renderer.postprocessing.BloomRenderer;
import com.trapdoor.engine.renderer.shadows.ShadowMap;
import com.trapdoor.engine.renderer.shadows.ShadowRenderer;
import com.trapdoor.engine.renderer.ui.DebugInfo;
import com.trapdoor.engine.renderer.ui.OptionsMenu;
import com.trapdoor.engine.tools.Logging;
import com.trapdoor.engine.tools.RayCasting;
import com.trapdoor.engine.tools.SettingsLoader;
import com.trapdoor.engine.world.entities.Entity;
import com.trapdoor.engine.world.entities.components.Transform;

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
	private RayCasting raycast;
	
	/*
	 * everything else
	 */
	public static int entityCount;
	
	private Camera c;
	private SkyboxRenderer skyboxRenderer;
	//private DeferredRenderer deferredRenderer;
	private ShadowRenderer shadowRenderer;
	private BloomRenderer bloomRenderer;
	private ParticleRenderer particleRenderer;
	
	private DepthPassRenderer depthRenderer;
	private MaterialPassRenderer materialRenderer;
	
	// render functions
	private DepthRenderFunction depthRenderFunction;
	private EntityRenderFunction entityRenderFunction;
	private ShadowRenderFunction shadowRenderFunction;

	@SuppressWarnings("deprecation")
	public World(Camera c) {
		UBOLoader.createLightingUBO();
		// entitiesinworld is shared memory between the renderer and the world object.
		//this.deferredRenderer = new DeferredRenderer(c);
		this.depthRenderer = new DepthPassRenderer();
		this.materialRenderer = new MaterialPassRenderer(c);
		this.skyboxRenderer = new SkyboxRenderer();
		this.c = c;
		this.entityStorage = new WorldEntityStorage(c);
		this.particleRenderer = new ParticleRenderer();
		if (SettingsLoader.GRAPHICS_LEVEL < 2) {
			this.shadowRenderer = new ShadowRenderer(c);
			this.bloomRenderer = new BloomRenderer();
			this.shadowRenderFunction = new ShadowRenderFunction(this.shadowRenderer.getShader());
		}
		this.depthRenderFunction = new DepthRenderFunction(this.depthRenderer.getShader());
		this.entityRenderFunction = new EntityRenderFunction(this.materialRenderer.getShader(), this.materialRenderer.getLightingArray());
		
		// setup physics
        this.physWorld = new PhysicsSpace(PhysicsSpace.BroadphaseType.DBVT);
        //this.physWorld.setMaxSubSteps(0);
        this.physWorld.useDeterministicDispatch(false);
        this.physWorld.useScr(false);
        // this helps/
        this.physWorld.setAccuracy(1f/120f);
        
        this.physWorld.addCollisionListener((PhysicsCollisionEvent event) -> {
        	Entity e1 = entityPhyiscsMap.get(event.getObjectA());
        	Entity e2 = entityPhyiscsMap.get(event.getObjectB());
        	if (e1 == null || e2 == null)
        		return;
        	e1.onCollision(e2, event);
        	e2.onCollision(e1, event);
        });
        this.physWorld.addOngoingCollisionListener((PhysicsCollisionEvent event) -> {
        	Entity e1 = entityPhyiscsMap.get(event.getObjectA());
        	Entity e2 = entityPhyiscsMap.get(event.getObjectB());
        	if (e1 == null || e2 == null)
        		return;
        	e1.onOngoingCollision(e2, event);
        	e2.onOngoingCollision(e1, event);
        });
		//this.physWorld.setGravity(new Vector3f(0.0f, 0.0f, 0.0f));
        raycast = new RayCasting(c, this);
	}
	
	/**
	 * called by the main thread
	 */
	public void render() {
		this.c.render();
		UBOLoader.updateMatrixUBO();
		
		DisplayManager.enableCulling();
		DisplayManager.disableTransparentcy(); 
		
		if (SettingsLoader.GRAPHICS_LEVEL <  2 && DisplayManager.enableShadows) {
			this.shadowRenderer.renderDepthMap(c, entityStorage, this.shadowRenderFunction);
			
			GL33.glViewport(0, 0, DisplayManager.WIDTH, DisplayManager.HEIGHT);
		}
		
		// TODO: add bloomy option
		if (SettingsLoader.GRAPHICS_LEVEL < 1) {
			this.bloomRenderer.bindBloom();
		}
		
		if (DebugInfo.enableLines.get())
			GL33.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL33.GL_LINE);
		
		// render depth pass
		this.depthRenderer.start();
		this.entityStorage.render(this.depthRenderFunction);
		this.depthRenderer.stop();
		
		// render material pass
		GL33.glDepthFunc(GL33.GL_EQUAL);
		
		this.materialRenderer.start();
		if (SettingsLoader.GRAPHICS_LEVEL < 2) {
			GL33.glActiveTexture(GL33.GL_TEXTURE5);
			GL33.glBindTexture(GL33.GL_TEXTURE_2D_ARRAY, this.getShadowMap().getDepthMapTexture());
		}
		this.entityStorage.render(this.entityRenderFunction);
		
		ArrayList<Entity> ents = this.entityStorage.getAllEntities();
		for (int i = 0; i < ents.size(); i++)
			ents.get(i).render();
		
		if (DebugInfo.enableLines.get())
			GL33.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL33.GL_FILL);
		
		this.materialRenderer.end();
		
		GL33.glDepthFunc(GL33.GL_LEQUAL);
		
		this.skyboxRenderer.render(c);
		for (int i = 0; i < particleSystems.size(); i++) {
			particleSystems.get(i).update();
		}
		particleRenderer.update(this, c);
		this.particleRenderer.render(this, c);
		
		if (SettingsLoader.GRAPHICS_LEVEL < 1) {
			this.bloomRenderer.applyBlur();
			this.bloomRenderer.render();
		}
		
		DisplayManager.disableCulling();
		EntityRenderFunction.reset();
		
		OptionsMenu.menu.render();
	}
	
	/**
	 * called by the physics thread
	 */
	@SuppressWarnings("deprecation")
	public void update() {
		ArrayList<Entity> allEnts = this.entityStorage.getAllEntities();
		entityCount = allEnts.size();
		c.move();
		c.updateViewMatrix();
		c.calculateFrustum(ProjectionMatrix.projectionMatrix, c.getViewMatrix());
		// TODO: fix this
		//c.calculateFrustum(ProjectionMatrix.projectionMatrix, c.getViewMatrix());
		
		for (int i = 0; i < allEnts.size(); i++) {
			Entity a = allEnts.get(i);
			a.update();
			Transform t = a.getComponent(Transform.class);
			if (t == null)
				continue;
			t.updateDistanceToCamera(c);
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
		raycast.update();
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
	
	public List<PhysicsRayTestResult> raycast(float distance) {
		return raycast(this.raycast.getCurrentRay(), distance);
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
	
	public List<PhysicsRayTestResult> raycastSorted(float distance) {
		return raycast(this.raycast.getCurrentRay(), distance);
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
	
	public Vector3f getGravity() {
		return physWorld.getGravity(new Vector3f());
	}
	
	public RayCasting getRaycast() {
		return raycast;
	}
	
	public void cleanup() {
		Logging.logger.debug("Destorying world!");
		//this.deferredRenderer.cleanup();
		this.entityRenderFunction.cleanup();
		try {
			this.shadowRenderer.cleanup();
			this.bloomRenderer.cleanup();
		} catch (Exception e) {
			Logging.logger.error(e.getMessage(), e);
		}
		this.particleRenderer.cleanUp();
	}
	
}

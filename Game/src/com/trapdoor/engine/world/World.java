package com.trapdoor.engine.world;

import java.util.ArrayList;

import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
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
	
	public static final CollisionShape flatPlaneCollisionShape = new StaticPlaneShape(new Vector3f(0, 1, 0), PLANE_BUFFER);
	public static final CollisionShape defaultSphereShape = new SphereShape(1.0f);
	public static final CollisionShape defaultEntityShape = new BoxShape(new Vector3f(0.5f, 0.5f, 0.5f));
	
	private final WorldEntityStorage entityStorage;
	
	// Physics container
	private DiscreteDynamicsWorld physWorld;
	
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
		BroadphaseInterface broadphase = new DbvtBroadphase();
		CollisionConfiguration collisionConfig = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfig);
		ConstraintSolver solver = new SequentialImpulseConstraintSolver();
		
		this.physWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfig);
		this.physWorld.setGravity(new Vector3f(0.0f, -9.8f, 0.0f));
		
	}
	
	/**
	 * called by the main thread
	 */
	public void render() {
		//this.c.move();
		
		DisplayManager.enableCulling();
		DisplayManager.disableTransparentcy(); 
		
		this.deferredRenderer.startFirstPass();
		this.skyboxRenderer.render(c);
		
		this.deferredRenderer.enableMainShaders();
		this.deferredRenderer.getShader().loadViewPos(this.c.getPosition());
		this.entityStorage.render(this.deferredRenderer);
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
		physWorld.stepSimulation((float) Threading.getFrameTimeSeconds());
	}
	
	public void modelChanged(Entity e, Model old, Model n) {
		this.entityStorage.changeModel(e, old, n);
	}
	
	public void removeEntityPhysics(Entity e) {
		this.physWorld.removeRigidBody(e.getRigidbody());
	}
	
	public void addEntityPhysics(Entity e) {
		this.physWorld.addRigidBody(e.getRigidbody());
	}
	
	public void addEntityToWorld(Entity e) {
		this.entityStorage.addEntity(e);
		e.setWorld(this);
		this.physWorld.addRigidBody(e.getRigidbody());
	}
	
	public void removeEntityFromWorld(Entity e) {
		this.entityStorage.removeEntity(e);
		this.removeEntityPhysics(e);
	}
	
	public Camera getCamera() {
		return c;
	}
	
	public void cleanup() {
		Logging.logger.debug("Destorying world!");
		this.deferredRenderer.cleanup();
	}
	
}

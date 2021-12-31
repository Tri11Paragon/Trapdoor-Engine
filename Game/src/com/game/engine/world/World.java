package com.game.engine.world;

import java.util.ArrayList;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
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
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.game.engine.camera.Camera;
import com.game.engine.datatypes.util.NdHashMap;
import com.game.engine.display.DisplayManager;
import com.game.engine.renderer.EntityRenderer;
import com.game.engine.renderer.SyncSave;
import com.game.engine.world.entities.Entity;
import com.game.engine.world.entities.EntityCamera;

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
	
	// fuzzy world pos (static entities only, ie non-movings)
	private NdHashMap<Integer, ArrayList<Entity>> entitiesAtPos = new NdHashMap<Integer, ArrayList<Entity>>();
	// Physics container
	private DiscreteDynamicsWorld physWorld;
	
	/*
	 * everything else
	 */
	private static long lastFrameTime;
	private static double delta;
	private static double frameTimeMs,frameTimeS;
	private static double fps;
	public static int entityCount;
	
	private static Thread physics;
	
	private Camera c;
	private EntityRenderer renderer;
	
	private ArrayList<Entity> entitiesInWorld = new ArrayList<Entity>();
	
	public World(Camera c) {
		// entitiesinworld is shared memory between the renderer and the world object.
		this.renderer = new EntityRenderer(c, entitiesInWorld);
		this.c = c;
		
		// setup physics
		BroadphaseInterface broadphase = new DbvtBroadphase();
		CollisionConfiguration collisionConfig = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfig);
		ConstraintSolver solver = new SequentialImpulseConstraintSolver();
		
		this.physWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfig);
		this.physWorld.setGravity(new Vector3f(0.0f, 0, 0.0f));
		
		World w = this;
		
		this.addEntityToWorld(new EntityCamera(c).setRigidbody(new RigidBody(50, new DefaultMotionState(
				new Transform(
						new Matrix4f(
								// rotation
								new Quat4f(0,0,0,1),
								// position, + w
								new Vector3f(0,0,0), 1.0f
								)
						)
				), new BoxShape(new Vector3f(0.75f, 0.75f, 0.75f)))));
		
		if (physics != null)
			throw new RuntimeException("Hey for some reason multiple worlds are attempting to be created. This is not currently supported.");
		DisplayManager.createdThreads++;
		physics = new Thread(() -> {
			while (DisplayManager.displayOpen) {
				w.update();
				c.move();
				c.updateViewMatrix();
				
				// TODO: update this mess
				// hhahahahhahahha
				// it's so fucking stupid and shit
				// you gotta love it
				// also its late so cut me some slack
				
				for (int i = 0; i < entitiesInWorld.size(); i++) {
					Entity a = entitiesInWorld.get(i);
					a.update();
					
					// only need to check collision on an entity if it has updated its position right
					/*if (a.isUpdated()) {
						// store the last safe position
						float x=a.getLX(),y=a.getLY(),z=a.getLZ();
						// get the position we want to move to
						float mx=a.getNX(),my=a.getNY(),mz=a.getNZ();
						
						float dx = mx-a.getX(),dy=my-a.getY(),dz=mz-a.getZ();
						float stepX = dx/MAX_STEPS,stepY = dy/MAX_STEPS,stepZ = dz/MAX_STEPS;
						
						// if we are not changing position we want to make sure we are not colliding by existing. this resolves that
						if (dx == 0 && dy == 0 && dz == 0) {
							for (int j = 0; j < entitiesInWorld.size(); j++) {
								Entity b = entitiesInWorld.get(j);
								if (a == b)
									continue;
								boolean restored = false;
								
								while (true) {
									// get the updated collider each loop
									AABB bbA = a.getCollider();
									AABB bbB = b.getCollider();
									// break the while loop if is not colliding
									if (!bbA.intersects(bbB))
										break;
									
									// get the current position
									float cx=a.getX(),cy=a.getY(),cz=a.getZ();
									
									// if the current position is the last position and we are still colliding
									// then we have a problem
									if (x == cx && y == cy && z == cz) {
										restored = true;
									}
									if (restored) {
										Vector3d bbBc = bbB.getCenterSafe();
										Vector3d bbAc = bbA.getCenterSafe();
										bbAc.sub(bbBc, dist);
										
										// all distances are equal, so move in smallest direction
										if (dist.x == 0 && dist.y == 0 && dist.z == 0) {
											final float f = 5.0f;
											if (a.getSx() < a.getSy())
												dist.x = f * getFrameTimeSeconds();
											else if (a.getSy() < a.getSz())
												dist.y = f * getFrameTimeSeconds();
											else
												dist.z = f * getFrameTimeSeconds();
										}
										a.addPositionWorld((float)dist.x, (float)dist.y, (float)dist.z);
									}
								}
							}
						} else {
							// oh boy we hate the cpu
							// this really needs to be redone lol
							for (int cu = 0; cu < MAX_STEPS; cu++) {
								// have fun with this gay function
								
								// otherwise we are moving and need to safely apply it
								a.addPositionWorld(stepX, stepY, stepZ);
								for (int j = 0; j < entitiesInWorld.size(); j++) {
									Entity b = entitiesInWorld.get(j);
									if (a == b)
										continue;
									AABB bbA = a.getCollider();
									AABB bbB = b.getCollider();
									if (!bbA.intersects(bbB))
										continue;
									else {
										// this isn't perfect but is better then checking each axis individually
										// as that is much more expensive
										// first check if its the change in x that is causing this collision
										a.addPositionWorld(-stepX, 0, 0);
										bbA = a.getCollider();
										// if it isn't then undo and move to y
										if (bbA.intersects(bbB)) {
											a.addPositionWorld(stepX, 0, 0);
											
											// check y
											a.addPositionWorld(0, -stepY, 0);
											bbA = a.getCollider();
											if (bbA.intersects(bbB)) {
												a.addPositionWorld(0, stepY, 0);
												
												// if not y check z
												a.addPositionWorld(0, 0, -stepZ);
												bbA = a.getCollider();
												if (bbA.intersects(bbB)) {
													// this should never be reached
													a.addPositionWorld(0, 0, stepZ);
												}
											}
										}
									}
								}
							}
						}
						// once we have applied the position change, reset the N vector
						a.updateNCoords();
						a.clearUpdate();
					}*/
				}
				
				// calcualte the phys, stepped relative to the game speed
				// faster it is running the smaller the steps.
				physWorld.stepSimulation((float) getFrameTimeSeconds());
				
				SyncSave.syncPhy(DisplayManager.FPS_MAX);
				
				long currentFrameTime = System.nanoTime();
				delta = currentFrameTime - lastFrameTime;
				lastFrameTime = currentFrameTime;
				frameTimeMs = delta / 1000000d;
				frameTimeS = delta / 1000000000d;
				fps = 1000d/frameTimeMs;
			}
			System.out.println("Physics thread exiting! ");
			DisplayManager.exited++;
		});
		physics.start();
	}
	
	/**
	 * called by the main thread
	 */
	public void render() {
		//this.c.move();
		this.renderer.render();
	}
	
	/**
	 * called by the physics thread
	 */
	public void update() {
		entityCount = entitiesInWorld.size();
	}
	
	@Deprecated
	public void addStaticEntityToWorld(Entity e) {
		this.entitiesInWorld.add(e);
		e.setWorld(this);
		// stores the entity at its integer position allowing for easy access of entities at a pos
		int x = (int)e.getX(),y = (int)e.getY(),z = (int)e.getZ();
		ArrayList<Entity> el = this.entitiesAtPos.get(x, y, z);
		if (el == null) {
			el = new ArrayList<Entity>();
			this.entitiesAtPos.set(x, y, z, el);
		}
		el.add(e);
		this.physWorld.addRigidBody(e.getRigidbody());
	}
	
	public void removeEntityPhysics(Entity e) {
		this.physWorld.removeRigidBody(e.getRigidbody());
	}
	
	public void addEntityPhysics(Entity e) {
		this.physWorld.addRigidBody(e.getRigidbody());
	}
	
	public void addEntityToWorld(Entity e) {
		this.entitiesInWorld.add(e);
		e.setWorld(this);
		this.physWorld.addRigidBody(e.getRigidbody());
	}
	
	public Camera getCamera() {
		return c;
	}
	
	public static double getFrameTimeMilis() {
		return frameTimeMs;
	}

	public static double getFrameTimeSeconds() {
		return frameTimeS;
	}
	
	public static double getFPS() {
		return fps;
	}
	
}

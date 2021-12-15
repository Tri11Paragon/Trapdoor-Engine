package com.game.engine.world;

import java.util.ArrayList;

import com.game.engine.camera.Camera;
import com.game.engine.datatypes.util.NdHashMap;
import com.game.engine.display.DisplayManager;
import com.game.engine.renderer.EntityRenderer;
import com.game.engine.renderer.SyncSave;

/**
 * @author brett
 * @date Dec. 13, 2021
 * Handles physics, world, rendering of world
 * etc.
 */
public class World {

	private static long lastFrameTime;
	private static double delta;
	private static double frameTimeMs,frameTimeS;
	private static double fps;
	public static int entityCount;
	
	private static Thread physics;
	
	private Camera c;
	private EntityRenderer renderer;
	
	private ArrayList<Entity> entitiesInWorld = new ArrayList<Entity>();
	// fuzzy world pos
	private NdHashMap<Integer, ArrayList<Entity>> entitiesAtPos = new NdHashMap<Integer, ArrayList<Entity>>();
	
	public World(Camera c) {
		// entitiesinworld is shared memory between the renderer and the world object.
		this.renderer = new EntityRenderer(c, entitiesInWorld);
		this.c = c;
		
		World w = this;
		
		if (physics != null)
			throw new RuntimeException("Hey for some reason multiple worlds are attempting to be created. This is not currently supported.");
		physics = new Thread(() -> {
			while (DisplayManager.displayOpen) {
				w.update();
				
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
		this.c.move();
		this.renderer.render();
	}
	
	/**
	 * called by the physics thread
	 */
	public void update() {
		entityCount = entitiesInWorld.size();
	}
	
	public void addEntityToWorld(Entity e) {
		this.entitiesInWorld.add(e);
		// stores the entity at its integer position allowing for easy access of entities at a pos
		int x = (int)e.getX(),y = (int)e.getY(),z = (int)e.getZ();
		ArrayList<Entity> el = this.entitiesAtPos.get(x, y, z);
		if (el == null) {
			el = new ArrayList<Entity>();
			el.add(e);
			this.entitiesAtPos.set(x, y, z, el);
		}
		el.add(e);
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

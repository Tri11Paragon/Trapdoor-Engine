package com.game.engine.world;

import java.util.ArrayList;

import com.game.engine.camera.Camera;
import com.game.engine.datatypes.util.NdHashMap;
import com.game.engine.renderer.EntityRenderer;

/**
 * @author brett
 * @date Dec. 13, 2021
 * Handles physics, world, rendering of world
 * etc.
 */
public class World {

	private Camera c;
	private EntityRenderer renderer;
	
	private ArrayList<Entity> entitiesInWorld = new ArrayList<Entity>();
	// fuzzy world pos
	private NdHashMap<Integer, ArrayList<Entity>> entitiesAtPos = new NdHashMap<Integer, ArrayList<Entity>>();
	
	public World(Camera c) {
		// entitiesinworld is shared memory between the renderer and the world object.
		this.renderer = new EntityRenderer(c, entitiesInWorld);
		this.c = c;
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
	
}

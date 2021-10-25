package com.game.engine.world;

import java.util.ArrayList;
import java.util.HashMap;

import com.game.engine.datatypes.world.Entity;

/**
 * @author brett
 * @date Oct. 25, 2021
 * 
 */
public class World {
	
	private static int lastID = 0;
	private static final ArrayList<Entity> entities = new ArrayList<Entity>();
	private static final HashMap<Integer, Entity> worldEntities = new HashMap<Integer, Entity>();
	
	/**
	 * Creates a world based on the input tile size
	 * @param tx tile size x
	 * @param ty tile size y
	 */
	public static void init(int tx, int ty) {
		
	}
	
	public static void update() {
		for (int i = 0; i < entities.size(); i++) {
			entities.get(i).onUpdate();
		}
	}
	
	public static boolean checkCollision(Entity e) {
		return false;
	}
	
	public static void addEntity(Entity e) {
		e.setWorldID(lastID);
		worldEntities.put(lastID, e);
		lastID++;
		entities.add(e);
	}
	
	public static void deleteEntity(Entity e) {
		entities.remove(e);
		worldEntities.remove(e.getWorldID());
	}
	
	public static void deleteAllEntities() {
		entities.clear();
		worldEntities.clear();
	}
	
	
}

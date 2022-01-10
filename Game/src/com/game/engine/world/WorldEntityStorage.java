package com.game.engine.world;

import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Vector3d;

import com.game.engine.camera.Camera;
import com.game.engine.datatypes.ogl.assimp.Model;
import com.game.engine.datatypes.util.NdHashMap;
import com.game.engine.renderer.EntityRenderer;
import com.game.engine.tools.SettingsLoader;
import com.game.engine.world.entities.Entity;

/**
 * @author brett
 * @date Jan. 8, 2022
 * 
 */
public class WorldEntityStorage {
	
	// list of entities which don't move
	private NdHashMap<Integer, Entity> staticEntities = new NdHashMap<Integer, Entity>();
	// total list of all entities
	private ArrayList<Entity> entities = new ArrayList<Entity>();
	
	/* Renderers */
	private HashMap<Model, ArrayList<Entity>> mappedEntities = new HashMap<Model, ArrayList<Entity>>();
	
	/* chunking */
	private NdHashMap<Integer, WorldChunk> chunks = new NdHashMap<Integer, WorldChunk>();
	
	
	private Camera camera;
	private EntityRenderer renderer;
	
	public WorldEntityStorage(Camera camera, EntityRenderer renderer) {
		this.camera = camera;
		this.renderer = renderer;
	}
	
	public void changeModel(Model old, Entity e) {
		// TODO?
	}
	
	public void render() {
		this.renderer.prepare();
		final int f = SettingsLoader.RENDER_DISTANCE;
		Vector3d pos = camera.getPosition();
		for (int i = -f; i < f; i++) {
			for (int j = -f; j < f; j++) {
				for (int k = -f; k < f; k++) {
					int x = (int)pos.x;
					int y = (int)pos.y;
					int z = (int)pos.z;
					
					int cx = (x >> 5) + i;
					int cy = (y >> 5) + j;
					int cz = (z >> 5) + k;
					
					WorldChunk c = this.chunks.get(cx, cy, cz);
					
					if (c != null)
						c.render(i, j, k);
				}
			}
		}
	}
	
	public void addEntity(Entity e) {
		if (e.isStatic()) {
			staticEntities.set((int) e.getX(), (int) e.getY(), (int) e.getZ(), e);
			
			int x = (int)e.getX();
			int y = (int)e.getY();
			int z = (int)e.getZ();
			
			int cx = x >> 5;
			int cy = y >> 5;
			int cz = z >> 5;
			
			WorldChunk c = this.chunks.get(cx, cy, cz);
			
			if (c == null) {
				c = new WorldChunk(renderer, cx, cy, cz);
				this.chunks.set(cx, cy, cz, c);
			}
			
			c.addEntity(e);
			entities.add(e);
			
		} else {
			ArrayList<Entity> ents = this.mappedEntities.get(e.getModel());
			if (ents == null) {
				ents = new ArrayList<Entity>();
				this.mappedEntities.put(e.getModel(), ents);
			}
			ents.add(e);
			entities.add(e);
			}
	}
	
	public void removeEntity(Entity e) {
		if (e.isStatic()) {
			staticEntities.remove((int) e.getX(), (int) e.getY(), (int) e.getZ());
			
			int x = (int)e.getX();
			int y = (int)e.getY();
			int z = (int)e.getZ();
			
			int cx = x >> 5;
			int cy = y >> 5;
			int cz = z >> 5;
			
			WorldChunk c = this.chunks.get(cx, cy, cz);
			
			if (c != null)
				c.removeEntity(e);
			
		} else {
			ArrayList<Entity> ents = this.mappedEntities.get(e.getModel());
			if (ents != null)
				ents.remove(e);
			entities.remove(e);
		}
	}
	
	public WorldChunk getEntityChunk(float x, float y, float z) {
		return this.getChunk( ((int)x) >> 5, ((int)y) >> 5, ((int) z) >> 5);
	}
	
	public WorldChunk getChunk(float x, float y, float z) {
		return getChunk((int) x, (int) y, (int) z);
	}
	
	public WorldChunk getChunk(int x, int y, int z) {
		return this.chunks.get(x , y, z);
	}
	
	public HashMap<Model, ArrayList<Entity>> getMappedEntities(){
		return mappedEntities;
	}
	
	public ArrayList<Entity> getAllEntities(){
		return entities;
	}
}

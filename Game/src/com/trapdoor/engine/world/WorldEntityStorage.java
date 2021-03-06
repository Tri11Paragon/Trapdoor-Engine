package com.trapdoor.engine.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.joml.Vector3d;
import org.joml.Vector4f;

import com.trapdoor.engine.camera.Camera;
import com.trapdoor.engine.datatypes.ogl.assimp.Model;
import com.trapdoor.engine.datatypes.util.NdHashMap;
import com.trapdoor.engine.renderer.functions.RenderFunction;
import com.trapdoor.engine.tools.SettingsLoader;
import com.trapdoor.engine.world.entities.Entity;
import com.trapdoor.engine.world.entities.components.Transform;

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
	private Vector4f store = new Vector4f(1.0f);
	
	public WorldEntityStorage(Camera camera) {
		this.camera = camera;
	}
	
	public void changeModel(Entity e, Model old, Model n) {
		if (e.isStatic()) {
			Transform t = (Transform)e.getComponent(Transform.class);
			int x = (int)t.getX();
			int y = (int)t.getY();
			int z = (int)t.getZ();
			
			int cx = x >> 5;
			int cy = y >> 5;
			int cz = z >> 5;
			
			WorldChunk c = this.chunks.get(cx, cy, cz);
			
			if (c != null)
				c.changeModel(e, old, n);
		} else {
			ArrayList<Entity> ents = this.mappedEntities.get(old);
			if (ents != null)
				ents.remove(e);
			ents = this.mappedEntities.get(n);
			if (ents == null) {
				ents = new ArrayList<Entity>();
				this.mappedEntities.put(n, ents);
			}
			ents.add(e);
		}
	}
	
	public void sort() {
		chunks.iterate((NdHashMap<Integer, WorldChunk> dt, Integer k1, Integer k2, Integer k3, WorldChunk v1) -> {
			v1.updateDistance(camera);
		});
	}
	
	public void render(RenderFunction render) {
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
					
					store.x = cx * WorldChunk.CHUNK_SIZE;
					store.y = cy * WorldChunk.CHUNK_SIZE;
					store.z = cz * WorldChunk.CHUNK_SIZE;
					
					WorldChunk c = this.chunks.get(cx, cy, cz);
					
					if (c != null)
						c.render(render, camera, i, j, k);
				}
			}
		}
		
		Iterator<Entry<Model, ArrayList<Entity>>> iter = mappedEntities.entrySet().iterator();
		
		while (iter.hasNext()) {
			Entry<Model, ArrayList<Entity>> entry = iter.next();
			ArrayList<Entity> lis = entry.getValue();
			Model m = entry.getKey();
			
			if (m == null)
				continue;
			
			render.render(m, lis, camera);
		}
	}
	
	public void addEntity(Entity e) {
		if (e.isStatic()) {
			Transform t = (Transform)e.getComponent(Transform.class);
			staticEntities.set((int) t.getX(), (int) t.getY(), (int) t.getZ(), e);
			
			int x = (int)t.getX();
			int y = (int)t.getY();
			int z = (int)t.getZ();
			
			int cx = x >> 5;
			int cy = y >> 5;
			int cz = z >> 5;
			
			WorldChunk c = this.chunks.get(cx, cy, cz);
			
			if (c == null) {
				c = new WorldChunk(cx, cy, cz);
				this.chunks.set(cx, cy, cz, c);
			}
			
			c.addEntity(e);
			
		} else {
			ArrayList<Entity> ents = this.mappedEntities.get(e.getModel());
			if (ents == null) {
				ents = new ArrayList<Entity>();
				this.mappedEntities.put(e.getModel(), ents);
			}
			ents.add(e);
		}
		entities.add(e);
	}
	
	public void removeEntity(Entity e) {
		if (e.isStatic()) {
			Transform t = (Transform)e.getComponent(Transform.class);
			staticEntities.remove((int) t.getX(), (int) t.getY(), (int) t.getZ());
			
			int x = (int)t.getX();
			int y = (int)t.getY();
			int z = (int)t.getZ();
			
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
		}
		entities.remove(e);
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

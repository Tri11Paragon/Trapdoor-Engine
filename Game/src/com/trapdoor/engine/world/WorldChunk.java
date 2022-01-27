package com.trapdoor.engine.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.trapdoor.engine.datatypes.ogl.assimp.Model;
import com.trapdoor.engine.renderer.DeferredRenderer;
import com.trapdoor.engine.renderer.EntityRenderer;
import com.trapdoor.engine.world.entities.Entity;

/**
 * @author brett
 * @date Jan. 9, 2022
 * 
 */
public class WorldChunk {
	
	public static int CHUNK_SIZE = 32;
	
	private final ArrayList<Entity> entities = new ArrayList<Entity>();
	private final HashMap<Model, ArrayList<Entity>> entityMap = new HashMap<Model, ArrayList<Entity>>();
	
	private final int cx, cy, cz;
	
	private EntityRenderer renderer;
	
	public WorldChunk(EntityRenderer renderer, int cx, int cy, int cz) {
		this.cx = cx;
		this.cy = cy;
		this.cz = cz;
		this.renderer = renderer;
	}
	
	public void changeModel(Entity e, Model old, Model n) {
		ArrayList<Entity> ents = this.entityMap.get(old);
		if (ents != null)
			ents.remove(e);
		ents = this.entityMap.get(n);
		if (ents == null) {
			ents = new ArrayList<Entity>();
			this.entityMap.put(n, ents);
		}
		ents.add(e);
	}
	
	public void render(DeferredRenderer render, int i, int j, int k) {
		
		Iterator<Entry<Model, ArrayList<Entity>>> iter = entityMap.entrySet().iterator();
		
		while (iter.hasNext()) {
			Entry<Model, ArrayList<Entity>> entry = iter.next();
			ArrayList<Entity> lis = entry.getValue();
			Model m = entry.getKey();
			
			if (m == null)
				continue;
			
			renderer.renderChunk(render, m, lis);
		}
	}
	
	public void addEntity(Entity e) {
		ArrayList<Entity> ents = this.entityMap.get(e.getModel());
		if (ents == null) {
			ents = new ArrayList<Entity>();
			this.entityMap.put(e.getModel(), ents);
		}
		ents.add(e);
		entities.add(e);
	}
	
	public void removeEntity(Entity e) {
		ArrayList<Entity> ents = this.entityMap.get(e.getModel());
		if (ents != null)
			ents.remove(e);
		entities.remove(e);
	}
	
	public ArrayList<Entity> getEntities(){
		return entities;
	}
	
	public HashMap<Model, ArrayList<Entity>> getEntityMap(){
		return entityMap;
	}
	
	public int getChunkX() {
		return this.cx;
	}
	
	public int getChunkY() {
		return this.cy;
	}
	
	public int getChunkZ() {
		return this.cz;
	}
	
}

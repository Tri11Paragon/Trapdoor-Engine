package com.game.engine.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.game.engine.datatypes.ogl.assimp.Model;
import com.game.engine.renderer.EntityRenderer;
import com.game.engine.world.entities.Entity;

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
	
	public void render(int i, int j, int k) {
		//Vector3d pos = camera.getPosition();
		//float fx = (((int) (pos.x / CHUNK_SIZE)) + i) * CHUNK_SIZE;
		//float fy = (((int) (pos.y / CHUNK_SIZE)) + j) * CHUNK_SIZE;
		//float fz = (((int) (pos.z / CHUNK_SIZE)) + k) * CHUNK_SIZE;
		//if (!camera.cubeInFrustum(fx, fy, fz, fx + CHUNK_SIZE, fy + CHUNK_SIZE, fz + CHUNK_SIZE))
		//	return;
		
		Iterator<Entry<Model, ArrayList<Entity>>> iter = entityMap.entrySet().iterator();
		
		while (iter.hasNext()) {
			Entry<Model, ArrayList<Entity>> entry = iter.next();
			ArrayList<Entity> lis = entry.getValue();
			Model m = entry.getKey();
			
			if (m == null)
				continue;
			
			renderer.renderChunk(m, lis);
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

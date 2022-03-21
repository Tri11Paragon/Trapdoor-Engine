package com.trapdoor.engine.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.joml.Vector3d;

import com.trapdoor.engine.camera.Camera;
import com.trapdoor.engine.datatypes.ogl.assimp.Model;
import com.trapdoor.engine.renderer.functions.RenderFunction;
import com.trapdoor.engine.world.entities.Entity;

/**
 * @author brett
 * @date Jan. 9, 2022
 * 
 */
public class WorldChunk {
	
	public static final int CHUNK_SIZE = 32;
	
	private final ArrayList<Entity> entities = new ArrayList<Entity>();
	private final HashMap<Model, ArrayList<Entity>> entityMap = new HashMap<Model, ArrayList<Entity>>();
	
	private final int cx, cy, cz;
	
	private float distance;
	
	public WorldChunk(int cx, int cy, int cz) {
		this.cx = cx;
		this.cy = cy;
		this.cz = cz;
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
	
	public void render(RenderFunction render, Camera camera, int i, int j, int k) {
		
		Iterator<Entry<Model, ArrayList<Entity>>> iter = entityMap.entrySet().iterator();
		
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
	
	public void updateDistance(Camera c) {
		Vector3d pos = c.getPosition();
		float dx = (float) (pos.x - this.cx);
		float dy = (float) (pos.y - this.cy);
		float dz = (float) (pos.z - this.cz);
		this.distance = dx * dx + dy * dy + dz * dz;
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
	
	public float getDistance() {
		return distance;
	}
	
}

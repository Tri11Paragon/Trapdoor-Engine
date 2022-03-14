package com.trapdoor.engine.world.entities.tools.ai;

import com.trapdoor.engine.world.entities.Entity;

/**
 * @author brett
 * @date Mar. 8, 2022
 * 
 */
public abstract class EntitySpawnType {
	
	private Entity e;
	
	public EntitySpawnType(Entity e) {
		this.e = e;
	}
	
	public abstract Entity spawnEntity(float x, float y, float z);
	
	public Entity getRepresentedEntity() {
		return e;
	}
	
}

package com.trapdoor.engine.world.entities.components;

import com.trapdoor.engine.world.entities.Entity;

/**
 * @author brett
 * @date Jan. 27, 2022
 * 
 */
public abstract class IComponent {
	
	private final int id;
	protected Entity e;
	
	public IComponent() {
		id = ComponentManager.leaseID();
	}
	
	// called by renderer
	public abstract void render();
	
	// called by physics
	public abstract void update();
	
	public void setAssociatedEntity(Entity e) {;
		this.e = e;
	}
	
	public Entity associatedEntity() {
		return this.e;
	}
	
	// component id
	public int getID() {
		return id;
	}
	
}

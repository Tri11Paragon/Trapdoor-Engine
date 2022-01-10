package com.game.engine.world.entities.components;

import com.game.engine.world.World;
import com.game.engine.world.entities.Entity;

/**
 * @author brett
 * @date Dec. 22, 2021
 * 
 */
public abstract class Component {
	
	public static final String TYPE_COLLIDER = "collider";
	
	protected Entity associatedEntity;
	protected World world;
	private boolean enabled = true;
	protected String type;
	
	public Component(String type) {
		this.type = type.toLowerCase();
	}
	
	public void componentAddedToEntity(Entity e, World world) {
		this.associatedEntity = e;
		this.world = world;
	}
	
	// called on render tick
	public abstract void render();
	
	// called on physics tick
	public abstract void update();
	
	public Entity getAssociatedEntity() {
		return associatedEntity;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public Component setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}
	
	public String getType() {
		return type;
	}
	
}

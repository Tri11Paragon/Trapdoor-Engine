package com.trapdoor.engine.world.entities;

import com.trapdoor.engine.display.DisplayManager;

/**
 * @author brett
 * @date Mar. 1, 2022
 * 
 */
public class SelfDeletingEntity extends Entity {
	
	private float lifetime;
	private float currTime;
	
	// lifetime is in ms
	public SelfDeletingEntity(float mass, float lifetime) {
		super (mass);
		this.lifetime = lifetime;
	}
	
	@Override
	public void update() {
		super.update();
		currTime += DisplayManager.getFrameTimeMilis();
		if (currTime > lifetime)
			this.world.removeEntityFromWorld(this);
	}
	
}

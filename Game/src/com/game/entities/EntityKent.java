package com.game.entities;

import com.trapdoor.engine.world.entities.Entity;
import com.trapdoor.engine.world.entities.components.Transform;

public class EntityKent extends Entity{
	
	private float yaw;
	private Transform t;
	
	public EntityKent() {
		super(5);
		this.yaw = 0;
		this.t = (Transform) this.getComponent(Transform.class);
	}
	
	@Override
	public void update() {
		super.update(); // need this for overriding functions
		this.t.setRotation(this.yaw, this.t.getPitch(), this.t.getRoll());
		this.yaw += 0.01;
	}
	
}

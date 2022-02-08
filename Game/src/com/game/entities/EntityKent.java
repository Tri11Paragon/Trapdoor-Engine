package com.game.entities;

import com.trapdoor.engine.world.entities.Entity;
import com.trapdoor.engine.world.entities.components.Transform;

public class EntityKent extends Entity{
	
	private float yaw, pitch, roll;
	private Transform t;
	
	public EntityKent() {
		super(5);
		this.yaw = 0;
		this.t = (Transform) this.getComponent(Transform.class);
	}
	
	@Override
	public void update() {
		super.update(); // need this for overriding functions
		this.t.setRotation(this.yaw, this.pitch, this.roll);
		this.yaw += 0.01;
		//this.pitch += 0.02;
		//this.roll += 0.03;
	}
	
}

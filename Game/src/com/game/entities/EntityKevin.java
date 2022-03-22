package com.game.entities;

import com.jme3.math.Vector3f;
import com.trapdoor.engine.tools.input.Keyboard;
import com.trapdoor.engine.world.entities.Entity;
import com.trapdoor.engine.world.entities.components.Transform;

public class EntityKevin extends Entity {
	
	private Transform t;
	private float x, y, z, speed = 2000;
	
	public EntityKevin() {
		super(10);
		this.t = (Transform) this.getComponent(Transform.class);
	}
	
	public EntityKevin(int x, int y, int z) {
		super(100);
		this.t = (Transform) this.getComponent(Transform.class);
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public void onAddedToWorld() {
		super.onAddedToWorld();
//		this.getRigidbody().setGravity(new Vector3f(0, 0, 0)); // Sets gravity to 0
//		this.getRigidbody().setLinearVelocity(new Vector3f(0, 1000, 0)); // Sets velocity at start
	}
	
	@Override
	public void update() {
		super.update(); // need this for overriding functions
		
//		this.t.setPosition(this.x, this.y, this.z);
		
		if (Keyboard.isKeyDown(Keyboard.W))
			this.applyCentralForce(0, 0, -this.speed);
		if (Keyboard.isKeyDown(Keyboard.S))
			this.applyCentralForce(0, 0, this.speed);
		if (Keyboard.isKeyDown(Keyboard.A))
			this.applyCentralForce(-this.speed, 0, 0);
		if (Keyboard.isKeyDown(Keyboard.D))
			this.applyCentralForce(this.speed, 0, 0);
		
	}
	
}

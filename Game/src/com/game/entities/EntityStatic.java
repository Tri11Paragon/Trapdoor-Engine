package com.game.entities;

import com.jme3.math.Vector3f;
import com.trapdoor.engine.tools.input.Keyboard;
import com.trapdoor.engine.world.entities.Entity;
import com.trapdoor.engine.world.entities.components.Transform;

public class EntityStatic extends Entity {
	
	private Transform t;
	
	public EntityStatic() {
		super(0);
		this.t = (Transform) this.getComponent(Transform.class);
	}
	
	@Override
	public void onAddedToWorld() {
		super.onAddedToWorld();
		this.getRigidbody().setGravity(new Vector3f(0, 0, 0)); // Sets gravity to 0
//		this.getRigidbody().setLinearVelocity(new Vector3f(0, 1000, 0)); // Sets velocity at start
	}
	
}

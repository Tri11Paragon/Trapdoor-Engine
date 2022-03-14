package com.game.entities;

import com.jme3.math.Vector3f;
import com.trapdoor.engine.tools.input.Keyboard;
import com.trapdoor.engine.world.entities.Entity;
import com.trapdoor.engine.world.entities.components.Transform;

public class EntityBall extends Entity {
	
	private Transform t;
	private float p = 0, x, y, z;
	private boolean dir = true;
	
	private final float zero = 6.325f;
	
	public EntityBall() {
		super(0);
		this.t = (Transform) this.getComponent(Transform.class);
	}
	
	public EntityBall(int z) {
		super(0);
		this.t = (Transform) this.getComponent(Transform.class);
		this.z = z;
	}
	
	@Override
	public void onAddedToWorld() {
		super.onAddedToWorld();
		this.getRigidbody().setGravity(new Vector3f(0, 0, 0)); // Sets gravity to 0
//		this.getRigidbody().setLinearVelocity(new Vector3f(0, 1000, 0)); // Sets velocity at start
	}
	
	@Override
	public void update() {
		super.update(); // need this for overriding functions
		
		this.t.setPosition(this.x, this.y, this.z);
		
		if (this.p > zero) {
			dir = false;
			this.p = zero;
		}
		else if (this.p < -zero) {
			dir = true;
			this.p = -zero;
		}
		
		if (this.dir)
			this.p += 0.1f;
		else
			this.p -= 0.1f;
		
		this.x = this.p;
		this.y = (float) (-0.5 * this.x * this.x + 20);
	}
	
}

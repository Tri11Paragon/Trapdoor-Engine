package com.game.game.entities;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.game.engine.world.entities.Entity;

public class EntityPoop extends Entity{
	
	public EntityPoop() {
		super();
		RigidBody a = new RigidBody(1, new DefaultMotionState(new Transform()), new SphereShape(5));
		this.setRigidbody(a);
	}
	
	@Override
	public void update() {
		super.update(); // need this for overriding functions
		this.setLinearVelocity(5, 0, 5);
	}
	
}

package com.trapdoor.game.entities;

import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.trapdoor.engine.tools.input.Keyboard;
import com.trapdoor.engine.world.entities.Entity;

public class EntityPoop extends Entity{
	
	public EntityPoop() {
		super();
		RigidBody a = new RigidBody(10, new DefaultMotionState(new Transform()), new SphereShape(2));
		this.setRigidbody(a);
	}
	
	@Override
	public void update() {
		super.update(); // need this for overriding functions
		if (Keyboard.isKeyDown(Keyboard.Q))
			this.applyCentralForce(100, 0, 0);
	}
	
}

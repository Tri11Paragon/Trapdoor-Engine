package com.game.entities;

import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.trapdoor.engine.registry.Threading;
import com.trapdoor.engine.tools.input.Keyboard;
import com.trapdoor.engine.world.entities.Entity;

public class EntityPiss extends Entity{
	
	public EntityPiss() {
		super();
		RigidBody a = new RigidBody(10, new DefaultMotionState(new Transform()), new SphereShape(10));
		this.setRigidbody(a);
	}
	
	@Override
	public void update() {
		super.update(); // need this for overriding functions
		if (Keyboard.isKeyDown(Keyboard.P))
			this.applyCentralForce(100, 0, 0);
		if (Keyboard.isKeyDown(Keyboard.I))
			this.setSz((float) (this.getSz() + 0.1f * Threading.getFrameTimeSeconds()));
	}
	
}

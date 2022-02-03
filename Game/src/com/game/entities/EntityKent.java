package com.game.entities;

import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.trapdoor.engine.registry.Threading;
import com.trapdoor.engine.tools.input.Keyboard;
import com.trapdoor.engine.world.entities.Entity;

public class EntityKent extends Entity{
	
	float yaw;
	
	public EntityKent() {
		super(5);
		this.yaw = 0;
	}
	
	@Override
	public void update() {
		super.update(); // need this for overriding functions
		this.setYaw(this.yaw);
		this.yaw += 0.01;
	}
	
}

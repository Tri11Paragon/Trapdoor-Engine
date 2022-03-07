package com.game.entities;

import com.trapdoor.engine.tools.input.Keyboard;
import com.trapdoor.engine.world.entities.Entity;
import com.trapdoor.engine.world.entities.components.Transform;

public class EntityGolfBall extends Entity {
	
	private Transform t;
	
	public EntityGolfBall() {
		super();
		this.t = (Transform) getComponent(Transform.class);
	}
	
	@Override
	public void update() {
		super.update(); // need this for overriding functions
		
	}
	
}

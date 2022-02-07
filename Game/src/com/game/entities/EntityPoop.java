package com.game.entities;

import com.trapdoor.engine.registry.Threading;
import com.trapdoor.engine.tools.input.Keyboard;
import com.trapdoor.engine.world.entities.Entity;
import com.trapdoor.engine.world.entities.components.Transform;

public class EntityPoop extends Entity{
	
	private Transform t;
	
	public EntityPoop() {
		super();
		this.t = (Transform) getComponent(Transform.class);
	}
	
	@Override
	public void update() {
		super.update(); // need this for overriding functions
		if (Keyboard.isKeyDown(Keyboard.Q))
			this.applyCentralForce(100, 0, 0);
		if (Keyboard.isKeyDown(Keyboard.E))
			this.t.setScale(1.0f, 1.0f, (float) (this.t.getScaleZ() + 0.1f * Threading.getFrameTimeSeconds()));
	}
	
}

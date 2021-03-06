package com.game.entities;

import com.trapdoor.engine.camera.Camera;
import com.trapdoor.engine.tools.input.Keyboard;
import com.trapdoor.engine.tools.input.Mouse;
import com.trapdoor.engine.world.entities.EntityCamera;

public class SloshyEntityCamera extends EntityCamera{
	
	public SloshyEntityCamera(Camera c) {
		super(c);
		ch.setJumpSpeed(15.0f);
	}
	
	@Override
	public void move() {
		if (!Mouse.isGrabbed())
			return;
				
		float speed = 1;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			moveAtX -= speed;
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			moveAtX += speed;
		}
		else {
			if (moveAtX != 0) {
				moveAtX += -Math.signum(moveAtX) * speed;
			}
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			moveAtZ += speed;
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			moveAtZ -= speed;
		}
		else {
			if (moveAtZ != 0) {
				moveAtZ += -Math.signum(moveAtZ) * (speed / 2);
			}
		}
		
		float dx = (float) ((((-((moveAtX) * Math.sin(Math.toRadians(c.getYaw()))  )) + -((moveAtZ) * Math.cos(Math.toRadians(c.getYaw())) ))) );
		float dz = (float) ( (((moveAtX) * Math.cos(Math.toRadians(c.getYaw()))  ) + -((moveAtZ) * Math.sin(Math.toRadians(c.getYaw())) )) );
		
		store.x = dx / 100;
		store.y = 0;
		store.z = dz / 100;
		ch.setWalkDirection(store);
		
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) && ch.onGround()) {
			ch.jump();
		}
	}

}

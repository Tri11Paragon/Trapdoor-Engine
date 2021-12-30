package com.game.engine.world.entities;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import com.game.engine.camera.Camera;
import com.game.engine.renderer.ui.DebugInfo;
import com.game.engine.tools.input.Keyboard;
import com.game.engine.tools.input.Mouse;

/**
 * @author brett
 * @date Dec. 20, 2021
 * 
 */
public class EntityCamera extends Entity {
	
	private static float speed = 40f;
	@SuppressWarnings("unused")
	private static final int RECUR_AMT = 100;
	
	private float moveAtX = 0;
	private float moveAtY = 0;
	private float moveatZ = 0;
	
	private Vector3f pos;
	
	private Camera c;
	
	public EntityCamera(Camera c) {
		this.c = c;
		pos = new Vector3f();
	}
	
	@Override
	public void update() {
		super.update();
		this.setYaw(c.getYaw());
		this.setPitch(c.getPitch());
		this.setRoll(c.getRoll());
		move();
		this.pos.x = this.getX();
		this.pos.y = this.getY();
		this.pos.z = this.getZ();
		DebugInfo.x = this.pos.x;
		DebugInfo.y = this.pos.y;
		DebugInfo.z = this.pos.z;
		c.setPosition(pos);
	}
	
	private void move() {
		if (!Mouse.isGrabbed())
			return;
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_ALT))
			speed = 5f;
		else if (Keyboard.isKeyDown(Keyboard.L_CONTROL))
			speed=150f;
		else
			speed = 40f;
		
		final float timeConstant = (float) 1;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W))
			moveAtX = (-speed * timeConstant);
		
		else if (Keyboard.isKeyDown(Keyboard.KEY_S))
			moveAtX = (speed * timeConstant);
		else
			moveAtX = 0;
			
		if (Keyboard.isKeyDown(Keyboard.KEY_A))
			moveatZ = (speed * timeConstant);
		else
		if (Keyboard.isKeyDown(Keyboard.KEY_D))
			moveatZ = (-speed * timeConstant);
		else 
			moveatZ = 0;

		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))
			moveAtY = (speed * timeConstant);
		else
			moveAtY = 0;
			
		if (Keyboard.isKeyDown(Keyboard.LEFT_SHIFT))
			moveAtY = (-speed * timeConstant);
		
		float dx = (float) ((((-((moveAtX) * Math.sin(Math.toRadians(c.getYaw()))  )) + -((moveatZ) * Math.cos(Math.toRadians(c.getYaw())) ))) );
		float dy = moveAtY;
		float dz = (float) ( (((moveAtX) * Math.cos(Math.toRadians(c.getYaw()))  ) + -((moveatZ) * Math.sin(Math.toRadians(c.getYaw())) )) );
		
		applyWithoutBreakingVelocity(dx, dy, dz);
		
	}
	
	//private final javax.vecmath.Vector3f vel = new javax.vecmath.Vector3f();
	public void applyWithoutBreakingVelocity(float x, float y, float z) {
		// TODO: this;
		/*vel.x = 0;
		vel.y = 0;
		vel.z = 0;
		this.getRigidbody().getLinearVelocity(vel);
		float vx = vel.x + x, vy = vel.y + y, vz = vel.z + z;
		
		if (vx >= x)
			vel.x = x;
		if (vy >= y)
			vel.y = y;
		if (vz >= z)
			vel.z = z;
		
		this.getRigidbody().setLinearVelocity(vel);*/
		this.setLinearVelocity(x, y, z);
	}
	
}

package com.game.engine.world.entities;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import com.game.engine.camera.Camera;
import com.game.engine.display.DisplayManager;
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
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W))
			moveAtX = (float) (-speed * DisplayManager.getFrameTimeSeconds());
		
		else if (Keyboard.isKeyDown(Keyboard.KEY_S))
			moveAtX = (float) (speed * DisplayManager.getFrameTimeSeconds());
		else
			moveAtX = 0;
			
		if (Keyboard.isKeyDown(Keyboard.KEY_A))
			moveatZ = (float) (speed * DisplayManager.getFrameTimeSeconds());
		else
		if (Keyboard.isKeyDown(Keyboard.KEY_D))
			moveatZ = (float) (-speed * DisplayManager.getFrameTimeSeconds());
		else 
			moveatZ = 0;

		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))
			moveAtY = (float) (speed * DisplayManager.getFrameTimeSeconds());
		else
			moveAtY = 0;
			
		if (Keyboard.isKeyDown(Keyboard.LEFT_SHIFT))
			moveAtY = (float) (-speed * DisplayManager.getFrameTimeSeconds());
		
		float dx = (float) ((((-((moveAtX) * Math.sin(Math.toRadians(c.getYaw()))  )) + -((moveatZ) * Math.cos(Math.toRadians(c.getYaw())) ))) );
		float dy = moveAtY;
		float dz = (float) ( (((moveAtX) * Math.cos(Math.toRadians(c.getYaw()))  ) + -((moveatZ) * Math.sin(Math.toRadians(c.getYaw())) )) );
		
		/*float xStep = (dx)/RECUR_AMT;
		float yStep = (dy)/RECUR_AMT;
		float zStep = (dz)/RECUR_AMT;
		
		float wx = 0, wy = 0, wz = 0;
		float xb = 0, yb = 0, zb = 0;

		for (int i = 0; i < RECUR_AMT; i++) {
			wx += xStep;
			if (world.chunk.getBlock(position.x + (wx), position.y, position.z) == 0) {
				xb = wx;
			} else
				break;
		}
		for (int i = 0; i < RECUR_AMT; i++) {
			wy += yStep;
			if (world.chunk.getBlock(position.x, position.y + (wy), position.z) == 0) {
				yb = wy;
			} else
				break;
		}
		for (int i = 0; i < RECUR_AMT; i++) {
			wz += zStep;
			if (world.chunk.getBlock(position.x, position.y, position.z + (wz)) == 0) {
				zb = wz;
			} else 
				break;
		}*/
		this.addPosition(dx, dy, dz);
	}
	
}

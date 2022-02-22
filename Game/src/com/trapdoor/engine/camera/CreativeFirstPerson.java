package com.trapdoor.engine.camera;

import org.lwjgl.glfw.GLFW;

import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.tools.SettingsLoader;
import com.trapdoor.engine.tools.input.Keyboard;
import com.trapdoor.engine.tools.input.Mouse;

/**
 * @author laptop
 * @date Nov. 21, 2021
 * 
 */
public class CreativeFirstPerson extends Camera{
	
	private static float speed = 40f;
	private static final float turnSpeedY = 5f;
	private static final float turnSpeedX = 4.5f;
	@SuppressWarnings("unused")
	private static final int RECUR_AMT = 100;
	
	private float moveAtX = 0;
	private float moveAtY = 0;
	private float moveatZ = 0;
	
	public boolean allowFreeMovement = false;
	
	@Override
	public void render() {
		if (Mouse.isGrabbed()) {
			pitch += Mouse.getDY() * (turnSpeedY * SettingsLoader.SENSITIVITY_Y)/100;
			yaw += Mouse.getDX() * (turnSpeedX * SettingsLoader.SENSITIVITY_X)/100;
		}
	}
	
	@Override
	public void move() {
		if (!Mouse.isGrabbed())
			return;
		final float speedd = 30f;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
			yaw += -speedd * turnSpeedX * DisplayManager.getFrameTimeSeconds();
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
			yaw += speedd * turnSpeedX * DisplayManager.getFrameTimeSeconds();
		if (Keyboard.isKeyDown(Keyboard.KEY_UP))
			pitch += -speedd * turnSpeedY * DisplayManager.getFrameTimeSeconds();
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
			pitch += speedd * turnSpeedY * DisplayManager.getFrameTimeSeconds();
		
		if (this.pitch > 90)
			this.pitch = 90;
		if (this.pitch < -90)
			this.pitch = -90;
		if (this.yaw < -360)
			this.yaw = 0;
		if (this.yaw > 360)
			this.yaw = 0;

	}
	
	public void move2() {
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
		
		// TODO: remove this shit
		double prez = 1000000d;
		float dx = (float) (Math.round((((-((moveAtX) * Math.round(Math.sin(Math.toRadians(yaw)) * prez) / prez)) + -((moveatZ) * Math.round(Math.cos(Math.toRadians(yaw))*prez)/prez)))*prez)/prez);
		float dy = (float) ((((moveAtX * (Math.sin(Math.toRadians(roll)))) + moveAtY)));
		float dz = (float) (Math.round((((moveAtX) * Math.round(Math.cos(Math.toRadians(yaw)) * prez)/prez) + -((moveatZ) * Math.round(Math.sin(Math.toRadians(yaw))*prez)/prez))*prez)/prez);
		
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
		
		//if (world.chunk.getBlock(position.x + (xb), position.y, position.z) == 0)
			position.x += dx;
		
		//if (world.chunk.getBlock(position.x, position.y + (yb), position.z) == 0)
			position.y += dy;
		
		//if (world.chunk.getBlock(position.x , position.y, position.z + (zb)) == 0)
			position.z += dz;
	}
	
}

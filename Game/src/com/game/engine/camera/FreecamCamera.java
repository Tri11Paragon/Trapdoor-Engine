package com.game.engine.camera;

import org.lwjgl.glfw.GLFW;

import com.game.engine.display.DisplayManager;
import com.game.engine.tools.input.Keyboard;

/**
 * @author brett
 * @date Oct. 18, 2021
 * 
 */
public class FreecamCamera extends Camera {

	public FreecamCamera() {
		//this.position.z = -20;
		//this.yaw = 25;
	}
	
	private float speed = 150;
	
	@Override
	public void move() {
		super.move();
		if (Keyboard.isKeyDown(Keyboard.W)) {
			this.position.y -= speed * DisplayManager.getFrameTimeSeconds();
		}
		if (Keyboard.isKeyDown(Keyboard.S)) {
			this.position.y += speed * DisplayManager.getFrameTimeSeconds();
		}
		if (Keyboard.isKeyDown(Keyboard.A)) {
			this.position.x -= speed * DisplayManager.getFrameTimeSeconds();
		}
		if (Keyboard.isKeyDown(Keyboard.D)) {
			this.position.x += speed * DisplayManager.getFrameTimeSeconds();
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_UP)) {
			this.pitch += speed * DisplayManager.getFrameTimeSeconds();
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_DOWN)) {
			this.pitch -= speed * DisplayManager.getFrameTimeSeconds();
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT)) {
			this.yaw -= speed * DisplayManager.getFrameTimeSeconds();
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_RIGHT))
			this.yaw += speed * DisplayManager.getFrameTimeSeconds();
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_COMMA)) {
			this.position.z -= speed * DisplayManager.getFrameTimeSeconds();
		}
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_PERIOD)) {
			this.position.z += speed * DisplayManager.getFrameTimeSeconds();
		}
	}
	
}

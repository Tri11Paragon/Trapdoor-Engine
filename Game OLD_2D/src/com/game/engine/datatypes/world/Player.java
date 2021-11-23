package com.game.engine.datatypes.world;

import com.game.engine.camera.Camera;
import com.game.engine.display.DisplayManager;
import com.game.engine.tools.input.Keyboard;

/**
 * @author brett
 * @date Oct. 25, 2021
 * 
 */
public class Player extends Entity {

	private Camera c;
	private float movementSpeed = 150;
	private String oldTexture;
	private float zoom = 1;
	@SuppressWarnings("unused")
	private float zoomSpeed = 150;

	public Player(Camera c, float width, float height, String texture) {
		super(0, 0, width, height, texture);
		this.c = c;
		this.oldTexture = texture;
	}
	
	@Override
	public Entity addPosition(float x, float y) {
		this.setTexture("diamond.png");
		return super.addPosition(x, y);
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		c.setX(this.x() - DisplayManager.WIDTH/2 + this.getWidth()/2);
		c.setY(this.y() - DisplayManager.HEIGHT/2 + this.getHeight()/2);
		c.setZ(zoom);
		
		this.setTexture(oldTexture);
		if (Keyboard.isKeyDown(Keyboard.W)) {
			this.addPosition(0, (float) (-movementSpeed * DisplayManager.getFrameTimeSeconds()));
		}
		if (Keyboard.isKeyDown(Keyboard.S)) {
			this.addPosition(0, (float) (movementSpeed * DisplayManager.getFrameTimeSeconds()));
		}
		if (Keyboard.isKeyDown(Keyboard.A)) {
			this.addPosition((float) (-movementSpeed * DisplayManager.getFrameTimeSeconds()), 0);
		}
		if (Keyboard.isKeyDown(Keyboard.D)) {
			this.addPosition((float) (movementSpeed * DisplayManager.getFrameTimeSeconds()), 0);
		}
		//if (InputMaster.scrolledLastFrame) {
			//zoom += zoomSpeed * InputMaster.lastScrollState * DisplayManager.getFrameTimeSeconds();
		//}
	}
	
	
	
	public Camera getCamera() {
		return c;
	}
	
	public float getMovementSpeed() {
		return movementSpeed;
	}
	
	public float getMovementSpeedAdj() {
		return (float) (movementSpeed * DisplayManager.getFrameTimeSeconds());
	}
	
}

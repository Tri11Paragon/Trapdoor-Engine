package com.trapdoor.engine.camera;

import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.tools.input.InputMaster;
import com.trapdoor.engine.tools.input.Keyboard;
import com.trapdoor.engine.tools.input.Mouse;

/**
 * @author brett
 * @date Jan. 3, 2022
 * 
 */
public class RotatingCamera extends Camera {
	
	private static final float speed = 40f;
	private static final float vertSpeed = 20f;
	private static final float turnSpeedYaw = 5.0f;
	private static final float turnSpeedPitch = 5.0f;
	private static final float minDistance = 1;
	
	private float angleAround;
	private float distance = Math.max(minDistance, 10);
	private float height = 2.5f;
	
	@Override
	public void move() {
		
		if (Mouse.isGrabbed()) {
			this.pitch += Mouse.getDY() * turnSpeedPitch/100;
			angleAround += Mouse.getDX() * turnSpeedYaw/100;
		} else {
			this.pitch = 30;
			angleAround += DisplayManager.getFrameTimeSeconds() * turnSpeedYaw;
		}
		
		if (angleAround > 360 || angleAround < -360)
			angleAround = 0;
		
		if (this.pitch > 90)
			this.pitch = 90;
		if (this.pitch < -90)
			this.pitch = -90;
		
		if (InputMaster.scrolledLastFrame && Keyboard.isKeyDown(Keyboard.KEY_LEFT_SHIFT))
			distance += InputMaster.lastScrollState * DisplayManager.getFrameTimeSeconds() * speed;
		if (InputMaster.scrolledLastFrame && Keyboard.isKeyDown(Keyboard.KEY_LEFT_CONTROL))
			height += InputMaster.lastScrollState * DisplayManager.getFrameTimeSeconds() * vertSpeed;
		
		if (distance < minDistance)
			distance = minDistance;
		
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		calculateCameraPosition(horizontalDistance, verticalDistance);

		this.yaw = 360 - angleAround;
		this.yaw %= 360;
	}
	
	/**
	 * @return The horizontal distance of the camera from the origin.
	 */
	private float calculateHorizontalDistance() {
		return (float) (distance * Math.cos(Math.toRadians(this.pitch)));
	}

	/**
	 * @return The height of the camera from the aim point.
	 */
	private float calculateVerticalDistance() {
		return (float) (distance * Math.sin(Math.toRadians(this.pitch)));
	}
	
	private void calculateCameraPosition(float horizDistance, float verticDistance) {
		float theta = angleAround;
		position.x = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
		position.y = (float) (verticDistance) + height;
		position.z = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
	}
	
}

package com.game.engine.tools.input;

import org.lwjgl.glfw.GLFW;

import com.game.engine.display.DisplayManager;

public class Mouse {

	public static final int LEFT_CLICK = GLFW.GLFW_MOUSE_BUTTON_LEFT;
	public static final int LEFT = GLFW.GLFW_MOUSE_BUTTON_LEFT;
	
	public static final int RIGHT_CLICK = GLFW.GLFW_MOUSE_BUTTON_RIGHT;
	public static final int RIGHT = GLFW.GLFW_MOUSE_BUTTON_RIGHT;
	
	public static final int MIDDLE_CLICK = GLFW.GLFW_MOUSE_BUTTON_MIDDLE;
	public static final int MIDDLE = GLFW.GLFW_MOUSE_BUTTON_MIDDLE;
	
	/**
	 * @param button
	 * @return true if the supplied button is currently down
	 */
	public static boolean isMouseDown(int button) {
		return InputMaster.mouseDown[button];
	}
	
	/**
	 * @param button
	 * @return true if the supplied button is currently down
	 */
	public static boolean mouseDown(int button) {
		return InputMaster.mouseDown[button];
	}
	
	/**
	 * @return true if the left mouse button is down
	 */
	public static boolean isLeftClick() {
		return InputMaster.mouseDown[LEFT];
	}
	
	/**
	 * @return true if the right mouse button is down
	 */
	public static boolean isRightClick() {
		return InputMaster.mouseDown[RIGHT];
	}
	
	/**
	 * @return true if the middle mouse button is down
	 */
	public static boolean isMiddleClick() {
		return InputMaster.mouseDown[MIDDLE];
	}
	
	/**
	 * WARNING - THIS APPLIES TO ANY MOUSE BUTTON BEING PRESSED
	 * @return true when the mouse was pressed in this frame
	 */
	public static boolean mouseState() {
		return InputMaster.mouseState;
	}
	
	/**
	 * @return true if the left mouse button was pressed in this frame.
	 */
	public static boolean mouseStateLeft() {
		return InputMaster.mouseStateLeft;
	}
	
	/**
	 * @return true if the right mouse button was pressed in this frame.
	 */
	public static boolean mouseStateRight() {
		return InputMaster.mouseStateRight;
	}
	
	/**
	 * @return true if the middle mouse button was pressed in this frame.
	 */
	public static boolean mouseStateMiddle() {
		return InputMaster.mouseStateMiddle;
	}
	
	public static double getDX() {
		return DisplayManager.getDX();
	}
	
	public static double getDY() {
		return DisplayManager.getDY();
	}
	
	public static boolean isGrabbed() {
		return DisplayManager.isMouseGrabbed;
	}
	
}

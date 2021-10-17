package com.game.engine.tools.input;

public class Keyboard {
	
	/**
	 * @param key
	 * @return true if the specified key is currently being pressed in this frame
	 */
	public static boolean isKeyDown(int key) {
		return InputMaster.keyDown[key];
	}
	
	/**
	 * @param key
	 * @return true if the specified key is currently being pressed in this frame
	 */
	public static boolean keyDown(int key) {
		return InputMaster.keyDown[key];
	}
	
	/**
	 * @return true if a key was pressed in the frame
	 */
	public static boolean state() {
		return InputMaster.state;
	}
	
}

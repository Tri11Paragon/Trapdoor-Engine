package com.game.engine.display;

/**
 * @author brett
 * @date Oct. 18, 2021
 * 
 */
public abstract class IDisplay {
	
	// called when the game loads
	public abstract void onCreate();
	
	// called when this display is switched to
	public abstract void onSwitch();
	
	// called on every render update
	public abstract void render();
	
	// called when this display is left
	public abstract void onLeave();
	
	// called when the game quits
	public abstract void onDestory();
	
}

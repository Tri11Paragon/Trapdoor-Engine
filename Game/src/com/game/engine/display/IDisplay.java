package com.game.engine.display;

import org.joml.Vector3f;

/**
 * @author brett
 * @date Oct. 18, 2021
 * 
 */
public abstract class IDisplay {
	
	public IDisplay() {
		
	}
	
	private final Vector3f skyColor = new Vector3f(0.5444f, 0.62f, 0.69f);
	
	// called when the game loads
	public abstract void onCreate();
	
	// called when this display is switched to
	public abstract void onSwitch();
	
	// called on every render update
	public abstract void render();
	
	// called on every physics update
	public abstract void update();
	
	// called when this display is left
	public abstract void onLeave();
	
	// called when the game quits
	public abstract void onDestory();
	
	public Vector3f getSkyColor() {
		return skyColor;
	}
	
	public IDisplay setSkyColor(float r, float g, float b) {
		this.skyColor.x = r;
		this.skyColor.y = g;
		this.skyColor.z = b;
		return this;
	}
	
	public float getSkyR() {
		return skyColor.x;
	}
	
	public float getSkyG() {
		return skyColor.y;
	}
	
	public float getSkyB() {
		return skyColor.z;
	}
	
}

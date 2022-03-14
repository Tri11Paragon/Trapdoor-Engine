package com.trapdoor.engine.world.entities.tools;

import com.trapdoor.engine.world.World;
import com.trapdoor.engine.world.entities.EntityCamera;

/**
 * @author brett
 * @date Mar. 14, 2022
 * 
 */
public class Weapon {
	
	protected EntityCamera player;
	protected World world;
	
	public Weapon(EntityCamera player, World world) {
		this.player = player;
		this.world = world;
	}
	
	/**
	 * called once per render frame
	 */
	public void render() {
		
	}
	
	/**
	 * called once per update frame
	 */
	public void update() {
		
	}

	/*
	 * called by left click equivalent
	 */
	public void shoot() {
		
	}
	
	/**
	 * called by right click equivalent
	 */
	public void alt() {
		
	}
	
	/** 
	 * called as a middle button equivalent
	 */
	public void extra() {
		
	}
	
	/*
	 * called by left click equivalent when the button has been being pressed but is no longer pressed
	 */
	public void shootRelease() {
		
	}
	
	/**
	 * called by right click equivalent when the button has been being pressed but is no longer pressed
	 */
	public void altRelease() {
		
	}
	
	/** 
	 * called as a middle button equivalent when the button has been being pressed but is no longer pressed
	 */
	public void extraRelease() {
		
	}

	/*
	 * called when NOT left click equivalent
	 */
	public void shootN() {
		
	}
	
	/**
	 * called when NOT right click equivalent
	 */
	public void altN() {
		
	}
	
	/** 
	 * called when NOT a middle button equivalent
	 */
	public void extraN() {
		
	}
	
}

package com.game.engine.datatypes.world;

/**
 * @author laptop
 * @date Oct. 25, 2021
 * 
 */
public class Tile {
	
	public static final int TILE_WIDTH=32;
	public static final int TILE_HEIGHT=32;
	
	private String texture;
	
	public Tile(String texture) {
		this.texture=texture;
	}
	
	public String getTexture() {
		return this.texture;
	}
	
}

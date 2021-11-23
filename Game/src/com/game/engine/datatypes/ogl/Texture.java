package com.game.engine.datatypes.ogl;

import java.io.Serializable;

/**
 * @author brett
 * Stores the texture id for a texture object
 * this used to have a lot of stuff, but I removed it as it wasn't useful.
 */
public class Texture implements Serializable {

	private static final long serialVersionUID = -7222158174860775396L;
	// texture ids
	private int textureID;
	private int width;
	private int height;
	private int channels;
	
	public Texture(int texture, int width, int height, int channels){
		this.textureID = texture;
		this.width = width;
		this.height = height;
		this.channels = channels;
	}

	public int getID(){
		return textureID;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getChannels() {
		return channels;
	}
	
	

}

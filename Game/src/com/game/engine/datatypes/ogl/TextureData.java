package com.game.engine.datatypes.ogl;

import java.nio.ByteBuffer;

/**
 * @author brett
 * holds data loaded from a texture file
 */
public class TextureData {
	
	private int width;
	private int height;
	private int channels;
	// byte buffer containing all the bytes of an image.
	private ByteBuffer buffer;
	private String name;
	
	public TextureData(ByteBuffer buffer, int width, int height, int channels, String name){
		this.buffer = buffer;
		this.width = width;
		this.height = height;
		this.channels = channels;
		this.name = name;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public ByteBuffer getBuffer(){
		return buffer;
	}

	public int getChannels() {
		return channels;
	}
	
	public String getName() {
		return name;
	}


}
package com.trapdoor.engine.datatypes.ogl;

import com.trapdoor.engine.datatypes.ogl.obj.VAO;

/**
 * @author brett
 * Stores the VAO and Texture of a model
 * created a textured model!
 */
public class TexturedModel {

	private VAO rawModel;
	private Texture texture;
	
	public TexturedModel(VAO model, Texture texture){
		this.rawModel = model;
		this.texture = texture;
	}
	
	public void setRawModel(VAO model) {
		this.rawModel = model;
	}
	
	public VAO getRawModel() {
		return rawModel;
	}

	public Texture getTexture() {
		return texture;
	}
	
	
}

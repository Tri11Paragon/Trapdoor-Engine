package com.game.engine.datatypes.ogl;

import com.game.engine.datatypes.ogl.obj.LoadedModel;

/**
 * @author brett
 * Stores the VAO and Texture of a model
 * created a textured model!
 */
public class TexturedModel {

	private LoadedModel rawModel;
	private Texture texture;
	
	public TexturedModel(LoadedModel model, Texture texture){
		this.rawModel = model;
		this.texture = texture;
	}
	
	public void setRawModel(LoadedModel model) {
		this.rawModel = model;
	}
	
	public LoadedModel getRawModel() {
		return rawModel;
	}

	public Texture getTexture() {
		return texture;
	}
	
	
}

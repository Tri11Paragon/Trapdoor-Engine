package com.game.engine.shaders;

import org.joml.Matrix4f;

/**
 * @author brett
 * @date Jan. 10, 2022
 * 
 */
public class DeferredFirstPassShader extends ShaderProgram {

	private int location_translationMatrix;
	private int location_projectViewMatrix;

	private int location_diffuseTexture;
	private int location_normalMap;
	
	public DeferredFirstPassShader() {
		super("deferredFirstPass.vs", "deferredFirstPass.fs");
		this.start();
		this.connectTextureUnits();
		setUniformBlockLocation("Matricies", 1);
		this.stop();
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoordinates");
		super.bindAttribute(2, "normal");
	}

	@Override
	protected void getAllUniformLocations() {
		location_translationMatrix = super.getUniformLocation("translationMatrix");
		location_projectViewMatrix = super.getUniformLocation("projectViewMatrix");
		location_diffuseTexture = super.getUniformLocation("diffuseTexture");
		location_normalMap = super.getUniformLocation("normalMap");
	}
	
	public void loadTranslationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_translationMatrix, matrix);
	}
	
	public void loadTransformationMatrix(Matrix4f matrix){
		loadTranslationMatrix(matrix);
	}
	
	public void loadProjectViewMatrix(Matrix4f matrix) {
		super.loadMatrix(location_projectViewMatrix, matrix);
	}
	
	public void connectTextureUnits() {
		super.loadInt(location_diffuseTexture, 0);
		super.loadInt(location_normalMap, 1);
	}

}

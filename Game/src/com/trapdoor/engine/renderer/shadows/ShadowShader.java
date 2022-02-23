package com.trapdoor.engine.renderer.shadows;

import org.joml.Matrix4f;

import com.trapdoor.engine.renderer.ShaderProgram;

public class ShadowShader extends ShaderProgram {

	private int location_translationMatrix;
	private int location_persMatrix;
	
	public ShadowShader() {
		super("shadowDepth.vs", "shadowDepth.fs");
		//this.start();
		//setUniformBlockLocation("Matricies", 1);
		//this.stop();
	}

	@Override
	protected void getAllUniformLocations() {
		location_translationMatrix = super.getUniformLocation("transformMatrix");
		location_persMatrix = super.getUniformLocation("perspectiveMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoordinates");
		super.bindAttribute(2, "normal");
	}
	
	public void loadPerspectiveMatrix(Matrix4f matrix) {
		super.loadMatrix(location_persMatrix, matrix);
	}
	
	public void loadTranslationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_translationMatrix, matrix);
	}
	
	public void loadTransformationMatrix(Matrix4f matrix){
		loadTranslationMatrix(matrix);
	}

}

package com.trapdoor.engine.renderer.shadows;

import org.joml.Matrix4f;

import com.trapdoor.engine.renderer.ShaderProgram;

public class ShadowShader extends ShaderProgram {

	private int location_translationMatrix;

	public ShadowShader() {
		super("shadowDepth.vs", "shadowDepth.gs", "shadowDepth.fs");
		this.start();
		setUniformBlockLocation("LightSpaceMatrices", 3);
		this.stop();
	}

	@Override
	protected void getAllUniformLocations() {
		location_translationMatrix = super.getUniformLocation("transformMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoordinates");
		super.bindAttribute(2, "normal");
	}
	
	public void loadTranslationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_translationMatrix, matrix);
	}
	
	public void loadTransformationMatrix(Matrix4f matrix){
		loadTranslationMatrix(matrix);
	}

}

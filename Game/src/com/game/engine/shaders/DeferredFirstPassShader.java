package com.game.engine.shaders;

import org.joml.Matrix4f;
import org.joml.Vector3d;

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
	private int location_displacementMap;
	private int location_aoMap;
	private int location_specMap;
	
	private int location_specAmount;
	
	private int location_viewPos;
	
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
		location_specMap = super.getUniformLocation("specMap");
		location_displacementMap = super.getUniformLocation("displacementMap");
		location_aoMap = super.getUniformLocation("aoMap");
		
		location_specAmount = super.getUniformLocation("specAmount");
		location_viewPos = super.getUniformLocation("viewPos");
	}
	
	public void loadViewPos(Vector3d pos) {
		super.loadVector(location_viewPos, pos);
	}
	
	public void loadSpecAmount(float amount) {
		super.loadFloat(location_specAmount, amount);
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
		super.loadInt(location_displacementMap, 2);
		super.loadInt(location_specMap, 3);
		super.loadInt(location_aoMap, 4);
	}

}

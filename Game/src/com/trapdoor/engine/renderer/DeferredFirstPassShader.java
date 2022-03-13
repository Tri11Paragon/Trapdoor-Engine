package com.trapdoor.engine.renderer;

import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;

import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.renderer.shadows.ShadowRenderer;

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
	private int location_shadowMap;
	
	private int location_specAmount;
	private int location_diffuseAmount;
	
	private int location_cascadeCount;
	private int[] location_cascadePlaneDistances;
	private int location_lightDir;
	private int location_farPlane;
	
	private int location_viewPos;
	
	public DeferredFirstPassShader() {
		super("deferredFirstPass.vs", "deferredFirstPass.fs");
		this.start();
		this.connectTextureUnits();
		setUniformBlockLocation("Matricies", 1);
		setUniformBlockLocation("LightSpaceMatrices", 3);
		super.loadInt(location_cascadeCount, ShadowRenderer.shadowCascadeLevels.length);
		for (int i = 0; i < ShadowRenderer.shadowCascadeLevels.length; i++)
			super.loadFloat(location_cascadePlaneDistances[i], ShadowRenderer.shadowCascadeLevels[i]);
		super.loadVector(location_lightDir, DisplayManager.lightDirection);
		super.loadFloat(location_farPlane, ShadowRenderer.FAR_PLANE);
		this.stop();
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoordinates");
		super.bindAttribute(2, "normal");
		super.bindAttribute(3, "tangent");
		super.bindAttribute(4, "bitangent");
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
		location_shadowMap = super.getUniformLocation("shadowMap");
		
		location_specAmount = super.getUniformLocation("specAmount");
		location_diffuseAmount = super.getUniformLocation("diffuse");
		location_viewPos = super.getUniformLocation("viewPos");
		
		location_cascadeCount = super.getUniformLocation("cascadeCount");
		location_cascadePlaneDistances = new int[ShadowRenderer.shadowCascadeLevels.length];
		for (int i = 0; i < location_cascadePlaneDistances.length; i++) {
			location_cascadePlaneDistances[i] = super.getUniformLocation("cascadePlaneDistances[" + i + "]");
		}
		
		location_lightDir = super.getUniformLocation("lightDir");
		location_farPlane = super.getUniformLocation("farPlane");
	}
	
	public void loadViewPos(Vector3d pos) {
		super.loadVector(location_viewPos, pos);
	}
	
	public void loadSpecAmount(float amount) {
		super.loadFloat(location_specAmount, amount);
	}
	
	public void loadDiffuseAmount(Vector3f diffuse) {
		super.loadVector(location_diffuseAmount, diffuse);
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
		super.loadInt(location_shadowMap, 5);
	}

}

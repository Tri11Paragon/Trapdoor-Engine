package com.trapdoor.engine.renderer;

import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.renderer.shadows.ShadowRenderer;

/**
 * @author laptop
 * @date Mar. 16, 2022
 * 
 */
public class MaterialPassShader extends ShaderProgram {

	public MaterialPassShader() {
		super("materialPass.vs", "materialPass.fs");
		this.start();
			setUniformBlockLocation("Matricies", 1);
			setUniformBlockLocation("Lightings", 2);
			setUniformBlockLocation("LightSpaceMatrices", 3);
			connectTextureUnits();
			super.loadInt("cascadeCount", ShadowRenderer.shadowCascadeLevels.length);
			for (int i = 0; i < ShadowRenderer.shadowCascadeLevels.length; i++)
				super.loadFloat("cascadePlaneDistances[" + i + "]", ShadowRenderer.shadowCascadeLevels[i]);
			super.loadVector("lightDir", DisplayManager.lightDirection);
			super.loadVector("directLightColor", DisplayManager.lightColor);
			super.loadFloat("farPlane", ShadowRenderer.FAR_PLANE);
		this.stop();
	}

	@Override
	protected void getAllUniformLocations() {
		super.getUniformLocation("directLightColor");
		super.getUniformLocation("diffuseValue");
		super.getUniformLocation("specAmount");
		super.getUniformLocation("viewPos");
		super.getUniformLocation("textures");
		//super.getUniformLocation("diffuseTexture");
		//super.getUniformLocation("normalMap");
		//super.getUniformLocation("displacementMap");
		//super.getUniformLocation("specMap");
		super.getUniformLocation("shadowMap");
		super.getUniformLocation("emissionMap");
		super.getUniformLocation("farPlane");
		super.getUniformLocation("lightDir");
		super.getUniformLocation("cascadeCount");
		super.getUniformLocation("flags");
		for (int i = 0; i < 5; i++)
			super.getUniformLocation("cascadePlaneDistances[" + i + "]");
		super.getUniformLocation("transformMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoordinates");
		super.bindAttribute(2, "normal");
		super.bindAttribute(3, "tangent");
		super.bindAttribute(4, "bitangent");
	}
	
	public void connectTextureUnits() {
		super.loadInt(super.getStoredLocation("textures"), 0);
//		super.loadInt(super.getStoredLocation("normalMap"), 1);
//		super.loadInt(super.getStoredLocation("displacementMap"), 2);
//		super.loadInt(super.getStoredLocation("specMap"), 3);
//		super.loadInt(super.getStoredLocation("emissionMap"), 4);
		super.loadInt(super.getStoredLocation("shadowMap"), 5);
	}
	
}

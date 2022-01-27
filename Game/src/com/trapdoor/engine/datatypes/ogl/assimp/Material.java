package com.trapdoor.engine.datatypes.ogl.assimp;

import org.joml.Vector3f;
import org.joml.Vector4f;

import com.trapdoor.engine.datatypes.ogl.Texture;
import com.trapdoor.engine.registry.GameRegistry;

/**
 * @author brett
 * @date Nov. 22, 2021
 * 
 */
public class Material {
	
	public static final Vector4f DEFAULT_COLOUR = new Vector4f();
	
	private String diffuseTexturePath;
	private String normalTexturePath;
	private String displacementTexturePath;
	private String ambientOcclusionTexturePath;
	private String specularTexturePath;
	
	private boolean usingSpecialMaterial = false;
	private Vector3f colorInformation;
	
	private Texture diffuseTexture;
	private Texture normalTexture;
	private Texture displacementTexture;
	private Texture ambientOcclusionTexture;
	private Texture specularTexture;
	
	public Material(String diffuseTexture, String normalMapTexture, String displacementTexturePath, String ambientOcclusionTexturePath, String specularTexturePath, Vector3f colorInformation) {
		this.diffuseTexturePath = diffuseTexture;
		
		//if (normalMapTexture == GameRegistry.DEFAULT_EMPTY_NORMAL_MAP)
		//	normalMapTexture = null;
		
		if (normalMapTexture != null || displacementTexturePath != null || ambientOcclusionTexturePath != null)
			usingSpecialMaterial = true;
		this.normalTexturePath = normalMapTexture;
		this.colorInformation = colorInformation;
		this.displacementTexturePath = displacementTexturePath;
		this.ambientOcclusionTexturePath = ambientOcclusionTexturePath;
		this.specularTexturePath = specularTexturePath;
	}
	
	public void loadTexturesFromGameRegistry() {
		if (!this.diffuseTexturePath.contains("NORENDER"))
			this.diffuseTexture = GameRegistry.getTexture(this.diffuseTexturePath);
		if (usingSpecialMaterial) {
			this.normalTexture = GameRegistry.getTexture(this.normalTexturePath);
			this.displacementTexture = GameRegistry.getTexture(this.displacementTexturePath);
			this.ambientOcclusionTexture = GameRegistry.getTexture(this.ambientOcclusionTexturePath);
			this.specularTexture = GameRegistry.getTexture(this.specularTexturePath);
		}
	}

	public String getDiffuseTexturePath() {
		return diffuseTexturePath;
	}

	public String getNormalTexturePath() {
		return normalTexturePath;
	}

	public Texture getDiffuseTexture() {
		return diffuseTexture;
	}

	public Texture getDisplacementTexture() {
		return displacementTexture;
	}

	public Texture getAmbientOcclusionTexture() {
		return ambientOcclusionTexture;
	}

	public Texture getSpecularTexture() {
		return specularTexture;
	}

	public void setDiffuseTexture(Texture diffuseTexture) {
		this.diffuseTexture = diffuseTexture;
	}
	
	public void setDiffuseTexturePath(String texture) {
		this.diffuseTexturePath = texture;
	}

	public Texture getNormalTexture() {
		return normalTexture;
	}

	public void setNormalTexture(Texture normalTexture) {
		this.normalTexture = normalTexture;
	}
	
	public void setNormalTexturePath(String texture) {
		this.normalTexturePath = texture;
	}
	
	public Vector3f getColorInformation() {
		return this.colorInformation;
	}

	public String getDisplacementTexturePath() {
		return displacementTexturePath;
	}

	public void setDisplacementTexturePath(String displacementTexturePath) {
		this.displacementTexturePath = displacementTexturePath;
	}

	public String getAmbientOcclusionTexturePath() {
		return ambientOcclusionTexturePath;
	}

	public void setAmbientOcclusionTexturePath(String ambientOcclusionTexturePath) {
		this.ambientOcclusionTexturePath = ambientOcclusionTexturePath;
	}

	public String getSpecularTexturePath() {
		return specularTexturePath;
	}

	public void setSpecularTexturePath(String specularTexturePath) {
		this.specularTexturePath = specularTexturePath;
	}

	public void setDisplacementTexture(Texture displacementTexture) {
		this.displacementTexture = displacementTexture;
	}

	public void setAmbientOcclusionTexture(Texture ambientOcclusionTexture) {
		this.ambientOcclusionTexture = ambientOcclusionTexture;
	}

	public void setSpecularTexture(Texture specularTexture) {
		this.specularTexture = specularTexture;
	}
	
}

package com.trapdoor.engine.datatypes.ogl.assimp;

import org.joml.Vector3f;

import com.trapdoor.engine.datatypes.ogl.Texture;
import com.trapdoor.engine.registry.GameRegistry;

/**
 * @author brett
 * @date Nov. 22, 2021
 * 
 */
public class Material {
	
	public static final Vector3f DEFAULT_COLOUR = new Vector3f();
	
	private String diffuseTexturePath;
	private String normalTexturePath;
	private String displacementTexturePath;
	private String ambientOcclusionTexturePath;
	private String specularTexturePath;
	
	private boolean usingSpecialMaterial = false;
	private boolean usingNormalMap = false;
	private boolean usingSpecMap = false;
	
	private Vector3f diffuse;
	private Vector3f ambient;
	private Vector3f specular;
	
	private Texture diffuseTexture;
	private Texture normalTexture;
	private Texture displacementTexture;
	private Texture ambientOcclusionTexture;
	private Texture specularTexture;
	
	public Material(String diffuseTexture, String normalMapTexture, String displacementTexturePath, String ambientOcclusionTexturePath, String specularTexturePath, Vector3f diffuse, Vector3f ambient, Vector3f specular) {
		this.diffuseTexturePath = diffuseTexture;
		
		//if (normalMapTexture == GameRegistry.DEFAULT_EMPTY_NORMAL_MAP)
		//	normalMapTexture = null;
		
		if (!normalMapTexture.contains("default_normal.png"))
			usingNormalMap = true;
		if (!specularTexturePath.contains("default_spec.png"))
			usingSpecMap = true;
		if (!displacementTexturePath.contains("default_disp.png") && !ambientOcclusionTexturePath.contains("default_ao.png"))
			usingSpecialMaterial = true;
		
		this.normalTexturePath = normalMapTexture;
		this.diffuse = diffuse;
		this.ambient = ambient;
		this.specular = specular;
		this.displacementTexturePath = displacementTexturePath;
		this.ambientOcclusionTexturePath = ambientOcclusionTexturePath;
		this.specularTexturePath = specularTexturePath;
	}
	
	public void loadTexturesFromGameRegistry() {
		if (!this.diffuseTexturePath.contains("NORENDER")) {
			this.diffuseTexture = GameRegistry.getTexture(this.diffuseTexturePath);
			if (usingSpecialMaterial) {
				this.normalTexture = GameRegistry.getTexture(this.normalTexturePath);
				this.displacementTexture = GameRegistry.getTexture(this.displacementTexturePath);
				this.ambientOcclusionTexture = GameRegistry.getTexture(this.ambientOcclusionTexturePath);
				this.specularTexture = GameRegistry.getTexture(this.specularTexturePath);
			}
		}
	}
	
	public Material clone() {
		Material m = new Material(diffuseTexturePath, diffuseTexturePath, displacementTexturePath, ambientOcclusionTexturePath, specularTexturePath, diffuse, ambient, specular);
		//GameRegistry.registerMaterial2(m);
		return m;
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

	public Vector3f getDiffuse() {
		return diffuse;
	}

	public Vector3f getAmbient() {
		return ambient;
	}

	public Vector3f getSpecular() {
		return specular;
	}

	public boolean isUsingSpecialMaterial() {
		return usingSpecialMaterial;
	}

	public boolean isUsingNormalMap() {
		return usingNormalMap;
	}

	public boolean isUsingSpecMap() {
		return usingSpecMap;
	}
	
}

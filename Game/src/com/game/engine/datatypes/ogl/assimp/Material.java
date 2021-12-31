package com.game.engine.datatypes.ogl.assimp;

import com.game.engine.datatypes.ogl.Texture;
import com.game.engine.threading.GameRegistry;

/**
 * @author brett
 * @date Nov. 22, 2021
 * 
 */
public class Material {
	
	private String diffuseTexturePath;
	private String normalTexturePath;
	
	private Texture diffuseTexture;
	private Texture normalTexture;
	
	public Material(String diffuseTexture, String normalMapTexture) {
		this.diffuseTexturePath = diffuseTexture;
		this.normalTexturePath = normalMapTexture;
	}
	
	public void loadTexturesFromGameRegistry() {
		this.diffuseTexture = GameRegistry.getTexture(diffuseTexturePath);
		this.normalTexture = GameRegistry.getTexture(normalTexturePath);
	}

	public String getDiffuseTexturePath() {
		return diffuseTexturePath;
	}

	public void setDiffuseTexturePath(String diffuseTexturePath) {
		this.diffuseTexturePath = diffuseTexturePath;
	}

	public String getNormalTexturePath() {
		return normalTexturePath;
	}

	public void setNormalTexturePath(String normalTexturePath) {
		this.normalTexturePath = normalTexturePath;
	}

	public Texture getDiffuseTexture() {
		return diffuseTexture;
	}

	public void setDiffuseTexture(Texture diffuseTexture) {
		this.diffuseTexture = diffuseTexture;
	}

	public Texture getNormalTexture() {
		return normalTexture;
	}

	public void setNormalTexture(Texture normalTexture) {
		this.normalTexture = normalTexture;
	}
	
}

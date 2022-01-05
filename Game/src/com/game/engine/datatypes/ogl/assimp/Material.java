package com.game.engine.datatypes.ogl.assimp;

import org.joml.Vector3f;
import org.joml.Vector4f;

import com.game.engine.datatypes.ogl.Texture;
import com.game.engine.threading.GameRegistry;

/**
 * @author brett
 * @date Nov. 22, 2021
 * 
 */
public class Material {
	
	public static final Vector4f DEFAULT_COLOUR = new Vector4f();
	
	private String diffuseTexturePath;
	private String normalTexturePath;
	
	private Vector3f colorInformation;
	
	private Texture diffuseTexture;
	private Texture normalTexture;
	
	public Material(String diffuseTexture, String normalMapTexture, Vector3f colorInformation) {
		this.diffuseTexturePath = diffuseTexture;
		this.normalTexturePath = normalMapTexture;
		this.colorInformation = colorInformation;
	}
	
	public void loadTexturesFromGameRegistry() {
		this.diffuseTexture = GameRegistry.getTexture(diffuseTexturePath);
		this.normalTexture = GameRegistry.getTexture(normalTexturePath);
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
	
}

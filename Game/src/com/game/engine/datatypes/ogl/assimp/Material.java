package com.game.engine.datatypes.ogl.assimp;

import org.joml.Vector4f;

import com.game.engine.datatypes.ogl.Texture;

/**
 * @author brett
 * @date Nov. 22, 2021
 * 
 */
public class Material {
	
	public static final Vector4f DEFAULT_AMBIENT = new Vector4f();
	public static final Vector4f DEFAULT_DIFFUSE = new Vector4f();
	public static final Vector4f DEFAULT_SPECULAR = new Vector4f();
	
	private Texture diffuseTexture;
	private Texture normalTexture;
	private Texture bumpTexture;
	private Texture specularTexture;
	private Texture ambientTexture;
	private Texture lightmapTexture;
	private Texture shinnyTexture;
	private Vector4f ambient;
	private Vector4f diffuse;
	private Vector4f specular;
	
	public Material(Texture t, Vector4f ambient, Vector4f diffuse, Vector4f specular) {
		super();
		this.diffuseTexture = t;
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.specular = specular;
	}

	public Texture getNormal() {
		return normalTexture;
	}

	public Material setNormal(Texture normal) {
		this.normalTexture = normal;
		return this;
	}

	public Texture getBump() {
		return bumpTexture;
	}

	public Material setBump(Texture bump) {
		this.bumpTexture = bump;
		return this;
	}

	public Texture getTexture() {
		return diffuseTexture;
	}

	public Vector4f getAmbient() {
		return ambient;
	}

	public Vector4f getDiffuse() {
		return diffuse;
	}

	public Vector4f getSpecular() {
		return specular;
	}
	
}

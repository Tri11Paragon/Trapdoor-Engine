package com.game.engine.datatypes.ogl.assimp;

/**
 * @author brett
 * @date Nov. 22, 2021
 * 
 */
public class Model {
	
	private Mesh[] meshes;
	private Material[] materials;
	
	public Model(Mesh[] meshes, Material[] materials) {
		this.meshes = meshes;
		this.materials = materials;
	}

	public Mesh[] getMeshes() {
		return meshes;
	}

	public Material[] getMaterials() {
		return materials;
	}
	
}

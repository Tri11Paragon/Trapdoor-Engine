package com.game.engine.datatypes.ogl.assimp;

import com.game.engine.threading.GameRegistry;

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
	
	/**
	 * sets all submesh material to the supplied Material m
	 * @param m material to set to
	 */
	public Model setMaterial(Material m) {
		for (int i = 0; i < meshes.length; i++)
			meshes[i].setMaterial(m);
		return this;
	}
	
	/**
	 * sets all submesh material to the material represented by the supplied diffuse material string
	 * @param m
	 * @return
	 */
	public Model setMaterial(String m) {
		for (int i = 0; i < meshes.length; i++)
			meshes[i].setMaterial(GameRegistry.getMaterial(m));
		return this;
	}

	public Mesh[] getMeshes() {
		return meshes;
	}

	public Material[] getMaterials() {
		return materials;
	}
	
}

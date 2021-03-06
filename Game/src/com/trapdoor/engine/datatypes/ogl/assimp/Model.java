package com.trapdoor.engine.datatypes.ogl.assimp;

import org.lwjgl.assimp.AIScene;

import com.jme3.bullet.collision.shapes.infos.IndexedMesh;
import com.trapdoor.engine.datatypes.collision.AxisAlignedBoundingBox;
import com.trapdoor.engine.registry.GameRegistry;

/**
 * @author brett
 * @date Nov. 22, 2021
 * 
 */
public class Model {
	
	private Mesh[] meshes;
	private Material[] materials;
	private AIScene scene;
	private String path;
	private IndexedMesh[] meshColliderData;
	private AxisAlignedBoundingBox aabb;
	
	public Model(Mesh[] meshes, Material[] materials, AIScene scene, String path) {
		this.meshes = meshes;
		this.materials = materials;
		this.scene = scene;
		this.path = path;
		double minX = Float.MAX_VALUE;
		double maxX = Float.MIN_VALUE;
		double minY = Float.MAX_VALUE;
		double maxY = Float.MIN_VALUE;
		double minZ = Float.MAX_VALUE;
		double maxZ = Float.MIN_VALUE;
		meshColliderData = new IndexedMesh[meshes.length];
		for (int i = 0; i < meshes.length; i++) {
			meshColliderData[i] = (meshes[i].getMeshColliderInfo());
			AxisAlignedBoundingBox box = meshes[i].getBoundingBox();
			if (box.getMinX() < minX)
				minX = box.getMinX();
			if (box.getMaxX() > maxX)
				maxX = box.getMaxX();
			if (box.getMinY() < minY)
				minY = box.getMinY();
			if (box.getMaxY() > maxY)
				maxY = box.getMaxY();
			if (box.getMinZ() < minZ)
				minZ = box.getMinZ();
			if (box.getMaxZ() > maxZ)
				maxZ = box.getMaxZ();
		}
		aabb = new AxisAlignedBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	/**
	 * sets all submesh material to the supplied Material m
	 * @param m material to set to
	 */
	public Model setMaterial(Material m) {
		System.err.println("IT'S UNLIKELY THAT YOU NEED THE SET MATERIAL FUNCTION.\n\t PLEASE CONSIDER WHAT YOU ARE DOING!");
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
		System.err.println("IT'S UNLIKELY THAT YOU NEED THE SET MATERIAL FUNCTION.\n\t PLEASE CONSIDER WHAT YOU ARE DOING!");
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
	
	public AIScene getScene() {
		return this.scene;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public AxisAlignedBoundingBox getAABB() {
		return aabb;
	}
	
	public IndexedMesh[] getMeshColliderData() {
		return this.meshColliderData;
	}
	
	public Model clone() {
		Material[] materials = new Material[this.materials.length];
		for (int i = 0; i < this.materials.length; i++)
			materials[i] = this.materials[i].clone();
		return new Model(meshes, materials, scene, path);
	}
	
}

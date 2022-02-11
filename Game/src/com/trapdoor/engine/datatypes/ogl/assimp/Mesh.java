package com.trapdoor.engine.datatypes.ogl.assimp;

import com.jme3.bullet.collision.shapes.infos.IndexedMesh;
import com.trapdoor.engine.datatypes.collision.AxisAlignedBoundingBox;
import com.trapdoor.engine.datatypes.ogl.obj.VAO;

/**
 * @author brett
 * @date Nov. 22, 2021
 * 
 */
public class Mesh {

	private Material material;
	private float[] vertices;
	private float[] textures;
	private float[] normals;
	private int[] indices;
	private IndexedMesh meshColliderInfo;
	
	private VAO meshVAO;
	private AxisAlignedBoundingBox meshBoundingBox;
	
	public Mesh(Material material, AxisAlignedBoundingBox boundingBox, float[] vertices, float[] textures, float[] normals, int[] indices, IndexedMesh meshColliderInfo) {
		super();
		this.material = material;
		this.vertices = vertices;
		this.textures = textures;
		this.normals = normals;
		this.indices = indices;
		this.meshColliderInfo = meshColliderInfo;
		this.meshBoundingBox = boundingBox;
	}
	
	public Mesh assignVAO(VAO vao) {
		this.meshVAO = vao;
		return this;
	}
	
	public VAO getVAO() {
		return this.meshVAO;
	}
	
	public Material getMaterial() {
		return material;
	}
	
	public Mesh setMaterial(Material m) {
		this.material = m;
		return this;
	}
	
	public float[] getVertices() {
		return vertices;
	}
	
	public float[] getTextures() {
		return textures;
	}
	
	public float[] getNormals() {
		return normals;
	}
	
	public int[] getIndices() {
		return indices;
	}
	
	@Override
	public String toString() {
		return this.material.getDiffuseTexturePath();
	}

	public IndexedMesh getMeshColliderInfo() {
		return meshColliderInfo;
	}
	
	public AxisAlignedBoundingBox getBoundingBox() {
		return this.meshBoundingBox;
	}
}

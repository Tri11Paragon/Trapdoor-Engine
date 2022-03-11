package com.trapdoor.engine.datatypes.ogl.assimp;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

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
	private FloatBuffer vertices;
	private FloatBuffer textures;
	private FloatBuffer normals;
	private FloatBuffer tangents;
	private FloatBuffer bitangents;
	private IntBuffer indices;
	private IndexedMesh meshColliderInfo;
	
	private VAO meshVAO;
	private AxisAlignedBoundingBox meshBoundingBox;
	
	public Mesh(Material material, AxisAlignedBoundingBox boundingBox, FloatBuffer vertices, FloatBuffer textures, FloatBuffer normals, FloatBuffer tangents, FloatBuffer bitangents, IntBuffer indices, IndexedMesh meshColliderInfo) {
		super();
		this.material = material;
		this.vertices = vertices;
		this.textures = textures;
		this.normals = normals;
		this.indices = indices;
		this.tangents = tangents;
		this.bitangents = bitangents;
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
	
	public FloatBuffer getVertices() {
		return vertices;
	}

	public FloatBuffer getTextures() {
		return textures;
	}

	public FloatBuffer getNormals() {
		return normals;
	}

	public IntBuffer getIndices() {
		return indices;
	}

	public FloatBuffer getTangents() {
		return tangents;
	}

	public FloatBuffer getBitangents() {
		return bitangents;
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

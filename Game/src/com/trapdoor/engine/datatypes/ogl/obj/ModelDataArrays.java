package com.trapdoor.engine.datatypes.ogl.obj;

/**
*
* @author brett
* @date May 16, 2020
* holds float[]s for the verts and uvs.
* Very simple data storage type
*/

public class ModelDataArrays {
	
	private float[] verts;
	private float[] uvs;
	
	public ModelDataArrays(float[] verts, float[] uvs) {
		this.verts = verts;
		this.uvs = uvs;
	}

	public float[] getVerts() {
		return verts;
	}

	public void setVerts(float[] verts) {
		this.verts = verts;
	}

	public float[] getUvs() {
		return uvs;
	}

	public void setUvs(float[] uvs) {
		this.uvs = uvs;
	}
	
}

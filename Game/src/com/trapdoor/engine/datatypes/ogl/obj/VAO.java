package com.trapdoor.engine.datatypes.ogl.obj;

/**
*
* @author brett
* @date Mar. 3, 2020
* Just a ModelVAO that stores the VBOs in an int[]
*/

public class VAO  {
	
	private int[] vbos;
	private int vaoID;
	private int vertexCount;
	
	public VAO(int vaoID, int[] vbos, int vertexCount) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
		this.vbos = vbos;
	}

	public int[] getVbos() {
		return vbos;
	}

	public int getVaoID() {
		return vaoID;
	}

	public int getVertexCount() {
		return vertexCount;
	}
}

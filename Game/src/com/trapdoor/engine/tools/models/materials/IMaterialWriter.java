package com.trapdoor.engine.tools.models.materials;

import com.trapdoor.engine.datatypes.ogl.assimp.Material;

/**
 * @author brett
 * @date Jan. 20, 2022
 * 
 */
public interface IMaterialWriter {
	
	public void writeMaterial(Material m);
	
	public int getVersion();
	
}

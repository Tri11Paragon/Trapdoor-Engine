package com.trapdoor.engine.tools.models.materials.versions;

import com.trapdoor.engine.datatypes.ogl.assimp.Material;
import com.trapdoor.engine.tools.models.materials.IMaterialReader;
import com.trapdoor.engine.tools.models.materials.MaterialFileReader;

/**
 * @author brett
 * @date Mar. 9, 2022
 * 
 */
public class BaseMaterialReader implements IMaterialReader {

	@Override
	public Material readMaterial(MaterialFileReader reader) {
		return null;
	}

	@Override
	public int getVersion() {
		return 0;
	}

}

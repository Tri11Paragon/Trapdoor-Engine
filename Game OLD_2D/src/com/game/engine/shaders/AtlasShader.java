package com.game.engine.shaders;

/**
 * @author brett
 * @date Oct. 18, 2021
 * 
 */
public class AtlasShader extends WorldShader {
	
	public int location_textureID;

	public AtlasShader(String vertexFile, String fragmentFile) {
		super(vertexFile, fragmentFile);
	}

	@Override
	protected void getAllUniformLocations() {
		super.getAllUniformLocations();
		location_textureID = super.getUniformLocation("textureID");
	}
	
	public void loadTextureID(int id) {
		super.loadFloat(location_textureID, id);
	}

}

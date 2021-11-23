package com.game.engine.shaders;

/**
 * @author brett
 * @date Oct. 25, 2021
 * 
 */
public class TileShader extends WorldShader {

	private int[] texturelocations;
	
	public TileShader(String vertexFile, String fragmentFile) {
		super(vertexFile, fragmentFile);
	}
	
	@Override
	protected void getAllUniformLocations() {
		super.getAllUniformLocations();
		texturelocations = new int[16];
		for (int i = 0; i < texturelocations.length; i++) {
			texturelocations[i] = super.getUniformLocation("ourTexture[" + i  + "]");
		}
	}
	
	public void connectTextureUnits() {
		for (int i = 0; i < texturelocations.length; i++)
			super.loadInt(texturelocations[i], i);
	}

}

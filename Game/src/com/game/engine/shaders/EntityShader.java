package com.game.engine.shaders;

/**
 * @author laptop
 * @date Nov. 21, 2021
 * 
 */
public class EntityShader extends WorldShader {
	
	public EntityShader(String vertexFile, String fragmentFile) {
		super(vertexFile, fragmentFile);
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoordinates");
		super.bindAttribute(2, "normal");
	}
	
}

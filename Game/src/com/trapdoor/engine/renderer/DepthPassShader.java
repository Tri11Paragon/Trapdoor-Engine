package com.trapdoor.engine.renderer;

/**
 * @author brett
 * @date Mar. 16, 2022
 * 
 */
public class DepthPassShader extends ShaderProgram {
	
	public DepthPassShader() {
		super("depth.vs", "depth.fs");
		this.start();
		setUniformBlockLocation("Matricies", 1);
		this.stop();
	}

	@Override
	protected void getAllUniformLocations() {
		super.getUniformLocation("transformMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoordinates");
		super.bindAttribute(2, "normal");
		super.bindAttribute(3, "tangent");
		super.bindAttribute(4, "bitangent");
	}
	
}

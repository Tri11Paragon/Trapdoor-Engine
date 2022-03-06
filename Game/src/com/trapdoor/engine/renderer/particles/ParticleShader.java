package com.trapdoor.engine.renderer.particles;

import com.trapdoor.engine.renderer.ShaderProgram;

public class ParticleShader extends ShaderProgram {

	private static final String VERTEX_FILE = "particle/particleShader.vs";
	private static final String FRAGMENT_FILE = "particle/particleShader.fs";

	public ParticleShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		this.start();
		setUniformBlockLocation("Matricies", 1);
		this.stop();
	}

	@Override
	protected void getAllUniformLocations() {
		
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "modelMatrix");
		super.bindAttribute(5, "textureData");
	}

}

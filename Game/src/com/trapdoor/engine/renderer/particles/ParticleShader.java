package com.trapdoor.engine.renderer.particles;

import org.joml.Vector3d;

import com.trapdoor.engine.renderer.ShaderProgram;

public class ParticleShader extends ShaderProgram {

	private static final String VERTEX_FILE = "particle/particleShader.vs";
	private static final String FRAGMENT_FILE = "particle/particleShader.fs";

	private int location_viewPos;
	
	public ParticleShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		this.start();
		setUniformBlockLocation("Matricies", 1);
		this.stop();
	}

	@Override
	protected void getAllUniformLocations() {
		location_viewPos = super.getUniformLocation("viewPos");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "modelMatrix");
		super.bindAttribute(5, "textureData");
	}
	
	public void loadViewPos(Vector3d viewPos) {
		super.loadVector(location_viewPos, viewPos);
	}

}

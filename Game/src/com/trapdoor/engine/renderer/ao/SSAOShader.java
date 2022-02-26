package com.trapdoor.engine.renderer.ao;

import org.joml.Vector3f;

import com.trapdoor.engine.renderer.ShaderProgram;

public class SSAOShader extends ShaderProgram {

	public SSAOShader() {
		super("deferredSecondPass.vs", "ssao.fs");
		start();
		this.connectTextureUnits();
		setUniformBlockLocation("Matricies", 1);
		stop();
	}

	private int location_gposition;
	private int location_gnormal;
	private int location_noiseTexture;
	private int location_noiseScale;
	private int location_samples[];
	
	@Override
	protected void bindAttributes() {
		bindAttribute(0, "position");
		bindAttribute(1, "texCoords");
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_gposition = getUniformLocation("gPosition");
		location_gnormal = getUniformLocation("gNormal");
		location_noiseTexture = getUniformLocation("texNoise");
		location_noiseScale = getUniformLocation("noiseScale");
		
		location_samples = new int[64];
		for (int i = 0; i < location_samples.length; i++)
			location_samples[i] = getUniformLocation("samples[" + i + "]");
	}
	
	public void loadSamples(Vector3f[] samples) {
		for (int i = 0; i < location_samples.length; i++) {
			super.loadVector(location_samples[i], samples[i]);
		}
	}
	
	public void loadNoiseScale(float x, float y) {
		super.load2DVector(location_noiseScale, x, y);
	}
	
	public void connectTextureUnits() {
		super.loadInt(location_gposition, 0);
		super.loadInt(location_gnormal, 1);
		super.loadInt(location_noiseTexture, 2);
	}
	
}

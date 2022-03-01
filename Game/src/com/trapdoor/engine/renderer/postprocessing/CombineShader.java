package com.trapdoor.engine.renderer.postprocessing;

import com.trapdoor.engine.renderer.ShaderProgram;

/**
 * @author brett
 * @date Feb. 28, 2022
 * 
 */
public class CombineShader extends ShaderProgram {
	
	private int location_texture1;
	private int location_texture2;
	private int location_exposure;
	
	public CombineShader() {
		super("deferredSecondPass.vs", "postprocessing/combine.fs");
		start();
		connectTextureLocations();
		stop();
	}
	
	@Override
	protected void bindAttributes() {
		bindAttribute(0, "position");
		bindAttribute(1, "texCoords");
	}

	@Override
	protected void getAllUniformLocations() {
		location_texture1 = super.getUniformLocation("mainTexture");
		location_texture2 = super.getUniformLocation("combineTexture");
		location_exposure = super.getUniformLocation("exposure");
	}
	
	public void loadExposure(float exp) {
		super.loadFloat(location_exposure, exp);
	}
	
	public void connectTextureLocations() {
		super.loadInt(location_texture1, 0);
		super.loadInt(location_texture2, 1);
	}
	
}

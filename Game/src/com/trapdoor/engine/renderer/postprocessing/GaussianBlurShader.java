package com.trapdoor.engine.renderer.postprocessing;

import com.trapdoor.engine.renderer.ShaderProgram;

/**
 * @author brett
 * @date Feb. 28, 2022
 * 
 */
public class GaussianBlurShader extends ShaderProgram {
	
	public GaussianBlurShader() {
		super("deferredSecondPass.vs", "postprocessing/gaussianblur.fs");
	}

	private int location_horizontal;
	private int[] location_weight;
	
	@Override
	protected void bindAttributes() {
		bindAttribute(0, "position");
		bindAttribute(1, "texCoords");
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_weight = new int[5];
		location_horizontal = getUniformLocation("horizontal");
		
		for (int i = 0; i < location_weight.length; i++)
			location_weight[i] = getUniformLocation("weight[" + i + "]");
	}
	
	public void loadUseHorizontal(boolean useHorizontal) {
		super.loadBoolean(location_horizontal, useHorizontal);
	}
	
	public void loadWeights(float[] weights) {
		for (int i = 0; i < location_weight.length; i++)
			super.loadFloat(location_weight[i], weights[i]);
	}
	
}

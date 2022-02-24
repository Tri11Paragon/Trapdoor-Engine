package com.trapdoor.engine.renderer.ao;

import com.trapdoor.engine.renderer.ShaderProgram;

public class SSAOBlurShader extends ShaderProgram {

	public SSAOBlurShader() {
		super("deferredSecondPass.vs", "ssaoBlur.fs");
	}
	
	@Override
	protected void bindAttributes() {
		bindAttribute(0, "position");
		bindAttribute(1, "texCoords");
	}

	@Override
	protected void getAllUniformLocations() {
		
	}
	
}

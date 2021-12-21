package com.karl.Engine.skybox;

import com.game.engine.shaders.WorldShader;
import com.karl.Engine.utils.MyFile;

public class SkyboxShader extends WorldShader {

	private static final MyFile VERTEX_SHADER = new MyFile("skybox", "skyboxVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile("skybox", "skyboxFragment.glsl");

	public SkyboxShader() {
		super(VERTEX_SHADER.getPath(), FRAGMENT_SHADER.getPath());
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttributes();
		super.bindAttribute(0, "position");
	}
}

package com.karl.Engine.skybox;

import org.joml.Vector4f;

import com.karl.Engine.utils.MyFile;
import com.trapdoor.engine.renderer.WorldShader;

public class SkyboxShader extends WorldShader {

	private static final MyFile VERTEX_SHADER = new MyFile("skybox", "skyboxVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile("skybox", "skyboxFragment.glsl");

	private int location_color1;
	private int location_color2;
	private int location_useColor;
	
	public SkyboxShader() {
		super(VERTEX_SHADER.getPath(), FRAGMENT_SHADER.getPath());
	}
	
	@Override
	protected void getAllUniformLocations() {
		super.getAllUniformLocations();
		location_color1 = getUniformLocation("color1");
		location_color2 = getUniformLocation("color2");
		location_useColor = getUniformLocation("useColor");
		
		super.loadFloat(location_useColor, 0);
	}
	
	public void loadColors(Vector4f color1, Vector4f color2) {
		super.load4DVector(location_color1, color1);
		super.load4DVector(location_color2, color2);
		super.loadFloat(location_useColor, 1);
	}
	
	public void useTexture() {
		super.loadFloat(location_useColor, 0);
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttributes();
		super.bindAttribute(0, "position");
	}
}

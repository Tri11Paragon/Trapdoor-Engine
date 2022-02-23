package com.karl.Animation.renderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.karl.Engine.utils.MyFile;
import com.trapdoor.engine.renderer.WorldShader;

public class AnimatedModelShader extends WorldShader {

	private static final int MAX_JOINTS = 50;// max number of joints in a skeleton
	private static final int DIFFUSE_TEX_UNIT = 0;

	private static final MyFile VERTEX_SHADER = new MyFile("renderer", "animatedEntityVertex.glsl");
	private static final MyFile FRAGMENT_SHADER = new MyFile("renderer", "animatedEntityFragment.glsl");

	private int location_lightDirection;
	private int[] location_jointTransforms;
	
	/**
	 * Creates the shader program for the {@link AnimatedModelRenderer} by
	 * loading up the vertex and fragment shader code files. It also gets the
	 * location of all the specified uniform variables, and also indicates that
	 * the diffuse texture will be sampled from texture unit 0.
	 */
	public AnimatedModelShader() {
		super(VERTEX_SHADER.getPath(), FRAGMENT_SHADER.getPath());
		connectTextureUnits();
	}
	
	@Override
	protected void getAllUniformLocations() {
		super.getAllUniformLocations();
		location_jointTransforms = new int[MAX_JOINTS];
		for (int i = 0; i < MAX_JOINTS; i++)
			this.location_jointTransforms[i] = super.getUniformLocation("jointTransforms[" + i + "]");
		this.location_lightDirection = super.getUniformLocation("lightDirection");
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttributes();
		bindAttribute(0, "position");
		bindAttribute(1, "textureCoords");
		bindAttribute(2, "normal");
		bindAttribute(3, "jointIndices");
		bindAttribute(4, "weights");
	}
	
	public void loadTransforms(Matrix4f[] jointTransforms) {
		for (int i = 0; i < jointTransforms.length; i++)
			super.loadMatrix(location_jointTransforms[i], jointTransforms[i]);
	}
	
	public void loadLightDirection(Vector3f dir) {
		super.loadVector(location_lightDirection, dir);
	}

	/**
	 * Indicates which texture unit the diffuse texture should be sampled from.
	 */
	private void connectTextureUnits() {
		super.start();
		super.loadInt(super.getUniformLocation("diffuseMap"), DIFFUSE_TEX_UNIT);
		super.stop();
	}

}

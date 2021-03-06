package com.trapdoor.engine.renderer;

import org.joml.Vector3d;
import org.joml.Vector3f;

import com.trapdoor.engine.display.DisplayManager;

/**
 * @author brett
 * @date Jan. 10, 2022
 * 
 */
public class DeferredSecondPassShader extends WorldShader {
	
	public static final int MAX_LIGHTS = 32;
	// number of floats
	public static final int STRIDE_SIZE = 8;
	
	private int location_gposition;
	private int location_gnormal;
	private int location_gcolor;
	private int location_gRenderState;
	private int location_view;
	private int location_lightDir;
	private int location_lightDirColor;
	private int location_depthTexture;
	
	public DeferredSecondPassShader() {
		super("deferredSecondPass.vs", "deferredSecondPass.fs");
		this.start();
		setUniformBlockLocation("Lightings", 2);
		connectTextureUnits();
		loadLightDir(DisplayManager.lightDirection);
		loadLightColor(DisplayManager.lightColor);
		this.stop();
	}
	
	@Override
	protected void bindAttributes() {
		bindAttribute(0, "position");
		bindAttribute(1, "texCoords");
	}

	@Override
	protected void getAllUniformLocations() {
		super.getAllUniformLocations();
		location_gposition = getUniformLocation("gPosition");
		location_gnormal = getUniformLocation("gNormal");
		location_gcolor = getUniformLocation("gAlbedoSpec");
		location_gRenderState = getUniformLocation("gRenderState");
		location_depthTexture = getUniformLocation("depthTexture");
		location_view = super.getUniformLocation("viewPos");
		location_lightDir = super.getUniformLocation("directLight");
		location_lightDirColor = super.getUniformLocation("directLightColor");
	}
	
	public void loadLightDir(Vector3f dir) {
		super.loadVector(location_lightDir, dir);
	}
	
	public void loadLightColor(Vector3f dir) {
		super.loadVector(location_lightDirColor, dir);
	}

	public void loadViewPos(Vector3d pos) {
		super.loadVector(location_view, pos);
	}
	
	public void connectTextureUnits() {
		super.loadInt(location_gposition, 0);
		super.loadInt(location_gnormal, 1);
		super.loadInt(location_gcolor, 2);
		super.loadInt(location_gRenderState, 3);
		super.loadInt(location_depthTexture, 4);
	}

}

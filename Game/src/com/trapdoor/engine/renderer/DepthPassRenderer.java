package com.trapdoor.engine.renderer;

import org.lwjgl.opengl.GL33;

/**
 * @author brett
 * @date Mar. 16, 2022
 * 
 */
public class DepthPassRenderer {
	
	private DepthPassShader shader;
	
	public DepthPassRenderer() {
		this.shader = new DepthPassShader();
	}
	
	public void start() {
		GL33.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GL33.glClear(GL33.GL_COLOR_BUFFER_BIT | GL33.GL_DEPTH_BUFFER_BIT);
		shader.start();
	}
	
	public void stop() {
		shader.stop();
	}
	
	public DepthPassShader getShader() {
		return shader;
	}
	
}

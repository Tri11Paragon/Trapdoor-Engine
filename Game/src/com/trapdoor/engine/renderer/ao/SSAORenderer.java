package com.trapdoor.engine.renderer.ao;

import org.lwjgl.opengl.GL33;

import com.trapdoor.engine.ProjectionMatrix;
import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.renderer.DeferredRenderer;

public class SSAORenderer implements Runnable {
	
	private SSAOShader ssaoShader;
	private SSAOBlurShader ssaoBlurShader;
	private ScreenSpaceAO ssao;
	
	public SSAORenderer() {
		this.ssaoShader = new SSAOShader();
		this.ssaoBlurShader = new SSAOBlurShader();
		this.ssao = new ScreenSpaceAO();
		this.ssaoShader.start();
		this.ssaoShader.loadNoiseScale(DisplayManager.WIDTH/4.0f, DisplayManager.HEIGHT/4.0f);
		this.ssaoShader.loadSamples(this.ssao.getSampleKernels());
		this.ssaoShader.stop();
		ProjectionMatrix.addProjectionUpdateListener(this);
	}
	
	public void render(DeferredRenderer renderer) {
		this.ssao.bindSSAOBuffer();
		this.ssaoShader.start();
		GL33.glClear(GL33.GL_COLOR_BUFFER_BIT);
		renderer.bindBuffersTextures();
		GL33.glActiveTexture(GL33.GL_TEXTURE4);
		GL33.glBindTexture(GL33.GL_TEXTURE_2D, this.ssao.getNoiseTexture());
		renderer.bindAndRenderQuad();
		
		this.ssaoShader.stop();

		this.ssao.bindBlurBuffer();
		this.ssaoBlurShader.start();
		GL33.glClear(GL33.GL_COLOR_BUFFER_BIT);
		GL33.glActiveTexture(GL33.GL_TEXTURE0);
		GL33.glBindTexture(GL33.GL_TEXTURE_2D, getSSAOTexture());
		renderer.bindAndRenderQuad();
		
		this.ssaoBlurShader.stop();
		
		this.ssao.unbindBuffer();
		GL33.glClear(GL33.GL_COLOR_BUFFER_BIT | GL33.GL_DEPTH_BUFFER_BIT);
		
	}
	
	public void cleanup() {
		this.ssaoShader.cleanUp();
		this.ssaoBlurShader.cleanUp();
		this.ssao.cleanup();
	}
	
	public int getSSAOTexture() {
		return this.ssao.getSSAOTexture();
	}
	
	public int getSSAOBluredTexture() {
		return this.ssao.getSSAOBluredTexture();
	}

	@Override
	public void run() {
		this.ssaoShader.start();
		this.ssaoShader.loadNoiseScale(DisplayManager.WIDTH/4.0f, DisplayManager.HEIGHT/4.0f);
		this.ssaoShader.stop();
		this.ssao.run();
	}
	
}

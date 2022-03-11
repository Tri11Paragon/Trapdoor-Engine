package com.trapdoor.engine.renderer.postprocessing;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL33;

import com.trapdoor.engine.ProjectionMatrix;
import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.renderer.DeferredRenderer;
import com.trapdoor.engine.tools.SettingsLoader;
import com.trapdoor.engine.tools.math.Maths;

/**
 * @author brett
 * @date Feb. 28, 2022
 * 
 */
public class BloomRenderer implements Runnable {

	private int hdrFBO;
	private int colorTexture1, colorTexture2;
	private int[] colorBuffers;
	
	private int blur1FBO, blur2FBO;
	private int blur1Texture, blur2Texture;
	private int multiRboDepth;
	
	private GaussianBlurShader blurShader;
	private CombineShader combineShader;
	
	public BloomRenderer() {
		blurShader = new GaussianBlurShader();
		combineShader = new CombineShader();
		create();
		ProjectionMatrix.addProjectionUpdateListener(this);
	}
	
	private void create() {
		hdrFBO = GL33.glGenFramebuffers();
		GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, hdrFBO);
		colorTexture1 = GL33.glGenTextures();
		colorTexture2 = GL33.glGenTextures();
		colorBuffers = new int[]{colorTexture1, colorTexture2};
		for (int i = 0; i < 2; i++)
		{
			GL33.glBindTexture(GL33.GL_TEXTURE_2D, colorBuffers[i]);
			GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGBA16F, DisplayManager.WIDTH, DisplayManager.HEIGHT, 0, GL33.GL_RGBA, GL33.GL_FLOAT, (ByteBuffer) null);
			GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_LINEAR);
			GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_LINEAR);
			GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_S, GL33.GL_CLAMP_TO_EDGE);
			GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_T, GL33.GL_CLAMP_TO_EDGE);
		    // attach texture to framebuffer
			GL33.glFramebufferTexture2D(GL33.GL_FRAMEBUFFER, GL33.GL_COLOR_ATTACHMENT0 + i, GL33.GL_TEXTURE_2D, colorBuffers[i], 0);
		}  
		GL33.glDrawBuffers(new int[] {GL33.GL_COLOR_ATTACHMENT0, GL33.GL_COLOR_ATTACHMENT1});
		
		multiRboDepth = GL33.glGenRenderbuffers();
		GL33.glBindRenderbuffer(GL33.GL_RENDERBUFFER, multiRboDepth);
		GL33.glRenderbufferStorage(GL33.GL_RENDERBUFFER, GL33.GL_DEPTH_COMPONENT32, DisplayManager.WIDTH, DisplayManager.HEIGHT);
		GL33.glFramebufferRenderbuffer(GL33.GL_FRAMEBUFFER, GL33.GL_DEPTH_ATTACHMENT, GL33.GL_RENDERBUFFER, multiRboDepth);
		
		blur1FBO = GL33.glGenFramebuffers();
		blur2FBO = GL33.glGenFramebuffers();
		blur1Texture = GL33.glGenTextures();
		blur2Texture = GL33.glGenTextures();
		
		GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, blur1FBO);
		GL33.glBindTexture(GL33.GL_TEXTURE_2D, blur1Texture);
	    GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGBA16F, DisplayManager.WIDTH, DisplayManager.HEIGHT, 0, GL33.GL_RGBA, GL33.GL_FLOAT, (ByteBuffer) null);
	    GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_LINEAR);
	    GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_LINEAR);
	    GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_S, GL33.GL_CLAMP_TO_EDGE);
	    GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_T, GL33.GL_CLAMP_TO_EDGE);
	    GL33.glFramebufferTexture2D(GL33.GL_FRAMEBUFFER, GL33.GL_COLOR_ATTACHMENT0, GL33.GL_TEXTURE_2D, blur1Texture, 0);
	    
	    GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, blur2FBO);
		GL33.glBindTexture(GL33.GL_TEXTURE_2D, blur2Texture);
	    GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGBA16F, DisplayManager.WIDTH, DisplayManager.HEIGHT, 0, GL33.GL_RGBA, GL33.GL_FLOAT, (ByteBuffer) null);
	    GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_LINEAR);
	    GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_LINEAR);
	    GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_S, GL33.GL_CLAMP_TO_EDGE);
	    GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_T, GL33.GL_CLAMP_TO_EDGE);
	    GL33.glFramebufferTexture2D(GL33.GL_FRAMEBUFFER, GL33.GL_COLOR_ATTACHMENT0, GL33.GL_TEXTURE_2D, blur2Texture, 0);
	    
		if (GL33.glCheckFramebufferStatus(GL33.GL_FRAMEBUFFER) != GL33.GL_FRAMEBUFFER_COMPLETE) {
			System.err.println("Error creating framebuffer! (2)");
			System.err.println(GL33.glGetError());
		}
		
	    GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, 0);
	}
	
	public void bindBloom() {
		GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, hdrFBO);
	}
	
	public int getBloomFBO() {
		return hdrFBO;
	}
	
	public void applyBlur(DeferredRenderer renderer) {
		boolean horizontal = true, first_iteration = true;
		final int amount = 10;
		blurShader.start();
		for (int i = 0; i < amount; i++)
		{
			GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, horizontal ? blur2FBO : blur1FBO); 
		    blurShader.loadUseHorizontal(horizontal);
		    GL33.glActiveTexture(GL33.GL_TEXTURE0);
		    GL33.glBindTexture(GL33.GL_TEXTURE_2D, first_iteration ? colorBuffers[1] : (horizontal ? blur1Texture : blur2Texture)); 
		    renderer.bindAndRenderQuad();
		    horizontal = !horizontal;
		    if (first_iteration)
		        first_iteration = false;
		}
		blurShader.stop();
		GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, 0); 
	}
	
	private float exposure;
	private final float exposureMultiplier = 0.25f;
	private final float exposureRangeMin = 0.5f;
	private final float exposureRangeMax = 2.5f;
	
	public void render(DeferredRenderer renderer) {
		combineShader.start();
		GL33.glActiveTexture(GL33.GL_TEXTURE0);
		GL33.glBindTexture(GL33.GL_TEXTURE_2D, colorTexture1);
		if (SettingsLoader.GRAPHICS_LEVEL < 1) {
			// TODO: compute shader?
			GL33.glGenerateMipmap(GL33.GL_TEXTURE_2D);
			float[] vec3 = new float[3];
			GL33.glGetTexImage(GL33.GL_TEXTURE_2D, 10, GL33.GL_RGB, GL33.GL_FLOAT, vec3);
			
			final float lum = 0.2126f * vec3[0] + 0.7152f * vec3[1] + 0.0722f * vec3[2];
			
			final float adjSpeed = 0.05f;
			
			exposure = Maths.lerp(exposure, 0.5f / lum * exposureMultiplier, adjSpeed);
			exposure = Maths.clamp(exposure, exposureRangeMin, exposureRangeMax);
			
			combineShader.loadExposure(1.0f);
		}
		GL33.glActiveTexture(GL33.GL_TEXTURE1);
		GL33.glBindTexture(GL33.GL_TEXTURE_2D, blur2Texture);
		
		renderer.bindAndRenderQuad();
		
		combineShader.stop();
	}
	
	public void unbindBloom() {
		GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, 0);
	}
	
	private void destroy() {
		GL33.glDeleteFramebuffers(hdrFBO);
		GL33.glDeleteFramebuffers(blur1FBO);
		GL33.glDeleteFramebuffers(blur2FBO);
		GL33.glDeleteRenderbuffers(multiRboDepth);
		GL33.glDeleteTextures(colorTexture1);
		GL33.glDeleteTextures(colorTexture2);
		GL33.glDeleteTextures(blur1Texture);
		GL33.glDeleteTextures(blur2Texture);
	}
	
	public void cleanup() {
		destroy();
		blurShader.cleanUp();
		combineShader.cleanUp();
	}
	
	@Override
	public void run() {
		destroy();
		create();
	}
	
}

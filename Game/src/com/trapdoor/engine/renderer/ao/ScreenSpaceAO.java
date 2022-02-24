package com.trapdoor.engine.renderer.ao;

import java.nio.ByteBuffer;
import java.util.Random;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;

import com.trapdoor.engine.display.DisplayManager;

public class ScreenSpaceAO implements Runnable {
	
	private final Random randomizer = new Random();
	
	private Vector3f[] sampleKernels = new Vector3f[64];
	private Vector3f[] ssaoNoise = new Vector3f[16];
	
	private int noiseTexture;
	private int ssaoBuffer;
	private int blurBuffer;
	private int colorBuffer;
	private int colorBlurBuffer;
	
	public ScreenSpaceAO() {
		// create sample data
		for (int i = 0; i < sampleKernels.length; i++) {
			sampleKernels[i] = new Vector3f(
					transformedRandom(),
					transformedRandom(),
					randomizer.nextFloat());
			sampleKernels[i] = sampleKernels[i].normalize();
			//sampleKernels[i].mul(randomizer.nextFloat());
			float scale = (float)i / 64.0f; 
			scale = lerp(0.1f, 1.0f, scale * scale);
			sampleKernels[i].mul(scale);
		}
		for (int i = 0; i < ssaoNoise.length; i++) {
			ssaoNoise[i] = new Vector3f(
					transformedRandom(),
					transformedRandom(),
					0.0f);
		}
		
		// create the noise texture
		noiseTexture = GL33.glGenTextures();
		GL33.glBindTexture(GL33.GL_TEXTURE_2D, noiseTexture);
		
		// put the noise data into a readable byte buffer
		ByteBuffer data = BufferUtils.createByteBuffer(ssaoNoise.length * Float.SIZE * 3);
		for (int i = 0; i < ssaoNoise.length; i++) {
			data.putFloat(ssaoNoise[i].x);
			data.putFloat(ssaoNoise[i].y);
			data.putFloat(ssaoNoise[i].y);
		}
		data.flip();
		
		// set the texture data to the noise data
		GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGBA32F, 4, 4, 0, GL33.GL_RGB, GL33.GL_FLOAT, data);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_NEAREST);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_NEAREST);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_S, GL33.GL_REPEAT);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_T, GL33.GL_REPEAT);  
		
		createFrameBuffer();
	}
	
	public void bindSSAOBuffer() {
		GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, ssaoBuffer);
	}
	
	public void bindBlurBuffer() {
		GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, blurBuffer);
	}
	
	public void unbindBuffer() {
		GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, 0);
	}
	
	private void createFrameBuffer() {
		// create ssao framebuffers
		ssaoBuffer = GL33.glGenFramebuffers();
		
		bindSSAOBuffer();

		colorBuffer = GL33.glGenTextures();
		GL33.glBindTexture(GL33.GL_TEXTURE_2D, colorBuffer);
		GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RED, DisplayManager.WIDTH, DisplayManager.HEIGHT, 0, GL33.GL_RED, GL33.GL_FLOAT, (ByteBuffer) null);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_NEAREST);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_NEAREST);
		  
		GL33.glFramebufferTexture2D(GL33.GL_FRAMEBUFFER, GL33.GL_COLOR_ATTACHMENT0, GL33.GL_TEXTURE_2D, colorBuffer, 0);  
		
		GL33.glDrawBuffers(new int[] {GL33.GL_COLOR_ATTACHMENT0});
		
		if (GL33.glCheckFramebufferStatus(GL33.GL_FRAMEBUFFER) != GL33.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Could not create ShadowMap FrameBuffer");
        }
		
		unbindBuffer();
		
		
		
		blurBuffer = GL33.glGenFramebuffers();
		bindBlurBuffer();

		colorBlurBuffer = GL33.glGenTextures();
		GL33.glBindTexture(GL33.GL_TEXTURE_2D, colorBlurBuffer);
		GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RED, DisplayManager.WIDTH, DisplayManager.HEIGHT, 0, GL33.GL_RED, GL33.GL_FLOAT, (ByteBuffer) null);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_NEAREST);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_NEAREST);
		
		GL33.glFramebufferTexture2D(GL33.GL_FRAMEBUFFER, GL33.GL_COLOR_ATTACHMENT0, GL33.GL_TEXTURE_2D, colorBlurBuffer, 0);  
		
		GL33.glDrawBuffers(new int[] {GL33.GL_COLOR_ATTACHMENT0});
		
		if (GL33.glCheckFramebufferStatus(GL33.GL_FRAMEBUFFER) != GL33.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Could not create ShadowMap FrameBuffer");
        }
		
		unbindBuffer();
	}
	
	public int getSSAOTexture() {
		return colorBuffer;
	}
	
	public int getSSAOBluredTexture() {
		return colorBlurBuffer;
	}
	
	public int getNoiseTexture() {
		return noiseTexture;
	}
	
	public Vector3f[] getSampleKernels() {
		return sampleKernels;
	}
	
	private void updateFrameBuffer() {
		deleteBuffers();
		createFrameBuffer();
	}
	
	private void deleteBuffers() {
		GL33.glDeleteTextures(colorBuffer);
		GL33.glDeleteTextures(colorBlurBuffer);
		GL33.glDeleteFramebuffers(ssaoBuffer);
		GL33.glDeleteFramebuffers(blurBuffer);
	}
	
	public void cleanup() {
		deleteBuffers();
		GL33.glDeleteTextures(noiseTexture);
	}
	
	private float lerp(float a, float b, float f) {
	    return a + f * (b - a);
	}  
	
	private float transformedRandom() {
		return randomizer.nextFloat() * 2.0f - 1.0f;
	}

	@Override
	public void run() {
		updateFrameBuffer();
	}
	
}

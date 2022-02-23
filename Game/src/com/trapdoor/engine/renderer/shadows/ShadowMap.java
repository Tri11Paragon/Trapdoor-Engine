package com.trapdoor.engine.renderer.shadows;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL33;

public class ShadowMap {
	
	public static int SHADOW_MAP_WIDTH = 1024;
	public static int SHADOW_MAP_HEIGHT = 1024;
	
	private final int depthMapFBO;
	private final int depthMap;
	
	public ShadowMap() {
		// create shadow map texture
		depthMap = GL33.glGenTextures();
		GL33.glBindTexture(GL33.GL_TEXTURE_2D, depthMap);
		GL33.glTexImage2D(
				GL33.GL_TEXTURE_2D, 
				0, 
				GL33.GL_DEPTH_COMPONENT, 
				SHADOW_MAP_WIDTH, 
				SHADOW_MAP_HEIGHT, 
				0, 
				GL33.GL_DEPTH_COMPONENT, 
				GL33.GL_FLOAT, 
				(ByteBuffer) null);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_NEAREST);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_NEAREST);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_S, GL33.GL_CLAMP_TO_EDGE);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_T, GL33.GL_CLAMP_TO_EDGE);
		
		// create the frame buffer for the shadow map
		depthMapFBO = GL33.glGenFramebuffers();
		
		// attach the texture to the frame buffer
		GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, depthMapFBO);
		GL33.glFramebufferTexture2D(GL33.GL_FRAMEBUFFER, GL33.GL_DEPTH_ATTACHMENT, GL33.GL_TEXTURE_2D, depthMap, 0);
		// only draw depth
		GL33.glDrawBuffer(GL33.GL_NONE);
		GL33.glReadBuffer(GL33.GL_NONE);
		
		if (GL33.glCheckFramebufferStatus(GL33.GL_FRAMEBUFFER) != GL33.GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Could not create ShadowMap FrameBuffer");
        }
		
        GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, 0);
		
	}
	
	public int getDepthMapTexture() {
        return depthMap;
    }

    public int getDepthMapFBO() {
        return depthMapFBO;
    }

	
	public void cleanup() {
		GL33.glDeleteTextures(depthMap);
		GL33.glDeleteFramebuffers(depthMapFBO);
	}
	
}

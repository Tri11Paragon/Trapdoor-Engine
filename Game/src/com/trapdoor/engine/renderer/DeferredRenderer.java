package com.trapdoor.engine.renderer;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.lwjgl.opengl.GL33;

import com.trapdoor.engine.ProjectionMatrix;
import com.trapdoor.engine.camera.Camera;
import com.trapdoor.engine.datatypes.lighting.ExtensibleLightingArray;
import com.trapdoor.engine.datatypes.lighting.Light;
import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.renderer.ao.SSAORenderer;
import com.trapdoor.engine.tools.SettingsLoader;
import com.trapdoor.engine.world.World;
import com.trapdoor.engine.world.entities.Entity;

/**
 * @author brett
 * @date Jan. 10, 2022
 * 
 */
public class DeferredRenderer implements Runnable {
	
	
	// geometry buffer
	private int gBuffer;
	// world information textures/
	private int gPosition;
	private int gNormal;
	private int gColorSpec;
	private int gRenderState;
	private int rboDepth;
	private int quadVAO = 0;
	private int quadVBO;
	
	// rendered out multisampled GBuffer
	private int multiGBuffer;
	private int multiGPosition;
	private int multiGNormal;
	private int multiColorSpec;
	private int multiGRenderState;
	private int multiRboDepth;
	
	private DeferredFirstPassShader firstPassShader;
	private DeferredSecondPassShader secondPassShader;
	
	private Camera camera;
	
	private ExtensibleLightingArray lights = new ExtensibleLightingArray();
	
	public DeferredRenderer(Camera camera) {
		this.camera = camera;
		firstPassShader = new DeferredFirstPassShader();
		secondPassShader = new DeferredSecondPassShader();
		ProjectionMatrix.addProjectionUpdateListener(this);
		
		
		final float quadVertices[] = {
	            // positions        	// texture Coords
	            -1.0f,  1.0f, 0.0f, 	0.0f, 1.0f,
	            -1.0f, -1.0f, 0.0f, 	0.0f, 0.0f,
	             1.0f,  1.0f, 0.0f, 	1.0f, 1.0f,
	             1.0f, -1.0f, 0.0f, 	1.0f, 0.0f,
	       };
		quadVAO = GL33.glGenVertexArrays();
		quadVBO = GL33.glGenBuffers();
		GL33.glBindVertexArray(quadVAO);
		GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, quadVBO);
		GL33.glBufferData(GL33.GL_ARRAY_BUFFER, quadVertices, GL33.GL_STATIC_DRAW);
		GL33.glEnableVertexAttribArray(0);
		GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 5 * 4, 0);
		GL33.glEnableVertexAttribArray(1);
		GL33.glVertexAttribPointer(1, 2, GL33.GL_FLOAT, false, 5 * 4, (3 * 4));
		
		createFrameBuffers();
	}
	
	private void createFrameBuffers() {
		// generate the frame buffer
		gBuffer = GL33.glGenFramebuffers();
		GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, gBuffer);
		
		// create and bind the positions texture
		gPosition = GL33.glGenTextures();
		GL33.glBindTexture(GL33.GL_TEXTURE_2D_MULTISAMPLE, gPosition);
		// TODO: use 32F?
		GL33.glTexImage2DMultisample(GL33.GL_TEXTURE_2D_MULTISAMPLE, SettingsLoader.SAMPLES, GL33.GL_RGBA16F, DisplayManager.WIDTH, DisplayManager.HEIGHT, true);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D_MULTISAMPLE, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_NEAREST);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D_MULTISAMPLE, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_NEAREST);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D_MULTISAMPLE, GL33.GL_TEXTURE_WRAP_S, GL33.GL_CLAMP_TO_EDGE);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D_MULTISAMPLE, GL33.GL_TEXTURE_WRAP_T, GL33.GL_CLAMP_TO_EDGE);
		GL33.glFramebufferTexture2D(GL33.GL_FRAMEBUFFER, GL33.GL_COLOR_ATTACHMENT0, GL33.GL_TEXTURE_2D_MULTISAMPLE, gPosition, 0);
		
		// create and bind the normals texture
		gNormal = GL33.glGenTextures();
		GL33.glBindTexture(GL33.GL_TEXTURE_2D_MULTISAMPLE, gNormal);
		GL33.glTexImage2DMultisample(GL33.GL_TEXTURE_2D_MULTISAMPLE, SettingsLoader.SAMPLES, GL33.GL_RGBA16F, DisplayManager.WIDTH, DisplayManager.HEIGHT, true);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D_MULTISAMPLE, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_NEAREST);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D_MULTISAMPLE, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_NEAREST);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D_MULTISAMPLE, GL33.GL_TEXTURE_WRAP_S, GL33.GL_CLAMP_TO_EDGE);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D_MULTISAMPLE, GL33.GL_TEXTURE_WRAP_T, GL33.GL_CLAMP_TO_EDGE);
		GL33.glFramebufferTexture2D(GL33.GL_FRAMEBUFFER, GL33.GL_COLOR_ATTACHMENT1, GL33.GL_TEXTURE_2D_MULTISAMPLE, gNormal, 0);
		
		// create and bind the color buffer (with specular component in the alpha channel)
		gColorSpec = GL33.glGenTextures();
		GL33.glBindTexture(GL33.GL_TEXTURE_2D_MULTISAMPLE, gColorSpec);
		GL33.glTexImage2DMultisample(GL33.GL_TEXTURE_2D_MULTISAMPLE, SettingsLoader.SAMPLES, GL33.GL_RGBA16F, DisplayManager.WIDTH, DisplayManager.HEIGHT, true);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D_MULTISAMPLE, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_NEAREST);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D_MULTISAMPLE, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_NEAREST);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D_MULTISAMPLE, GL33.GL_TEXTURE_WRAP_S, GL33.GL_CLAMP_TO_EDGE);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D_MULTISAMPLE, GL33.GL_TEXTURE_WRAP_T, GL33.GL_CLAMP_TO_EDGE);
		GL33.glFramebufferTexture2D(GL33.GL_FRAMEBUFFER, GL33.GL_COLOR_ATTACHMENT2, GL33.GL_TEXTURE_2D_MULTISAMPLE, gColorSpec, 0);
		
		gRenderState = GL33.glGenTextures();
		GL33.glBindTexture(GL33.GL_TEXTURE_2D_MULTISAMPLE, gRenderState);
		GL33.glTexImage2DMultisample(GL33.GL_TEXTURE_2D_MULTISAMPLE, SettingsLoader.SAMPLES, GL33.GL_RGBA, DisplayManager.WIDTH, DisplayManager.HEIGHT, true);		
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D_MULTISAMPLE, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_NEAREST);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D_MULTISAMPLE, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_NEAREST);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D_MULTISAMPLE, GL33.GL_TEXTURE_WRAP_S, GL33.GL_CLAMP_TO_EDGE);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D_MULTISAMPLE, GL33.GL_TEXTURE_WRAP_T, GL33.GL_CLAMP_TO_EDGE);
		GL33.glFramebufferTexture2D(GL33.GL_FRAMEBUFFER, GL33.GL_COLOR_ATTACHMENT3, GL33.GL_TEXTURE_2D_MULTISAMPLE, gRenderState, 0);
		
		GL33.glDrawBuffers(new int[] {GL33.GL_COLOR_ATTACHMENT0, GL33.GL_COLOR_ATTACHMENT1, GL33.GL_COLOR_ATTACHMENT2, GL33.GL_COLOR_ATTACHMENT3});
		
		rboDepth = GL33.glGenRenderbuffers();
		GL33.glBindRenderbuffer(GL33.GL_RENDERBUFFER, rboDepth);
		GL33.glRenderbufferStorageMultisample(GL33.GL_RENDERBUFFER, SettingsLoader.SAMPLES, GL33.GL_DEPTH_COMPONENT32, DisplayManager.WIDTH, DisplayManager.HEIGHT);
		GL33.glFramebufferRenderbuffer(GL33.GL_FRAMEBUFFER, GL33.GL_DEPTH_ATTACHMENT, GL33.GL_RENDERBUFFER, rboDepth);
		
		if (GL33.glCheckFramebufferStatus(GL33.GL_FRAMEBUFFER) != GL33.GL_FRAMEBUFFER_COMPLETE) {
			System.err.println("Error creating framebuffer!");
			System.err.println(GL33.glGetError());
		}
		GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, 0);
		
		// create the rendered out multi sampled buffer
		
		multiGBuffer = GL33.glGenFramebuffers();
		GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, multiGBuffer);
		
		// create and bind the positions texture
		multiGPosition = GL33.glGenTextures();
		GL33.glBindTexture(GL33.GL_TEXTURE_2D, multiGPosition);
		// TODO: use 32F?
		GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGBA16F, DisplayManager.WIDTH, DisplayManager.HEIGHT, 0, GL33.GL_RGBA, GL33.GL_FLOAT, (ByteBuffer) null);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_NEAREST);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_NEAREST);
		GL33.glFramebufferTexture2D(GL33.GL_FRAMEBUFFER, GL33.GL_COLOR_ATTACHMENT0, GL33.GL_TEXTURE_2D, multiGPosition, 0);

		// create and bind the normals texture
		multiGNormal = GL33.glGenTextures();
		GL33.glBindTexture(GL33.GL_TEXTURE_2D, multiGNormal);
		GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGBA16F, DisplayManager.WIDTH, DisplayManager.HEIGHT, 0, GL33.GL_RGBA, GL33.GL_FLOAT, (ByteBuffer) null);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_NEAREST);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_NEAREST);
		GL33.glFramebufferTexture2D(GL33.GL_FRAMEBUFFER, GL33.GL_COLOR_ATTACHMENT1, GL33.GL_TEXTURE_2D, multiGNormal, 0);

		// create and bind the color buffer (with specular component in the alpha
		// channel)
		multiColorSpec = GL33.glGenTextures();
		GL33.glBindTexture(GL33.GL_TEXTURE_2D, multiColorSpec);
		GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGBA16F, DisplayManager.WIDTH, DisplayManager.HEIGHT, 0, GL33.GL_RGBA, GL33.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_NEAREST);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_NEAREST);
		GL33.glFramebufferTexture2D(GL33.GL_FRAMEBUFFER, GL33.GL_COLOR_ATTACHMENT2, GL33.GL_TEXTURE_2D, multiColorSpec, 0);

		multiGRenderState = GL33.glGenTextures();
		GL33.glBindTexture(GL33.GL_TEXTURE_2D, multiGRenderState);
		GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGBA, DisplayManager.WIDTH, DisplayManager.HEIGHT, 0, GL33.GL_RGBA, GL33.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_NEAREST);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_NEAREST);
		GL33.glFramebufferTexture2D(GL33.GL_FRAMEBUFFER, GL33.GL_COLOR_ATTACHMENT3, GL33.GL_TEXTURE_2D, multiGRenderState, 0);

		GL33.glDrawBuffers(new int[] { GL33.GL_COLOR_ATTACHMENT0, GL33.GL_COLOR_ATTACHMENT1, GL33.GL_COLOR_ATTACHMENT2, GL33.GL_COLOR_ATTACHMENT3 });

		multiRboDepth = GL33.glGenRenderbuffers();
		GL33.glBindRenderbuffer(GL33.GL_RENDERBUFFER, multiRboDepth);
		GL33.glRenderbufferStorage(GL33.GL_RENDERBUFFER, GL33.GL_DEPTH_COMPONENT32, DisplayManager.WIDTH,
				DisplayManager.HEIGHT);
		GL33.glFramebufferRenderbuffer(GL33.GL_FRAMEBUFFER, GL33.GL_DEPTH_ATTACHMENT, GL33.GL_RENDERBUFFER, multiRboDepth);

		if (GL33.glCheckFramebufferStatus(GL33.GL_FRAMEBUFFER) != GL33.GL_FRAMEBUFFER_COMPLETE) {
			System.err.println("Error creating framebuffer! (2)");
			System.err.println(GL33.glGetError());
		}
		GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, 0);
		
	}
	
	private void updateFrameBuffers() {
		destroyFrameBuffers();
		createFrameBuffers();
	}
	
	public void startFirstPass(World world) {
		GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, gBuffer);
		//GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, PolygonCommand.POLYMODE);
		//Vector3f skyColor = DisplayManager.getClearColor();
		//GL33.glClearColor(skyColor.x, skyColor.y, skyColor.z, 1.0f);
		GL33.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GL33.glClear(GL33.GL_COLOR_BUFFER_BIT | GL33.GL_DEPTH_BUFFER_BIT);
		
		GL33.glActiveTexture(GL33.GL_TEXTURE5);
		GL33.glBindTexture(GL33.GL_TEXTURE_2D, world.getShadowMap().getDepthMapTexture());
		
	}
	
	public void enableMainShaders() {
		firstPassShader.start();
	}
	
	public void endFirstPass() {
		// blit positions
		GL33.glBindFramebuffer(GL33.GL_READ_FRAMEBUFFER, gBuffer);
		GL33.glBindFramebuffer(GL33.GL_DRAW_FRAMEBUFFER, multiGBuffer);
		GL33.glReadBuffer(GL33.GL_COLOR_ATTACHMENT0);
		GL33.glDrawBuffer(GL33.GL_COLOR_ATTACHMENT0);
		GL33.glBlitFramebuffer(0, 0, DisplayManager.WIDTH, DisplayManager.HEIGHT, 0, 0, DisplayManager.WIDTH, DisplayManager.HEIGHT, GL33.GL_COLOR_BUFFER_BIT, GL33.GL_NEAREST);
		// blit normals
		GL33.glBindFramebuffer(GL33.GL_READ_FRAMEBUFFER, gBuffer);
		GL33.glBindFramebuffer(GL33.GL_DRAW_FRAMEBUFFER, multiGBuffer);
		GL33.glReadBuffer(GL33.GL_COLOR_ATTACHMENT1);
		GL33.glDrawBuffer(GL33.GL_COLOR_ATTACHMENT1);
		GL33.glBlitFramebuffer(0, 0, DisplayManager.WIDTH, DisplayManager.HEIGHT, 0, 0, DisplayManager.WIDTH, DisplayManager.HEIGHT, GL33.GL_COLOR_BUFFER_BIT, GL33.GL_NEAREST);
		// blit color spec
		GL33.glBindFramebuffer(GL33.GL_READ_FRAMEBUFFER, gBuffer);
		GL33.glBindFramebuffer(GL33.GL_DRAW_FRAMEBUFFER, multiGBuffer);
		GL33.glReadBuffer(GL33.GL_COLOR_ATTACHMENT2);
		GL33.glDrawBuffer(GL33.GL_COLOR_ATTACHMENT2);
		GL33.glBlitFramebuffer(0, 0, DisplayManager.WIDTH, DisplayManager.HEIGHT, 0, 0, DisplayManager.WIDTH, DisplayManager.HEIGHT, GL33.GL_COLOR_BUFFER_BIT, GL33.GL_NEAREST);
		// blit render state
		GL33.glBindFramebuffer(GL33.GL_READ_FRAMEBUFFER, gBuffer);
		GL33.glBindFramebuffer(GL33.GL_DRAW_FRAMEBUFFER, multiGBuffer);
		GL33.glReadBuffer(GL33.GL_COLOR_ATTACHMENT3);
		GL33.glDrawBuffer(GL33.GL_COLOR_ATTACHMENT3);
		GL33.glBlitFramebuffer(0, 0, DisplayManager.WIDTH, DisplayManager.HEIGHT, 0, 0, DisplayManager.WIDTH, DisplayManager.HEIGHT, GL33.GL_COLOR_BUFFER_BIT, GL33.GL_NEAREST);
		
		GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, 0);
		GL33.glClear(GL33.GL_COLOR_BUFFER_BIT | GL33.GL_DEPTH_BUFFER_BIT);
	}
	
	public void bindBuffersTextures() {
		GL33.glActiveTexture(GL33.GL_TEXTURE0);
		GL33.glBindTexture(GL33.GL_TEXTURE_2D, multiGPosition);
		GL33.glActiveTexture(GL33.GL_TEXTURE1);
		GL33.glBindTexture(GL33.GL_TEXTURE_2D, multiGNormal);
		GL33.glActiveTexture(GL33.GL_TEXTURE2);
		GL33.glBindTexture(GL33.GL_TEXTURE_2D, multiColorSpec);
		GL33.glActiveTexture(GL33.GL_TEXTURE3);
		GL33.glBindTexture(GL33.GL_TEXTURE_2D, multiGRenderState);
	}
	
	public void bindAndRenderQuad() {
		GL33.glBindVertexArray(quadVAO);
		GL33.glDrawArrays(GL33.GL_TRIANGLE_STRIP, 0, 4);
		GL33.glBindVertexArray(0);
	}
	
	public void runSecondPass(SSAORenderer renderer) {
		secondPassShader.start();
		secondPassShader.loadViewMatrix(camera.getViewMatrix());
		secondPassShader.loadViewPos(camera.getPosition());

		lights.updateUBO();
		lights.clear();
		
		bindBuffersTextures();
		GL33.glActiveTexture(GL33.GL_TEXTURE4);
		GL33.glBindTexture(GL33.GL_TEXTURE_2D, renderer.getSSAOBluredTexture());
		
		bindAndRenderQuad();
	    
	    secondPassShader.stop();
	}
	
	public DeferredFirstPassShader getShader() {
		return firstPassShader;
	}
	
	public void cleanup() {
		destroyFrameBuffers();
		secondPassShader.cleanUp();
		firstPassShader.cleanUp();
	}
	
	private void destroyFrameBuffers() {
		GL33.glDeleteFramebuffers(gBuffer);
		GL33.glDeleteTextures(gPosition);
		GL33.glDeleteTextures(gNormal);
		GL33.glDeleteTextures(gColorSpec);
		GL33.glDeleteTextures(gRenderState);
		GL33.glDeleteRenderbuffers(rboDepth);
	}
	
	public void addLightingArray(ArrayList<Light> lights, Entity e) {
		this.lights.add(lights, e);
	}

	@Override
	public void run() {
		updateFrameBuffers();
	}
	
}

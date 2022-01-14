package com.game.engine;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL33;

import com.game.engine.shaders.DeferredSecondPassShader;

/**
 * @author brett
 * @date Jan. 13, 2022
 * 
 */
public class UBOLoader {
	
	// TODO: this for all the shader variables
	
	private static int lightingUBO = -1;
	
	public static void createLightingUBO() {
		// prevent duplicate UBOs from being generated.
		if (lightingUBO != -1)
			return;
		lightingUBO = GL33.glGenBuffers();
		GL33.glBindBuffer(GL33.GL_UNIFORM_BUFFER, lightingUBO);
		// max lights * number of floats * 4 bytes per float
		GL33.glBufferData(GL33.GL_UNIFORM_BUFFER, DeferredSecondPassShader.MAX_LIGHTS * DeferredSecondPassShader.STRIDE_SIZE * 4, GL33.GL_STATIC_DRAW);
		// uniform location (2 since the UI engine uses UBO 0)
		GL33.glBindBufferBase(GL33.GL_UNIFORM_BUFFER, 2, lightingUBO);
		GL33.glBindBuffer(GL33.GL_UNIFORM_BUFFER, 0);
	}
	
	public static void updateLightingUBO(float[] data) {
		GL15.glBindBuffer(GL33.GL_UNIFORM_BUFFER, lightingUBO);
		GL33.glBufferSubData(GL33.GL_UNIFORM_BUFFER, 0, data);
		GL15.glBindBuffer(GL33.GL_UNIFORM_BUFFER, 0);
	}
	
	public static void cleanup() {
		GL33.glDeleteBuffers(lightingUBO);
	}
	
	
}

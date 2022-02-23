package com.trapdoor.engine;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL33;

import com.trapdoor.engine.renderer.DeferredSecondPassShader;

/**
 * @author brett
 * @date Jan. 13, 2022
 * 
 */
public class UBOLoader {
	
	// TODO: this for all the shader variables
	
	private static int lightingUBO = -1;
	private static int matrixUBO = -1;
	
	private static final int MATRIX_COUNT = 5;
	private static float[] projectMatrixBuffer = new float[16];
	private static float[] orthoMatrixBuffer = new float[16];
	private static float[] shadowMatrixBuffer = new float[16];
	private static float[] viewMatrixBuffer = new float[16];
	private static float[] matrixData = new float[MATRIX_COUNT * 16];
	
	
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
	
	public static void createMatrixUBO() {
		if (matrixUBO != -1)
			return;
		matrixUBO = GL33.glGenBuffers();
		GL33.glBindBuffer(GL33.GL_UNIFORM_BUFFER, matrixUBO);
		// max lights * number of floats * 4 bytes per float
		GL33.glBufferData(GL33.GL_UNIFORM_BUFFER, MATRIX_COUNT * 16 * 4, GL33.GL_STATIC_DRAW);
		// uniform location (2 since the UI engine uses UBO 0)
		GL33.glBindBufferBase(GL33.GL_UNIFORM_BUFFER, 1, matrixUBO);
		GL33.glBindBuffer(GL33.GL_UNIFORM_BUFFER, 0);
	}
	
	public static void updateMatrixUBO() {
		// sends a single update command to the GPU rather than doing the perspective and view matrices individually.
		GL33.glBindBuffer(GL33.GL_UNIFORM_BUFFER, matrixUBO);
		GL33.glBufferSubData(GL33.GL_UNIFORM_BUFFER, 0, matrixData);
		GL33.glBindBuffer(GL33.GL_UNIFORM_BUFFER, 0);
	}
	
	public static void updateLightingUBO(float[] data) {
		GL33.glBindBuffer(GL33.GL_UNIFORM_BUFFER, lightingUBO);
		GL33.glBufferSubData(GL33.GL_UNIFORM_BUFFER, 0, data);
		GL33.glBindBuffer(GL33.GL_UNIFORM_BUFFER, 0);
	}
	
	public static void cleanup() {
		GL33.glDeleteBuffers(lightingUBO);
		GL33.glDeleteBuffers(matrixUBO);
	}
	
	public static synchronized void updateProjectionMatrix(Matrix4f matrix) {
		matrix.get(projectMatrixBuffer);
		
		for (int i = 0; i < projectMatrixBuffer.length; i++)
			matrixData[i] = projectMatrixBuffer[i];
	}
	
	public static synchronized void updateViewMatrix(Matrix4f matrix) {
		matrix.get(viewMatrixBuffer);
		
		for (int i = 0; i < viewMatrixBuffer.length; i++)
			matrixData[i + 16] = viewMatrixBuffer[i];
	}
	
	public static synchronized void updateProjectViewMatrix(Matrix4f matrix) {
		matrix.get(viewMatrixBuffer);
		
		for (int i = 0; i < viewMatrixBuffer.length; i++)
			matrixData[i + 32] = viewMatrixBuffer[i];
	}
	
	public static synchronized void updateOrthoMatrix(Matrix4f matrix) {
		matrix.get(orthoMatrixBuffer);
		
		for (int i = 0; i < orthoMatrixBuffer.length; i++)
			matrixData[i + 48] = orthoMatrixBuffer[i];
	}
	
	public static synchronized void updateShadowMatrix(Matrix4f matrix) {
		matrix.get(shadowMatrixBuffer);
		
		for (int i = 0; i < shadowMatrixBuffer.length; i++)
			matrixData[i + 64] = shadowMatrixBuffer[i];
	}
	
	
}

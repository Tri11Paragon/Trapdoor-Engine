package com.trapdoor.engine.renderer.debug;

import org.lwjgl.opengl.GL33;

import com.trapdoor.engine.VAOLoader;
import com.trapdoor.engine.datatypes.ogl.obj.VAO;
import com.trapdoor.engine.tools.math.Maths;

public class TextureRenderer {

	private static VAO quad;
	private static TextureShader shader;
	
	public static void init() {
		float[] positions = {0, 1, 0, 0, 1, 1, 1, 0};
		quad = VAOLoader.loadToVAO(positions, 2);
		shader = new TextureShader();
	}
	
	public static void renderTexture(int id, int x, int y, int width, int height) {
		shader.start();
		
		GL33.glDisable(GL33.GL_CULL_FACE);
		
		GL33.glBindVertexArray(quad.getVaoID());
		GL33.glEnableVertexAttribArray(0);
		
		GL33.glActiveTexture(GL33.GL_TEXTURE0);
		GL33.glBindTexture(GL33.GL_TEXTURE_2D, id);
		
		shader.loadTransformation(Maths.createTransformationMatrix(x, y, width, height));
		GL33.glDrawArrays(GL33.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		
		GL33.glDisableVertexAttribArray(0);
		GL33.glEnable(GL33.GL_CULL_FACE);
		shader.stop();
	}
	
	public static void cleanup() {
		shader.cleanUp();
	}
	
}

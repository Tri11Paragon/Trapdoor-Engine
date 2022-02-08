package com.karl.Engine.skybox;

import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.karl.Engine.openglObjects.Vao;
import com.trapdoor.engine.camera.ICamera;
import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.display.IDisplay;

public class SkyboxRenderer {

	private static final float SIZE = 200;

	private SkyboxShader shader;
	private Vao box;

	public SkyboxRenderer() {
		this.shader = new SkyboxShader();
		this.box = CubeGenerator.generateCube(SIZE);
	}

	/**
	 * Renders the skybox.
	 * 
	 * @param camera
	 *            - the scene's camera.
	 */
	public void render(ICamera camera) {
		prepare(camera);
		box.bind(0);
		
		// TODO: maybe do this once? somehow BIG TODO TODO TODO
		IDisplay display = DisplayManager.getCurrentDisplay();
		
		if (display.usingSkyTexture()) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, display.getSkyTexture().getID());
			shader.useTexture();
		} else {
			// maybe just make two methods for this? TODO
			Vector4f[] colr = display.getSkyColors();
			shader.loadColors(colr[0], colr[1]);
		}
		
		GL11.glDrawElements(GL11.GL_TRIANGLES, box.getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
		box.unbind(0);
		shader.stop();
	}

	/**
	 * Delete the shader when the game closes.
	 */
	public void cleanUp() {
		shader.cleanUp();
	}

	/**
	 * Starts the shader, loads the projection-view matrix to the uniform
	 * variable, and sets some OpenGL state which should be mostly
	 * self-explanatory.
	 * 
	 * @param camera
	 *            - the scene's camera.
	 */
	private void prepare(ICamera camera) {
		shader.start();
		//OpenGlUtils.disableBlending();
		//OpenGlUtils.enableDepthTesting(true);
		//OpenGlUtils.cullBackFaces(true);
		//OpenGlUtils.antialias(false);
	}

}

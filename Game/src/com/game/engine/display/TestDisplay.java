package com.game.engine.display;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;

import com.game.engine.Loader;
import com.game.engine.camera.FreecamCamera;
import com.game.engine.datatypes.BlockModelVAO;
import com.game.engine.shaders.AtlasShader;
import com.game.engine.tools.math.Maths;

/**
 * @author brett
 * @date Oct. 18, 2021
 * 
 */
public class TestDisplay extends IDisplay {

	private static final float vertices[] = {
		    // positions        // texture coords
		     1f,  1f, 0.0f,  1.0f, 1.0f,   // top right
		     1f,  0f, 0.0f,  1.0f, 0.0f,   // bottom right
		     0f,  0f, 0.0f,  0.0f, 0.0f,   // bottom left
		     0f,  1f, 0.0f,  0.0f, 1.0f    // top left 
		};
	private static final int indices[] = {
	        0, 1, 3, // first triangle
	        1, 2, 3  // second triangle
	    };
	
	private FreecamCamera camera;
	private Matrix4f view;
	private AtlasShader shader;
	private BlockModelVAO vao;
	
	@Override
	public void onCreate() {
		camera = new FreecamCamera();
		shader = new AtlasShader("test.vs", "test.fs");
		vao = Loader.loadToVAO(vertices, indices);
	}

	@Override
	public void onSwitch() {
		
	}

	float z = 0;
	float dir = 0.01f;
	
	@Override
	public void render() {
		camera.move();
		this.view = Maths.createViewMatrix(camera);
		
		shader.start();
		GL30.glBindVertexArray(vao.getVaoID());
		GL30.glEnableVertexAttribArray(0);
		GL30.glEnableVertexAttribArray(1);
		
		shader.loadViewMatrix(view);
		GL30.glActiveTexture(GL30.GL_TEXTURE0);
		GL30.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, Loader.loadTexture("1531688878833.png"));
		
		shader.loadTranslationMatrix(Maths.createTransformationMatrix(0, 0, 0.0f, 500, 500));
		GL30.glDrawElements(GL30.GL_TRIANGLES, vao.getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
		
		GL30.glDisableVertexAttribArray(0);
		GL30.glDisableVertexAttribArray(1);
		shader.stop();
		
		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onLeave() {
		
	}

	@Override
	public void onDestory() {
		
	}
	
}

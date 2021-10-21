package com.game.engine.display;

import org.joml.Matrix4f;
import com.game.engine.camera.FreecamCamera;
import com.game.engine.datatypes.world.Entity;
import com.game.engine.renderer.EntityRenderer;
import com.game.engine.shaders.AtlasShader;
import com.game.engine.shaders.WorldShader;
import com.game.engine.tools.math.Maths;

/**
 * @author brett
 * @date Oct. 18, 2021
 * 
 */
public class TestDisplay extends IDisplay {
	
	private FreecamCamera camera;
	private Matrix4f view;
	private AtlasShader shader;
	private WorldShader wshader;
	private Entity e;
	
	private String[] test = {"1540093100131.jpg", "1540046285552.jpg", "1534874238973.png", "1531696571587.jpg"};
	
	@Override
	public void onCreate() {
		camera = new FreecamCamera();
		shader = new AtlasShader("main.vs", "main.fs");
		wshader = new AtlasShader("main.vs", "main.fs");
		for (int i = 0; i < 10000; i++) {
			e = new Entity(i%DisplayManager.WIDTH*500, 50, 500, 500, test[i%test.length], true).enable();
			if (i % 2 == 0)
				e.disable();
		}
	}

	@Override
	public void onSwitch() {
		e.enable();
	}

	float z = 0;
	float dir = 0.01f;
	
	@Override
	public void render() {
		camera.move();
		this.view = Maths.createViewMatrix(camera);
		
		EntityRenderer.render(shader, wshader, view, camera);
		
	}

	@Override
	public void onLeave() {
		
	}

	@Override
	public void onDestory() {
		
	}
	
}

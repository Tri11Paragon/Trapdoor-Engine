package com.game.engine.display;

import java.util.ArrayList;

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
	private ArrayList<Entity> e = new ArrayList<Entity>();
	
	private String[] test = {"1540093100131.jpg", "1540046285552.jpg", "1534874238973.png", "1531696571587.jpg",
			"1200px-bi_flag-svg.png", "1540146676067.png", "1540333513771.jpg", "1540168411469.jpg", "1540139171163.jpg",
		"1540138132856.jpg"};
	
	@Override
	public void onCreate() {
		camera = new FreecamCamera();
		shader = new AtlasShader("main.vs", "main.fs");
		wshader = new AtlasShader("main.vs", "main.fs");
		for (int i = 0; i < 10000; i++) {
			if (i % 2 == 0)
				e.add(new Entity(i%DisplayManager.WIDTH*150, 500, 150, 150, test[i%test.length], true).enable());
			else
				e.add(new Entity(i%DisplayManager.WIDTH*150, 50, 150, 150, test[i%test.length], true).enable());
		}
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
		
		EntityRenderer.render(shader, wshader, view, camera);
		
		for (int i = 0; i < e.size(); i++) {
			Entity ee = e.get(i);
			ee.setRotation((float) (ee.getRotation()+(25 * DisplayManager.getFrameTimeSeconds())));
		}
		
	}

	@Override
	public void onLeave() {
		
	}

	@Override
	public void onDestory() {
		
	}
	
}

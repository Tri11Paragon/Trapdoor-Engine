package com.game.engine.display;

import java.util.ArrayList;

import com.game.engine.TextureLoader;
import com.game.engine.datatypes.world.Entity;
import com.game.engine.renderer.EntityRenderer;
import com.game.engine.world.World;

/**
 * @author brett
 * @date Oct. 18, 2021
 * 
 */
public class TestDisplay extends IDisplay {
	
	
	private ArrayList<Entity> e = new ArrayList<Entity>();
	
	
	@Override
	public void onCreate() {
		World.preinit();
	}

	@Override
	public void onSwitch() {
		World.init();
		
		ArrayList<String> test = TextureLoader.textureNames.get(TextureLoader.getTextureAtlas("1540093100131.jpg"));
		for (int i = 0; i < 100; i++) {
			if (i % 2 == 0)
				e.add(new Entity(i%DisplayManager.WIDTH*150, 500, 150, 150, test.get(i%test.size())).enable());
			else
				e.add(new Entity(i%DisplayManager.WIDTH*150, 50, 150, 150, test.get(i%test.size())).enable());
		}
	}

	float z = 0;
	float dir = 0.01f;
	float c = 0;
	
	@Override
	public void render() {
		
		
		World.render();
		World.update();
		
		for (int i = 0; i < e.size(); i++) {
			Entity ee = e.get(i);
			ee.setRotation((float) (ee.getRotation() + Math.sin(Math.toRadians((c/180) + 60 * DisplayManager.getFrameTimeSeconds()))));
			if (ee.getPosition().y < 500) {
				ee.setPosition(ee.x(), (float) (Math.sin(Math.toRadians((c+ee.x())%360)) * 150));
			}
		}
		
		c += 120 * DisplayManager.getFrameTimeSeconds();
		c %= 360;
	}

	@Override
	public void onLeave() {
		EntityRenderer.deleteAllEntities();
		World.deleteAllEntities();
	}

	@Override
	public void onDestory() {
		
	}
	
}

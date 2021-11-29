package com.game.engine.display;

import com.game.engine.VAOLoader;
import com.game.engine.TextureLoader;
import com.game.engine.camera.CreativeFirstPerson;
import com.game.engine.datatypes.ogl.Texture;
import com.game.engine.datatypes.ogl.obj.VAO;
import com.game.engine.renderer.EntityRenderer;
import com.game.engine.threading.GameRegistry;
import com.game.engine.tools.models.OBJLoader;
import com.game.engine.world.Entity;

/**
 * @author brett
 * @date Oct. 18, 2021
 * 
 */
public class TestDisplay extends IDisplay {
	
	
	//private ArrayList<Entity> e = new ArrayList<Entity>();
	public CreativeFirstPerson camera;
	public EntityRenderer renderer;
	public VAO vao;
	public Texture texture;
	
	@Override
	public void onCreate() {
		this.camera = new CreativeFirstPerson();
		this.renderer = new EntityRenderer(this.camera);
		this.vao = VAOLoader.loadToVAO(OBJLoader.loadOBJ("depression"));
		this.texture = GameRegistry.textures.get("resources/textures/512.png");
		this.renderer.ents.add(new Entity().setModel(VAOLoader.loadToVAO(OBJLoader.loadOBJ("hmmmmtriangles"))).setTexture(texture).setPosition(0, 0, 0));
		this.renderer.ents.add(new Entity().setModel(vao).setTexture(texture).setPosition(25, 0, 0));
		this.renderer.ents.add(new Entity().setModel(vao).setTexture(texture).setPosition(-25, 0, 0));
		this.renderer.ents.add(new Entity().setModel(vao).setTexture(texture).setPosition(0, 0, 25));
		this.renderer.ents.add(new Entity().setModel(vao).setTexture(texture).setPosition(0, 0, -25));
		//World.preinit();
	}

	@Override
	public void onSwitch() {
		
		
		
	}

	float z = 0;
	float dir = 0.01f;
	float c = 0;
	
	@Override
	public void render() {
		
		this.camera.move();
		this.renderer.render();
		
		//World.render();
		//World.update();
		
		//for (int i = 0; i < e.size(); i++) {
		//	Entity ee = e.get(i);
		//	ee.setRotation((float) (ee.getRotation() + Math.sin(Math.toRadians((c/180) + 60 * DisplayManager.getFrameTimeSeconds()))));
		//	if (ee.getPosition().y < 500) {
		//		ee.setPosition(ee.x(), (float) (Math.sin(Math.toRadians((c+ee.x())%360)) * 150));
		//	}
		//}
		
		//c += 120 * DisplayManager.getFrameTimeSeconds();
		//c %= 360;
	}

	@Override
	public void onLeave() {
		//EntityRenderer.deleteAllEntities();
		//World.deleteAllEntities();
	}

	@Override
	public void onDestory() {
		
	}
	
}

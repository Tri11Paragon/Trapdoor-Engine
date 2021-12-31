package com.game.engine.display;

import com.game.engine.camera.CreativeFirstPerson;
import com.game.engine.datatypes.ogl.Texture;
import com.game.engine.datatypes.ogl.assimp.Model;
import com.game.engine.threading.GameRegistry;
import com.game.engine.world.World;
import com.game.engine.world.entities.Entity;

/**
 * @author brett
 * @date Oct. 18, 2021
 * 
 */
public class TestDisplay extends IDisplay {
	
	
	//private ArrayList<Entity> e = new ArrayList<Entity>();
	public CreativeFirstPerson camera;
	public World world;
	public Model vao;
	public Texture texture;
	
	@Override
	public void onCreate() {
		this.camera = new CreativeFirstPerson();
		this.world = new World(camera);
		
		this.vao = GameRegistry.getModel("resources/models/depression.obj");
		this.texture = GameRegistry.getTexture("resources/textures/512.png");
		this.world.addEntityToWorld(new Entity().setModel(GameRegistry.getModel("resources/models/power model.obj")).setTexture(texture).setPosition(0, -10.0f, 0));
		this.world.addEntityToWorld(new Entity().setModel(vao).setTexture(texture).setPosition(25, 0, 0));
		this.world.addEntityToWorld(new Entity().setModel(vao).setTexture(texture).setPosition(-25, 0, 0));
		this.world.addEntityToWorld(new Entity().setModel(vao).setTexture(texture).setPosition(0, 0, 25));
		this.world.addEntityToWorld(new Entity().setModel(vao).setTexture(texture).setPosition(0, 0, -25));
		this.world.addEntityToWorld(new Entity().setModel(GameRegistry.getModel("resources/models/hellolosers.obj")).setTexture(GameRegistry.getTexture("resources/textures/yes.png")).setPosition(5, 5, 5));
		// add entity
		this.world.addEntityToWorld(
				new Entity()
					// set the model
					.setModel(
							GameRegistry.getModel("resources/models/lll3.obj"))
					// set the texture
					.setTexture(
							// get the texture from the preloaded texture assets 
							// (this will be how models are done soon)
							GameRegistry.getTexture("resources/textures/yes.png"))
					// change position
					.setPosition(15, 5, 25)); 
	}

	@Override
	public void onSwitch() {
		
		
		
	}

	float z = 0;
	float dir = 0.01f;
	float c = 0;
	
	@Override
	public void render() {
		
		this.world.render();
		
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

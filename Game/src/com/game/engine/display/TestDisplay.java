package com.game.engine.display;

import com.game.engine.camera.CreativeFirstPerson;
import com.game.engine.datatypes.ogl.Texture;
import com.game.engine.datatypes.ogl.assimp.Model;
import com.game.engine.threading.GameRegistry;
import com.game.engine.world.World;
import com.game.engine.world.entities.Entity;
import com.game.game.entities.EntityPoop;

/**
 * @author brett
 * @date Oct. 18, 2021
 * 
 */
public class TestDisplay extends IDisplay {
	
	
	//private ArrayList<Entity> e = new ArrayList<Entity>();
	public CreativeFirstPerson camera;
	public World world;
	public Model cubeModel;
	public Texture texture;
	
	@Override
	public void onCreate() {
		this.camera = new CreativeFirstPerson();
		this.world = new World(camera);
		
		this.cubeModel = GameRegistry.getModel("resources/models/depression.dae");
		this.texture = GameRegistry.getTexture("resources/textures/512.png");
		this.world.addEntityToWorld(new Entity().setModel(GameRegistry.getModel("resources/models/test object.dae")).setPosition(0, -10.0f, 0));
		this.world.addEntityToWorld(new Entity().setModel(cubeModel).setPosition(25, 0, 0));
		this.world.addEntityToWorld(new Entity().setModel(cubeModel).setPosition(-25, 0, 0));
		this.world.addEntityToWorld(new Entity().setModel(cubeModel).setPosition(0, 0, 25));
		this.world.addEntityToWorld(new Entity().setModel(cubeModel).setPosition(0, 0, -25));
		this.world.addEntityToWorld(new Entity().setModel(GameRegistry.getModel("resources/models/hellolosers.obj")).setPosition(5, 5, 5));
		// add entity
		this.world.addEntityToWorld(
				new Entity()
					// set the model
					.setModel(
							GameRegistry.getModel("resources/models/lll3.obj"))
					// change position
					.setPosition(15, 5, 25)); 
		//add poop
		this.world.addEntityToWorld(
				new EntityPoop()
					// set the model
					.setModel(
							GameRegistry.getModel("resources/models/poop.dae"))
					// change position
					.setPosition(-15, -4, -15).setYaw((float) (Math.PI/2)).setScale(0.1f));
	}

	@Override
	public void onSwitch() {
		
	}
	
	@Override
	public void render() {
		this.world.render();
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

package com.game.game.displays;

import com.game.engine.camera.CreativeFirstPerson;
import com.game.engine.display.IDisplay;
import com.game.engine.threading.GameRegistry;
import com.game.engine.world.World;
import com.game.game.entities.EntityPoop;

public class SinglePlayerDisplay extends IDisplay{
	
	public CreativeFirstPerson camera;
	public World world;
	
	@Override
	public void onCreate() {
		this.camera = new CreativeFirstPerson();
		this.world = new World(camera);
		
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		this.world.render();
	}

	@Override
	public void onLeave() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDestory() {
		// TODO Auto-generated method stub
		
	}	
	
}

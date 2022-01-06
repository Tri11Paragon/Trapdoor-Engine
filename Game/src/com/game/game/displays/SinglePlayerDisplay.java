package com.game.game.displays;

import com.game.engine.camera.CreativeFirstPerson;
import com.game.engine.display.IDisplay;
import com.game.engine.threading.GameRegistry;
import com.game.engine.world.World;
import com.game.engine.world.entities.Entity;
import com.game.engine.world.entities.EntityCamera;
import com.game.game.entities.EntityPoop;

public class SinglePlayerDisplay extends IDisplay{
	
	public CreativeFirstPerson camera;
	public World world;
	
	@Override
	public void onCreate() {
		this.camera = new CreativeFirstPerson();
		this.world = new World(camera);
		
		this.world.addEntityToWorld(new EntityCamera(this.camera));
		
		this.world.addEntityToWorld(new Entity().setModel(
				GameRegistry.getModel("resources/models/chess/w_king.dae")).setPosition(1, 0, -10));
		this.world.addEntityToWorld(new Entity().setModel(
				GameRegistry.getModel("resources/models/chess/w_queen.dae")).setPosition(-1, 0, -10));
		this.world.addEntityToWorld(new Entity().setModel(
				GameRegistry.getModel("resources/models/chess/w_bishop.dae")).setPosition(3, 0, -10));
		this.world.addEntityToWorld(new Entity().setModel(
				GameRegistry.getModel("resources/models/chess/w_bishop.dae")).setPosition(-3, 0, -10));
		this.world.addEntityToWorld(new Entity().setModel(
				GameRegistry.getModel("resources/models/chess/w_knight.dae")).setPosition(5, 0, -10));
		this.world.addEntityToWorld(new Entity().setModel(
				GameRegistry.getModel("resources/models/chess/w_knight.dae")).setPosition(-5, 0, -10));
		this.world.addEntityToWorld(new Entity().setModel(
				GameRegistry.getModel("resources/models/chess/w_rook.dae")).setPosition(7, 0, -10));
		this.world.addEntityToWorld(new Entity().setModel(
				GameRegistry.getModel("resources/models/chess/w_rook.dae")).setPosition(-7, 0, -10));
		
		this.setSkyColor(0, 0, 0);
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
	public void update() {
		this.world.update();
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

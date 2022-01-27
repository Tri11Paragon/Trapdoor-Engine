package com.game.game.displays;

import com.game.engine.camera.CreativeFirstPerson;
import com.game.engine.display.IDisplay;
import com.game.engine.registry.GameRegistry;
import com.game.engine.registry.annotations.RegistrationEventSubscriber;
import com.game.engine.world.World;
import com.game.engine.world.entities.Entity;
import com.game.engine.world.entities.EntityCamera;

public class SinglePlayerDisplay extends IDisplay{
	
	public CreativeFirstPerson camera;
	public World world;
	
	@RegistrationEventSubscriber
	public static void register() {
		GameRegistry.registerModel("resources/models/poop.dae");
		GameRegistry.registerModel("resources/models/chess/w_king.dae");

		GameRegistry.registerModel("resources/models/chess/w_king_bt.dae");
		GameRegistry.registerModel("resources/models/chess/w_king_bt2.dae");
		GameRegistry.registerModel("resources/models/chess/w_king_bt3.dae");

		GameRegistry.registerModel("resources/models/chess/w_queen.dae");
		GameRegistry.registerModel("resources/models/chess/w_bishop.dae");
		GameRegistry.registerModel("resources/models/chess/w_knight.dae");
		GameRegistry.registerModel("resources/models/chess/w_rook.dae");
	}
	
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
				GameRegistry.getModel("resources/models/chess/w_rook.dae")).setPosition(8, 0, -10));
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
		this.world.cleanup();
	}
	
}

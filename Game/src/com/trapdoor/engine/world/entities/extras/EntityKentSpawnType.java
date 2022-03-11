package com.trapdoor.engine.world.entities.extras;

import com.game.entities.EntityKent;
import com.trapdoor.engine.registry.GameRegistry;
import com.trapdoor.engine.world.entities.Entity;

/**
 * @author brett
 * @date Mar. 8, 2022
 * 
 */
public class EntityKentSpawnType extends EntitySpawnType {

	private Entity track;
	
	public EntityKentSpawnType(Entity track) {
		super(new EntityKent(0, track).setModel(GameRegistry.getModel("resources/models/kent.dae")));
		this.track = track;
	}

	@Override
	public Entity spawnEntity(float x, float y, float z) {
		return new EntityKent(1, 3, track).setBasePosition(x, y, z).setModel(GameRegistry.getModel("resources/models/kent.dae"));
	}

}

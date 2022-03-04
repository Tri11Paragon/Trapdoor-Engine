package com.game.entities;

import com.trapdoor.engine.datatypes.ogl.assimp.Model;
import com.trapdoor.engine.registry.GameRegistry;
import com.trapdoor.engine.world.World;
import com.trapdoor.engine.world.entities.Entity;

public class Kentipede {
	
	public Kentipede(World w, int length, int density, Entity camera) {
		
		Model m = GameRegistry.getModel("resources/models/kent.dae");
		Entity a;
		
		for (float i = 0; i < length*Math.PI; i+=Math.PI/density) {
			a = new EntityKent(i + 1, 3, camera).setModel(m);
			w.addEntityToWorld(a);
		}
		
	}
	
	

}

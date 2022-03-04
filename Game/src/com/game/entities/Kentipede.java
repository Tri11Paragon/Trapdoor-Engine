package com.game.entities;

import com.trapdoor.engine.datatypes.ogl.assimp.Model;
import com.trapdoor.engine.registry.GameRegistry;
import com.trapdoor.engine.world.World;
import com.trapdoor.engine.world.entities.Entity;
import com.trapdoor.engine.world.entities.components.Transform;

public class Kentipede {
	
	public Kentipede(World w, int length, int density, Transform other) {
		
		Model m = GameRegistry.getModel("resources/models/kent.dae");
		Entity a;
		
		for (float i = 0; i < length*Math.PI; i+=Math.PI/density) {
			System.out.println("i: " + i);
			a = new EntityKent(i, 2, other).setModel(m);
			w.addEntityToWorld(a);
		}
		
	}
	
	

}

package com.game.entities;

import com.trapdoor.engine.datatypes.ogl.assimp.Model;
import com.trapdoor.engine.registry.GameRegistry;
import com.trapdoor.engine.world.World;
import com.trapdoor.engine.world.entities.Entity;

public class Kentipede {
	
	public Kentipede(World w, int length, int density, Entity camera) {
		this(w, length, 3, density, camera);
	}
	
	public Kentipede(World w, int length, int type, int density, Entity camera) {
		this(w, 0, 0, 0, length, type, density, camera);
	}
	
	public Kentipede(World w, float baseX, float baseY, float baseZ, int length, int type, int density, Entity camera) {
		
		Model m = GameRegistry.getModel("resources/models/kent.dae");
		Entity a;
		
		for (float i = 0; i < length*Math.PI; i+=Math.PI/density) {
			a = new EntityKent(i + 1, type, camera).setBasePosition(baseX, baseY, baseZ).setModel(m);
			a.generateApproximateCollider();
			w.addEntityToWorld(a);
		}
		
	}
	

}

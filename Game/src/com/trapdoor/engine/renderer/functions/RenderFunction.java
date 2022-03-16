package com.trapdoor.engine.renderer.functions;

import java.util.ArrayList;

import com.trapdoor.engine.datatypes.ogl.assimp.Model;
import com.trapdoor.engine.renderer.ShaderProgram;
import com.trapdoor.engine.world.entities.Entity;

/**
 * @author brett
 * @date Mar. 16, 2022
 * 
 */
public interface RenderFunction {
	
	public void render(ShaderProgram program, Model m, ArrayList<Entity> lis);
	
}

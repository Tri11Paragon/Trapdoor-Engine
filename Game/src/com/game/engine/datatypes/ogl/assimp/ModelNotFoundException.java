package com.game.engine.datatypes.ogl.assimp;

/**
 * @author brett
 * @date Nov. 22, 2021
 * 
 */
public class ModelNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = -4821076275093855992L;

	public ModelNotFoundException(String name) {
		super("The model " + name + " was not found!");
	}
	
}

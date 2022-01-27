package com.game.engine.datatypes.collision;

/**
 * @author brett
 * @date Dec. 23, 2021
 * 
 */
public interface ICollider {
	
	public boolean intersects(ICollider c);
	
}

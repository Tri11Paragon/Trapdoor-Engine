package com.game.engine.datatypes.collision.colliders;

/**
 * @author brett
 * @date Dec. 23, 2021
 * 
 */
public interface ICollider {
	
	public boolean intersects(ICollider c);
	
}

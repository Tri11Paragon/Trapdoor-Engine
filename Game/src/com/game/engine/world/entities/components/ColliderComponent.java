package com.game.engine.world.entities.components;

import com.game.engine.datatypes.collision.colliders.ICollider;

/**
 * @author brett
 * @date Dec. 23, 2021
 * 
 */
public abstract class ColliderComponent extends Component {

	protected ICollider collider;
	
	public ColliderComponent() {
		super(Component.TYPE_COLLIDER);
	}

	public abstract boolean intersects(ColliderComponent c);
	
	public ICollider getCollider() {
		return collider;
	}
	
}

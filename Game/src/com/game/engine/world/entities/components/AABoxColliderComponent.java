package com.game.engine.world.entities.components;

import com.game.engine.datatypes.collision.colliders.AABB;

/**
 * @author brett
 * @date Dec. 22, 2021
 * 
 */
public class AABoxColliderComponent extends ColliderComponent {
	
	public AABoxColliderComponent() {
		this.collider = new AABB(0, 0, 0, 1, 1, 1);
	}
	
	@Override
	public void update() {
		
	}
	
	@Override
	public boolean intersects(ColliderComponent c) {
		if (!(c.collider instanceof AABB))
			return false;
		AABB o = (AABB) c.collider;
		return o.translateThis(c.associatedEntity.getX(), c.associatedEntity.getY(), c.associatedEntity.getZ())
				.intersects(((AABB)this.collider).translateThis(this.associatedEntity.getX(), this.associatedEntity.getY(), this.associatedEntity.getZ()));
	}
	
	/**
	 * creates and sets the collider based on input parameters.
	 * note: this function does nothing special to make this based on entity position and nor should it
	 * as the collision system will automatically translate this collider.
	 */
	public AABoxColliderComponent setCollider(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		this.collider = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
		return this;
	}
	
	/**
	 * creates a collider based on the supplied size, mins are set on 0
	 */
	public AABoxColliderComponent setCollider(double size) {
		this.collider = new AABB(0, 0, 0, size, size, size);
		return this;
	}
	
	/**
	 * creates a collider centered on 0,0,0 with size
	 */
	public AABoxColliderComponent setColliderCentered(double size) {
		this.collider = new AABB(-size, -size, -size, size,size,size);
		return this;
	}
	
	/**
	 * sets a translated AABB box collider. NOTE: this should not be translated based on entity world position but rather in entity local space
	 * it the X,Y,Z should be based around the entity's center position, not the world origin or entity position.
	 * @param size of the box
	 */
	public AABoxColliderComponent setCollider(double x, double y, double z, double size) {
		this.collider = new AABB(x, y, z, x+size, y+size, z+size);
		return this;
	}
	
	/**
	 * sets a translated AABB box collider centered on the x,y,z. NOTE: this should not be translated based on entity world position but rather in entity local space
	 * it the X,Y,Z should be based around the entity's center position, not the world origin or entity position.
	 * @param size of the box
	 */
	public AABoxColliderComponent setColliderCentered(int x, int y, int z, int size) {
		this.collider = new AABB(x-size, y-size, z-size, x+size, y+size, z+size);
		return this;
	}
	
	/**
	 * this function translates the internal box AABB collider based on the current position of the entity
	 * you can modify the box collider of this entity using the setCollider function
	 * @return
	 */
	@Override
	public synchronized AABB getCollider() {
		return ((AABB) collider).translateThis(this.associatedEntity.getX(), this.associatedEntity.getY(), this.associatedEntity.getZ());
	}

}

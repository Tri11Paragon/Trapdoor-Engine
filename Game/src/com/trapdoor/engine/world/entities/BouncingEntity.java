package com.trapdoor.engine.world.entities;

import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.trapdoor.engine.world.entities.components.Transform;

/**
 * @author brett
 * @date Mar. 4, 2022
 * 
 */
public class BouncingEntity extends Entity {
	
	private boolean bounced = false;
	private long lastTime = 0;
	private float ox, oy, oz;
	private Transform t;
	
	public BouncingEntity(float mass) {
		super(mass);
		this.t = super.getComponent(Transform.class);
	}
	
	@Override
	public void update() {
		super.update();
		if (!bounced && System.currentTimeMillis() - lastTime > 5000) {
			this.applyCentralImpulse(0, 2500, 0);
			bounced = true;
			lastTime = System.currentTimeMillis();
		}
		if (Math.abs(this.ox - t.getX()) > 50 || Math.abs(this.oy - t.getY()) > 50 || Math.abs(this.oz - t.getY()) > 50 ) {
			this.t.setPosition(ox, oy, oz);
			this.setLinearVelocity(0, 0, 0);
		}
	}
	
	@Override
	public void onCollision(Entity other, PhysicsCollisionEvent event) {
		super.onCollision(other, event);
		bounced = false;
	}
	
	@Override
	public Entity setPosition(float x, float y, float z) {
		this.ox = x;
		this.oy = y;
		this.oz = z;
		return super.setPosition(x, y, z);
	}
	
}

package com.game.entities;

import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.math.Vector3f;
import com.trapdoor.engine.world.entities.Entity;
import com.trapdoor.engine.world.entities.components.Transform;
import static java.lang.Math.*;

import java.util.Random;

public class EntityKent extends Entity {
	
	private float yaw, pitch, roll;
	private float x = 0f, y = 0f, z = 0f;
	private float rad, offset;
	private Transform other;
	private Entity cameraEnt;
	private int type = 0;
	private Transform t;
	private Random rand;
	
	public EntityKent() {
		super(5);
		this.t = (Transform) this.getComponent(Transform.class);
		rand = new Random(System.nanoTime() - System.currentTimeMillis());
	}
	
	public EntityKent(float offset) {
		this();
		this.offset = offset;
	}
	
	public EntityKent(float offset, int type) {
		this();
		this.offset = offset;
		this.type = type;
	}
	
	public EntityKent(float offset, int type, Entity cameraEnt) {
		this();
		this.offset = offset;
		this.type = type;
		this.other = cameraEnt.getComponent(Transform.class);
		this.cameraEnt = cameraEnt;
	}
	
	@Override
	public void onAddedToWorld() {
		// TODO Auto-generated method stub
		super.onAddedToWorld();
		this.getRigidbody().setGravity(new Vector3f(0, 0, 0)); // Sets gravity to 0
		this.getRigidbody().setCollisionShape(new BoxCollisionShape(1.0f));
//		this.getRigidbody().setLinearVelocity(new Vector3f(0, 1000, 0)); // Sets velocity at start
	}
	
	@Override
	public void update() {
		super.update(); // need this for overriding functions
		this.t.setRotation(this.yaw, this.pitch, this.roll);
		this.yaw += 0.01 + offset/180;
		this.pitch += 0.02 + offset/180;
		this.roll += 0.03 + offset/180;
		
		this.t.setPosition(this.x, this.y, this.z);
		
		switch (this.type) {
		case 0:
			// do nothing
		case 1:
			this.x = 10 * (float) -sin( 1 * (this.rad + this.offset / 4));
			this.y = 2 * (float) -sin( 0.5 * (this.rad + this.offset / 2));
//			this.y = (float) (this.rad - 1 + (9 * sin( this.offset )/2 ));
			this.z = 10 * (float)  cos( 1 * (this.rad + this.offset / 4));
			this.rad += Math.PI/50;
			break;
		case 2:
			this.x = 20 * (float) -sin( 1 * (this.rad + this.offset));
			this.y = (float) (this.rad - 1 + (4 * cos( 3 * this.offset )/2 ));
			this.z = 20 * (float)  cos( 1 * (this.rad + this.offset));
			this.rad += Math.PI/300;
			break;
		case 3:
			final float distConst = 15;
			this.x += (other.getX() - this.x) / ((this.offset) * distConst);
			this.y += (other.getY() - this.y) / ((this.offset) * distConst);
			this.z += (other.getZ() - this.z) / ((this.offset) * distConst);
			break;
		case 4:
			this.t.setPosition(this.x, this.y, this.z);
			this.x = 8 * (float) -sin(0.6 * (this.rad + this.offset));
			this.y = 10 * (float) -sin(2 * (this.rad + this.offset));
			this.z = 5 * (float) cos(this.rad + this.offset);
			this.rad += Math.PI/100;
		}
	}
	
	@Override
	public void onCollision(Entity other, PhysicsCollisionEvent event) {
		super.onCollision(other, event);
		if (cameraEnt != null && (other == cameraEnt || other instanceof EntityKent)){
			this.x = this.other.getX() + random();
			this.y = this.other.getY() + random();
			this.z = this.other.getZ() + random();
		}
	}
	
	private final float radius = 320;
	
	private float random() {
		return rand.nextFloat() * radius - radius/2;
	}
	
}

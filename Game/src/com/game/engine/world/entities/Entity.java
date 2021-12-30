package com.game.engine.world.entities;

import java.util.ArrayList;
import java.util.HashMap;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.game.engine.datatypes.ogl.Texture;
import com.game.engine.datatypes.ogl.obj.VAO;
import com.game.engine.world.World;
import com.game.engine.world.entities.components.Component;

/**
 * @author laptop
 * @date Nov. 21, 2021
 * 
 */
public class Entity {
	
	//private float x,y,z;
	private float sx=1,sy=1,sz=1;
	private VAO model;
	private Texture texture;
	
	// physics stuff
	private RigidBody rigidbody;
	private final Transform transformOut;
	private final Vector3f positionStore;
	
	// components stuff
	private ArrayList<Component> entityComponents = new ArrayList<Component>();
	private HashMap<String, ArrayList<Component>> componentMap = new HashMap<String, ArrayList<Component>>();
	// world reference
	private World world;
	
	public Entity() {
		this.transformOut = new Transform();
		this.positionStore = new Vector3f();
		// default no mass (ie collision object, nothing else.
		RigidBodyConstructionInfo cor = new RigidBodyConstructionInfo(0, new DefaultMotionState(
				new Transform(
						new Matrix4f(
								// rotation
								new Quat4f(0,0,0,1),
								// position, + w
								new Vector3f(0,0,0), 1.0f
								)
						)
				), new BoxShape(new Vector3f(0.5f, 0.5f, 0.5f)));
		cor.restitution = 0.0f;
		cor.angularDamping = 0.95f;
		cor.friction = 0.5f;
		rigidbody = new RigidBody(cor);
		this.rigidbody.getWorldTransform(transformOut);
		
		//this.rigidbody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
	}
	
	public void update() {
		this.rigidbody.getWorldTransform(transformOut);
	}
	
	public void render() {
		
	}
	
	/**
	 * NOTE: setting an entity's position will ignore collision.
	 * if the entity's mass is 0 that will be fine
	 * otherwise you can result in some bad things happening
	 * this function is for when creating the entity only please.
	 */
	public synchronized Entity setPosition(float x, float y, float z) {
		positionStore.x = x - getX();
		positionStore.y = y - getY();
		positionStore.z = z - getZ();
		this.rigidbody.translate(positionStore);
		return this;
	}
	
	/**
	 * SETS the linear velocity on the rigid body.
	 */
	public synchronized Entity setLinearVelocity(float x, float y, float z) {
		positionStore.x = x;
		positionStore.y = y;
		positionStore.z = z;
		this.rigidbody.setLinearVelocity(positionStore);
		this.rigidbody.activate();
		return this;
	}
	
	/**
	 * Applies a force from the center of the entity
	 */
	public synchronized Entity applyCentralForce(float x, float y, float z) {
		positionStore.x = x;
		positionStore.y = y;
		positionStore.z = z;
		this.rigidbody.applyCentralForce(positionStore);
		this.rigidbody.activate();
		return this;
	}
	
	/**
	 * Applies a impulse from the center of the entity
	 * Note: this ignores already existing forces, but not mass (it directly modifies the velocity)
	 */
	public synchronized Entity applyCentralImpulse(float x, float y, float z) {
		positionStore.x = x;
		positionStore.y = y;
		positionStore.z = z;
		this.rigidbody.applyCentralImpulse(positionStore);
		this.rigidbody.activate();
		return this;
	}
	
	public synchronized Vector3f getLinearVelocity() {
		this.rigidbody.getLinearVelocity(positionStore);
		return positionStore;
	}
	
	/**
	 * Best simulation results using zero restitution*
	 * Set the restitution, also known as the bounciness or spring. 
	 * The restitution may range from 0.0 (not bouncy) to 1.0 (extremely bouncy).
	 */
	public Entity setRestitution(float r) {
		this.rigidbody.setRestitution(r);
		return this;
	}
	
	/**
	 * Best simulation results when friction is non-zero*
	 * 
	 */
	public Entity setFriction(float f) {
		this.rigidbody.setFriction(f);
		return this;
	}
	
	/**
	 * friction but for spheres
	 * 
	 */
	public Entity setAngularDamping(float a) {
		this.rigidbody.setDamping(this.rigidbody.getLinearDamping(), a);
		return this;
	}
	
	public Entity addComponent(Component c) {
		this.entityComponents.add(c);
		ArrayList<Component> components = this.componentMap.get(c.getType());
		if (components == null) {
			components = new ArrayList<Component>();
			this.componentMap.put(c.getType(), components);
		}
		components.add(c);
		c.componentAddedToEntity(this, world);
		return this;
	}
	
	public ArrayList<Component> getComponents(){
		return entityComponents;
	}
	
	public ArrayList<Component> getComponentsByType(String type){
		return this.componentMap.get(type);
	}
	
	public void setWorld(World w) {
		this.world = w;
	}
	
	public synchronized float getX() {
		return this.transformOut.origin.x;
	}
	public synchronized Entity setX(float x) {
		this.transformOut.origin.x = x;
		return this;
	}
	public synchronized float getY() {
		return this.transformOut.origin.y;
	}
	public synchronized Entity setY(float y) {
		this.transformOut.origin.y = y;
		return this;
	}
	public synchronized float getZ() {
		return this.transformOut.origin.z;
	}
	public synchronized Entity setZ(float z) {
		this.transformOut.origin.z = z;
		return this;
	}
	public Matrix3f getRotationMatrix() {
		return this.transformOut.basis;
	}
	
	/**
	 * Note: this function is inefficient and shouldn't be used.
	 * @return the yaw computed from the entity's rigidbody rotation matrix
	 * TODO: fix this
	 */
	public float getYaw() {
		return (float) Math.atan(this.transformOut.basis.m10/this.transformOut.basis.m00);
	}
	
	public float getPitch() {
		float sqrt = invSqrt((this.transformOut.basis.m21 * this.transformOut.basis.m21) + (this.transformOut.basis.m22 * this.transformOut.basis.m22));
		return (float) Math.atan(-this.transformOut.basis.m20 * sqrt);
	}
	
	public float getRoll() {
		return (float) Math.atan(this.transformOut.basis.m21 / this.transformOut.basis.m22);
	}
	
	
	public synchronized Entity setYaw(float yaw) {
		this.transformOut.basis.rotX(yaw);
		return this;
	}
	public synchronized Entity setPitch(float pitch) {
		this.transformOut.basis.rotY(pitch);
		//this.pitch = pitch;
		return this;
	}
	public synchronized Entity setRoll(float roll) {
		this.transformOut.basis.rotZ(roll);
		//this.roll = roll;
		return this;
	}

	public VAO getModel() {
		return model;
	}
	public Entity setModel(VAO model) {
		synchronized (model) {
			this.model = model;
			return this;
		}
	}
	public Texture getTexture() {
		synchronized (texture) {
			return texture;
		}
	}
	public Entity setTexture(Texture texture) {
		synchronized (texture) {
			this.texture = texture;
			return this;
		}
	}
	public synchronized float getSx() {
		return sx;
	}
	public synchronized Entity setSx(float sx) {
		this.sx = sx;
		return this;
	}
	public synchronized float getSy() {
		return sy;
	}
	public synchronized Entity setSy(float sy) {
		this.sy = sy;
		return this;
	}
	public synchronized float getSz() {
		return sz;
	}
	public synchronized Entity setSz(float sz) {
		this.sz = sz;
		return this;
	}
	public synchronized Entity setScale(float s) {
		this.sx = s;
		this.sy = s;
		this.sz = s;
		return this;
	}

	public RigidBody getRigidbody() {
		return rigidbody;
	}

	public Entity setRigidbody(RigidBody rigidbody) {
		// only if the world has been set (ie we've already spawned into the world)
		if (world != null)
			this.world.removeEntityPhysics(this);
		this.rigidbody = rigidbody;
		if (world != null)
			this.world.addEntityPhysics(this);
		return this;
	}
	
	private static float invSqrt(float x) {
	    float xhalf = 0.5f * x;
	    int i = Float.floatToIntBits(x);
	    i = 0x5f3759df - (i >> 1);
	    x = Float.intBitsToFloat(i);
	    x *= (1.5f - xhalf * x * x);
	    return x;
	}
	
}

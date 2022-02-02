package com.trapdoor.engine.world.entities;

import java.util.ArrayList;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.trapdoor.engine.datatypes.lighting.Light;
import com.trapdoor.engine.datatypes.ogl.assimp.Model;
import com.trapdoor.engine.tools.math.Maths;
import com.trapdoor.engine.world.World;
import com.trapdoor.engine.world.entities.components.IComponent;

/**
 * @author laptop
 * @date Nov. 21, 2021
 * 
 */
public class Entity {
	
	private float sx=1,sy=1,sz=1;
	private Model model;
	private boolean usingAssignedCollisonState = false;
	
	// physics stuff
	private RigidBody rigidbody;
	private final Transform transformOut;
	private final Vector3f positionStore;
	
	// components stuff
	private ArrayList<IComponent> entityComponents = new ArrayList<IComponent>();
	
	// TODO: make sub-entity for entity that has lights, since most don't need light array
	protected ArrayList<Light> lights = new ArrayList<Light>();
	// world reference
	protected World world;
	
	// entity flags (8 boolean)
	// 0000 0000
	// nnnn nnns
	// where:
	// n is unassigned
	// s is isentitystatic
	private byte flags = 0;
	
	public Entity() {
		// entities with no mass are effectively static
		this(0, true);
	}
	
	public Entity(float mass) {
		this(mass, false);
	}
	
	public Entity(float mass, boolean isStatic) {
		this(mass, isStatic, null);
	}
	
	public Entity(CollisionShape shape) {
		this(0, true, shape);
	}
	
	public Entity(float mass, boolean isStatic, CollisionShape collider) {
		this.transformOut = new Transform();
		this.positionStore = new Vector3f();
		byte mask = isStatic ? (byte) 1 : (byte) 0;
		this.flags = (byte) (flags | mask);
		// default no mass (ie collision object, nothing else.
		if (collider == null)
			collider = new BoxShape(new Vector3f());
		else
			usingAssignedCollisonState = true;
		RigidBodyConstructionInfo cor = new RigidBodyConstructionInfo(mass, new DefaultMotionState(
				new Transform(
						new Matrix4f(
								// rotation
								new Quat4f(0,0,0,1),
								// position, + w
								new Vector3f(0,0,0), 1.0f
								)
						)
				), collider);
		cor.restitution = 0.0f;
		cor.angularDamping = 0.95f;
		cor.friction = 0.5f;
		rigidbody = new RigidBody(cor);
		this.rigidbody.getWorldTransform(transformOut);
		
		//this.rigidbody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
	}
	
	public void update() {
		this.rigidbody.getWorldTransform(transformOut);
		for (int i = 0; i < entityComponents.size(); i++)
			entityComponents.get(i).update();
	}
	
	public void render() {
		for (int i = 0; i < entityComponents.size(); i++)
			entityComponents.get(i).render();
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
	
	public Entity addComponent(IComponent c) {
		this.entityComponents.add(c);
		return this;
	}
	
	public ArrayList<IComponent> getComponents(){
		return entityComponents;
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
		float sqrt = Maths.invSqrt((this.transformOut.basis.m21 * this.transformOut.basis.m21) + (this.transformOut.basis.m22 * this.transformOut.basis.m22));
		return (float) Math.atan(-this.transformOut.basis.m20 * sqrt);
	}
	
	public float getRoll() {
		return (float) Math.atan(this.transformOut.basis.m21 / this.transformOut.basis.m22);
	}
	
	
	public synchronized Entity setYaw(float yaw) {
		this.transformOut.basis.rotY(yaw);
		return this;
	}
	public synchronized Entity setPitch(float pitch) {
		this.transformOut.basis.rotX(pitch);
		//this.pitch = pitch;
		return this;
	}
	public synchronized Entity setRoll(float roll) {
		this.transformOut.basis.rotZ(roll);
		//this.roll = roll;
		return this;
	}
	public Entity addLight(Light l) {
		this.lights.add(l);
		return this;
	}

	public Model getModel() {
		return model;
	}
	public Entity setModel(Model model) {
		synchronized (model) {
			this.model = model;
			return updateCollisionStateFromModel();
		}
	}
	public Entity changeModel(Model model) {
		synchronized (model) {
			if (world != null && this.model != model)
				world.modelChanged(this, this.model, model);
			this.model = model;
			return updateCollisionStateFromModel();
		}
	}
	/**
	 * updates the entity rigid body with mesh collider from the assigned model
	 * @return
	 */
	public Entity updateCollisionStateFromModel() {
		if (this.model == null)
			throw new RuntimeException("Model cannot be null while trying to generate ridigid body from model!");
		if (usingAssignedCollisonState)
			return this;
		// TODO: fix this
		this.rigidbody.setCollisionShape(new BvhTriangleMeshShape(this.model.getMeshColliderData(), true, true));
		return this;
	}
	
	// these are unlikely to be needed but are availible
	
	public Entity setUsingModelMeshCollider() {
		this.usingAssignedCollisonState = false;
		return this;
	}
	
	public Entity setUsingAssignedCollider() {
		this.usingAssignedCollisonState = true;
		return this;
	}
	
	public synchronized float getSx() {
		return sx;
	}
	public synchronized Entity setSx(float sx) {
		this.sx = sx;
		this.rigidbody.getCollisionShape().setLocalScaling(new Vector3f(sx, sy, sz));
		return this;
	}
	public synchronized float getSy() {
		return sy;
	}
	public synchronized Entity setSy(float sy) {
		this.sy = sy;
		this.rigidbody.getCollisionShape().setLocalScaling(new Vector3f(sx, sy, sz));
		return this;
	}
	public synchronized float getSz() {
		return sz;
	}
	public synchronized Entity setSz(float sz) {
		this.sz = sz;
		this.rigidbody.getCollisionShape().setLocalScaling(new Vector3f(sx, sy, sz));
		return this;
	}
	public synchronized Entity setScale(float s) {
		this.sx = s;
		this.sy = s;
		this.sz = s;
		// TODO: this work? (it seems to)
		this.rigidbody.getCollisionShape().setLocalScaling(new Vector3f(sx, sy, sz));
		return this;
	}
	
	public ArrayList<Light> getLights(){
		return lights;
	}
	
	public boolean isStatic() {
		return (flags & 0x01) == 0x01;
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
	
}

package com.trapdoor.engine.world.entities;

import java.util.ArrayList;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.BvhTriangleMeshShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.trapdoor.engine.datatypes.lighting.Light;
import com.trapdoor.engine.datatypes.ogl.assimp.Model;
import com.trapdoor.engine.world.World;
import com.trapdoor.engine.world.entities.components.ComponentStore;
import com.trapdoor.engine.world.entities.components.IComponent;
import com.trapdoor.engine.world.entities.components.Transform;

/**
 * @author laptop
 * @date Nov. 21, 2021
 * 
 */
public class Entity {
	
	private Model model;
	
	// physics stuff
	private RigidBody rigidbody;
	private boolean usingAssignedCollisonState = false;
	private Vector3f positionStore;
	
	// components stuff
	private ComponentStore components = new ComponentStore();
	
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
		this.positionStore = new Vector3f();
		byte mask = isStatic ? (byte) 1 : (byte) 0;
		this.flags = (byte) (flags | mask);
		// default no mass (ie collision object, nothing else.
		if (collider == null)
			collider = new BoxShape(new Vector3f());
		else
			usingAssignedCollisonState = true;
		RigidBodyConstructionInfo cor = new RigidBodyConstructionInfo(mass, new DefaultMotionState(
				new com.bulletphysics.linearmath.Transform(
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
		this.addComponent(new Transform());
	}
	
	public void update() {
		((Transform) components.getCompoent(Transform.class)).commit(rigidbody);
		components.update();
	}
	
	public void render() {
		components.render();
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
		this.components.addComponent(c);
		return this;
	}
	
	public IComponent getComponent(Class<? extends IComponent> c) {
		return components.getCompoent(c);
	}
	
	public ComponentStore getComponentStore() {
		return components;
	}
	
	public ArrayList<IComponent> getComponents(){
		return components.getComponents();
	}
	
	public void setWorld(World w) {
		this.world = w;
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
	
	/**
	 * Only use this for creating entities. It will likely be removed soon
	 * I recommend creating a subclass and setting the transform after the super constructor finishes
	 * see Entity camera for details.
	 */
	@Deprecated
	public Entity setPosition(float x, float y, float z) {
		((Transform)this.getComponent(Transform.class)).setPosition(x, y, z);
		return this;
	}
	
}

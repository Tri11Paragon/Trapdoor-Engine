package com.trapdoor.engine.world.entities;

import java.util.ArrayList;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.GImpactCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
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
public class Entity implements Comparable<Entity> {
	
	// makes the dynamic storage a little faster
	private int id;
	private static volatile long globalID = 0;
	
	private Model model;
	
	// physics stuff
	private PhysicsCollisionObject collisionObject;
	private PhysicsRigidBody rigidBody;
	private boolean usingRigidBody = false;
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
		// house keeping nonsense eh
		long v = ((globalID++) - Integer.MAX_VALUE);
		if (v > Integer.MAX_VALUE-120)
			globalID = 0;
		this.id = (int)v;
		
		this.positionStore = new Vector3f();
		byte mask = isStatic ? (byte) 1 : (byte) 0;
		this.flags = (byte) (flags | mask);
		// default no mass (ie collision object, nothing else.
		if (collider == null)
			collider = new BoxCollisionShape(0.5f);
		else
			usingAssignedCollisonState = true;
		rigidBody = new PhysicsRigidBody(collider, mass);
		collisionObject = rigidBody;
		rigidBody.setRestitution(0.0f);
		rigidBody.setAngularDamping(0.95f);
		rigidBody.setFriction(0.5f);
		usingRigidBody = true;
		
		this.addComponent(new Transform());
	}
	
	/**
	 * called by the update thread
	 */
	public void update() {
		((Transform) components.getCompoent(Transform.class)).commit(collisionObject);
		components.update();
	}
	
	/**
	 * called after all entities have finished rendering
	 */
	public void render() {
		components.render();
	}
	
	/**
	 * called when the entity is added to the world
	 */
	public void onAddedToWorld() {
		
	}
	
	/**
	 * called when the entity is removed from the world
	 */
	public void onRemovedFromWorld() {
		
	}
	
	/**
	 * SETS the linear velocity on the rigid body.
	 */
	public synchronized Entity setLinearVelocity(float x, float y, float z) {
		if (!usingRigidBody)
			return this;
		positionStore.x = x;
		positionStore.y = y;
		positionStore.z = z;
		this.rigidBody.setLinearVelocity(positionStore);
		this.rigidBody.activate();
		return this;
	}
	
	/**
	 * Applies a force from the center of the entity
	 */
	public synchronized Entity applyCentralForce(float x, float y, float z) {
		if (!usingRigidBody)
			return this;
		positionStore.x = x;
		positionStore.y = y;
		positionStore.z = z;
		this.rigidBody.applyCentralForce(positionStore);
		this.rigidBody.activate();
		return this;
	}
	
	/**
	 * Applies a impulse from the center of the entity
	 * Note: this ignores already existing forces, but not mass (it directly modifies the velocity)
	 */
	public synchronized Entity applyCentralImpulse(float x, float y, float z) {
		if (!usingRigidBody)
			return this;
		positionStore.x = x;
		positionStore.y = y;
		positionStore.z = z;
		this.rigidBody.applyCentralImpulse(positionStore);
		this.rigidBody.activate();
		return this;
	}
	
	public synchronized Vector3f getLinearVelocity() {
		if (!usingRigidBody)
			return null;
		this.rigidBody.getLinearVelocity(positionStore);
		return positionStore;
	}
	
	/**
	 * Best simulation results using zero restitution*
	 * Set the restitution, also known as the bounciness or spring. 
	 * The restitution may range from 0.0 (not bouncy) to 1.0 (extremely bouncy).
	 */
	public Entity setRestitution(float r) {
		if (!usingRigidBody)
			return this;
		this.rigidBody.setRestitution(r);
		return this;
	}
	
	/**
	 * Best simulation results when friction is non-zero*
	 * 
	 */
	public Entity setFriction(float f) {
		if (!usingRigidBody)
			return this;
		this.rigidBody.setFriction(f);
		return this;
	}
	
	/**
	 * friction but for spheres
	 * 
	 */
	public Entity setAngularDamping(float a) {
		if (!usingRigidBody)
			return this;
		this.rigidBody.setDamping(this.rigidBody.getLinearDamping(), a);
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
		
		if (this.isStatic())
			this.collisionObject.setCollisionShape(new MeshCollisionShape(true, this.model.getMeshColliderData()));
		else {
			// TODO: dynamics
			GImpactCollisionShape gics = new GImpactCollisionShape(this.model.getMeshColliderData());
			
			//CompoundCollisionShape ccs = new CompoundCollisionShape(this.model.getMeshes().length);
			
			//for (int i = 0; i < this.model.getMeshes().length; i++) {
			//	ccs.addChildShape(new HullCollisionShape(this.model.getMeshes()[i].getVertices()));
			//}
			
			this.collisionObject.setCollisionShape(gics);
		}
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

	public PhysicsRigidBody getRigidbody() {
		return rigidBody;
	}
	
	public PhysicsCollisionObject getCollisionObject() {
		return collisionObject;
	}

	public Entity setRigidbody(PhysicsRigidBody rigidbody) {
		// only if the world has been set (ie we've already spawned into the world)
		if (world != null)
			this.world.removeEntityPhysics(this);
		this.rigidBody = rigidbody;
		this.collisionObject = rigidbody;
		this.usingRigidBody = true;
		if (world != null)
			this.world.addEntityPhysics(this);
		return this;
	}
	
	public Entity setCollisionObject(PhysicsCollisionObject obj) {
		// only if the world has been set (ie we've already spawned into the world)
		if (world != null)
			this.world.removeEntityPhysics(this);
		this.collisionObject = obj;
		if (world != null)
			this.world.addEntityPhysics(this);
		this.usingRigidBody = false;
		return this;
	}
	
	public boolean usingRigidBody() {
		return usingRigidBody;
	}
	
	@Override
	public int hashCode() {
	    return id;
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

	/**
	 * allows for creation of trees inside HashMaps
	 */
	@Override
	public int compareTo(Entity o) {
		return (o.id < id) ? -1 : ((o.id == id) ? 0 : 1);
	}
	
}

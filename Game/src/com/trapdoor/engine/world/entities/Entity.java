package com.trapdoor.engine.world.entities;

import java.util.ArrayList;

import org.joml.Vector3d;

import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import com.trapdoor.engine.datatypes.collision.AxisAlignedBoundingBox;
import com.trapdoor.engine.datatypes.lighting.Light;
import com.trapdoor.engine.datatypes.ogl.assimp.Mesh;
import com.trapdoor.engine.datatypes.ogl.assimp.Model;
import com.trapdoor.engine.tools.Logging;
import com.trapdoor.engine.tools.input.Keyboard;
import com.trapdoor.engine.world.World;
import com.trapdoor.engine.world.entities.components.ComponentStore;
import com.trapdoor.engine.world.entities.components.IComponent;
import com.trapdoor.engine.world.entities.components.Transform;
import com.trapdoor.engine.world.entities.tools.EntityEvent;

/**
 * @author laptop
 * @date Nov. 21, 2021
 * 
 */
public class Entity implements Comparable<Entity>,Cloneable {
	
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
	
	public void updatePrint() {
		Transform t = (Transform) components.getCompoent(Transform.class);
		System.out.println("-----{PreCommit}-----");
		t.print();
		t.commit(collisionObject);
		System.out.println("-----{PostCommit}-----");
		t.print();
		components.update();
		if (Keyboard.isKeyDown(Keyboard.Q))
			System.exit(0);
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
	 * called every update frame while two entities are colliding
	 * @param other - the entity which we are colliding with
	 * @param event - the event itself (contains relevant data)
	 */
	public void onOngoingCollision(Entity other, PhysicsCollisionEvent event) {
		
	}
	
	/**
	 * called whenever an object collides with this entity
	 * NOTE: this can and often will be called multiple times per event.
	 * @param other - the entity which we are colliding with
	 * @param event - the event itself (contains relevant data)
	 */
	public void onCollision(Entity other, PhysicsCollisionEvent event) {
		
	}
	
	/**
	 * called by other entities when a specific event occurs
	 * eg the spawn event from the spawner entity 
	 * @param objects extra objects that might be supplied when this event is called
	 * TODO: use something other than ENUM that allows for expandability (on the game end)
	 */
	public void onSpecialEvent(EntityEvent e, Object... objects) {
		
	}
	
	public void delete() {
		this.world.removeEntityFromWorld(this);
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
		c.setAssociatedEntity(this);
		this.components.addComponent(c);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends IComponent> T getComponent(Class<T> c) {
		return (T) components.getCompoent(c);
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
		
		if (this.isStatic()) {
			MeshCollisionShape shape = new MeshCollisionShape(true, this.model.getMeshColliderData());
			shape.setMargin(0.10f);
			// disabled because this seems to prevent collision of certian objects with static entities
			shape.setContactFilterEnabled(false);
			this.collisionObject.setCollisionShape(shape);
		} else {
			//GImpactCollisionShape gics = new GImpactCollisionShape(this.model.getMeshColliderData());
			
			
			CompoundCollisionShape ccs = new CompoundCollisionShape(this.model.getMeshes().length);
			
			for (int i = 0; i < this.model.getMeshes().length; i++) {
				ccs.addChildShape(new HullCollisionShape(this.model.getMeshes()[i].getVertices()));
			}
			ccs.setMargin(0.10f);
			//ccs.setContactFilterEnabled(false);
			this.collisionObject.setCollisionShape(ccs);
			
			//this.collisionObject.setCollisionShape(gics);
		}
		return this;
	}
	
	/**
	 * sets the entity collider to a general box collider which attempts to fit the mesh data
	 * this is much more efficient then using a mesh collider (which is default)
	 */
	public Entity generateApproximateCollider() {
		if (this.model == null)
			throw new RuntimeException("Model cannot be null while trying to generate ridigid body from model!");
		if (usingAssignedCollisonState)
			return this;
		
		CompoundCollisionShape shaper = new CompoundCollisionShape();
		
		Mesh[] m = this.model.getMeshes();
		
		for (Mesh mesh : m) {
			AxisAlignedBoundingBox aabb = mesh.getBoundingBox();
			Vector3d ad = aabb.getCenterSafe();
			shaper.addChildShape(new BoxCollisionShape((float)aabb.getXHalfExtends(), (float)aabb.getYHalfExtends(), (float)aabb.getZHalfExtends()), 
									(float)ad.x, (float)ad.y, (float)ad.z);
		}
		
		this.collisionObject.setCollisionShape(shaper);
		
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
	 * 
	 * Update:
	 * 	ONLY USE THIS FOR CREATING ENTITIES.
	 * 	This is only to be used at entity creation! NEVER should it be used for anything else.
	 * 
	 */
	public Entity setPosition(float x, float y, float z) {
		((Transform)this.getComponent(Transform.class)).setPosition(this, x, y, z);
		return this;
	}

	/**
	 * allows for creation of trees inside HashMaps
	 */
	@Override
	public int compareTo(Entity o) {
		return (o.id < id) ? -1 : ((o.id == id) ? 0 : 1);
	}
	
	@Override
	@Deprecated
	protected Object clone() throws CloneNotSupportedException {
		Entity ent = (Entity) super.clone();
		ArrayList<IComponent> ar = ent.getComponents();
		for (int i = 0; i < ar.size(); i++) {
			IComponent c = (IComponent) ar.get(i).clone();
			c.setAssociatedEntity(ent);
			ent.getComponentStore().removeAll(c.getClass());
			ent.addComponent(c);
		}
		ent.positionStore = new Vector3f();
		ArrayList<Light> lights = new ArrayList<Light>();
		for (int i = 0; i < this.lights.size(); i++)
			lights.add(this.lights.get(i));
		ent.lights = lights;
		if (ent.collisionObject instanceof PhysicsRigidBody) {
			ent.rigidBody = copyRigidBody(ent.getRigidbody());
			ent.collisionObject = ent.rigidBody;
		} else 
			Logging.logger.error("Unable to properly clone phyisics object! Unexpected issues may occur!");
		return super.clone();
	}
	
	private static PhysicsRigidBody copyRigidBody(PhysicsRigidBody old) {
		PhysicsRigidBody body = new PhysicsRigidBody(old.getCollisionShape(), old.getMass());
		body.setRestitution(old.getRestitution());
		body.setAngularDamping(old.getAngularDamping());
		body.setAngularFactor(old.getAngularFactor(new Vector3f()));
		body.setFriction(old.getFriction());
		body.setRollingFriction(old.getRollingFriction());
		body.setSpinningFriction(old.getSpinningFriction());
		body.setContactStiffness(old.getContactStiffness());
		body.setContactDamping(old.getContactDamping());
		body.setDamping(old.getLinearDamping(), old.getAngularDamping());
		return body;
	}
	
}

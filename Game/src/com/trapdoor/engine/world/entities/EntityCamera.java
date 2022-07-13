package com.trapdoor.engine.world.entities;

import java.util.List;

import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.trapdoor.engine.UBOLoader;
import com.trapdoor.engine.camera.Camera;
import com.trapdoor.engine.camera.CreativeFirstPerson;
import com.trapdoor.engine.datatypes.collision.AxisAlignedBoundingBox;
import com.trapdoor.engine.registry.GameRegistry;
import com.trapdoor.engine.registry.Threading;
import com.trapdoor.engine.renderer.functions.INoRenderEntity;
import com.trapdoor.engine.renderer.ui.DebugInfo;
import com.trapdoor.engine.tools.input.InputMaster;
import com.trapdoor.engine.tools.input.Keyboard;
import com.trapdoor.engine.tools.input.Mouse;
import com.trapdoor.engine.world.entities.components.SoundListener;
import com.trapdoor.engine.world.entities.components.Transform;
import com.trapdoor.engine.world.entities.tools.Weapon;
import com.trapdoor.engine.world.entities.tools.WeaponGreg;

/**
 * @author brett
 * @date Dec. 20, 2021
 * 
 */
public class EntityCamera extends Entity implements INoRenderEntity {
	
	protected static float speed = 0.001f;
	@SuppressWarnings("unused")
	private static final int RECUR_AMT = 100;
	
	protected float moveAtX = 0;
	protected float moveAtZ = 0;
	
	private Vector3f pos;
	
	protected Camera c;
	
	private Transform localTransform;
	
	private Vector3f at,up;
	
	protected PhysicsCharacter ch;
	protected final com.jme3.math.Vector3f store = new com.jme3.math.Vector3f();
	private SoundListener sl;
	
	private WeaponGreg greg;
	
	public EntityCamera(Camera c) {
		super(50);
		this.c = c;
		pos = new Vector3f();
		at = new Vector3f();
		up = new Vector3f();
		this.localTransform = (Transform) this.getComponent(Transform.class);
		
		ch = new PhysicsCharacter(new CapsuleCollisionShape(0.5f, 2.0f), 0.5f);
		ch.setJumpSpeed(25.0f);
		this.setCollisionObject(ch);
		this.sl = new SoundListener();
		this.addComponent(sl);
		
	}
	
	@Override
	public void render() {
		super.render();
		UBOLoader.updateMatrixUBO();
	}
	
	@Override
	public void update() {
		super.update();
		
		if (c instanceof CreativeFirstPerson) {
			CreativeFirstPerson cr = ((CreativeFirstPerson) c);
			if (cr.allowFreeMovement) {
				cr.move2();
				return;	
			}
		}
		
		this.localTransform.setRotation(c.getYaw(), c.getPitch(), c.getRoll());
		move();
		this.pos.x = this.localTransform.getX();
		// TODO
		this.pos.y = this.localTransform.getY() + 1.5f;
		this.pos.z = this.localTransform.getZ();
		DebugInfo.x = this.pos.x;
		DebugInfo.y = this.pos.y;
		DebugInfo.z = this.pos.z;
		this.c.setPosition(pos);
		
		this.c.getViewMatrix().positiveZ(at).negate();
		this.c.getViewMatrix().positiveY(up);
		this.sl.setOrientation(at, up);
		
		c.updateViewMatrix();
	}
	
	private float offset = 2;
	private float force = 5000;
	private float scale = 0.5f;
	private Entity grabbedEnt = null;
	private float distance = -1200;
	@SuppressWarnings("unused")
	private float yaw, pitch, roll;
	private float scrollAmount = 0;
	
	public void grab(Vector3f ray) {
		if (grabbedEnt == null) {
			List<PhysicsRayTestResult> results = this.world.raycastSorted(ray, 50);
			for (int i = 0; i < results.size(); i++) {
				Entity ass = this.world.getEntity(results.get(i).getCollisionObject());
				if (!ass.isStatic() && ass != this) {
					grabbedEnt = ass;
					grabbedEnt.getRigidbody().setGravity(new com.jme3.math.Vector3f());
					Transform t = grabbedEnt.getComponent(Transform.class);
					yaw = t.getYaw();
					pitch = t.getPitch();
					roll = t.getRoll();
					break;
				}
			}
		} else {
			Transform t = grabbedEnt.getComponent(Transform.class);
			AxisAlignedBoundingBox aabb = grabbedEnt.getModel().getAABB();
			Vector3d center = aabb.getCenter();
			float ox = t.getX();
			float oy = t.getY();
			float oz = t.getZ();
			if (distance < 1) {
				float dx = ox - this.localTransform.getX();
				float dy = oy - this.localTransform.getY();
				float dz = oz - this.localTransform.getZ();
				distance = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
			} else {
				if (InputMaster.scrollState != 0)
					scrollAmount = 35 * InputMaster.scrollState;
				else {
					if (scrollAmount != 0)
						scrollAmount += -Math.signum(scrollAmount) * 2.0f;
				}
				distance += scrollAmount * Threading.getFrameTimeSeconds();
				double l = center.length();
				if (distance < l)
					distance = (float) l;
			}
			float lx = ray.x * distance + this.localTransform.getX() + (float) center.x;
			// add height of player
			float ly = ray.y * distance + this.localTransform.getY() + (float) center.y + 1.0f;
			float lz = ray.z * distance + this.localTransform.getZ() + (float) center.z;

			final float mul = 3250;

			float dx = (ox - lx);
			float dy = (oy - ly);
			float dz = (oz - lz);

			float localDist = (float) Math.sqrt(dx * dx + dy * dy + dz * dz) * 2;
			dx *= -mul * localDist;
			dy *= -mul * localDist;
			dz *= -mul * localDist;

			grabbedEnt.setLinearVelocity(0, 0, 0);
			grabbedEnt.applyCentralForce(dx, dy, dz);
		}
	}
	
	public void notgrab() {
		if (grabbedEnt != null)
			grabbedEnt.getRigidbody().setGravity(world.getGravity());
		grabbedEnt = null;
		distance = 0;
	}
	
	@Override
	public void onAddedToWorld() {
		super.onAddedToWorld();
		greg = new WeaponGreg(this, world);
	}
	
	public Weapon getGreg() {
		return greg;
	}
	
	public void shoot(Vector3f ray) {
		SelfDeletingEntity bullet = new SelfDeletingEntity(50, 50000);
		bullet.setModel(GameRegistry.getModel("resources/models/depression.dae"));
		bullet.getComponent(Transform.class).setScale(scale, scale, scale);
		bullet.setPosition(this.localTransform.getX() + ray.x * offset, this.localTransform.getY() + ray.y * offset, this.localTransform.getZ() + ray.z * offset);
		bullet.applyCentralImpulse(ray.x * force, ray.y * force, ray.z * force);
		bullet.getRigidbody().setFriction(5f);
		bullet.generateApproximateCollider();
		this.world.addEntityToWorld(bullet);
	}
	
	final float walkSpeed = 20f;
	final float runSpeed = 150f/2f;
	
	protected void move() {
		if (!Mouse.isGrabbed())
			return;
		
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_ALT))
			speed = walkSpeed / 8f;
		else if (Keyboard.isKeyDown(Keyboard.L_CONTROL))
			speed = runSpeed;
		else
			speed = walkSpeed;
		
		final float timeConstant = (float) 1;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W))
			moveAtX = (-speed * timeConstant);
		
		else if (Keyboard.isKeyDown(Keyboard.KEY_S))
			moveAtX = (speed * timeConstant);
		else
			moveAtX = 0;
			
		if (Keyboard.isKeyDown(Keyboard.KEY_A))
			moveAtZ = (speed * timeConstant);
		else
		if (Keyboard.isKeyDown(Keyboard.KEY_D))
			moveAtZ = (-speed * timeConstant);
		else 
			moveAtZ = 0;
		
		float dx = (float) ((((-((moveAtX) * Math.sin(Math.toRadians(c.getYaw()))  )) + -((moveAtZ) * Math.cos(Math.toRadians(c.getYaw())) ))) );
		float dz = (float) ( (((moveAtX) * Math.cos(Math.toRadians(c.getYaw()))  ) + -((moveAtZ) * Math.sin(Math.toRadians(c.getYaw())) )) );
		
		store.x = dx / 100;
		store.y = 0;
		store.z = dz / 100;
		ch.setWalkDirection(store);
		
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) && ch.onGround()) {
			ch.jump();
		}
	}
	
	public PhysicsCharacter getCharacter() {
		return ch;
	}
	
	@Override
	public Entity updateCollisionStateFromModel() {
		return this;
	}
	
	public Transform getLocalTransform() {
		return localTransform;
	}
	
}

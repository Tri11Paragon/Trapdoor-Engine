package com.trapdoor.engine.world.entities;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.trapdoor.engine.camera.Camera;
import com.trapdoor.engine.renderer.ui.DebugInfo;
import com.trapdoor.engine.tools.input.Keyboard;
import com.trapdoor.engine.tools.input.Mouse;
import com.trapdoor.engine.world.entities.components.Transform;

/**
 * @author brett
 * @date Dec. 20, 2021
 * 
 */
public class EntityCamera extends Entity {
	
	private static float speed = 40f;
	@SuppressWarnings("unused")
	private static final int RECUR_AMT = 100;
	
	private float moveAtX = 0;
	private float moveAtY = 0;
	private float moveatZ = 0;
	
	private Vector3f pos;
	
	private Camera c;
	
	private Transform localTransform;
	
	public EntityCamera(Camera c) {
		super(50);
		this.c = c;
		pos = new Vector3f();
		
		this.setRigidbody(new RigidBody(50, new DefaultMotionState(
				new com.bulletphysics.linearmath.Transform(
						new Matrix4f(
								// rotation
								new Quat4f(0,0,0,1),
								// position, + w
								new javax.vecmath.Vector3f(0,0,0), 1.0f
								)
						)
				), new BoxShape(new javax.vecmath.Vector3f(0.75f, 0.75f, 0.75f))) );
		
		this.getRigidbody().setGravity(new javax.vecmath.Vector3f(0.0f, 0.0f, 0.0f));
		
		this.localTransform = (Transform) this.getComponent(Transform.class);
	}
	
	@Override
	public void update() {
		super.update();
		this.localTransform.setRotation(c.getYaw(), c.getPitch(), c.getRoll());
		move();
		this.pos.x = this.localTransform.getX();
		this.pos.y = this.localTransform.getY();
		this.pos.z = this.localTransform.getZ();
		DebugInfo.x = this.pos.x;
		DebugInfo.y = this.pos.y;
		DebugInfo.z = this.pos.z;
		c.setPosition(pos);
	}
	
	private void move() {
		if (!Mouse.isGrabbed())
			return;
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_ALT))
			speed = 5f;
		else if (Keyboard.isKeyDown(Keyboard.L_CONTROL))
			speed=150f;
		else
			speed = 40f;
		
		final float timeConstant = (float) 1;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W))
			moveAtX = (-speed * timeConstant);
		
		else if (Keyboard.isKeyDown(Keyboard.KEY_S))
			moveAtX = (speed * timeConstant);
		else
			moveAtX = 0;
			
		if (Keyboard.isKeyDown(Keyboard.KEY_A))
			moveatZ = (speed * timeConstant);
		else
		if (Keyboard.isKeyDown(Keyboard.KEY_D))
			moveatZ = (-speed * timeConstant);
		else 
			moveatZ = 0;

		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))
			moveAtY = (speed * timeConstant);
		else
			moveAtY = 0;
			
		if (Keyboard.isKeyDown(Keyboard.LEFT_SHIFT))
			moveAtY = (-speed * timeConstant);
		
		float dx = (float) ((((-((moveAtX) * Math.sin(Math.toRadians(c.getYaw()))  )) + -((moveatZ) * Math.cos(Math.toRadians(c.getYaw())) ))) );
		float dy = moveAtY;
		float dz = (float) ( (((moveAtX) * Math.cos(Math.toRadians(c.getYaw()))  ) + -((moveatZ) * Math.sin(Math.toRadians(c.getYaw())) )) );
		
		//this.localTransform.setPosition(this.localTransform.getX() + dx, this.localTransform.getY() + dy, this.localTransform.getZ() + dz);
		applyWithoutBreakingVelocity(dx, dy, dz);
		
	}
	
	//private final javax.vecmath.Vector3f vel = new javax.vecmath.Vector3f();
	public void applyWithoutBreakingVelocity(float x, float y, float z) {
		// TODO: this;
		/*vel.x = 0;
		vel.y = 0;
		vel.z = 0;
		this.getRigidbody().getLinearVelocity(vel);
		float vx = vel.x + x, vy = vel.y + y, vz = vel.z + z;
		
		if (vx >= x)
			vel.x = x;
		if (vy >= y)
			vel.y = y;
		if (vz >= z)
			vel.z = z;
		
		this.getRigidbody().setLinearVelocity(vel);*/
		this.setLinearVelocity(x, y, z);
	}
	
}

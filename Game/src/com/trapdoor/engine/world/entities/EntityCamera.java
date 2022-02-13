package com.trapdoor.engine.world.entities;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.trapdoor.engine.camera.Camera;
import com.trapdoor.engine.renderer.ui.DebugInfo;
import com.trapdoor.engine.tools.input.Keyboard;
import com.trapdoor.engine.tools.input.Mouse;
import com.trapdoor.engine.world.entities.components.SoundListener;
import com.trapdoor.engine.world.entities.components.Transform;

/**
 * @author brett
 * @date Dec. 20, 2021
 * 
 */
public class EntityCamera extends Entity {
	
	private static float speed = 0.001f;
	@SuppressWarnings("unused")
	private static final int RECUR_AMT = 100;
	
	private float moveAtX = 0;
	private float moveatZ = 0;
	
	private Vector3f pos;
	
	private Camera c;
	
	private Transform localTransform;
	
	private Vector3f at,up;
	
	private PhysicsCharacter ch;
	private final com.jme3.math.Vector3f store = new com.jme3.math.Vector3f();
	private SoundListener sl;
	
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
	public void update() {
		super.update();
		
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
		sl.setOrientation(at, up);
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
		
		float dx = (float) ((((-((moveAtX) * Math.sin(Math.toRadians(c.getYaw()))  )) + -((moveatZ) * Math.cos(Math.toRadians(c.getYaw())) ))) );
		float dz = (float) ( (((moveAtX) * Math.cos(Math.toRadians(c.getYaw()))  ) + -((moveatZ) * Math.sin(Math.toRadians(c.getYaw())) )) );
		
		store.x = dx / 100;
		store.y = 0;
		store.z = dz / 100;
		ch.setWalkDirection(store);
		
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) && ch.onGround()) {
			ch.jump();
		}
	}
	
	@Override
	public Entity updateCollisionStateFromModel() {
		return this;
	}
	
}

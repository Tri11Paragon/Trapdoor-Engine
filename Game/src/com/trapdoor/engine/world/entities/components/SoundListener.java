package com.trapdoor.engine.world.entities.components;

import org.joml.Vector3f;
import org.lwjgl.openal.AL11;

import com.trapdoor.engine.world.entities.Entity;

/**
 * @author brett
 * @date Feb. 11, 2022
 * 
 */
public class SoundListener extends IComponent {

	private Transform entityTransform;

	public SoundListener() {
		this(0, 0, 0);
	}

	public SoundListener(Vector3f position) {
		AL11.alListener3f(AL11.AL_POSITION, position.x, position.y, position.z);
		AL11.alListener3f(AL11.AL_VELOCITY, 0, 0, 0);
	}

	public SoundListener(float x, float y, float z) {
		AL11.alListener3f(AL11.AL_POSITION, x, y, z);
		AL11.alListener3f(AL11.AL_VELOCITY, 0, 0, 0);
	}

	public void setSpeed(Vector3f speed) {
		setSpeed(speed.x, speed.y, speed.z);
	}

	public void setSpeed(float x, float y, float z) {
		AL11.alListener3f(AL11.AL_VELOCITY, x, y, z);
	}

	public void setPosition(Vector3f position) {
		setPosition(position.x, position.y, position.z);
	}

	public void setPosition(float x, float y, float z) {
		AL11.alListener3f(AL11.AL_POSITION, x, y, z);
	}

	public void setOrientation(Vector3f at, Vector3f up) {
		float[] data = new float[6];
		data[0] = at.x;
		data[1] = at.y;
		data[2] = at.z;
		data[3] = up.x;
		data[4] = up.y;
		data[5] = up.z;
		AL11.alListenerfv(AL11.AL_ORIENTATION, data);
	}

	@Override
	public void render() {

	}

	@Override
	public void update() {
		if (this.entityTransform != null)
			this.setPosition(this.entityTransform.getX(), this.entityTransform.getY(), this.entityTransform.getZ());
//		try (MemoryStack stack = MemoryStack.stackPush()){
//    		FloatBuffer b1 = stack.callocFloat(1);
//    		FloatBuffer b2 = stack.callocFloat(1);
//    		FloatBuffer b3 = stack.callocFloat(1);
//    		AL11.alGetListener3f(AL11.AL_POSITION, b1, b2, b3);
//    		System.out.println(b1.get() + " " + b2.get() + " " + b3.get());
//    	}
	}

	@Override
	public void setAssociatedEntity(Entity e) {
		super.setAssociatedEntity(e);
		this.entityTransform = (Transform) e.getComponent(Transform.class);
	}
}

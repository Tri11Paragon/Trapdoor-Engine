package com.trapdoor.engine.renderer.particles;

import org.joml.Vector3d;
import org.joml.Vector3f;

import com.trapdoor.engine.camera.Camera;
import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.world.World;

public class Particle {
	
	// TODO: update position and velocity on the phys thread
	
	private Vector3f position;
	private Vector3f velocity;
	private float gravityEffect;
	private float lifeLength;
	private float rotation;
	private float scale;
	
	private float elapsedTime = 0;
	private float distance;
	
	protected int currentTexture;
	protected int nextTexture;
	protected float blend;
	
	private final Vector3d store = new Vector3d();
	
	public Particle(Vector3f position, Vector3f velocity, float gravityEffect, float lifeLength, float rotation,
			float scale) {
		this.position = position;
		this.velocity = velocity;
		this.gravityEffect = gravityEffect;
		this.lifeLength = lifeLength;
		this.rotation = rotation;
		this.scale = scale;
	}

	public boolean update(World world, Camera camera) {
		velocity.y += world.getGravity().y * gravityEffect * DisplayManager.getFrameTimeSeconds();
		position.x += (float) (velocity.x * DisplayManager.getFrameTimeSeconds());
		position.y += (float) (velocity.y * DisplayManager.getFrameTimeSeconds());
		position.z += (float) (velocity.z * DisplayManager.getFrameTimeSeconds());
		store.x = position.x;
		store.y = position.y;
		store.z = position.z;
		store.sub(camera.getPosition());
		distance = (float) (store.x * store.x + store.y + store.y + store.z + store.z);
		elapsedTime += DisplayManager.getFrameTimeSeconds();
		updateTextureBlend();
		return elapsedTime < lifeLength;
	}
	
	protected void updateTextureBlend() {
		this.blend = elapsedTime / lifeLength;
	}
	
	public Vector3f getPosition() {
		return position;
	}

	public float getRotation() {
		return rotation;
	}

	public float getScale() {
		return scale;
	}
	
	public float getBlend() {
		return blend;
	}
	
	public float getDistance() {
		return distance;
	}
	
	public int getCurrentTexture() {
		return currentTexture;
	}
	
	public int getNextTexture() {
		return nextTexture;
	}

	public Particle setCurrentTexture(int currentTexture) {
		this.currentTexture = currentTexture;
		return this;
	}

	public Particle setNextTexture(int nextTexture) {
		this.nextTexture = nextTexture;
		return this;
	}
	
}

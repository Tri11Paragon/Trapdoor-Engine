package com.game.engine.world;

import com.game.engine.datatypes.ogl.Texture;
import com.game.engine.datatypes.ogl.obj.VAO;

/**
 * @author laptop
 * @date Nov. 21, 2021
 * 
 */
public class Entity {
	
	private float x,y,z;
	private float yaw,pitch,roll;
	private float sx=1,sy=1,sz=1;
	private VAO model;
	private Texture texture;
	
	public Entity() {
		
	}
	
	public void update() {
		
	}
	
	public void render() {
		
	}
	
	public synchronized float getX() {
		return x;
	}
	public synchronized Entity setX(float x) {
		this.x = x;
		return this;
	}
	public synchronized float getY() {
		return y;
	}
	public synchronized Entity setY(float y) {
		this.y = y;
		return this;
	}
	public synchronized float getZ() {
		return z;
	}
	public synchronized Entity setZ(float z) {
		this.z = z;
		return this;
	}
	public synchronized float getYaw() {
		return yaw;
	}
	public synchronized Entity setYaw(float yaw) {
		this.yaw = yaw;
		return this;
	}
	public synchronized float getPitch() {
		return pitch;
	}
	public synchronized Entity setPitch(float pitch) {
		this.pitch = pitch;
		return this;
	}
	public synchronized float getRoll() {
		return roll;
	}
	public synchronized Entity setRoll(float roll) {
		this.roll = roll;
		return this;
	}
	public VAO getModel() {
		synchronized (model) {
			return model;
		}
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
	public synchronized Entity setPosition(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	public synchronized Entity addPosition(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}
	
}

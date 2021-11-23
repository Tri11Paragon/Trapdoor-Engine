package com.game.engine.world;

import com.game.engine.datatypes.ogl.Texture;
import com.game.engine.datatypes.ogl.obj.LoadedModel;

/**
 * @author laptop
 * @date Nov. 21, 2021
 * 
 */
public class Entity {
	
	private float x,y,z;
	private float yaw,pitch,roll;
	private float sx=1,sy=1,sz=1;
	private LoadedModel model;
	private Texture texture;
	
	public Entity() {
		
	}
	
	public void update() {
		
	}
	
	public void render() {
		
	}
	
	public float getX() {
		return x;
	}
	public Entity setX(float x) {
		this.x = x;
		return this;
	}
	public float getY() {
		return y;
	}
	public Entity setY(float y) {
		this.y = y;
		return this;
	}
	public float getZ() {
		return z;
	}
	public Entity setZ(float z) {
		this.z = z;
		return this;
	}
	public float getYaw() {
		return yaw;
	}
	public Entity setYaw(float yaw) {
		this.yaw = yaw;
		return this;
	}
	public float getPitch() {
		return pitch;
	}
	public Entity setPitch(float pitch) {
		this.pitch = pitch;
		return this;
	}
	public float getRoll() {
		return roll;
	}
	public Entity setRoll(float roll) {
		this.roll = roll;
		return this;
	}
	public LoadedModel getModel() {
		return model;
	}
	public Entity setModel(LoadedModel model) {
		this.model = model;
		return this;
	}
	public Texture getTexture() {
		return texture;
	}
	public Entity setTexture(Texture texture) {
		this.texture = texture;
		return this;
	}
	public float getSx() {
		return sx;
	}
	public Entity setSx(float sx) {
		this.sx = sx;
		return this;
	}
	public float getSy() {
		return sy;
	}
	public Entity setSy(float sy) {
		this.sy = sy;
		return this;
	}
	public float getSz() {
		return sz;
	}
	public Entity setSz(float sz) {
		this.sz = sz;
		return this;
	}
	public Entity setScale(float s) {
		this.sx = s;
		this.sy = s;
		this.sz = s;
		return this;
	}
	public Entity setPosition(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	public Entity addPosition(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}
	
}

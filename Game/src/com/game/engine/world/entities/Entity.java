package com.game.engine.world.entities;

import com.game.engine.datatypes.collision.colliders.AABB;
import com.game.engine.datatypes.ogl.Texture;
import com.game.engine.datatypes.ogl.obj.VAO;
import com.game.engine.world.World;

/**
 * @author laptop
 * @date Nov. 21, 2021
 * 
 */
public class Entity {
	
	private float x,y,z, lx, ly, lz;
	private volatile float nx, ny, nz;
	private float vx, vy, vz;
	private float yaw,pitch,roll;
	private float sx=1,sy=1,sz=1;
	private VAO model;
	private Texture texture;
	private AABB collider;
	// world reference
	private World world;
	private boolean isStatic = false;
	private boolean entityPositionUpdated = false;
	
	public Entity() {
		collider = new AABB(0, 0, 0, sx, sy, sz);
	}
	
	public void update() {
		
	}
	
	public void render() {
		
	}
	
	/**
	 * creates and sets the collider based on input parameters.
	 * note: this function does nothing special to make this based on entity position and nor should it
	 * as the collision system will automatically translate this collider.
	 */
	public Entity setCollider(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		this.collider = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
		return this;
	}
	
	/**
	 * creates a collider based on the supplied size, mins are set on 0
	 */
	public Entity setCollider(double size) {
		this.collider = new AABB(0, 0, 0, size, size, size);
		return this;
	}
	
	/**
	 * creates a collider centered on 0,0,0 with size
	 */
	public Entity setColliderCentered(double size) {
		this.collider = new AABB(-size, -size, -size, size,size,size);
		return this;
	}
	
	/**
	 * sets a translated AABB box collider. NOTE: this should not be translated based on entity world position but rather in entity local space
	 * it the X,Y,Z should be based around the entity's center position, not the world origin or entity position.
	 * @param size of the box
	 */
	public Entity setCollider(double x, double y, double z, double size) {
		this.collider = new AABB(x, y, z, x+size, y+size, z+size);
		return this;
	}
	
	/**
	 * sets a translated AABB box collider centered on the x,y,z. NOTE: this should not be translated based on entity world position but rather in entity local space
	 * it the X,Y,Z should be based around the entity's center position, not the world origin or entity position.
	 * @param size of the box
	 */
	public Entity setColliderCentered(int x, int y, int z, int size) {
		this.collider = new AABB(x-size, y-size, z-size, x+size, y+size, z+size);
		return this;
	}
	
	/**
	 * this function translates the internal box AABB collider based on the current position of the entity
	 * you can modify the box collider of this entity using the setCollider function
	 * @return
	 */
	public synchronized AABB getCollider() {
		return collider.translateThis(x, y, z);
	}
	
	public void setWorld(World w) {
		this.world = w;
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
		return model;
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
		// allow for reversing if this ends up colliding
		//this.lx = this.x;
		//this.ly = this.y;
		//this.lz = this.z;
		
		// going to ignore collision
		this.x = x;
		this.y = y;
		this.z = z;
		//entityPositionUpdated = true;
		return this;
	}
	public synchronized Entity addPosition(float x, float y, float z) {
		this.lx = this.x;
		this.ly = this.y;
		this.lz = this.z;
		
		this.nx = this.nx + x;
		this.ny = this.ny + y;
		this.nz = this.nz + z;
		
		//this.x += x;
		//this.y += y;
		//this.z += z;
		entityPositionUpdated = true;
		return this;
	}
	
	// called by world
	public void applyVelocity() {
		if (this.vx != 0 || this.vy != 0 || this.vz != 0) {
			this.nx += this.vx * World.getFrameTimeSeconds();
			this.ny += this.vy * World.getFrameTimeSeconds();
			this.nz += this.vz * World.getFrameTimeSeconds();
			this.entityPositionUpdated = true;
			System.out.println(this.nx + " " + this.entityPositionUpdated);
		}
	}
	
	// also a world function
	public void updateNCoords() {
		this.nx = this.x;
		this.ny = this.y;
		this.nz = this.z;
	}
	/**
	 * this function is for the world ONLY
	 * DO NOT CALL IT.
	 */
	public Entity addPositionWorld(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}
	public boolean isStatic() {
		return isStatic;
	}
	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}
	public boolean isUpdated() {
		return entityPositionUpdated;
	}
	public Entity clearUpdate() {
		entityPositionUpdated = false;
		return this;
	}
	
	public Entity setVelocity(float x, float y, float z) {
		this.vx = x;
		this.vy = y;
		this.vz = z;
		return this;
	}
	
	public synchronized Entity addVelocity(float x, float y, float z) {
		this.vx += x;
		this.vy += y;
		this.vz += z;
		return this;
	}
	
	public float getVx() {
		return vx;
	}

	public Entity setVx(float vx) {
		this.vx = vx;
		return this;
	}

	public float getVy() {
		return vy;
	}

	public Entity setVy(float vy) {
		this.vy = vy;
		return this;
	}

	public float getVz() {
		return vz;
	}

	public Entity setVz(float vz) {
		this.vz = vz;
		return this;
	}

	public float getLX() {
		return lx;
	}
	
	public float getLY() {
		return ly;
	}
	
	public float getLZ() {
		return lz;
	}
	
	public float getNX() {
		return nx;
	}
	
	public float getNY() {
		return ny;
	}
	
	public float getNZ() {
		return nz;
	}
	
}

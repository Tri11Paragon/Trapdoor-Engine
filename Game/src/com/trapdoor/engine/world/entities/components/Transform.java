package com.trapdoor.engine.world.entities.components;

import java.util.concurrent.atomic.AtomicBoolean;

import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.trapdoor.engine.camera.Camera;
import com.trapdoor.engine.world.entities.Entity;

/**
 * @author brett
 * @date Jan. 27, 2022
 * 
 */
public class Transform extends IComponent {
	
	// TODO: compress this to bit logic
	//private volatile boolean awaitingPositionChange = false;
	private volatile AtomicBoolean awaitingPositionChange = new AtomicBoolean(false);
	private volatile boolean awaitingRotationChange = false;
	private volatile boolean awaitingScaleChange = false;
	private volatile boolean isStatic = false;
	
	// phys
	private volatile com.jme3.math.Transform pysTransformOut;
	private volatile boolean transformReady = false;
	private Vector3f scaleStore;
	private Vector3f positionStore;
	private Quaternion physQuat;
	private Quaternion physQuatStore;
	private final Quaterniond localStore = new Quaterniond();
	private float distanceToCamera;
	
	
	// render
	private volatile float yaw,pitch,roll;
	private volatile float setYaw,setPitch,setRoll;
	private volatile Matrix4f mainRotation;
	
	private volatile float x,y,z;
	private volatile float setX,setY,setZ;
	private Vector3f positionOut;
	
	private volatile float scaleX=1, scaleY=1, scaleZ=1;
	private volatile float setScaleX=1, setScaleY=1, setScaleZ=1;
	
	public Transform() {
		super();
		this.pysTransformOut = new com.jme3.math.Transform();
		this.mainRotation = new Matrix4f();
		this.positionOut = new Vector3f();
		this.scaleStore = new Vector3f();
		this.positionStore = new Vector3f();
		this.physQuat = new Quaternion();
		this.physQuatStore = new Quaternion();
	}
	
	@Override
	public void render() {
		if (isStatic)
			return;
		if (transformReady) {
			this.x = pysTransformOut.getTranslation().x;
			this.y = pysTransformOut.getTranslation().y;
			this.z = pysTransformOut.getTranslation().z;
	
			this.positionOut.x = this.x;
			this.positionOut.y = this.y;
			this.positionOut.z = this.z;
			
			localStore.set(this.physQuat.getX(), this.physQuat.getY(), this.physQuat.getZ(), this.physQuat.getW());
			this.mainRotation.set(localStore);
			
			this.transformReady = false;
		}
	}

	@Override
	public void update() {
		
	}

	@Override
	public void setAssociatedEntity(Entity e) {
		super.setAssociatedEntity(e);
		isStatic = e.isStatic();
	}
	
	public void commit(PhysicsCollisionObject b) {
		if (isStatic)
			return;
		if (!this.transformReady) {
			PhysicsRigidBody body = null;
			if (b instanceof PhysicsRigidBody)
				body = (PhysicsRigidBody) b;
			b.getTransform(pysTransformOut);
			b.getPhysicsRotation(physQuat);
			
			if (this.awaitingPositionChange.get() && body != null) {
				this.positionStore.x = this.setX;
				this.positionStore.y = this.setY;
				this.positionStore.z = this.setZ;
				
				body.setPhysicsLocation(positionStore);
				
				this.awaitingPositionChange.set(false);
			}
			
			if (this.awaitingRotationChange && body != null) {
				// TODO: get yaw, pitch and roll from the matrix
				this.yaw = this.setYaw;
				this.pitch = this.setPitch;
				this.roll = this.setRoll;

				this.physQuatStore.fromAngles(pitch, yaw, roll);
				body.setPhysicsRotation(physQuatStore);
				
				this.awaitingRotationChange = false;
			}
			
			if (this.awaitingScaleChange) {
				this.scaleStore.x = this.setScaleX;
				this.scaleStore.y = this.setScaleY;
				this.scaleStore.z = this.setScaleZ;
					
				b.getCollisionShape().setScale(this.scaleStore);
				if (this.associatedEntity().getModel() != null)
					this.associatedEntity().getModel().getAABB().scale(this.setScaleX, this.setScaleY, this.setScaleZ);
				
				this.scaleX = setScaleX;
				this.scaleY = setScaleY;
				this.scaleZ = setScaleZ;
				
				this.awaitingScaleChange = false;
			}
			
			this.transformReady = true;
		}
	}
	
	public void updateDistanceToCamera(Camera c) {
		Vector3d pos = c.getPosition();
		float dx = (float) (this.x - pos.x);
		float dy = (float) (this.y - pos.y);
		float dz = (float) (this.z - pos.z);
		
		this.distanceToCamera = dx * dx + dy * dy + dz * dz;
	}
	
	/**
	 * only use for setting position at startup!
	 */
	public Transform setPosition(Entity e, float x, float y, float z) {
		this.setX = x;
		this.setY = y;
		this.setZ = z;
		PhysicsRigidBody r = e.getRigidbody();
		if (r != null) {
			this.positionStore.x = this.setX;
			this.positionStore.y = this.setY;
			this.positionStore.z = this.setZ;
			r.setPhysicsLocation(positionStore);
		}
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	public Transform setPosition(float x, float y, float z) {
		this.setX = x;
		this.setY = y;
		this.setZ = z;
		this.awaitingPositionChange.set(true);
		return this;
	}
	
	public Transform setX(float x) {
		this.setX = x;
		this.setY = this.y;
		this.setZ = this.z;
		this.awaitingPositionChange.set(true);
		return this;
	}
	public Transform setY(float y) {
		this.setX = this.x;
		this.setY = y;
		this.setZ = this.z;
		this.awaitingPositionChange.set(true);
		return this;
	}
	public Transform setZ(float z) {
		this.setX = this.x;
		this.setY = this.y;
		this.setZ = z;
		this.awaitingPositionChange.set(true);
		return this;
	}
	
	public Transform setScale(float x, float y, float z) {
		this.setScaleX = x;
		this.setScaleY = y;
		this.setScaleZ = z;
		this.awaitingScaleChange = true;
		return this;
	}
	/*public Transform setScaleX(float x) {
		this.setScaleX = x;
		this.awaitingScaleChange = true;
		return this;
	}
	public Transform setScaleY(float y) {
		this.setScaleY = y;
		this.awaitingScaleChange = true;
		return this;
	}
	public Transform setScaleZ(float z) {
		this.setScaleZ = z;
		this.awaitingScaleChange = true;
		return this;
	}*/
	public Transform setRotation(float yaw, float pitch, float roll) {
		this.setYaw = yaw;
		this.setPitch = pitch;
		this.setRoll = roll;
		this.awaitingRotationChange = true;
		return this;
	}
	public Transform setYaw(float y) {
		this.setYaw = y;
		this.awaitingRotationChange = true;
		return this;
	}
	public Transform setPitch(float p) {
		this.setPitch = p;
		this.awaitingRotationChange = true;
		return this;
	}
	public Transform setRoll(float r) {
		this.setRoll = r;
		this.awaitingRotationChange = true;
		return this;
	}
	
	

	public float getDistanceToCamera() {
		return distanceToCamera;
	}
	public float getX() {
		return this.x;
	}
	public float getY() {
		return this.y;
	}
	public float getZ() {
		return this.z;
	}
	public Vector3f getPosition() {
		return this.positionOut;
	}
	public Vector3f getPosition(Vector3f out) {
		out.x = this.x;
		out.y = this.y;
		out.z = this.z;
		return out;
	}
	public float getScaleX() {
		return this.scaleX;
	}
	public float getScaleY() {
		return this.scaleY;
	}
	public float getScaleZ() {
		return this.scaleZ;
	}
	public Matrix4f getRotationMatrix() {
		return this.mainRotation;
	}
	public float getYaw() {
		return yaw;
	}
	public float getPitch() {
		return pitch;
	}
	public float getRoll() {
		return roll;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		Transform clone = new Transform();
		clone.awaitingPositionChange = this.awaitingPositionChange;
		clone.awaitingRotationChange = this.awaitingRotationChange;
		clone.awaitingScaleChange = this.awaitingScaleChange;
		clone.e = this.e;
		clone.isStatic = this.isStatic;
		clone.localStore.set(this.localStore);
		clone.mainRotation.set(this.mainRotation);
		clone.physQuat.set(this.physQuat);
		clone.physQuatStore.set(this.physQuatStore);
		clone.pitch = this.pitch;
		clone.positionOut.set(this.positionOut);
		clone.positionStore.set(this.positionStore);
		clone.roll = this.roll;
		clone.scaleStore.set(this.scaleStore);
		clone.scaleX = this.scaleX;
		clone.scaleY = this.scaleY;
		clone.scaleZ = this.scaleZ;
		clone.setPitch = this.setPitch;
		clone.setRoll = this.setRoll;
		clone.setScaleX = this.setScaleX;
		clone.setScaleY = this.setScaleY;
		clone.setScaleZ = this.setScaleZ;
		clone.setX = this.setX;
		clone.setY = this.setY;
		clone.setZ = this.setZ;
		clone.setYaw = this.setYaw;
		clone.yaw = this.yaw;
		clone.transformReady = this.transformReady;
		clone.x = this.x;
		clone.y = this.y;
		clone.z = this.z;
		return super.clone();
	}
	
}

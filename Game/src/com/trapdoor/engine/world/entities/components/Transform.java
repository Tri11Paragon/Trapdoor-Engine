package com.trapdoor.engine.world.entities.components;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.vecmath.Vector3f;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import com.bulletphysics.dynamics.RigidBody;
import com.trapdoor.engine.tools.math.Maths;
import com.trapdoor.engine.world.entities.Entity;

/**
 * @author brett
 * @date Jan. 27, 2022
 * 
 */
public class Transform extends IComponent {

	private final Matrix3f rotMatrix = new Matrix3f();
	private final float[] rotFloatArray = new float[3 * 3];
	
	// TODO: compress this to bit logic
	//private volatile boolean awaitingPositionChange = false;
	private volatile AtomicBoolean awaitingPositionChange = new AtomicBoolean(false);
	private volatile boolean awaitingRotationChange = false;
	private volatile boolean awaitingScaleChange = false;
	
	// phys
	private volatile com.bulletphysics.linearmath.Transform pysTransformOut;
	private volatile boolean transformReady = false;
	private Vector3f scaleStore;
	private Vector3f positionStore;
	
	
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
		this.pysTransformOut = new com.bulletphysics.linearmath.Transform();
		this.mainRotation = new Matrix4f();
		this.positionOut = new Vector3f();
		this.scaleStore = new Vector3f();
		this.positionStore = new Vector3f();
	}
	
	@Override
	public void render() {
		if (transformReady) {
			this.x = this.pysTransformOut.origin.x;
			this.y = this.pysTransformOut.origin.y;
			this.z = this.pysTransformOut.origin.z;
	
			this.positionOut.x = x;
			this.positionOut.y = y;
			this.positionOut.z = z;
	
			//this.mainRotation.identity();
			this.mainRotation.m00(this.pysTransformOut.basis.m00);
			this.mainRotation.m01(this.pysTransformOut.basis.m01);
			this.mainRotation.m02(this.pysTransformOut.basis.m02);
			this.mainRotation.m10(this.pysTransformOut.basis.m10);
			this.mainRotation.m11(this.pysTransformOut.basis.m11);
			this.mainRotation.m12(this.pysTransformOut.basis.m12);
			this.mainRotation.m20(this.pysTransformOut.basis.m20);
			this.mainRotation.m21(this.pysTransformOut.basis.m21);
			this.mainRotation.m22(this.pysTransformOut.basis.m22);
			
			this.transformReady = false;
		}
	}

	@Override
	public void update() {
		
	}

	@Override
	public void setAssociatedEntity(Entity e) {
		
	}
	
	public void commit(RigidBody b) {
		//if (this.awaitingPositionChange.get())
		//	System.out.println("Eeee" + this.transformReady);
		if (!this.transformReady) {
			b.getWorldTransform(pysTransformOut);
			
			if (this.awaitingPositionChange.get()) {
				this.positionStore.x = this.setX;
				this.positionStore.y = this.setY;
				this.positionStore.z = this.setZ;
				
				b.translate(positionStore);
				
				this.awaitingPositionChange.set(false);
			}
			
			if (this.awaitingRotationChange) {
				// TODO: get yaw, pitch and roll from the matrix
				this.yaw = this.setYaw;
				this.pitch = this.setPitch;
				this.roll = this.setRoll;
				
				// TODO: fix this gay mess by using another phyiscs engine!
				this.rotMatrix.identity();
				this.rotMatrix.rotate(pitch, Maths.rx);
				this.rotMatrix.rotate(yaw, Maths.ry);
				this.rotMatrix.rotate(roll, Maths.rz);
				this.pysTransformOut.basis.set(this.rotMatrix.get(rotFloatArray));
				
				this.awaitingRotationChange = false;
			}
			
			if (this.awaitingScaleChange) {
				this.scaleStore.x = this.setScaleX;
				this.scaleStore.y = this.setScaleY;
				this.scaleStore.z = this.setScaleZ;
					
				b.getCollisionShape().setLocalScaling(this.scaleStore);
					
				this.scaleX = setScaleX;
				this.scaleY = setScaleY;
				this.scaleZ = setScaleZ;
				
				this.awaitingScaleChange = false;
			}
			
			this.transformReady = true;
		}
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
	
}

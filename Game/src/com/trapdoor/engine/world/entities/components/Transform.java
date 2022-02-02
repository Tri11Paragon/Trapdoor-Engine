package com.trapdoor.engine.world.entities.components;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

import com.bulletphysics.dynamics.RigidBody;
import com.trapdoor.engine.registry.Threading;
import com.trapdoor.engine.tools.Logging;
import com.trapdoor.engine.world.entities.Entity;

/**
 * @author brett
 * @date Jan. 27, 2022
 * 
 */
public class Transform extends IComponent {

	// phys
	private volatile boolean transformReady = false;
	private volatile boolean scaleReady = false;
	private volatile com.bulletphysics.linearmath.Transform pysTransformOut;
	private Vector3f scaleStore;
	
	
	// render
	private volatile Matrix3f mainRotation;
	private volatile float x,y,z;
	private volatile float setX,setY,setZ;
	private volatile Vector3f positionStore;
	
	private volatile float scaleX, scaleY, scaleZ;
	private volatile float setScaleX, setScaleY, setScaleZ;
	
	public Transform() {
		super();
		pysTransformOut = new com.bulletphysics.linearmath.Transform();
		mainRotation = new Matrix3f();
		positionStore = new Vector3f();
		scaleStore = new Vector3f();
	}
	
	@Override
	public void render() {
		if (transformReady) {
			this.mainRotation.m00 = this.pysTransformOut.basis.m00;
			this.mainRotation.m01 = this.pysTransformOut.basis.m01;
			this.mainRotation.m02 = this.pysTransformOut.basis.m02;
			this.mainRotation.m10 = this.pysTransformOut.basis.m10;
			this.mainRotation.m11 = this.pysTransformOut.basis.m11;
			this.mainRotation.m12 = this.pysTransformOut.basis.m12;
			this.mainRotation.m20 = this.pysTransformOut.basis.m20;
			this.mainRotation.m21 = this.pysTransformOut.basis.m21;
			this.mainRotation.m22 = this.pysTransformOut.basis.m22;
			
			this.x = this.pysTransformOut.origin.x;
			this.y = this.pysTransformOut.origin.y;
			this.z = this.pysTransformOut.origin.z;
			
			this.positionStore.x = this.setX;
			this.positionStore.y = this.setY;
			this.positionStore.z = this.setZ;
			
			if (this.scaleReady) {
				this.scaleX = setScaleX;
				this.scaleY = setScaleY;
				this.scaleZ = setScaleZ;
				
				this.scaleReady = false;
			}
			
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
		while (this.transformReady)
			sleep(1000);
		
		b.getWorldTransform(pysTransformOut);
		pysTransformOut.origin.set(positionStore);
		
		if (!this.scaleReady) {
			if (this.scaleX != this.setScaleX || this.scaleY != this.setScaleY || this.scaleZ != this.setScaleZ) {
				this.scaleStore.x = this.setScaleX;
				this.scaleStore.y = this.setScaleY;
				this.scaleStore.z = this.setScaleZ;
				
				b.getCollisionShape().setLocalScaling(this.scaleStore);
				this.scaleReady = true;
			}
		}
		
		this.transformReady = true;
	}
	
	public Transform setPosition(float x, float y, float z) {
		if (Threading.isThreadLocal()) {
			this.pysTransformOut.origin.x = x;
			this.pysTransformOut.origin.y = y;
			this.pysTransformOut.origin.z = z;
		} else {
			this.setX = x;
			this.setY = y;
			this.setZ = z;
		}
		return this;
	}
	
	public Transform setX(float x) {
		if (Threading.isThreadLocal())
			this.pysTransformOut.origin.x = x;
		else
			this.setX = x;
		return this;
	}
	public Transform setY(float y) {
		if (Threading.isThreadLocal())
			this.pysTransformOut.origin.y = y;
		else
			this.setY = y;
		return this;
	}
	public Transform setZ(float z) {
		if (Threading.isThreadLocal())
			this.pysTransformOut.origin.z = z;
		else
			this.setZ = z;
		return this;
	}
	
	public Transform setScale(float x, float y, float z) {
		this.setScaleX = x;
		this.setScaleY = y;
		this.setScaleZ = z;
		return this;
	}
	public Transform setScaleX(float x) {
		this.setScaleX = x;
		return this;
	}
	public Transform setScaleY(float y) {
		this.setScaleY = y;
		return this;
	}
	public Transform setScaleZ(float z) {
		this.setScaleZ = z;
		return this;
	}
	
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
	public float getZ() {
		return z;
	}
	
	public float getScaleX() {
		return scaleX;
	}
	public float getScaleY() {
		return scaleY;
	}
	public float getScaleZ() {
		return scaleZ;
	}
	
	public Matrix3f getRotationMatrix() {
		return this.mainRotation;
	}
	
	private static void sleep(long ns) {
		try {
			Thread.sleep(0, (int) ns);
		} catch (InterruptedException e) {
			Logging.logger.error(e.getLocalizedMessage(), e);
		}
	}
	
}

package com.trapdoor.engine.datatypes.collision;

import org.joml.Math;
import org.joml.Vector3d;

import com.trapdoor.engine.registry.GameRegistry;
import com.trapdoor.engine.tools.Logging;

/**
 * @author brett
 * @date Dec. 15, 2021
 * 
 */
public class AxisAlignedBoundingBox implements ICollider {
	
	private boolean useJavaHash = false;
	private Vector3d centered = new Vector3d();
	private AxisAlignedBoundingBox store;
	
	private double minX,minY,minZ;
	private double maxX,maxY,maxZ;
	
	public AxisAlignedBoundingBox() {
		this.minX = 0;
		this.minY = 0;
		this.minZ = 0;
		this.maxX = 0;
		this.maxY = 0;
		this.maxZ = 0;
	}
	
	public AxisAlignedBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
		this.store = new AxisAlignedBoundingBox();
	}
	
	public AxisAlignedBoundingBox(double x, double y, double z, double size) {
		this(x, y, z, x+size, y+size, z+size);
	}
	
	/**
	 * translates based on the x,y,z provided, returning a new object
	 * @return A hard copy of this AABB
	 */
	public AxisAlignedBoundingBox translate(double x, double y, double z) {
    	return new AxisAlignedBoundingBox(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
    }
	
	public AxisAlignedBoundingBox translateInteral(double x, double y, double z) {
		try {
			this.store.minX = this.minX + x;
			this.store.minY = this.minY + y;
			this.store.minZ = this.minZ + z;
			this.store.maxX = this.maxX + x;
			this.store.maxY = this.maxY + y;
			this.store.maxZ = this.maxZ + z;
		} catch (NullPointerException e) {
			Logging.logger.error("Incorrect usage of the translate internal function!");
			Logging.logger.error(e.getMessage(), e);
			GameRegistry.printErrorMethodCallers();
		}
    	return this.store;
    }
	
	/**
	 * translates the supplied AABB object based on an X Y Z.
	 * @return the supplied aabb
	 */
	public AxisAlignedBoundingBox translate(AxisAlignedBoundingBox box, double x, double y, double z) {
		box.minX = this.minX + x;
		box.minY = this.minY + y;
		box.minZ = this.minZ + z;
		box.maxX = this.maxX + x;
		box.maxY = this.maxY + y;
		box.maxZ = this.maxZ + z;
		return box;
	}
	
	public boolean intersects(AxisAlignedBoundingBox other) {
        return this.intersects(other.minX, other.minY, other.minZ, other.maxX, other.maxY, other.maxZ);
    }

    public boolean intersects(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return this.minX < maxX && this.maxX > minX && this.minY < maxY && this.maxY > minY && this.minZ < maxZ && this.maxZ > minZ;
    }
    
    public boolean isInside(double x, double y, double z) {
    	return x > this.minX && x < this.maxX && y > this.minY && y < this.maxY && z > this.minZ && z < this.maxZ;
    }

    public boolean intersectsWithYZ(float y, float z) {
        return y >= this.minY && y <= this.maxY && z >= this.minZ && z <= this.maxZ;
    }

    public boolean intersectsWithXZ(float x, float z) {
        return x >= this.minX && x <= this.maxX && z >= this.minZ && z <= this.maxZ;
    }

    public boolean intersectsWithXY(float x, float y) {
        return x >= this.minX && x <= this.maxX && y >= this.minY && y <= this.maxY;
    }

    public String toString() {
    	StringBuilder builder = new StringBuilder();
    	builder.append("AABB [");
    	builder.append(this.minX);
    	builder.append(", ");
    	builder.append(this.minY);
    	builder.append(", ");
    	builder.append(this.minZ);
    	builder.append(" :: ");
    	builder.append(this.maxX);
    	builder.append(", ");
    	builder.append(this.maxY);
    	builder.append(", ");
    	builder.append(this.maxZ);
    	builder.append("]");
        return builder.toString();
    }

    public boolean hasNaN() {
        return Double.isNaN(this.minX) || Double.isNaN(this.minY) || Double.isNaN(this.minZ) || Double.isNaN(this.maxX) || Double.isNaN(this.maxY) || Double.isNaN(this.maxZ);
    }

    public Vector3d getCenter() {
        return new Vector3d(this.minX + (this.maxX - this.minX) * 0.5D, this.minY + (this.maxY - this.minY) * 0.5D, this.minZ + (this.maxZ - this.minZ) * 0.5D);
    }
    
    public float longestDistanceFromCenter() {
    	Vector3d center = getCenter();
    	double maxX = Math.abs(this.maxX - center.x);
    	double minX = Math.abs(this.minX - center.x);
    	double maxY = Math.abs(this.maxY - center.y);
    	double minY = Math.abs(this.minY - center.y);
    	double maxZ = Math.abs(this.maxZ - center.z);
    	double minZ = Math.abs(this.minZ - center.z);
    	return (float) Math.max(maxX, Math.max(minX, Math.max(maxY, Math.max(minY, Math.max(maxZ, minZ)))));
    }
    
    public float avgDistanceFromCenter() {
    	Vector3d center = getCenter();
    	double maxX = Math.abs(this.maxX - center.x);
    	double minX = Math.abs(this.minX - center.x);
    	double maxY = Math.abs(this.maxY - center.y);
    	double minY = Math.abs(this.minY - center.y);
    	double maxZ = Math.abs(this.maxZ - center.z);
    	double minZ = Math.abs(this.minZ - center.z);
    	maxX *= maxX;
    	minX *= minX;
    	maxY *= maxY;
    	minY *= minY;
    	maxZ *= maxZ;
    	minZ *= minZ;
    	return (float) Math.sqrt(maxX + minX + maxY + minY + maxZ + minZ);
    }
    
    public Vector3d getCenterSafe() {
    	centered.x = this.minX + (this.maxX - this.minX) * 0.5D;
    	centered.y = this.minY + (this.maxY - this.minY) * 0.5D;
    	centered.z = this.minZ + (this.maxZ - this.minZ) * 0.5D;
    	return centered;
    }
    
    public double getXHalfExtends() {
    	double min = Math.abs(Math.abs(this.minX) - centered.x);
    	double max = Math.abs(Math.abs(this.maxX) - centered.x);
    	return min > max ? min : max;
    }
    public double getYHalfExtends() {
    	double min = Math.abs(Math.abs(this.minY) - centered.y);
    	double max = Math.abs(Math.abs(this.maxY) - centered.y);
    	return min > max ? min : max;
    }
    public double getZHalfExtends() {
    	double min = Math.abs(Math.abs(this.minZ) - centered.z);
    	double max = Math.abs(Math.abs(this.maxZ) - centered.z);
    	return min > max ? min : max;
    }
    
    public void scale(float x, float y, float z) {
    	this.maxX *= x;
    	this.minX *= x;
    	this.maxY *= y;
    	this.minY *= y;
    	this.maxZ *= z;
    	this.minZ *= z;
    }
	
	@Override
	public int hashCode() {
		if (useJavaHash)
			return super.hashCode();
        long i = Double.doubleToLongBits(this.minX);
        int j = (int)(i ^ i >>> 31);
        j = 31 * j + (int)(i ^ i >>> 31);
        i = Double.doubleToLongBits(this.maxY);
        j = 31 * j + (int)(i ^ i >>> 31);
        i = Double.doubleToLongBits(this.maxZ);
        j = 31 * j + (int)(i ^ i >>> 31);
        i = Double.doubleToLongBits(this.minY);
        j = 31 * j + (int)(i ^ i >>> 31);
        i = Double.doubleToLongBits(this.minZ);
        j = 31 * j + (int)(i ^ i >>> 31);
        i = Double.doubleToLongBits(this.maxX);
        return j;
    }
	
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof AxisAlignedBoundingBox))
			return false;
		AxisAlignedBoundingBox other = (AxisAlignedBoundingBox) o;
		return other.maxX == this.maxX && other.maxY == this.maxY && other.maxZ == this.maxZ 
				&& other.minX == this.minX && other.minY == this.maxY && other.minZ == this.minZ;
	}

	public double getMinX() {
		return minX;
	}

	public double getMinY() {
		return minY;
	}

	public double getMinZ() {
		return minZ;
	}

	public double getMaxX() {
		return maxX;
	}

	public double getMaxY() {
		return maxY;
	}

	public double getMaxZ() {
		return maxZ;
	}

	public boolean isUseJavaHash() {
		return useJavaHash;
	}

	public void setUseJavaHash(boolean useJavaHash) {
		this.useJavaHash = useJavaHash;
	}

	@Override
	public boolean intersects(ICollider c) {
		if (!(c instanceof AxisAlignedBoundingBox))
			return false;
		AxisAlignedBoundingBox o = (AxisAlignedBoundingBox) c;
		return o.intersects(this);
	}
	
}

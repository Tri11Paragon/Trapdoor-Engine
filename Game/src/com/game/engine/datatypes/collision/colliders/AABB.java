package com.game.engine.datatypes.collision.colliders;

import org.joml.Vector3d;

/**
 * @author brett
 * @date Dec. 15, 2021
 * 
 */
public class AABB {
	
	private boolean useJavaHash = false;
	private Vector3d centered = new Vector3d();
	
	private final double minXF,minYF,minZF;
	private final double maxXF,maxYF,maxZF;
	private double minX,minY,minZ;
	private double maxX,maxY,maxZ;
	
	public AABB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
		
		this.minXF = minX;
		this.minYF = minY;
		this.minZF = minZ;
		this.maxXF = maxX;
		this.maxYF = maxY;
		this.maxZF = maxZ;
	}
	
	public AABB(double x, double y, double z, double size) {
		this(x, y, z, x+size, y+size, z+size);
	}
	
	public AABB(double x, double y, double z) {
		this(x, y, z, 1);
	}
	
	/**
	 * translates based on the x,y,z provided, returning a new object
	 * @return A hard copy of this AABB
	 */
	public AABB translate(double x, double y, double z) {
    	return new AABB(this.minXF + x, this.minYF + y, this.minZF + z, this.maxXF + x, this.maxYF + y, this.maxZF + z);
    }
	
	/**
	 * translates this AABB object based on an X Y Z.
	 * @return this AABB
	 */
	public AABB translateThis(double x, double y, double z) {
		this.minX = minXF + x;
		this.minY = minYF + y;
		this.minZ = minZF + z;
		this.maxX = maxXF + x;
		this.maxY = maxYF + y;
		this.maxZ = maxZF + z;
		return this;
	}
	
	public boolean intersects(AABB other) {
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

    public boolean hasNaN()
    {
        return Double.isNaN(this.minX) || Double.isNaN(this.minY) || Double.isNaN(this.minZ) || Double.isNaN(this.maxX) || Double.isNaN(this.maxY) || Double.isNaN(this.maxZ);
    }

    public Vector3d getCenter()
    {
        return new Vector3d(this.minX + (this.maxX - this.minX) * 0.5D, this.minY + (this.maxY - this.minY) * 0.5D, this.minZ + (this.maxZ - this.minZ) * 0.5D);
    }
    
    public Vector3d getCenterSafe() {
    	centered.x = this.minX + (this.maxX - this.minX) * 0.5D;
    	centered.y = this.minY + (this.maxY - this.minY) * 0.5D;
    	centered.z = this.minZ + (this.maxZ - this.minZ) * 0.5D;
    	return centered;
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
		if (!(o instanceof AABB))
			return false;
		AABB other = (AABB) o;
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
	
}

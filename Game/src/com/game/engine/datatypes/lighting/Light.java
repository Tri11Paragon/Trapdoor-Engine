package com.game.engine.datatypes.lighting;

/**
 * @author brett
 * @date Jan. 13, 2022
 * 
 */
public class Light {
	
	/**
	 * [0] 	--> 7		&emsp;&emsp;&ensp;
	 * [1] 	--> 13		<br>
	 * [2] 	--> 20		&emsp;&emsp;
	 * [3] 	--> 32		<br>
	 * [4] 	--> 50		&emsp;&emsp;
	 * [5] 	--> 65		<br>
	 * [6] 	--> 100		&emsp;&ensp;
	 * [7] 	--> 160		<br>
	 * [8] 	--> 200		&emsp;&ensp;
	 * [9] 	--> 325		<br>
	 * [10] --> 600		&emsp;
	 * [11] --> 3250	<br>
	 */
	public static final float[][] lightings = {
			{0.7f, 1.8f},		// 7
			{0.35f, 0.44f},		// 13
			{0.22f, 0.20f},		// 20
			{0.14f, 0.07f},		// 32
			{0.09f, 0.032f},	// 50
			{0.07f, 0.017f},	// 65
			{0.045f, 0.0075f},	// 100
			{0.027f, 0.0028f},	// 160
			{0.022f, 0.0019f},	// 200
			{0.014f, 0.0007f},	// 325
			{0.007f, 0.0002f},	// 600
			{0.0014f, 0.000007f}// 3250
	};
	
	private float linear = 0.7f, quadratic = 1.2f;
	private float r = 1.0f,g = 1.0f,b = 1.0f;
	private float x,y,z;
	
	public Light(float[] lighting, float r, float g, float b, float x, float y, float z) {
		this.linear = lighting[0];
		this.quadratic = lighting[1];
		this.r = r;
		this.g = g;
		this.b = b;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Light(float linear, float quadratic, float r, float g, float b, float x, float y, float z) {
		this.linear = linear;
		this.quadratic = quadratic;
		this.r = r;
		this.g = g;
		this.b = b;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Light(float linear, float quadratic, float x, float y, float z) {
		this.linear = linear;
		this.quadratic = quadratic;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Light(float[] lighting, float x, float y, float z) {
		this.linear = lighting[0];
		this.quadratic = lighting[1];
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Light(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Light setLighting(float[] lighting) {
		this.linear = lighting[0];
		this.quadratic = lighting[1];
		return this;
	}
	
	/**
	 * sets RGB based on rgb255 and not rgb float 0.0 -> 1.0f
	 */
	public Light setRGB(int r, int g, int b) {
		this.r = r / 255f;
		this.g = g / 255f;
		this.b = b / 255f;
		return this;
	}

	public float getLinear() {
		return linear;
	}

	public Light setLinear(float linear) {
		this.linear = linear;
		return this;
	}

	public float getQuadratic() {
		return quadratic;
	}

	public Light setQuadratic(float quadratic) {
		this.quadratic = quadratic;
		return this;
	}

	public float getR() {
		return r;
	}

	public Light setR(float r) {
		this.r = r;
		return this;
	}

	public float getG() {
		return g;
	}

	public Light setG(float g) {
		this.g = g;
		return this;
	}

	public float getB() {
		return b;
	}

	public Light setB(float b) {
		this.b = b;
		return this;
	}

	public float getX() {
		return x;
	}

	public Light setX(float x) {
		this.x = x;
		return this;
	}

	public float getY() {
		return y;
	}

	public Light setY(float y) {
		this.y = y;
		return this;
	}

	public float getZ() {
		return z;
	}

	public Light setZ(float z) {
		this.z = z;
		return this;
	}
	
}

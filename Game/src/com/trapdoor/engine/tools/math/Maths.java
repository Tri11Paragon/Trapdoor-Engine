package com.trapdoor.engine.tools.math;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.vecmath.Matrix3f;

import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;

import com.trapdoor.engine.camera.ICamera;
import com.trapdoor.engine.display.DisplayManager;

/**
 * @author Brett
 * @date Jun. 20, 2020
 */

public class Maths {

	// callback to my first game using this math class
	// it was like my grade 11 game but I was working on it in grade 10
	// it was much worse
	public static enum colors {
		NULL, COLOR_RED, COLOR_BLUE, COLOR_GREEN
	};

	public final static double Epsilon = Math.pow(10, -5);
	
	// axis to do rotation over
	public static final Vector3f rx = new Vector3f(1, 0, 0);
	public static final Vector3f ry = new Vector3f(0, 1, 0);
	public static final Vector3f rz = new Vector3f(0, 0, 1);

	static Matrix4f mtx = new Matrix4f();

	// im not even sure how many of these I use.

	/**
	 * creates a translation matrix
	 */
	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
		mtx.identity();
		// these functions explain themselves and are the same for pretty much all the
		// transformation matrix generators
		// I have many of them because I had many ways of doing things.
		mtx.translate(translation);
		mtx.rotate((float) Math.toRadians(rx), Maths.rx);
		mtx.rotate((float) Math.toRadians(ry), Maths.ry);
		mtx.rotate((float) Math.toRadians(rz), Maths.rz);
		mtx.scale(new Vector3f(scale, scale, scale));
		return mtx;
	}

	/**
	 * creates a translation matrix
	 */
	public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, Vector3f scale) {
		mtx.identity();
		mtx.translate(translation);
		mtx.rotate((float) Math.toRadians(rotation.x), Maths.rx);
		mtx.rotate((float) Math.toRadians(rotation.y), Maths.ry);
		mtx.rotate((float) Math.toRadians(rotation.z), Maths.rz);
		mtx.scale(scale);
		return mtx;
	}

	static Matrix4f mrx = new Matrix4f();

	static Matrix4f matrix = new Matrix4f();
	static float fr = 0.5f;

	/**
	 * Creates a transformation matrix for a cube based on its X, Y, Z pos in the
	 * world.
	 */
	public static Matrix4f createTransformationMatrixCube(int x, int y, int z) {
		matrix.identity();
		// the 0.5 is added to adjust for cube scale.
		// took way to long to figure out this.
		// I do the raw matrix math here because I don't want to lose performance due to
		// abstraction
		matrix.translate(x+fr, y+fr, z+fr);
		//matrix.m30(matrix.m30() + matrix.m00() * (x + fr) + matrix.m10() * (y + fr) + matrix.m20() * (z + fr));
		//matrix.m31(matrix.m31() + matrix.m01() * (x + fr) + matrix.m11() * (y + fr) + matrix.m21() * (z + fr));
		//matrix.m32(matrix.m32() + matrix.m02() * (x + fr) + matrix.m12() * (y + fr) + matrix.m22() * (z + fr));
		//matrix.m33(matrix.m33() + matrix.m03() * (x + fr) + matrix.m13() * (y + fr) + matrix.m23() * (z + fr));

		return matrix;
	}
	
	public static float invSqrt(float x) {
	    float xhalf = 0.5f * x;
	    int i = Float.floatToIntBits(x);
	    i = 0x5f3759df - (i >> 1);
	    x = Float.intBitsToFloat(i);
	    x *= (1.5f - xhalf * x * x);
	    return x;
	}
	
	public static double Clamp(double value, double min, double max) {
        // Clamps a value into a given range
        return Math.max(min, Math.min(max, value));
    }
	
	public static double SmoothDamp(double current, double target, Vector2d refCurrent, double smoothTime, double maxSpeed, double deltaTime) {
        smoothTime = Math.max(Epsilon, smoothTime);

        double num1 = 2f / smoothTime;
        double num2 = num1 * deltaTime;
        double num3 = 1 / (1 + num2 + 0.48 * num2 * num2 + 0.235 * num2 * num2 * num2);
        double num4 = current - target;
        double num5 = target;
        double num6 = maxSpeed * smoothTime;
        num4 = Clamp(num4, -num6, num6);
        target = current - num4;
        double num7 = (refCurrent.x + num1 * num4) * deltaTime;
        refCurrent.x = (refCurrent.x - num1 * num7) * num3;
        double num8 = target + (num4 + num7) * num3;

        if (num5 - current > 0 == num8 > num5) {
            num8 = num5;
            refCurrent.x = (num8 - num5) / deltaTime;
        }
        return num8;

    }

	/**
	 * creates a translation matrix
	 */
	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		matrix.identity();
		matrix.translate(translation.x, translation.y, 0);
		matrix.scale(new Vector3f(scale.x, scale.y, 1f));
		return matrix;
	}
	
	public static Matrix4f createTransformationMatrix(float x, float y) {
		matrix.identity();
		matrix.translate(x, y, 0);
		//matrix.scale(sx, sy, 1f);
		return matrix;
	}
	
	public static Matrix4f createTransformationMatrix(float x, float y, float sx, float sy) {
		matrix.identity();
		matrix.translate(x, y, 0);
		matrix.scale(sx, sy, 1f);
		return matrix;
	}
	
	public static Matrix4f createTransformationMatrix(float x, float y, float z, float sx, float sy) {
		matrix.identity();
		matrix.translate(x, y, z);
		matrix.scale(sx, sy, 1f);
		return matrix;
	}
	
	public static Matrix4f createTransformationMatrix(float x, float y, float z, float rot, float sx, float sy) {
		matrix.identity();
		float sx2 = sx/2;
		float sy2 = sy/2;
		// transform
		matrix.m30(x + sx2);
		matrix.m31(y + sy2);
		matrix.m32(z);
		// scale
		matrix.m00(matrix.m00() * sx2);
		matrix.m11(matrix.m11() * sy2);
		// rotate
		// this is a costly function to run. so only run it when we actually need to
		if (rot != 0) {
			matrix.rotateZ((float) (rot / 180.0 * 3.14159265359));
		}
		return matrix;
	}
	
	public static Matrix4f createTransformationMatrix(float x, float y, float z, float sx, float sy, float rotx, float roty) {
		matrix.identity();
		matrix.translate(x, y, z);
		matrix.scale(sx, sy, 1f);
		matrix.rotate(rotx, rx);
		matrix.rotate(roty, ry);
		return matrix;
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param sx
	 * @param sy
	 * @param rotx flips top to bottom
	 * @param roty flips left to right
	 * @param rotz rotates the image
	 * @return
	 */
	public static Matrix4f createTransformationMatrix(float x, float y, float z, float sx, float sy, float rotx, float roty, float rotz) {
		matrix.identity();
		matrix.translate(x, y, z);
		matrix.scale(sx, sy, 1f);
		matrix.rotate(rotx, rx);
		matrix.rotate(roty, ry);
		matrix.rotate(rotz, rz);
		return matrix;
	}
	
	public static Matrix4f createTransformationMatrix(float x, float y, float z, float yaw, float pitch, float roll, float sx, float sy, float sz) {
		matrix.identity();
		matrix.translate(x, y, z);
		if (sx != 1 || sy != 1 || sz != 1)
			matrix.scale(sx, sy, sz);
		if (yaw != 0)
			matrix.rotate(yaw, ry);
		if (pitch != 0)
			matrix.rotate(pitch, rx);
		if (roll != 0)
			matrix.rotate(roll, rz);
		return matrix;
	}
	
	public static Matrix4f createTransformationMatrix(float x, float y, float z, Matrix3f rotmatrix, float sx, float sy, float sz) {
		matrix.identity();
		matrix.translate(x, y, z);
		if (sx != 1 || sy != 1 || sz != 1)
			matrix.scale(sx, sy, sz);
		matrix.mul(create4x4FromPhys(rotmatrix));
		return matrix;
	}
	
	private static Matrix4f data = new Matrix4f();
	private static Matrix4f create4x4FromPhys(Matrix3f rot) {
		data.identity();
		data.m00(rot.m00);
		data.m01(rot.m01);
		data.m02(rot.m02);
		data.m10(rot.m10);
		data.m11(rot.m11);
		data.m12(rot.m12);
		data.m20(rot.m20);
		data.m21(rot.m21);
		data.m22(rot.m22);
		return data;
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f translation) {
		matrix.identity();
		matrix.translate(translation.x, translation.y, translation.z);
		return matrix;
	}
	
	public static Matrix4f createTransformationMatrix(Vector3f translation, Vector2f scale) {
		matrix.identity();
		matrix.translate(translation.x, translation.y, translation.z);
		matrix.scale(scale.x, scale.y, 1);
		return matrix;
	}
	
	
	static Vector3f trans = new Vector3f();
	
	public static Matrix4f createTransformationMatrixYAW(float[] translationandrotation) {
		mtx.identity();
		trans.x = translationandrotation[0];
		trans.y = translationandrotation[1];
		trans.z = translationandrotation[2];
		mtx.translate(trans);
		//Matrix4f.rotate((float) Math.toRadians(translationandrotation[3]), Maths.rx, mtx, mtx);
		mtx.rotate((float) Math.toRadians(-translationandrotation[4]), Maths.ry);
		//Matrix4f.rotate((float) Math.toRadians(translationandrotation[5]), Maths.rz, mtx, mtx);
		return mtx;
	}
	
	/**
	 * creates a translation matrix
	 */
	/*public static Matrix4f createTransformationMatrix(float SWIDTH, float SHEIGHT, float x, float y, float width,
			float height) {
		matrix.identity();
		matrix.translate(x / SWIDTH, y / SHEIGHT, 0);
		matrix.scale(width / SWIDTH, height / SHEIGHT, 1f);
		return matrix;
	}*/

	/**
	 * creates a translation matrix
	 */
	public static Matrix4f createTransformationMatrixCenteredSTATIC(float SWIDTH, float SHEIGHT, float width,
			float height, float rot) {
		matrix.identity();
		matrix.translate(SWIDTH / 2 - width / 2, SHEIGHT / 2 - height / 2, 0);
		matrix.rotate(rot, rx);
		matrix.scale(width / SWIDTH, height / SHEIGHT, 1f);
		return matrix;
	}

	/**
	 * computes a dot product of two arrays. arrays need to be the same length.
	 */
	public static double dotProduct(double[] a, double[] b) {
		double sum = 0;
		for (int i = 0; i < a.length; i++) {
			sum += a[i] * b[i];
		}
		return sum;
	}

	// matrix maths is not fun
	// this took 3 hours to make work. turns out i needed to disable face culling
	// :(
	public static Matrix4f ortho() {
		Matrix4f m = new Matrix4f();
		
		m.identity();
		
		float right = DisplayManager.WIDTH;
		float left = 0;
		float top = 0;
		float bottom = DisplayManager.HEIGHT;
		float zFar = 50;
		float zNear = -50;
		
		m.m00(2/(right - left));
		m.m11(2/(top-bottom));
		m.m22(-2/(zFar-zNear));
		m.m30(-(right+left)/(right-left));
		m.m31(-(top+bottom)/(top-bottom));
		m.m32(-(zFar+zNear)/(zFar - zNear));
		
		return m;
	}

	/**
	 * creates a translation matrix
	 */
	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f rotation, Vector2f scale) {
		matrix.identity();
		matrix.translate(translation.x, translation.y, 0);
		matrix.scale(scale.x, scale.y, 1f);
		matrix.rotate((float) Math.toRadians(rotation.x), rx);
		matrix.rotate((float) Math.toRadians(rotation.y), ry);
		return matrix;
	}

	/**
	 * "In geometry, the barycentric coordinate system is a coordinate system in
	 * which the location of a point of a simplex is specified as the center of
	 * mass, or barycenter, of usually unequal masses placed at its vertices.
	 * Coordinates also extend outside the simplex, where one or more coordinates
	 * become negative."
	 */
	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		// I don't use this btw anymore
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}

	static Matrix4f viewMatrix = new Matrix4f();
	static Vector3f pos = new Vector3f();

	/**
	 * creates the view matrix based on the camera.
	 */
	public static Matrix4f createViewMatrix(ICamera camera) {
		// reset the matrix from the last frame
		// saves on making new objects.
		viewMatrix.identity();
		// rotates to the camera
		viewMatrix.rotate((float) Math.toRadians(camera.getPitch()), rx);
		viewMatrix.rotate((float) Math.toRadians(camera.getYaw()), ry);
		Vector3d cameraPos = camera.getPosition();
		// make negative.
		cameraPos.negate();
		// to make the game look like we are moving, what games to is actually move the
		// world negative
		// to where the camera is. Pretty neat eh?
		viewMatrix.translate((float)cameraPos.x, (float)cameraPos.y, (float)cameraPos.z);
		// reset the camera pos back to normal
		cameraPos.negate();
		return viewMatrix;
	}
	
	public static Matrix4f createViewMatrix(ICamera camera, Matrix4f matrix) {
		// reset the matrix from the last frame
		// saves on making new objects.
		matrix.identity();
		// rotates to the camera
		matrix.rotate((float) Math.toRadians(camera.getPitch()), rx);
		matrix.rotate((float) Math.toRadians(camera.getYaw()), ry);
		Vector3d cameraPos = camera.getPosition();
		// make negative.
		cameraPos.negate();
		// to make the game look like we are moving, what games to is actually move the
		// world negative
		// to where the camera is. Pretty neat eh?
		matrix.translate(cameraPos.get(pos));
		// reset the camera pos back to normal
		cameraPos.negate();
		return matrix;
	}

	static Matrix4f viewMatrixC = new Matrix4f();
	private static double x = 0, z = 0, y = 0;

	/**
	 * used to render chunks relative to the camera pos and not the world pos.
	 */
	public static Matrix4f createViewMatrixROT(ICamera camera) {
		viewMatrixC.identity();
		viewMatrixC.rotate((float) Math.toRadians(camera.getPitch()), rx);
		viewMatrixC.rotate((float) Math.toRadians(camera.getYaw()), ry);
		Vector3d cameraPos = camera.getPosition();
		// make sure we are not actually changing the camera pos
		// (due to objects being passed by reference)
		x = cameraPos.x;
		z = cameraPos.z;
		y = cameraPos.y;
		// make sure we can't actually move more then a chunk
		// this is due to the way im drawing chunk, since im rendering based on the
		// camera chunk pos
		// this just allows you to move in individual blocks
		// we modify the actual camera pos because it has a y
		cameraPos.x %= (double) 16;
		cameraPos.y %= (double) 16;
		cameraPos.z %= (double) 16;
		cameraPos.negate();
		viewMatrixC.translate(cameraPos.get(pos));
		cameraPos.negate();
		// resets back to the saved pos that we saved ^
		cameraPos.x = x;
		cameraPos.z = z;
		cameraPos.y = y;
		return viewMatrixC;
	}

	public static Matrix4f createViewMatrixOTHER(ICamera camera) {
		return createViewMatrix(camera);
	}

	/**
	 * does linear interpolation
	 */
	public static float lerp(float point1, float point2, float alpha) {
		return point1 + alpha * (point2 - point1);
	}

	/**
	 * does linear interpolation
	 */
	public static Vector3f lerp(Vector3f point1, Vector3f point2, float alpha) {
		float x = point1.x + alpha * (point2.x - point1.x);
		float y = point1.y + alpha * (point2.y - point1.y);
		float z = point1.z + alpha * (point2.z - point1.z);
		return new Vector3f(x, y, z);
	}

	/**
	 * does linear interpolation without creating a vector.
	 */
	public static void lerpN(Vector3f point1, Vector3f point2, float alpha) {
		point1.x = point1.x + (alpha * (point2.x - point1.x));
		point1.y = point1.y + (alpha * (point2.y - point1.y));
		point1.z = point1.z + (alpha * (point2.z - point1.z));
	}

	/**
	 * does linear interpolation using length 3 float[]
	 */
	public static void lerpVA3(float[] point1, float[] point2, float alpha) {
		point1[0] = point1[0] + (alpha * (point2[0] - point1[0]));
		point1[1] = point1[1] + (alpha * (point2[1] - point1[1]));
		point1[2] = point1[2] + (alpha * (point2[2] - point1[2]));
	}

	/**
	 * does linear interpolation using length 6 float[]
	 */
	public static void lerpVA6(float[] point1, float[] point2, float alpha) {
		point1[0] = point1[0] + (alpha * (point2[0] - point1[0]));
		point1[1] = point1[1] + (alpha * (point2[1] - point1[1]));
		point1[2] = point1[2] + (alpha * (point2[2] - point1[2]));

		point1[3] = point1[3] + (alpha * (point2[3] - point1[3]));
		point1[4] = point1[4] + (alpha * (point2[4] - point1[4]));
		point1[5] = point1[5] + (alpha * (point2[5] - point1[5]));
	}

	/**
	 * does linear interpolation changes the first vector.
	 */
	public static Vector3f lerpA(Vector3f point1, Vector3f point2, float alpha) {
		float x = point1.x + alpha * (point2.x - point1.x);
		float y = point1.y + alpha * (point2.y - point1.y);
		float z = point1.z + alpha * (point2.z - point1.z);
		point1.x += x;
		point1.y += y;
		point1.z += z;

		return new Vector3f(x, y, z);
	}

	/**
	 * calculates distance.
	 */
	public static Vector3f distance(Vector3f point1, Vector3f point2) {
		return new Vector3f(point1.x - point2.x, point1.y - point2.y, point1.z - point2.z);
	}

	/**
	 * calculates distance. Absolute distance.
	 */
	public static Vector3f distanceABS(Vector3f point1, Vector3f point2) {
		return new Vector3f(Math.abs(point1.x - point2.x), Math.abs(point1.y - point2.y),
				Math.abs(point1.z - point2.z));
	}

	/**
	 * converts RGB255(8bit) into normalized RBG (OpenGL) (0.0-1.0)
	 */
	public static Vector3f decodeRGB255(int r, int g, int b) {
		return new Vector3f(r / 255, g / 255, b / 255);
	}

	/**
	 * convets a single channel of RBG into normalized RGB
	 */
	public static float decodeRGB255(int b) {
		return b / 255;
	}

	/**
	 * rounds to 5 decimal places.
	 */
	public static double roundDown5(double d) {
		return (long) (d * 1e5) / 1e5;
	}

	/**
	 * rounds to 4 decimal places.
	 */
	public static double roundDown4(double d) {
		return (long) (d * 1e4) / 1e4;
	}

	/**
	 * rounds to 3 decimal places.
	 */
	public static double roundDown3(double d) {
		return (long) (d * 1e3) / 1e3;
	}

	/**
	 * rounds to 2 decimal places.
	 */
	public static double roundDown2(double d) {
		return (long) (d * 1e2) / 1e2;
	}

	/**
	 * uses the local thread to generate a random int
	 */
	public static int randomInt(int min, int max) {
		int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
		return randomNum;
	}

	// from old game. ignore this
	public static colors getEntityColor(Vector3f color) {
		if (color.x > color.y && color.x > color.z) {
			return colors.COLOR_RED;
		} else if (color.y > color.x && color.y > color.z) {
			return colors.COLOR_GREEN;
		} else if (color.z > color.y && color.z > color.x) {
			return colors.COLOR_BLUE;
		}
		System.err.println("We are at the end of the line \n Please fix this method!");
		return colors.NULL;
	}

	/**
	 * calculates distance
	 */
	public static Vector3f distance2(Vector3f first, Vector3f last) {
		return new Vector3f(first.x - last.x, first.y - last.y, first.z - last.z);
	}

	/**
	 * creates a random float between min(inclusive) and max (exclusive I think)
	 */
	public static float randomFloat(float min, float max) {
		return (float) (min + Math.random() * (max - min));
	}

	/**
	 * creates a random float between min(inclusive) and max (exclusive I think),
	 * using your random
	 */
	public static float randomFloat(float min, float max, Random rand) {
		return (float) (min + rand.nextDouble() * (max - min));
	}

	/**
	 * adds two vectors returning a new vector.
	 */
	public static Vector3f addVectors(Vector3f vec1, Vector3f vec2) {
		return new Vector3f(vec1.x + vec2.x, vec1.y + vec2.y, vec1.z + vec2.z);
	}

	// do not run at runtime. only at startup.
	public static long convertToBytes(String s) {
		long t = 0;
		char[] chars = s.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			t += (byte) chars[i];
		}

		return t;
	}

	/**
	 * converts a string into its raw bytes
	 */
	public static String convertToBytesString(String s) {
		String t = "";
		char[] chars = s.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			t += (int) chars[i];
		}

		return t;
	}

	/**
	 * prevents negative numbers. Sets all negatives to 0.
	 */
	public static long preventNegs(Long num) {
		if (num < 0)
			num = 0l;
		return num;
	}

}

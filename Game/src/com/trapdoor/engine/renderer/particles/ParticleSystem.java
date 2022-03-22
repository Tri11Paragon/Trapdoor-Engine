package com.trapdoor.engine.renderer.particles;

import java.util.Random;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.registry.GameRegistry;
import com.trapdoor.engine.tools.math.Maths;
import com.trapdoor.engine.world.World;

/**
 * @author brett
 * @date Mar. 6, 2022
 * 
 */
public class ParticleSystem {
	
	protected float x,y,z;
	
	protected float pps, averageSpeed, gravityComplient, averageLifeLength, averageScale;
	protected float ppsOld, averageSpeedOld, gravityComplientOld, averageLifeLengthOld, averageScaleOld;

	protected float speedError, lifeError, scaleError = 0;
	protected boolean randomRotation = false;
	protected Vector3f direction;
	protected float directionDeviation = 0;

	protected Random random = new Random();
	
	protected ParticleRenderer renderer;
	protected World world;

	public ParticleSystem(float pps, float speed, float gravityComplient, float lifeLength, float scale) {
		this.pps = this.ppsOld = pps;
		this.averageSpeed = this.averageSpeedOld = speed;
		this.gravityComplient = this.gravityComplientOld = gravityComplient;
		this.averageLifeLength = this.averageLifeLengthOld = lifeLength;
		this.averageScale = this.averageScaleOld = scale;
	}
	
	public void update() {
		
	}

	public void generateParticles(Vector3f systemCenter) {
		float delta = (float) DisplayManager.getFrameTimeSeconds();
		float particlesToCreate = pps * delta;
		int count = (int) Math.floor(particlesToCreate);
		float partialParticle = particlesToCreate % 1;
		for (int i = 0; i < count; i++) {
			emitParticle(systemCenter);
		}
		if (random.nextDouble() < partialParticle) {
			emitParticle(systemCenter);
		}
	}

	private void emitParticle(Vector3f center) {
		Vector3f velocity = null;
		if(direction!=null){
			velocity = generateRandomUnitVectorWithinCone(direction, directionDeviation);
		}else{
			velocity = generateRandomUnitVector();
		}
		velocity.normalize();
		velocity.mul(generateValue(averageSpeed, speedError));
		float scale = generateValue(averageScale, scaleError);
		float lifeLength = generateValue(averageLifeLength, lifeError);
		renderer.getStorage().add(
					createParticle(new Vector3f(center.x + x, center.y + y, center.z + z), velocity, gravityComplient, lifeLength, generateRotation(), scale)
				);
	}
	
	protected Particle createParticle(Vector3f center, Vector3f velocity, float gravity, float life, float rotation, float scale) {
		return new Particle(center, velocity, gravity, life, rotation, scale)
			.setCurrentTexture(GameRegistry.getParticleTexture("resources/textures/character Texture.png"))
			.setNextTexture(GameRegistry.getParticleTexture("resources/textures/particles/smoke/smoke_33.png"));
	}

	private float generateValue(float average, float errorMargin) {
		float offset = (random.nextFloat() - 0.5f) * 2f * errorMargin;
		return average + offset;
	}

	private float generateRotation() {
		if (randomRotation) {
			return random.nextFloat() * 360f;
		} else {
			return 0;
		}
	}
	
	public void create(ParticleRenderer renderer, World world) {
		this.renderer = renderer;
	}

	private static Vector3f generateRandomUnitVectorWithinCone(Vector3f coneDirection, float angle) {
		float cosAngle = (float) Math.cos(angle);
		Random random = new Random();
		float theta = (float) (random.nextFloat() * 2f * Math.PI);
		float z = cosAngle + (random.nextFloat() * (1 - cosAngle));
		float rootOneMinusZSquared = (float) Math.sqrt(1 - z * z);
		float x = (float) (rootOneMinusZSquared * Math.cos(theta));
		float y = (float) (rootOneMinusZSquared * Math.sin(theta));

		Vector4f direction = new Vector4f(x, y, z, 1);
		if (coneDirection.x != 0 || coneDirection.y != 0 || (coneDirection.z != 1 && coneDirection.z != -1)) {
			Vector3f rotateAxis = new Vector3f(coneDirection).cross(Maths.rz);
			rotateAxis.normalize();
			float rotateAngle = (float) Math.acos(new Vector3f(coneDirection).dot(Maths.rz));
			Matrix4f rotationMatrix = new Matrix4f();
			rotationMatrix.rotate(-rotateAngle, rotateAxis);
			rotationMatrix.transform(direction);
		} else if (coneDirection.z == -1) {
			direction.z *= -1;
		}
		return new Vector3f(direction.x, direction.y, direction.z);
	}
	
	private Vector3f generateRandomUnitVector() {
		float theta = (float) (random.nextFloat() * 2f * Math.PI);
		float z = (random.nextFloat() * 2) - 1;
		float rootOneMinusZSquared = (float) Math.sqrt(1 - z * z);
		float x = (float) (rootOneMinusZSquared * Math.cos(theta));
		float y = (float) (rootOneMinusZSquared * Math.sin(theta));
		return new Vector3f(x, y, z);
	}
	
	public void saveState(float pps, float speed, float gravityComplient, float lifeLength, float scale) {
		this.ppsOld = this.pps;
		this.averageSpeedOld = this.averageSpeed;
		this.gravityComplientOld = this.gravityComplient;
		this.averageLifeLengthOld = this.averageLifeLength;
		this.averageScaleOld = this.averageScale;
		
		this.pps = pps;
		this.averageSpeed = speed;
		this.gravityComplient = gravityComplient;
		this.averageLifeLength = lifeLength;
		this.averageScale = scale;
	}
	
	public void restoreState() {
		this.pps = this.ppsOld;
		this.averageSpeed = this.averageSpeedOld;
		this.gravityComplient = this.gravityComplientOld;
		this.averageLifeLength = this.averageLifeLengthOld;
		this.averageScale = this.averageScaleOld;
	}
	
	/**
	 * @param direction - The average direction in which particles are emitted.
	 * @param deviation - A value between 0 and 1 indicating how far from the chosen direction particles can deviate.
	 */
	public ParticleSystem setDirection(Vector3f direction, float deviation) {
		try {
			this.direction = new Vector3f(direction);
		} catch (Exception e) {this.direction = null;}
		this.directionDeviation = (float) (deviation * Math.PI);
		return this;
	}

	public ParticleSystem randomizeRotation() {
		randomRotation = true;
		return this;
	}

	/**
	 * @param error
	 *            - A number between 0 and 1, where 0 means no error margin.
	 */
	public ParticleSystem setSpeedError(float error) {
		this.speedError = error * averageSpeed;
		return this;
	}

	/**
	 * @param error
	 *            - A number between 0 and 1, where 0 means no error margin.
	 */
	public ParticleSystem setLifeError(float error) {
		this.lifeError = error * averageLifeLength;
		return this;
	}

	/**
	 * @param error
	 *            - A number between 0 and 1, where 0 means no error margin.
	 */
	public ParticleSystem setScaleError(float error) {
		this.scaleError = error * averageScale;
		return this;
	}
	
	public ParticleSystem setPosition(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	public ParticleSystem setPosition(Vector3f pos) {
		this.x = pos.x;
		this.y = pos.y;
		this.z = pos.z;
		return this;
	}

	public float getX() {
		return x;
	}

	public ParticleSystem setX(float x) {
		this.x = x;
		return this;
	}

	public float getY() {
		return y;
	}

	public ParticleSystem setY(float y) {
		this.y = y;
		return this;
	}

	public float getZ() {
		return z;
	}

	public ParticleSystem setZ(float z) {
		this.z = z;
		return this;
	}

	public float getPps() {
		return pps;
	}

	public float getAverageSpeed() {
		return averageSpeed;
	}

	public float getAverageLifeLength() {
		return averageLifeLength;
	}

	public float getAverageScale() {
		return averageScale;
	}

	public float getGravityComplient() {
		return gravityComplient;
	}
	
}

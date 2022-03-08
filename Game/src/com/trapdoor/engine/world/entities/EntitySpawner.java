package com.trapdoor.engine.world.entities;

import java.util.Random;

import org.joml.Vector3d;
import org.joml.Vector3f;

import com.karl.Engine.utils.SmoothFloat;
import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.renderer.particles.ParticleSystem;
import com.trapdoor.engine.tools.Logging;
import com.trapdoor.engine.world.entities.components.Transform;
import com.trapdoor.engine.world.entities.extras.EntityEvent;
import com.trapdoor.engine.world.entities.extras.EntitySpawnType;

/**
 * @author brett
 * @date Mar. 5, 2022
 * 
 */
public class EntitySpawner extends Entity {
	
	private EntitySpawnType spawn;
	private Transform spawnTransform;
	private Transform t;
	private Vector3d center;
	private ParticleSystem spawnParticles, fire;
	private Random random;
	
	private double delta;
	private SmoothFloat multiple;
	private long lastSpawn = System.currentTimeMillis();
	private long spawnTime;
	
	private float spawnRadius;
	private int spawnAmount;
	
	// doesn't matter what you pass as spawn, it currently only spawns EntityKent
	public EntitySpawner(EntitySpawnType spawn, ParticleSystem spawnParticles, ParticleSystem fire, long spawnTimeMs, float spawnRadius, int spawnAmount) {
		this.spawn = spawn;
		this.spawnParticles = spawnParticles;
		this.fire = fire;
		this.spawnTransform = spawn.getRepresentedEntity().getComponent(Transform.class);
		this.t = this.getComponent(Transform.class);
		this.spawnTime = spawnTimeMs;
		this.multiple = new SmoothFloat(0, 5);
		this.random = new Random(spawnTimeMs * spawn.hashCode() - spawnParticles.hashCode());
		this.spawnRadius = spawnRadius;
		this.spawnAmount = spawnAmount;
	}
	
	public EntitySpawner(EntitySpawnType spawn, ParticleSystem spawnParticles, ParticleSystem fire, long spawnTimeMs, int spawnAmount) {
		this(spawn, spawnParticles, fire, spawnTimeMs, 6, spawnAmount);
	}
	
	public EntitySpawner(EntitySpawnType spawn, ParticleSystem spawnParticles, ParticleSystem fire, long spawnTimeMs) {
		this(spawn, spawnParticles, fire, spawnTimeMs, 6, 3);
	}
	
	public EntitySpawner(EntitySpawnType spawn, ParticleSystem spawnParticles, ParticleSystem fire) {
		this(spawn, spawnParticles, fire, 32000, 6, 3);
	}
	
	@Override
	public void onAddedToWorld() {
		super.onAddedToWorld();
		
		center = this.getModel().getAABB().getCenter();
		spawn.getRepresentedEntity().setPosition((float)center.x + t.getX(), (float)center.y + t.getY(), (float)center.z + t.getZ());
		float dist = spawn.getRepresentedEntity().getModel().getAABB().longestDistanceFromCenter();
		this.spawnTransform.setScale(dist/2, dist/2, dist/2);
		
		super.world.addEntityToWorld(spawn.getRepresentedEntity());
	}

	@Override
	public void update() {
		super.update();
		final double constMultiple = 5;
		delta += DisplayManager.getFrameTimeSeconds() * multiple.get() * constMultiple;
		if (delta >= 360)
			delta = 0;
		if (delta % 1 <= 0.1d) {
			fire.saveState(fire.getPps() * 2, 1, 0, fire.getAverageLifeLength(), fire.getAverageScale() / 2);
			fire.generateParticles(new Vector3f(this.t.getX(), this.t.getY(), this.t.getZ()));
			fire.restoreState();
		}
		spawnTransform.setRotation(rotFunction(delta), 0, 0);
		
		multiple.setTarget((float) (((System.currentTimeMillis() - lastSpawn) / (double)spawnTime) + 0.01f));
		multiple.update((float) DisplayManager.getFrameTimeSeconds());
		
		if (System.currentTimeMillis() - lastSpawn > spawnTime) {
			
			for (int i = 0; i < spawnAmount; i++) {
				Vector3f spawnPoint = pickSpawnPoint();
				try {
					Entity e = spawn.spawnEntity(spawnPoint.x, spawnPoint.y, spawnPoint.z);
					
					e.onSpecialEvent(EntityEvent.SPAWNED, fire);
					this.world.addEntityToWorld(e);
					spawnParticles.generateParticles(spawnPoint);
					
					fire.saveState(fire.getPps() * 4, 1, 0, fire.getAverageLifeLength() * 2, fire.getAverageScale() * 2);
					Vector3f tf = new Vector3f(this.t.getX(), this.t.getY(), this.t.getZ());
					for (int j = 0; j < 5; j++)
						fire.generateParticles(tf);
					fire.restoreState();
				} catch (Exception e) {
					Logging.logger.error(e.getLocalizedMessage(), e);
				}
			}
			
			lastSpawn = System.currentTimeMillis();
		}
	}
	
	private Vector3f pickSpawnPoint() {
		Vector3f spawnPoint = nextSpawnPoint().add(this.t.getX(), this.t.getY(), this.t.getZ());
		while (checkForSpawnIntersect(spawnPoint))
			spawnPoint = nextSpawnPoint().add(this.t.getX(), this.t.getY(), this.t.getZ());
		return spawnPoint;
	}
	
	private boolean checkForSpawnIntersect(Vector3f point) {
		return this.getModel().getAABB().isInside(point.x, point.y, point.z);
	}
	
	private Vector3f nextSpawnPoint() {
		float x = (float) (random.nextDouble() * spawnRadius * 2 - spawnRadius);
		float y = (float) (random.nextDouble() * spawnRadius * 2 - spawnRadius);
		float z = (float) (random.nextDouble() * spawnRadius * 2 - spawnRadius);
		return new Vector3f(x, y, z);
	}
	
	private float rotFunction(double delta) {
		return (float) delta;
	}
	
}

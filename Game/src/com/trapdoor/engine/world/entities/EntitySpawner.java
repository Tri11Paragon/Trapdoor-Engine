package com.trapdoor.engine.world.entities;

import java.util.Random;

import org.joml.Vector3d;
import org.joml.Vector3f;

import com.game.entities.EntityKent;
import com.karl.Engine.utils.SmoothFloat;
import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.registry.GameRegistry;
import com.trapdoor.engine.tools.Logging;
import com.trapdoor.engine.world.entities.components.Transform;

/**
 * @author brett
 * @date Mar. 5, 2022
 * 
 */
public class EntitySpawner extends Entity {
	
	private Entity spawn;
	private Transform spawnTransform;
	private Transform t;
	private Vector3d center;
	private EntityCamera camera;
	private Random random;
	
	private double delta;
	private SmoothFloat multiple;
	private long lastSpawn = System.currentTimeMillis();
	private long spawnTime;
	
	private float spawnRadius;
	private int spawnAmount;
	
	// doesn't matter what you pass as spawn, it currently only spawns EntityKent
	public EntitySpawner(Entity spawn, EntityCamera camera, long spawnTimeMs, float spawnRadius, int spawnAmount) {
		this.spawn = spawn;
		this.camera = camera;
		this.spawnTransform = spawn.getComponent(Transform.class);
		this.t = this.getComponent(Transform.class);
		this.spawnTime = spawnTimeMs;
		this.multiple = new SmoothFloat(0, 5);
		this.random = new Random(spawnTimeMs * spawn.hashCode() - camera.hashCode());
		this.spawnRadius = spawnRadius;
		this.spawnAmount = spawnAmount;
	}
	
	public EntitySpawner(Entity spawn, EntityCamera camera, long spawnTimeMs, int spawnAmount) {
		this(spawn, camera, spawnTimeMs, 6, spawnAmount);
	}
	
	public EntitySpawner(Entity spawn, EntityCamera camera, long spawnTimeMs) {
		this(spawn, camera, spawnTimeMs, 6, 3);
	}
	
	public EntitySpawner(Entity spawn, EntityCamera camera) {
		this(spawn, camera, 32000, 6, 3);
	}
	
	@Override
	public void onAddedToWorld() {
		super.onAddedToWorld();
		
		center = this.getModel().getAABB().getCenter();
		spawn.setPosition((float)center.x + t.getX(), (float)center.y + t.getY(), (float)center.z + t.getZ());
		float dist = spawn.getModel().getAABB().longestDistanceFromCenter();
		this.spawnTransform.setScale(dist/2, dist/2, dist/2);
		
		super.world.addEntityToWorld(spawn);
	}

	@Override
	public void update() {
		super.update();
		final double constMultiple = 5;
		delta += DisplayManager.getFrameTimeSeconds() * multiple.get() * constMultiple;
		if (delta >= 360)
			delta = 0;
		spawnTransform.setRotation(rotFunction(delta), 0, 0);
		
		multiple.setTarget((float) (((System.currentTimeMillis() - lastSpawn) / (double)spawnTime) + 0.01f));
		multiple.update((float) DisplayManager.getFrameTimeSeconds());
		
		if (System.currentTimeMillis() - lastSpawn > spawnTime) {
			
			Vector3f spawnPoint = pickSpawnPoint();
			
			for (int i = 0; i < spawnAmount; i++) {
				try {
					/*Entity spawned = spawn.getClass().getDeclaredConstructor((Class<?>[])null).newInstance();
					System.out.println(spawnPoint);
					spawned.setPosition(spawnPoint.x, spawnPoint.y, spawnPoint.z);
					spawned.onSpecialEvent(EntityEvent.SPAWNED, camera);
					
					this.world.addEntityToWorld(spawned);*/
					EntityKent kent = new EntityKent(1, 3, camera);
					kent.setPosition(spawnPoint.x, spawnPoint.y, spawnPoint.z);
					kent.setModel(GameRegistry.getModel("resources/models/kent.dae"));
					
					kent.onSpecialEvent(EntityEvent.SPAWNED, camera);
					this.world.addEntityToWorld(kent);
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
		float x = random.nextFloat(spawnRadius*2) - spawnRadius;
		float y = random.nextFloat(spawnRadius*2) - spawnRadius;
		float z = random.nextFloat(spawnRadius*2) - spawnRadius;
		return new Vector3f(x, y, z);
	}
	
	private float rotFunction(double delta) {
		return (float) delta;
	}
	
}

package com.trapdoor.engine.tools;

import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.trapdoor.engine.ProjectionMatrix;
import com.trapdoor.engine.camera.Camera;
import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.world.World;
import com.trapdoor.engine.world.entities.Entity;
import com.trapdoor.engine.world.entities.EntityCamera;

/**
 * @author brett
 * @date Feb. 14, 2022
 * 
 */
public class RayCasting {

	private Vector3f currentRay = new Vector3f();

	private Entity entityLookingAt = null;
	
	private Camera camera;
	private World world;
	
	public RayCasting(Camera cam, World world) {
		this.camera = cam;
		this.world = world;
	}

	public Vector3f getCurrentRay() {
		return currentRay;
	}
	
	public Entity getEntityCurrentlyLookingAt() {
		return entityLookingAt;
	}

	public void update() {
		currentRay = calculateScreenRay();
		
		// TODO: this but better? is even needed?
		List<PhysicsRayTestResult> results = world.raycast(currentRay, 20);
		for (int i = 0; i < results.size(); i++) {
			Entity e = world.getEntity(results.get(i).getCollisionObject());
			if (e instanceof EntityCamera)
				continue;
			entityLookingAt = e;
		}
	}

	private Vector3f calculateScreenRay() {
		float mouseX = (float) DisplayManager.WIDTH / 2;
		float mouseY = (float) DisplayManager.HEIGHT /2;
		Vector2f normalizedCoords = getNormalisedDeviceCoordinates(mouseX, mouseY);
		Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f);
		Vector4f eyeCoords = toEyeCoords(clipCoords);
		Vector3f worldRay = toWorldCoords(eyeCoords);
		return worldRay;
	}
	
	@SuppressWarnings("unused")
	private Vector3f calculateMouseRay() {
		float mouseX = (float) DisplayManager.getMouseX();
		float mouseY = (float) DisplayManager.getMouseY();
		Vector2f normalizedCoords = getNormalisedDeviceCoordinates(mouseX, mouseY);
		Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f);
		Vector4f eyeCoords = toEyeCoords(clipCoords);
		Vector3f worldRay = toWorldCoords(eyeCoords);
		return worldRay;
	}

	
	private final Matrix4f storeC = new Matrix4f();
	private final Vector3f storeV3C = new Vector3f();
	private Vector3f toWorldCoords(Vector4f eyeCoords) {
		Matrix4f invertedView = camera.getViewMatrix().invert(storeC);
		Vector4f rayWorld = invertedView.transform(eyeCoords);
		storeV3C.x = rayWorld.x;
		storeV3C.y = rayWorld.y;
		storeV3C.z = rayWorld.z;
		storeV3C.normalize();
		return storeV3C;
	}

	private final Matrix4f storeE = new Matrix4f();
	private final Vector4f storeV4E = new Vector4f();
	private Vector4f toEyeCoords(Vector4f clipCoords) {
		Matrix4f invertedProjection = ProjectionMatrix.projectionMatrix.invert(storeE);
		Vector4f eyeCoords = invertedProjection.transform(clipCoords);
		storeV4E.x = eyeCoords.x;
		storeV4E.y = eyeCoords.y;
		storeV4E.z = -1.0f;
		storeV4E.w = 0.0f;
		return storeV4E;
	}

	private Vector2f getNormalisedDeviceCoordinates(float mouseX, float mouseY) {
		float x = (2.0f * mouseX) / DisplayManager.WIDTH - 1f;
		float y = (2.0f * mouseY) / DisplayManager.HEIGHT - 1f;
		return new Vector2f(x, y);
	}

}

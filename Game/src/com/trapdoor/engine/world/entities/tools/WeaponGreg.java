package com.trapdoor.engine.world.entities.tools;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;

import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.collision.shapes.infos.IndexedMesh;
import com.trapdoor.engine.VAOLoader;
import com.trapdoor.engine.datatypes.collision.AxisAlignedBoundingBox;
import com.trapdoor.engine.datatypes.ogl.assimp.Mesh;
import com.trapdoor.engine.datatypes.ogl.assimp.Model;
import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.tools.math.Noise;
import com.trapdoor.engine.world.World;
import com.trapdoor.engine.world.entities.Entity;
import com.trapdoor.engine.world.entities.EntityCamera;
import com.trapdoor.engine.world.entities.components.Transform;

/**
 * @author brett
 * @date Mar. 14, 2022
 * 
 */
public class WeaponGreg extends Weapon {
	
	private static final float range = 50;
	
	private boolean pendingDestruction = false;
	private boolean uploadingDestruction = false;
	private boolean applyingDestruction = false;
	private Entity heldEntity = null;
	
	private Model newModelStore;
	
	private float distance = 0;
	private float timer = 0;
	
	private Noise noisey;

	public WeaponGreg(EntityCamera player, World world) {
		super(player, world);
		this.noisey = new Noise(694);
	}

	@Override
	public void shoot() {
		
	}
	
	@Override
	public void update() {
		if (pendingDestruction && this.heldEntity != null) {
			newModelStore = this.heldEntity.getModel();
			Mesh[] newMehses = new Mesh[newModelStore.getMeshes().length];
			
			for (int i = 0; i < newModelStore.getMeshes().length; i++) {
				Mesh m = newModelStore.getMeshes()[i];
				FloatBuffer verts = BufferUtils.createFloatBuffer(m.getVertices().capacity());
				verts.put(m.getVertices());
				FloatBuffer normals = BufferUtils.createFloatBuffer(m.getNormals().capacity());
				normals.put(m.getNormals());
				FloatBuffer textures = BufferUtils.createFloatBuffer(m.getTextures().capacity());
				textures.put(m.getTextures());
				FloatBuffer tangents = BufferUtils.createFloatBuffer(m.getTangents().capacity());
				tangents.put(m.getTangents());
				FloatBuffer bitangents = BufferUtils.createFloatBuffer(m.getBitangents().capacity());
				bitangents.put(m.getBitangents());
				IntBuffer indicies = BufferUtils.createIntBuffer(m.getIndices().capacity());
				indicies.put(m.getIndices());
				
				verts.rewind();
				indicies.rewind();
				
				com.jme3.math.Vector3f[] vertsFloatArr = new com.jme3.math.Vector3f[verts.capacity()/3];
				int[] indexesIntArr = new int[indicies.capacity()];
				
				for (int j = 0; j < vertsFloatArr.length; j++)
					vertsFloatArr[j] = new com.jme3.math.Vector3f(verts.get(), verts.get(), verts.get());
				
				for (int j = 0; j < indexesIntArr.length; j++)
					indexesIntArr[j] = indicies.get();
				
				indicies.rewind();
				verts.rewind();
				textures.rewind();
				normals.rewind();
				tangents.rewind();
				bitangents.rewind();
				
				IndexedMesh imesh = new IndexedMesh(vertsFloatArr, indexesIntArr);
				
				newMehses[i] = new Mesh(m.getMaterial(), m.getBoundingBox(), verts, textures, normals, tangents, bitangents, indicies, imesh);
			}
			
			newModelStore = new Model(newMehses, this.heldEntity.getModel().getMaterials(), this.heldEntity.getModel().getScene(), this.heldEntity.getModel().getPath());
			pendingDestruction = false;
			uploadingDestruction = true;
		}
	}
	
	@Override
	public void render() {
		if (!pendingDestruction && uploadingDestruction) {
			VAOLoader.loadToVAO(newModelStore, GL33.GL_DYNAMIC_DRAW);
			
			Transform tra = heldEntity.getComponent(Transform.class);
			Entity he = new Entity(heldEntity.getRigidbody().getMass()).setModel(newModelStore).setPosition(tra.getX(), tra.getY(), tra.getZ());
			Transform trhe = he.getComponent(Transform.class);
			trhe.setScale(tra.getScaleX(), tra.getScaleY(), tra.getScaleZ());
			trhe.setRotation(tra.getYaw(), tra.getPitch(), tra.getRoll());
			
			world.removeEntityFromWorld(heldEntity);
			world.addEntityToWorld(he);
			
			heldEntity = he;
			
			uploadingDestruction = false;
			applyingDestruction = true;
		}
		if (applyingDestruction) {
			FloatBuffer verts = newModelStore.getMeshes()[0].getVertices();
			FloatBuffer newVerts = BufferUtils.createFloatBuffer(verts.capacity());
			AxisAlignedBoundingBox aabb = newModelStore.getMeshes()[0].getBoundingBox();
			Vector3d center = aabb.getCenter();
			for (int i = 0; i < verts.capacity() / 3; i++) {
				/*float oldX = verts.get();
				float oldY = verts.get();
				float oldZ = verts.get();
				
				float rvx = (float) (Math.random() * 4);
				float rvy = (float) (Math.random() * 4);
				float rvz = (float) (Math.random() * 4);
				
				float distance = distance(center, oldX + rvx, oldY + rvy, oldZ + rvz);
				
				float noise = (float) noisey.noise(distance + Math.random());
				
				float change = (1 - (1/distance)) * noise;
				
				newVerts.put(oldX * change);
				newVerts.put(oldY * change);
				newVerts.put(oldZ * change);
				
				System.out.println(oldX  * change + " " + oldY * change + " " + oldZ * change + " || " + change + " " + noise + " " + distance);*/
				float oldX = verts.get();
				float oldY = verts.get();
				float oldZ = verts.get();
				
				float vectorScale = 8.4234f;
				
				float tdx = distanceX(center, oldX, oldY, oldZ);
				float tdy = distanceY(center, oldX, oldY, oldZ);
				float tdz = distanceZ(center, oldX, oldY, oldZ);
				
				float dx = tdx / 8.6356f;
				float dy = tdy / 7.2395f;
				float dz = tdz / 8.5324f;
				
				//vectorScale *= 1 / ((tdx + tdy + tdz) / 3);
				
				float nx = (float) (noisey.noise(dx, dy, dz)) * vectorScale;
				float ny = (float) (noisey.noise(dz, dx, dy)) * vectorScale;
				float nz = (float) (noisey.noise(dy, dz, dx)) * vectorScale;
				
				Matrix4f mat4 = new Matrix4f();
				
				//mat4.translate(oldX, oldY, oldZ);
				
				mat4.translate(nx, ny, nz);
				
				final float rotScale = 16.0f;
				
				mat4.rotateX((float) (Math.PI * tdx / rotScale ));
				mat4.rotateY((float) (Math.PI * tdy / rotScale ));
				mat4.rotateZ((float) (Math.PI * tdz / rotScale ));
				
				Vector4f trans = mat4.transform(new Vector4f(oldX, oldY, oldZ, 1.0f));
				
				newVerts.put(trans.x);
				newVerts.put(trans.y);
				newVerts.put(trans.z);
				
				//float x = (float) Math.random() * 2.0f - 1.0f;
				//float y = (float) Math.random() * 2.0f - 1.0f;
				//float z = (float) Math.random() * 2.0f - 1.0f;
				//newVerts.put(verts.get() + x);
				//newVerts.put(y);
				//newVerts.put(z);
			}
			verts.flip();
			newVerts.flip();
			
			GL33.glBindVertexArray(newModelStore.getMeshes()[0].getVAO().getVaoID());
			VAOLoader.updateVBOGreg(newModelStore.getMeshes()[0].getVAO().getVbos()[0], newVerts);
			GL33.glBindVertexArray(0);
			
			//MemoryUtil.memFree(newVerts);
			applyingDestruction = false;
		}
	}
	
	@Override
	public void alt() {
		if (heldEntity == null) {
			List<PhysicsRayTestResult> results = this.world.raycastSorted(range);
			for (int i = 0; i < results.size(); i++) {
				Entity ass = this.world.getEntity(results.get(i).getCollisionObject());
				if (!ass.isStatic() && ass != player) {
					heldEntity = ass;
					heldEntity.getRigidbody().setGravity(new com.jme3.math.Vector3f());
					pendingDestruction = true;
					break;
				}
			}
		} else {
			Vector3f ray = this.world.getRaycast().getCurrentRay();
			Transform t = heldEntity.getComponent(Transform.class);
			AxisAlignedBoundingBox aabb = heldEntity.getModel().getAABB();
			Vector3d center = aabb.getCenter();
			float ox = t.getX();
			float oy = t.getY();
			float oz = t.getZ();
			if (distance < 1) {
				float dx = ox - this.player.getLocalTransform().getX();
				float dy = oy - this.player.getLocalTransform().getY();
				float dz = oz - this.player.getLocalTransform().getZ();
				distance = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
			}
			float lx = ray.x * distance + this.player.getLocalTransform().getX() + (float) center.x;
			// add height of player
			float ly = ray.y * distance + this.player.getLocalTransform().getY() + (float) center.y + 1.0f;
			float lz = ray.z * distance + this.player.getLocalTransform().getZ() + (float) center.z;

			final float mul = 3250;

			float dx = (ox - lx);
			float dy = (oy - ly);
			float dz = (oz - lz);

			float localDist = (float) Math.sqrt(dx * dx + dy * dy + dz * dz) * 2;
			dx *= -mul * localDist;
			dy *= -mul * localDist;
			dz *= -mul * localDist;

			heldEntity.setLinearVelocity(0, 0, 0);
			//heldEntity.applyCentralForce(dx, dy, dz);
			timer += DisplayManager.getFrameTimeSeconds();
		}
	}
	
	@Override
	public void altRelease() {
		if (heldEntity != null) {
			heldEntity.getRigidbody().setGravity(world.getGravity());
			if (timer > 0.5f) {
				Vector3f ray = world.getRaycast().getCurrentRay();
				float force = Math.min(2000, timer * 1000) * 5;
				//heldEntity.applyCentralImpulse(ray.x * force, ray.y * force, ray.z * force);
			}
		}
		heldEntity = null;
		distance = 0;
		timer = 0;
	}
	
	@Override
	public void altN() {
		super.altN();
	}
	
	public static float distance(Vector3d center, float x, float y, float z) {
		double dx = center.x - x;
		double dy = center.y - y;
		double dz = center.z - z;
		return (float)( dx * dx + dy * dy + dz * dz);
	}
	
	public static float distanceX(Vector3d center, float x, float y, float z) {
		double dx = center.x - x;
		return (float)(dx * dx);
	}
	
	public static float distanceY(Vector3d center, float x, float y, float z) {
		double dy = center.y - y;
		return (float)(dy * dy);
	}
	
	public static float distanceZ(Vector3d center, float x, float y, float z) {
		double dz = center.z - z;
		return (float)(dz * dz);
	}

}

package com.trapdoor.engine.world.entities.tools;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.collision.shapes.infos.IndexedMesh;
import com.trapdoor.engine.VAOLoader;
import com.trapdoor.engine.datatypes.collision.AxisAlignedBoundingBox;
import com.trapdoor.engine.datatypes.ogl.assimp.Mesh;
import com.trapdoor.engine.datatypes.ogl.assimp.Model;
import com.trapdoor.engine.display.DisplayManager;
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
	private Entity destroyOStore = null;
	
	private Model newModelStore;
	
	private float distance = 0;
	private float timer = 0;

	public WeaponGreg(EntityCamera player, World world) {
		super(player, world);
	}

	@Override
	public void shoot() {
		
	}
	
	@Override
	public void update() {
		if (pendingDestruction && this.heldEntity != null) {
			destroyOStore = new Entity();
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
				
				IndexedMesh imesh = new IndexedMesh(vertsFloatArr, indexesIntArr);
				
				newMehses[i] = new Mesh(m.getMaterial().clone(), m.getBoundingBox(), verts, textures, normals, tangents, bitangents, indicies, imesh);
			}
			
			newModelStore = new Model(newMehses, this.heldEntity.getModel().getMaterials(), this.heldEntity.getModel().getScene(), this.heldEntity.getModel().getPath());
			pendingDestruction = false;
			uploadingDestruction = true;
		}
	}
	
	@Override
	public void render() {
		if (!pendingDestruction && uploadingDestruction) {
			VAOLoader.loadToVAO(newModelStore);
			uploadingDestruction = false;
			applyingDestruction = true;
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
			heldEntity.applyCentralForce(dx, dy, dz);
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
				heldEntity.applyCentralImpulse(ray.x * force, ray.y * force, ray.z * force);
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

}

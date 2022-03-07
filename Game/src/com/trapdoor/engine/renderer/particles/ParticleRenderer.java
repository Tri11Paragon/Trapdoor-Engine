
package com.trapdoor.engine.renderer.particles;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;

import com.trapdoor.engine.VAOLoader;
import com.trapdoor.engine.camera.Camera;
import com.trapdoor.engine.datatypes.ogl.obj.VAO;
import com.trapdoor.engine.registry.GameRegistry;
import com.trapdoor.engine.tools.math.Maths;
import com.trapdoor.engine.world.World;

public class ParticleRenderer {
	
	private static final float[] VERTICES = {-0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f};
	
	private static final int DATA_SIZE = 16 + 3;
	
	private VAO quad;
	private int vbo;
	private ParticleShader shader;
	private Matrix4f modelView = new Matrix4f();
	
	private ParticleStorage storage;
	private FloatBuffer dataStorage;
	
	public ParticleRenderer(){
		shader = new ParticleShader();
		storage = new ParticleStorage();
		dataStorage = BufferUtils.createFloatBuffer(DATA_SIZE * storage.MAX_PARTICLES);
		quad = VAOLoader.loadToVAO(VERTICES, 2);
		vbo = VAOLoader.createEmptyVBO(DATA_SIZE * 4 * storage.MAX_PARTICLES);
		VAOLoader.addInstancedAttribute(quad.getVaoID(), vbo, 1, 4, DATA_SIZE * 4, 0);
		VAOLoader.addInstancedAttribute(quad.getVaoID(), vbo, 2, 4, DATA_SIZE * 4, 4 * 4);
		VAOLoader.addInstancedAttribute(quad.getVaoID(), vbo, 3, 4, DATA_SIZE * 4, 8 * 4);
		VAOLoader.addInstancedAttribute(quad.getVaoID(), vbo, 4, 4, DATA_SIZE * 4, 12 * 4);
		VAOLoader.addInstancedAttribute(quad.getVaoID(), vbo, 5, 3, DATA_SIZE * 4, 16 * 4);
	}
	
	public void update(World world, Camera camera) {
		for (int i = 0; i < storage.getSize(); i++) {
			Particle p = storage.getParticle(i);
			if (p != null && !p.update(world, camera)) 
				storage.remove(storage.get(i));
		}
		storage.sortHighToLow();
	}
	
	public void render(World world, Camera camera){
		shader.start();
		GL33.glBindVertexArray(quad.getVaoID());
		GL33.glEnableVertexAttribArray(0);
		GL33.glEnableVertexAttribArray(1);
		GL33.glEnableVertexAttribArray(2);
		GL33.glEnableVertexAttribArray(3);
		GL33.glEnableVertexAttribArray(4);
		GL33.glEnableVertexAttribArray(5);
		
		//GL33.glDepthMask(false);
		GL33.glEnable(GL33.GL_BLEND);
		GL33.glBlendFunc(GL33.GL_SRC_ALPHA, GL33.GL_ONE_MINUS_SRC_ALPHA);
		GL33.glActiveTexture(GL33.GL_TEXTURE0);
		GL33.glBindTexture(GL33.GL_TEXTURE_2D_ARRAY, GameRegistry.getParticleTextureAtlas());
		//GL33.glBlendFunc(GL33.GL_SRC_ALPHA, GL33.GL_ONE);
		
		dataStorage.clear();
		
		int nulls = 0;
		
		for (int i = 0; i < storage.size(); i++) {
			Particle p = storage.get(i);
			if (p == null) {
				nulls++;
				continue;
			}
			modelView.set(camera.getViewMatrix());
			Matrix4f mat = updateModelViewMatrix(p);
			dataStorage.put(mat.m00());
			dataStorage.put(mat.m01());
			dataStorage.put(mat.m02());
			dataStorage.put(mat.m03());
			dataStorage.put(mat.m10());
			dataStorage.put(mat.m11());
			dataStorage.put(mat.m12());
			dataStorage.put(mat.m13());
			dataStorage.put(mat.m20());
			dataStorage.put(mat.m21());
			dataStorage.put(mat.m22());
			dataStorage.put(mat.m23());
			dataStorage.put(mat.m30());
			dataStorage.put(mat.m31());
			dataStorage.put(mat.m32());
			dataStorage.put(mat.m33());
			dataStorage.put(p.getCurrentTexture());
			dataStorage.put(p.getNextTexture());
			dataStorage.put(p.getBlend());
		}
		VAOLoader.updateVBO(vbo, dataStorage);
		
		GL33.glDrawArraysInstanced(GL33.GL_TRIANGLE_STRIP, 0, quad.getVertexCount(), storage.size() - nulls);
		
		//GL33.glDepthMask(true);
		GL33.glDisable(GL33.GL_BLEND);
		
		GL33.glDisableVertexAttribArray(0);
		GL33.glDisableVertexAttribArray(1);
		GL33.glDisableVertexAttribArray(2);
		GL33.glDisableVertexAttribArray(3);
		GL33.glDisableVertexAttribArray(4);
		GL33.glDisableVertexAttribArray(5);
		GL33.glBindVertexArray(0);
		shader.stop();
	}

	
	private Matrix4f modelMatrix = new Matrix4f();
	private Matrix4f updateModelViewMatrix(Particle p) {
		modelMatrix.identity();
		modelMatrix.translate(p.getPosition());
		modelMatrix.m00(modelView.m00());
		modelMatrix.m01(modelView.m10());
		modelMatrix.m02(modelView.m20());
		modelMatrix.m10(modelView.m01());
		modelMatrix.m11(modelView.m11());
		modelMatrix.m12(modelView.m21());
		modelMatrix.m20(modelView.m02());
		modelMatrix.m21(modelView.m12());
		modelMatrix.m22(modelView.m22());
		modelMatrix.rotate(p.getRotation(), Maths.rz);
		modelMatrix.scale(p.getScale());
		return modelView.mul(modelMatrix);
	}
	
	public ParticleStorage getStorage() {
		return storage;
	}
	
	public void cleanUp(){
		shader.cleanUp();
	}

}

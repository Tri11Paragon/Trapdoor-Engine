package com.game.engine.renderer;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

import com.game.engine.Loader;
import com.game.engine.TextureLoader;
import com.game.engine.datatypes.ogl.BlockModelVAO;
import com.game.engine.datatypes.world.Entity;
import com.game.engine.shaders.AtlasShader;
import com.game.engine.shaders.WorldShader;
import com.game.engine.tools.math.Maths;

/**
 * @author brett
 * @date Oct. 19, 2021
 * 
 */
public class EntityRenderer {
	
	private static final float[] VERTICIES = {
		0, 1, 0.0f, 1.0f,
		0, 0, 0.0f, 0.0f,
		1, 1, 1.0f, 1.0f,
		1, 0, 1.0f, 0.0f
	};
	private static final int MAX_INSTANCES = 10000;
	private static final int INSTANCE_FLOAT_COUNT = 16 + 1;
	private static final int INSTANCE_DATA_LENGTH = INSTANCE_FLOAT_COUNT * 4;
	private static final FloatBuffer buffer = BufferUtils.createFloatBuffer(MAX_INSTANCES * INSTANCE_DATA_LENGTH);
	private static Matrix4f modelViewMatrix = new Matrix4f();
	private static int pointer = 0;
	private static int pointer2 = 0;
	
	private static final float[] vertices = {
		    // positions        // texture coords
		     1f,  1f, 0.0f,  1.0f, 1.0f,   // top right
		     1f,  0f, 0.0f,  1.0f, 0.0f,   // bottom right
		     0f,  0f, 0.0f,  0.0f, 0.0f,   // bottom left
		     0f,  1f, 0.0f,  0.0f, 1.0f    // top left 
		};
	private static final int[] indices = {
	        0, 1, 3, // first triangle
	        1, 2, 3  // second triangle
	    };
	private static BlockModelVAO vao;
	private static int vbo;
	private static final HashMap<Integer, HashMap<Integer, List<Entity>>> batchAtlasMap = new HashMap<Integer, HashMap<Integer, List<Entity>>>();
	private static int entityCount = 0;
	private static final HashMap<Integer, List<Entity>> batchMap = new HashMap<Integer, List<Entity>>();
	
	public static void init() {
		vao = Loader.loadToVAO(VERTICIES);
		vbo = Loader.createEmptyVBO(INSTANCE_DATA_LENGTH * MAX_INSTANCES);
		Loader.addInstancedAttribute(vao.getVaoID(), vbo, 2, 4, INSTANCE_DATA_LENGTH, 0);
		Loader.addInstancedAttribute(vao.getVaoID(), vbo, 3, 4, INSTANCE_DATA_LENGTH, 4);
		Loader.addInstancedAttribute(vao.getVaoID(), vbo, 4, 4, INSTANCE_DATA_LENGTH, 8);
		Loader.addInstancedAttribute(vao.getVaoID(), vbo, 5, 4, INSTANCE_DATA_LENGTH, 12);
		Loader.addInstancedAttribute(vao.getVaoID(), vbo, 6, 1, INSTANCE_DATA_LENGTH, 16);
	}
	
	public static void render(AtlasShader atlasShader, WorldShader normalShader, Matrix4f viewmatrix) {
		GL30.glBindVertexArray(vao.getVaoID());
		GL30.glEnableVertexAttribArray(0);
		GL30.glEnableVertexAttribArray(1);
		GL30.glEnableVertexAttribArray(2);
		GL30.glEnableVertexAttribArray(3);
		GL30.glEnableVertexAttribArray(4);
		GL30.glEnableVertexAttribArray(5);
		GL30.glEnableVertexAttribArray(6);
		atlasShader.start();
		
		atlasShader.loadViewMatrix(viewmatrix);
		
		GL30.glActiveTexture(GL30.GL_TEXTURE0);
		// TODO: maybe a less memory intensive way of doing this?
		Iterator<Entry<Integer, HashMap<Integer, List<Entity>>>> mapIT = batchAtlasMap.entrySet().iterator();
		while (mapIT.hasNext()) {
			Entry<Integer, HashMap<Integer, List<Entity>>> mapEN = mapIT.next();
			
			GL30.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, mapEN.getKey());
			
			pointer = 0;
			pointer2 = 0;
			float[] vboData = new float[entityCount * INSTANCE_FLOAT_COUNT];
			
			Iterator<Entry<Integer, List<Entity>>> inIT = mapEN.getValue().entrySet().iterator();
			while (inIT.hasNext()) {
				Entry<Integer, List<Entity>> inEN = inIT.next();
				List<Entity> el = inEN.getValue();
				
				
				//atlasShader.loadTextureID(inEN.getKey());
				
				for (int i = 0; i < el.size(); i++) {
					Entity e = el.get(i);
					if (!e.isEnabled())
						continue;
					//atlasShader.loadTranslationMatrix(Maths.createTransformationMatrix(e.x(), e.y(), e.z(), e.getRotation(), e.getWidth(), e.getHeight()));
					modelViewMatrix = viewmatrix.mul(Maths.createTransformationMatrix(e.x(), e.y(), e.z(), e.getRotation(), e.getWidth(), e.getHeight()));
					vboData[pointer++] = modelViewMatrix.m00();
					vboData[pointer++] = modelViewMatrix.m01();
					vboData[pointer++] = modelViewMatrix.m02();
					vboData[pointer++] = modelViewMatrix.m03();
					vboData[pointer++] = modelViewMatrix.m10();
					vboData[pointer++] = modelViewMatrix.m11();
					vboData[pointer++] = modelViewMatrix.m12();
					vboData[pointer++] = modelViewMatrix.m13();
					vboData[pointer++] = modelViewMatrix.m20();
					vboData[pointer++] = modelViewMatrix.m21();
					vboData[pointer++] = modelViewMatrix.m22();
					vboData[pointer++] = modelViewMatrix.m23();
					vboData[pointer++] = modelViewMatrix.m30();
					vboData[pointer++] = modelViewMatrix.m31();
					vboData[pointer++] = modelViewMatrix.m32();
					vboData[pointer++] = modelViewMatrix.m33();
					// extra float per entity that we don't need eh
					// but what can ya do
					vboData[pointer++] = inEN.getKey();
					// maybe use instance rendering?
					//GL30.glDrawElements(GL30.GL_TRIANGLES, vao.getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
				}
				Loader.updateVBO(vbo, vboData, buffer);
				GL33.glDrawArraysInstanced(GL30.GL_TRIANGLE_STRIP, 0, vao.getVertexCount(), entityCount);
			}
		}
		
		atlasShader.stop();
		//normalShader.start();
		
		//normalShader.loadViewMatrix(viewmatrix);
		
		GL30.glDisableVertexAttribArray(0);
		GL30.glDisableVertexAttribArray(1);
		GL30.glDisableVertexAttribArray(2);
		GL30.glDisableVertexAttribArray(3);
		GL30.glDisableVertexAttribArray(4);
		GL30.glDisableVertexAttribArray(5);
		GL30.glDisableVertexAttribArray(6);
	}
	
	public static void addEntity(Entity e) {
		if (e.getIsUsingAtlas()) {
			int at = TextureLoader.getTextureAtlas(e.getTexture());
			int id = TextureLoader.getTextureAtlasID(e.getTexture());
			HashMap<Integer, List<Entity>> ids = batchAtlasMap.get(at);
			if (ids == null) {
				ids = new HashMap<Integer, List<Entity>>();
				batchAtlasMap.put(at, ids);
			}
			List<Entity> list = ids.get(id);
			if (list == null) {
				list = new ArrayList<Entity>();
				ids.put(id, list);
			}
			list.add(e);
			entityCount++;
		} else {
			
		}
	}
	
	public static void deleteEntity(Entity e) {
		if (e.getIsUsingAtlas()) {
			 
			entityCount--;
		} else {
			
		}
	}
	
	public static void renderEntity(Entity e) {
		if (e.getIsUsingAtlas()) {
			
		} else {
			
		}
	}
	
	public static int getNumberOfEntitesInWorld() {
		return entityCount;
	}
	
	public static int getNumberOfEnabledEntitesInWorld() {
		int e = 0;
		Iterator<Entry<Integer, HashMap<Integer, List<Entity>>>> mapIT = batchAtlasMap.entrySet().iterator();
		while (mapIT.hasNext()) {
			Entry<Integer, HashMap<Integer, List<Entity>>> mapEN = mapIT.next();
			Iterator<Entry<Integer, List<Entity>>> inIT = mapEN.getValue().entrySet().iterator();
			while (inIT.hasNext()) {
				for (Entity ee : inIT.next().getValue())
					if (ee.isEnabled())
						e++;
			}
		}
		return e;
	}

}

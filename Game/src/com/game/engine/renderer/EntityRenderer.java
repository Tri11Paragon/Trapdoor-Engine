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
import com.game.engine.camera.Camera;
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
		-1, 1, 0.0f, 1.0f,
		1, -1, 1.0f, 0.0f,
		-1, -1, 0.0f, 0.0f,
		
		-1, 1, 0.0f, 1.0f,
		1, -1, 1.0f, 0.0f,
		1, 1, 1.0f, 1.0f,
	};
	private static final int MAX_INSTANCES = 100000;
	private static final int INSTANCE_FLOAT_COUNT = 16 + 1;
	private static final int INSTANCE_DATA_LENGTH = INSTANCE_FLOAT_COUNT * 4;
	private static final FloatBuffer buffer = BufferUtils.createFloatBuffer(MAX_INSTANCES * INSTANCE_DATA_LENGTH);
	private static Matrix4f modelViewMatrix = new Matrix4f();
	private static int pointer = 0;
	
	private static BlockModelVAO vao;
	private static int vbo;
	private static final HashMap<Integer, List<Entity>> batchAtlasMap = new HashMap<Integer, List<Entity>>();
	private static int entityCount = 0;
	private static final HashMap<Integer, List<Entity>> batchMap = new HashMap<Integer, List<Entity>>();
	
	public static void init() {
		vao = Loader.loadToVAO(VERTICIES);
		vbo = Loader.createEmptyVBO(INSTANCE_DATA_LENGTH * MAX_INSTANCES);
		Loader.addInstancedAttribute(vao.getVaoID(), vbo, 2, 4, INSTANCE_DATA_LENGTH, 0);
		Loader.addInstancedAttribute(vao.getVaoID(), vbo, 3, 4, INSTANCE_DATA_LENGTH, 4 * 4);
		Loader.addInstancedAttribute(vao.getVaoID(), vbo, 4, 4, INSTANCE_DATA_LENGTH, 8 * 4);
		Loader.addInstancedAttribute(vao.getVaoID(), vbo, 5, 4, INSTANCE_DATA_LENGTH, 12 * 4);
		Loader.addInstancedAttribute(vao.getVaoID(), vbo, 6, 1, INSTANCE_DATA_LENGTH, 16 * 4);
	}
	
	public static void render(AtlasShader atlasShader, WorldShader normalShader, Matrix4f viewmatrix, Camera camera) {
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
		Iterator<Entry<Integer, List<Entity>>> mapIT = batchAtlasMap.entrySet().iterator();
		while (mapIT.hasNext()) {
			Entry<Integer, List<Entity>> mapEN = mapIT.next();
			
			GL30.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, mapEN.getKey());
			
			pointer = 0;
			
			List<Entity> el = mapEN.getValue();
			float[] vboData = new float[el.size() * INSTANCE_FLOAT_COUNT];
			int count = 0;
			
			for (int i = 0; i < el.size(); i++) {
				Entity e = el.get(i);
				// maybe don't use this? idk
				// the isenbaled fine just the clipping nonsense
				// i guess it can save like 17 assignments and a matrix calc
				if (!e.isEnabled() || !camera.isIn2DFrustum(e.x(), e.y(), e.getWidth(), e.getHeight()))
					continue;
				count++;
				modelViewMatrix = Maths.createTransformationMatrix(e.x(), e.y(), e.z(), e.getRotation(), e.getWidth(), e.getHeight());
				// touch this and i'll kill you
				// not hard to understand if you know that opengl splits its 4x matrix
				// into 4 4 length column vectors
				// and that we need to store the texture atlas position as well
				// (making 17 floats, in order)
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
				vboData[pointer++] = e.getTextureID();
				// maybe use instance rendering?
				// GL30.glDrawElements(GL30.GL_TRIANGLES, vao.getVertexCount(),
				// GL30.GL_UNSIGNED_INT, 0);
			}
			Loader.updateVBO(vbo, vboData, buffer);
			// "Because some errors in user programs can cause the JVM to crash without a meaningful error message, 
			// since LWJGL 3 is tuned for extreme speed at the expense of robustness."
			// SO FUCKING TRUE
			// HOLY SHIT THIS GAY ASS FUNCTION
			// always crashes
			// (see all the hs_err_pid.......log)
			GL33.glDrawArraysInstanced(GL30.GL_TRIANGLES, 0, 6, count);
			// turns out the crashes was due to my own incompetence 
			// *shhhhhhhh
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
	
	/**
	 * Adds the entity to the internal data structure for rendering <br>
	 * <b>THIS FUNCTION SHOULD ONLY BE CALLED WHEN AN ENTITY IS CREATED. <b>
	 * @param e entity to add to the draw queue
	 */
	public static void addEntity(Entity e) {
		if (e.getIsUsingAtlas()) {
			int at = TextureLoader.getTextureAtlas(e.getTexture());
			List<Entity> list = batchAtlasMap.get(at);
			if (list == null) {
				list = new ArrayList<Entity>();
				batchAtlasMap.put(at, list);
			}
			list.add(e);
			
		} else {
			int id = TextureLoader.loadTexture(e.getTexture());
			List<Entity> list = batchMap.get(id);
			if (list == null) {
				list = new ArrayList<Entity>();
				batchMap.put(id, list);
			}
			list.add(e);
		}
		entityCount++;
	}
	
	/**
	 * deletes an entity from the renderer's internal data structure. <br>
	 * If you are intending for a entity to not be drawn, ie you are not removing it from the world,
	 * please use e.setEnabled(false); <br>
	 * <b> this method will make it impossible to draw this entity, ever. </b>
	 * @param e entity to be deleted
	 * @return if the entity was contained inside the renderer
	 */
	public static boolean deleteEntity(Entity e) {
		if (e.getIsUsingAtlas()) {
			 int at = TextureLoader.getTextureAtlas(e.getTexture());
			 List<Entity> list = batchAtlasMap.get(at);
			 if (list != null) {
				 entityCount--;
				 return list.remove(e);
			 }
			 return false;
		} else {
			int at = TextureLoader.loadTexture(e.getTexture());
			 List<Entity> list = batchMap.get(at);
			 if (list != null) {
				 entityCount--;
				 return list.remove(e);
			 }
			 return false;
		}
	}
	
	/**
	 * this function completely removes all entities from the draw queue. <br>
	 * Only use when screen are being swapped.
	 */
	public static void deleteAllEntities() {
		batchAtlasMap.clear();
		batchMap.clear();
	}

	public static int getNumberOfEntitesInWorld() {
		return entityCount;
	}
	
	public static int getNumberOfEnabledEntitesInWorld() {
		int e = 0;
		Iterator<Entry<Integer,  List<Entity>>> mapIT = batchAtlasMap.entrySet().iterator();
		while (mapIT.hasNext()) {
			for (Entity ee : mapIT.next().getValue())
				if (ee.isEnabled())
					e++;
		}
		return e;
	}

}

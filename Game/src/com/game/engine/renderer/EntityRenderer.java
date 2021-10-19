package com.game.engine.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;

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
	
	private static final float vertices[] = {
		    // positions        // texture coords
		     1f,  1f, 0.0f,  1.0f, 1.0f,   // top right
		     1f,  0f, 0.0f,  1.0f, 0.0f,   // bottom right
		     0f,  0f, 0.0f,  0.0f, 0.0f,   // bottom left
		     0f,  1f, 0.0f,  0.0f, 1.0f    // top left 
		};
	private static final int indices[] = {
	        0, 1, 3, // first triangle
	        1, 2, 3  // second triangle
	    };
	private static BlockModelVAO vao;
	private static final HashMap<Integer, HashMap<Integer, List<Entity>>> batchAtlasMap = new HashMap<Integer, HashMap<Integer, List<Entity>>>();
	private static final HashMap<Integer, List<Entity>> batchMap = new HashMap<Integer, List<Entity>>();
	
	public static void init() {
		vao = Loader.loadToVAO(vertices, indices);
	}
	
	public static void render(AtlasShader atlasShader, WorldShader normalShader, Matrix4f viewmatrix) {
		GL30.glBindVertexArray(vao.getVaoID());
		GL30.glEnableVertexAttribArray(0);
		GL30.glEnableVertexAttribArray(1);
		atlasShader.start();
		
		atlasShader.loadViewMatrix(viewmatrix);
		
		GL30.glActiveTexture(GL30.GL_TEXTURE0);
		// TODO: maybe a less memory intensive way of doing this?
		Iterator<Entry<Integer, HashMap<Integer, List<Entity>>>> mapIT = batchAtlasMap.entrySet().iterator();
		while (mapIT.hasNext()) {
			Entry<Integer, HashMap<Integer, List<Entity>>> mapEN = mapIT.next();
			
			GL30.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, mapEN.getKey());
			
			Iterator<Entry<Integer, List<Entity>>> inIT = mapEN.getValue().entrySet().iterator();
			while (inIT.hasNext()) {
				Entry<Integer, List<Entity>> inEN = inIT.next();
				List<Entity> el = inEN.getValue();
				
				
				atlasShader.loadTextureID(inEN.getKey());
				
				for (int i = 0; i < el.size(); i++) {
					Entity e = el.get(i);
					if (!e.isEnabled())
						continue;
					atlasShader.loadTranslationMatrix(Maths.createTransformationMatrix(e.x(), e.y(), e.z(), e.getRotation(), e.getWidth(), e.getHeight()));
					// maybe use instance rendering?
					GL30.glDrawElements(GL30.GL_TRIANGLES, vao.getVertexCount(), GL30.GL_UNSIGNED_INT, 0);
				}
			}
		}
		
		atlasShader.stop();
		//normalShader.start();
		
		//normalShader.loadViewMatrix(viewmatrix);
		
		GL30.glDisableVertexAttribArray(0);
		GL30.glDisableVertexAttribArray(1);
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
		} else {
			
		}
	}
	
	public static void deleteEntity(Entity e) {
		if (e.getIsUsingAtlas()) {
			
		} else {
			
		}
	}
	
	public static void renderEntity(Entity e) {
		if (e.getIsUsingAtlas()) {
			
		} else {
			
		}
	}
	
	public static int getNumberOfEntitesInWorld() {
		int e = 0;
		Iterator<Entry<Integer, HashMap<Integer, List<Entity>>>> mapIT = batchAtlasMap.entrySet().iterator();
		while (mapIT.hasNext()) {
			Entry<Integer, HashMap<Integer, List<Entity>>> mapEN = mapIT.next();
			Iterator<Entry<Integer, List<Entity>>> inIT = mapEN.getValue().entrySet().iterator();
			while (inIT.hasNext()) {
				inIT.next();
				e++;
			}
		}
		return e;
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

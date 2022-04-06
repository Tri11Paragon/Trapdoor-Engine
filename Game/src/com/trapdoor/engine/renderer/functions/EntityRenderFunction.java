package com.trapdoor.engine.renderer.functions;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL44;
import org.lwjgl.system.MemoryUtil;

import com.trapdoor.engine.VAOLoader;
import com.trapdoor.engine.camera.Camera;
import com.trapdoor.engine.datatypes.lighting.ExtensibleLightingArray;
import com.trapdoor.engine.datatypes.lighting.Light;
import com.trapdoor.engine.datatypes.ogl.assimp.Material;
import com.trapdoor.engine.datatypes.ogl.assimp.Mesh;
import com.trapdoor.engine.datatypes.ogl.assimp.Model;
import com.trapdoor.engine.datatypes.ogl.obj.VAO;
import com.trapdoor.engine.registry.GameRegistry;
import com.trapdoor.engine.renderer.MaterialPassShader;
import com.trapdoor.engine.renderer.ShaderProgram;
import com.trapdoor.engine.tools.math.Maths;
import com.trapdoor.engine.world.entities.Entity;
import com.trapdoor.engine.world.entities.components.Transform;

/**
 * @author laptop
 * @date Mar. 16, 2022
 * 
 */
public class EntityRenderFunction extends RenderFunction {

	private static int count = 0;
	private static int lastCount = 0;
	
	public static final int MAX_INSTANCING_ELEMENTS = 10000;
	public static final int DATA_SIZE_FLOAT = 16 + 3 + 3 + 2;
	public static final int DATA_SIZE_BYTES = DATA_SIZE_FLOAT * 4;
	
	private static FloatBuffer dataStorage;
	public static int vbo;
	
	public EntityRenderFunction(ShaderProgram program, ExtensibleLightingArray frameLights) {
		super(program, frameLights);
		if (vbo != 0)
			return;
		vbo = VAOLoader.createEmptyVBO(MAX_INSTANCING_ELEMENTS * DATA_SIZE_BYTES);
		dataStorage = MemoryUtil.memAllocFloat(MAX_INSTANCING_ELEMENTS * DATA_SIZE_FLOAT);
		GL44.glMapBuffer(GL33.GL_ARRAY_BUFFER, GL33.GL_WRITE_ONLY, MAX_INSTANCING_ELEMENTS * DATA_SIZE_BYTES, null);
	}

	private static final Vector3f nodiffuse = new Vector3f(-1);
	
	@Override
	public void doRender(Model m, Entity[] ents, Camera c) {
		MaterialPassShader shader = (MaterialPassShader) program;
		Mesh[] meshes = m.getMeshes();
		// don't want to add all lights for each submesh
		boolean first = true;
		for (int i = 0; i < meshes.length; i++) {
			VAO mod = meshes[i].getVAO();
			
			if (mod == null)
				continue;
			
			Material mat = meshes[i].getMaterial();
			
			if (mat.getDiffuseTexture() == null)
				continue;
			
			GL30.glBindVertexArray(mod.getVaoID());
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL20.glEnableVertexAttribArray(2);
			GL20.glEnableVertexAttribArray(3);
			//GL20.glEnableVertexAttribArray(4);
			
			GL13.glActiveTexture(GL13.GL_TEXTURE0);	
			if (mat.getDiffuseTexture() == GameRegistry.getErrorMaterial().getDiffuseTexture())
				shader.loadVector("diffuseValue", mat.getDiffuse());
			else {
				shader.loadVector("diffuseValue", nodiffuse);
			}
			
			shader.loadFloat("specAmount", mat.getSpecular().y);
			//int flag = 0;
			//if (mat.isUsingSpecialMaterial())
			//	flag |= 0b1;
//			if (mat.isUsingNormalMap())
//				flag |= 0b10;
//			if (mat.isUsingSpecMap())
//				flag |= 0b100;
			
			//flag = flag << 31;
			int pos = GameRegistry.getTextureBaseOffset(mat.getDiffuseTexturePath());
			//pos = (pos << 1) >> 1;
			//flag |= pos;
			
			shader.loadFloat("flags", pos);
			
			for (int j = 0; j < ents.length; j++) {
				Entity entity = ents[j];
				
				Transform t = entity.getComponent(Transform.class);
				
				ArrayList<Light> lights = entity.getLights();
				if (first && lights.size() > 0)
					frameLights.add(lights, entity);
				
				if (!checkInFrustum(c, meshes[i].getBoundingBox().translate(t.getX(), t.getY(), t.getZ())))
					continue;
				
				shader.loadMatrix("transformMatrix", Maths.createTransformationMatrix(t));
				
				GL11.glDrawElements(GL11.GL_TRIANGLES, mod.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
				count++;
			}
			
			first = false;
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			GL20.glDisableVertexAttribArray(2);
			GL20.glDisableVertexAttribArray(3);
			//GL20.glDisableVertexAttribArray(4);
			GL30.glBindVertexArray(0);
		}
	}
	
	@Override
	public void render(Model m, ArrayList<Entity> lis, Camera c) {
		doRender(m, sortEntities(lis), c);
	}
	
	public void cleanup() {
		GL44.glUnmapBuffer(GL44.GL_ARRAY_BUFFER);
		MemoryUtil.memFree(dataStorage);
	}
	
	public static int getCount() {
		return lastCount;
	}
	
	public static void reset() {
		lastCount = count;
		count = 0;
	}
	
}

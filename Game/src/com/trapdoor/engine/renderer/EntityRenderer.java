package com.trapdoor.engine.renderer;

import java.util.ArrayList;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

import com.trapdoor.engine.datatypes.lighting.Light;
import com.trapdoor.engine.datatypes.ogl.assimp.Material;
import com.trapdoor.engine.datatypes.ogl.assimp.Mesh;
import com.trapdoor.engine.datatypes.ogl.assimp.Model;
import com.trapdoor.engine.datatypes.ogl.obj.VAO;
import com.trapdoor.engine.registry.GameRegistry;
import com.trapdoor.engine.renderer.shadows.ShadowRenderer;
import com.trapdoor.engine.renderer.shadows.ShadowShader;
import com.trapdoor.engine.tools.math.Maths;
import com.trapdoor.engine.world.entities.Entity;
import com.trapdoor.engine.world.entities.components.Transform;

/**
 * @author laptop
 * @date Nov. 21, 2021
 * 
 */
public class EntityRenderer {
	
	private static final Vector3f nodiffuse = new Vector3f(-1);
	
	public void renderChunk(DeferredRenderer renderer, Model m, ArrayList<Entity> lis) {
		DeferredFirstPassShader shader = renderer.getShader();
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
			GL20.glEnableVertexAttribArray(4);
			
			GL13.glActiveTexture(GL13.GL_TEXTURE0);	
			if (mat.getDiffuseTexture() == GameRegistry.getErrorMaterial().getDiffuseTexture())
				shader.loadDiffuseAmount(mat.getDiffuse());
			else {
				shader.loadDiffuseAmount(nodiffuse);
			}
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mat.getDiffuseTexture().getID());
			GL13.glActiveTexture(GL13.GL_TEXTURE1);	
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mat.getNormalTexture().getID());
			GL13.glActiveTexture(GL13.GL_TEXTURE2);	
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mat.getDisplacementTexture().getID());
			GL13.glActiveTexture(GL13.GL_TEXTURE3);	
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mat.getSpecularTexture().getID());
			GL13.glActiveTexture(GL13.GL_TEXTURE4);	
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mat.getAmbientOcclusionTexture().getID());
			
			shader.loadSpecAmount(mat.getSpecular().y);
			
			for (int j = 0; j < lis.size(); j++) {
				Entity entity = lis.get(j);
				shader.loadTranslationMatrix(Maths.createTransformationMatrix((Transform)entity.getComponent(Transform.class)));
				
				ArrayList<Light> lights = entity.getLights();
				if (first && lights.size() > 0)
					renderer.addLightingArray(lights, entity);
				
				GL11.glDrawElements(GL11.GL_TRIANGLES, mod.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			
			first = false;
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			GL20.glDisableVertexAttribArray(2);
			GL20.glDisableVertexAttribArray(3);
			GL20.glDisableVertexAttribArray(4);
			GL30.glBindVertexArray(0);
		}
	}
	
	public void renderChunkDepth(DepthPassRenderer renderer, Model m, ArrayList<Entity> lis) {
		DepthPassShader shader = renderer.getShader();
		Mesh[] meshes = m.getMeshes();
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
			
			GL13.glActiveTexture(GL13.GL_TEXTURE0);	
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mat.getDiffuseTexture().getID());
			
			for (int j = 0; j < lis.size(); j++) {
				Entity entity = lis.get(j);
				shader.loadMatrix(ShaderProgram.std_TRANSFORM_MATRIX, Maths.createTransformationMatrix((Transform)entity.getComponent(Transform.class)));
				
				GL11.glDrawElements(GL11.GL_TRIANGLES, mod.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			GL30.glBindVertexArray(0);
		}
	}
	
	public void renderShadow(ShadowRenderer renderer, Model m, ArrayList<Entity> lis) {
		ShadowShader shader = renderer.getShader();
		Mesh[] meshes = m.getMeshes();
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
			
			GL33.glActiveTexture(GL33.GL_TEXTURE0);
			GL33.glBindTexture(GL33.GL_TEXTURE_2D, mat.getDiffuseTexture().getID());
			
			for (int j = 0; j < lis.size(); j++) {
				Entity entity = lis.get(j);
				shader.loadTranslationMatrix(Maths.createTransformationMatrix(entity.getComponent(Transform.class)));
				
				GL11.glDrawElements(GL11.GL_TRIANGLES, mod.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			GL20.glDisableVertexAttribArray(2);
			GL30.glBindVertexArray(0);
		}
	}
	
}

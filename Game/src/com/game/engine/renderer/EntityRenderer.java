package com.game.engine.renderer;

import java.util.ArrayList;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.game.engine.datatypes.lighting.Light;
import com.game.engine.datatypes.ogl.assimp.Material;
import com.game.engine.datatypes.ogl.assimp.Mesh;
import com.game.engine.datatypes.ogl.assimp.Model;
import com.game.engine.datatypes.ogl.obj.VAO;
import com.game.engine.shaders.DeferredFirstPassShader;
import com.game.engine.tools.math.Maths;
import com.game.engine.world.entities.Entity;

/**
 * @author laptop
 * @date Nov. 21, 2021
 * 
 */
public class EntityRenderer {
	
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
			
			GL13.glActiveTexture(GL13.GL_TEXTURE0);	
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mat.getDiffuseTexture().getID());
			GL13.glActiveTexture(GL13.GL_TEXTURE1);	
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mat.getNormalTexture().getID());
			GL13.glActiveTexture(GL13.GL_TEXTURE2);	
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mat.getDisplacementTexture().getID());
			GL13.glActiveTexture(GL13.GL_TEXTURE3);	
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mat.getSpecularTexture().getID());
			GL13.glActiveTexture(GL13.GL_TEXTURE4);	
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mat.getAmbientOcclusionTexture().getID());
			
			shader.loadSpecAmount(mat.getColorInformation().y);
			
			for (int j = 0; j < lis.size(); j++) {
				Entity entity = lis.get(j);
				shader.loadTranslationMatrix(Maths.createTransformationMatrix(
						entity.getX(), entity.getY(), entity.getZ(), 
						entity.getRotationMatrix(), 
						entity.getSx(), entity.getSy(), entity.getSz()));
				
				ArrayList<Light> lights = entity.getLights();
				if (first && lights.size() > 0)
					renderer.addLightingArray(lights, entity);
				
				GL11.glDrawElements(GL11.GL_TRIANGLES, mod.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			
			first = false;
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			GL20.glDisableVertexAttribArray(2);
			GL30.glBindVertexArray(0);
		}
	}
	
}

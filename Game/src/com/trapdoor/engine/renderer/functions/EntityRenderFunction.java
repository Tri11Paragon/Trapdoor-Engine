package com.trapdoor.engine.renderer.functions;

import java.util.ArrayList;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

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

	public EntityRenderFunction(ShaderProgram program, ExtensibleLightingArray frameLights) {
		super(program, frameLights);
	}

	private static final Vector3f nodiffuse = new Vector3f(-1);
	
	@Override
	public void render(Model m, ArrayList<Entity> lis) {
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
			GL20.glEnableVertexAttribArray(4);
			
			GL13.glActiveTexture(GL13.GL_TEXTURE0);	
			if (mat.getDiffuseTexture() == GameRegistry.getErrorMaterial().getDiffuseTexture())
				shader.loadVector("diffuseValue", mat.getDiffuse());
			else {
				shader.loadVector("diffuseValue", nodiffuse);
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
			
			shader.loadFloat("specAmount", mat.getSpecular().y);
			
			for (int j = 0; j < lis.size(); j++) {
				Entity entity = lis.get(j);
				shader.loadMatrix("transformMatrix", Maths.createTransformationMatrix((Transform)entity.getComponent(Transform.class)));
				
				ArrayList<Light> lights = entity.getLights();
				if (first && lights.size() > 0)
					frameLights.add(lights, entity);
				
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
	
}

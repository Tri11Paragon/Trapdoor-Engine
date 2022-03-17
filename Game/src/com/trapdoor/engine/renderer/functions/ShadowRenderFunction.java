package com.trapdoor.engine.renderer.functions;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

import com.trapdoor.engine.datatypes.ogl.assimp.Material;
import com.trapdoor.engine.datatypes.ogl.assimp.Mesh;
import com.trapdoor.engine.datatypes.ogl.assimp.Model;
import com.trapdoor.engine.datatypes.ogl.obj.VAO;
import com.trapdoor.engine.renderer.ShaderProgram;
import com.trapdoor.engine.renderer.shadows.ShadowShader;
import com.trapdoor.engine.tools.math.Maths;
import com.trapdoor.engine.world.entities.Entity;
import com.trapdoor.engine.world.entities.components.Transform;

/**
 * @author brett
 * @date Mar. 16, 2022
 * 
 */
public class ShadowRenderFunction extends RenderFunction {

	public ShadowRenderFunction(ShaderProgram program) {
		super(program, null);
	}

	@Override
	public void render(Model m, ArrayList<Entity> lis) {
		ShadowShader shader = (ShadowShader) program;
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
				shader.loadMatrix("transformMatrix", Maths.createTransformationMatrix(entity.getComponent(Transform.class)));
				
				GL11.glDrawElements(GL11.GL_TRIANGLES, mod.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			}
			
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			GL20.glDisableVertexAttribArray(2);
			GL30.glBindVertexArray(0);
		}
	}

}

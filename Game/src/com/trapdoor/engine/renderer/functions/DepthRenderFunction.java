package com.trapdoor.engine.renderer.functions;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.trapdoor.engine.camera.Camera;
import com.trapdoor.engine.datatypes.ogl.assimp.Material;
import com.trapdoor.engine.datatypes.ogl.assimp.Mesh;
import com.trapdoor.engine.datatypes.ogl.assimp.Model;
import com.trapdoor.engine.datatypes.ogl.obj.VAO;
import com.trapdoor.engine.registry.GameRegistry;
import com.trapdoor.engine.renderer.DepthPassShader;
import com.trapdoor.engine.renderer.ShaderProgram;
import com.trapdoor.engine.tools.math.Maths;
import com.trapdoor.engine.world.entities.Entity;
import com.trapdoor.engine.world.entities.components.Transform;

/**
 * @author laptop
 * @date Mar. 16, 2022
 * 
 */
public class DepthRenderFunction extends RenderFunction {

	public DepthRenderFunction(ShaderProgram program) {
		super(program, null);
	}

	@Override
	public void doRender(Model m, Entity[] ents, Camera c) {
		DepthPassShader shader = (DepthPassShader) program;
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
			
			int pos = GameRegistry.getTextureBaseOffset(mat.getDiffuseTexturePath());
			
			shader.loadFloat("flags", pos);
			
			for (int j = 0; j < ents.length; j++) {
				Entity entity = ents[j];
				
				try {
					Transform t = entity.getComponent(Transform.class);
					
					if (!checkInFrustum(c, meshes[i].getBoundingBox().translate(t.getX(), t.getY(), t.getZ())))
						continue;
					
					shader.loadMatrix(ShaderProgram.std_TRANSFORM_MATRIX, Maths.createTransformationMatrix(t));
					
					GL11.glDrawElements(GL11.GL_TRIANGLES, mod.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
				} catch (Exception e) {}
			}
			
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			GL30.glBindVertexArray(0);
		}
	}
	
	@Override
	public void render(Model m, ArrayList<Entity> lis, Camera c) {
		doRender(m, sortEntities(lis), c);
	}

}

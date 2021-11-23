package com.game.engine.renderer;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.game.engine.ProjectionMatrix;
import com.game.engine.camera.ICamera;
import com.game.engine.datatypes.ogl.ModelVAO;
import com.game.engine.shaders.EntityShader;
import com.game.engine.tools.math.Maths;
import com.game.engine.world.Entity;

/**
 * @author laptop
 * @date Nov. 21, 2021
 * 
 */
public class EntityRenderer {
	
	private EntityShader shader;
	public List<Entity> ents = new ArrayList<Entity>();
	private ICamera c;
	
	public EntityRenderer(ICamera c) {
		this.shader = new EntityShader("entity.vs", "entity.fs");
		this.shader.start();
		this.shader.loadProjectionMatrix(ProjectionMatrix.projectionMatrix);
		this.shader.stop();
		this.c = c;
		
	}
	
	public void render() {
		shader.start();
		shader.loadViewMatrix(Maths.createViewMatrix(c));
		GL13.glActiveTexture(GL13.GL_TEXTURE0);	
		
		for (Entity entity : ents) {
			entity.update();
			ModelVAO mod = entity.getModel();
			GL30.glBindVertexArray(mod.getVaoID());
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL20.glEnableVertexAttribArray(2);
			
			shader.loadTranslationMatrix(Maths.createTransformationMatrix(
					entity.getX(), entity.getY(), entity.getZ(), 
					entity.getYaw(), entity.getPitch(), entity.getRoll(), 
					entity.getSx(), entity.getSy(), entity.getSz()));
			
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, entity.getTexture().getID());
			GL11.glDrawElements(GL11.GL_TRIANGLES, mod.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
			
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			GL20.glDisableVertexAttribArray(2);
			GL30.glBindVertexArray(0);
		}
		shader.stop();
	}
	
}

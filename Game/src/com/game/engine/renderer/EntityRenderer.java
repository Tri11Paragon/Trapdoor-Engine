package com.game.engine.renderer;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.game.engine.ProjectionMatrix;
import com.game.engine.camera.ICamera;
import com.game.engine.datatypes.ogl.obj.VAO;
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
	private List<Entity> ents;
	private ICamera c;
	
	public EntityRenderer(ICamera c, ArrayList<Entity> ents) {
		this.shader = new EntityShader("entity.vs", "entity.fs");
		this.shader.start();
		this.shader.loadProjectionMatrix(ProjectionMatrix.projectionMatrix);
		this.shader.stop();
		this.c = c;
		this.ents = ents;
	}
	
	public void render() {
		shader.start();
		shader.loadViewMatrix(Maths.createViewMatrix(c));
		GL13.glActiveTexture(GL13.GL_TEXTURE0);	
		
		for (Entity entity : ents) {
			entity.update();
			VAO mod = entity.getModel();
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

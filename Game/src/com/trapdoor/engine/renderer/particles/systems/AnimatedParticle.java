package com.trapdoor.engine.renderer.particles.systems;

import org.joml.Vector3f;

import com.trapdoor.engine.camera.Camera;
import com.trapdoor.engine.registry.GameRegistry;
import com.trapdoor.engine.renderer.particles.Particle;
import com.trapdoor.engine.world.World;

/**
 * @author laptop
 * @date Mar. 6, 2022
 * 
 */
public class AnimatedParticle extends Particle {

	private String[] textures;
	private float current;
	
	public AnimatedParticle(String[] textures, Vector3f position, Vector3f velocity, float gravityEffect, float lifeLength, float rotation, float scale) {
		super(position, velocity, gravityEffect, lifeLength, rotation, scale);
		this.textures = textures;
	}
	
	@Override
	public boolean update(World world, Camera camera) {
		current = (textures.length * getElapsedPercent());
		
		setCurrentTexture(GameRegistry.getParticleTexture(textures[(int) (current % textures.length)]));
		setNextTexture(GameRegistry.getParticleTexture(textures[(int) ((current < textures.length - 1 ? current + 1 : current) % textures.length)]));
		return super.update(world, camera);
	}
	
	@Override
	protected void updateTextureBlend() {
		this.blend = current % 1; 
	}

}

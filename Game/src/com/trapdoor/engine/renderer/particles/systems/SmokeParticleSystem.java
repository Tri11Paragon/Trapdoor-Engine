package com.trapdoor.engine.renderer.particles.systems;

import java.io.File;

import org.joml.Vector3f;

import com.trapdoor.engine.renderer.particles.Particle;
import com.trapdoor.engine.renderer.particles.ParticleSystem;

/**
 * @author laptop
 * @date Mar. 6, 2022
 * 
 */
public class SmokeParticleSystem extends ParticleSystem {

	private String[] textures;
	
	public SmokeParticleSystem(String smokeFolder, float pps, float speed, float gravityComplient, float lifeLength, float scale) {
		super(pps, speed, gravityComplient, lifeLength, scale);
		File[] files = new File(smokeFolder).listFiles();
		textures = new String[files.length];
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory())
				continue;
			textures[i] = f.getPath();
		}
	}
	
	@Override
	protected Particle createParticle(Vector3f center, Vector3f velocity, float gravity, float life, float rotation, float scale) {
		return new SmokeParticle(textures, center, velocity, gravity, life, rotation, scale);
	}
	
}

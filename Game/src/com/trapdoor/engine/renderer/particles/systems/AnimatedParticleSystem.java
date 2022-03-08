package com.trapdoor.engine.renderer.particles.systems;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import com.trapdoor.engine.datatypes.Tuple;
import com.trapdoor.engine.renderer.particles.Particle;
import com.trapdoor.engine.renderer.particles.ParticleSystem;

/**
 * @author laptop
 * @date Mar. 6, 2022
 * 
 */
public class AnimatedParticleSystem extends ParticleSystem {

	private String[] textures;
	private ArrayList<Tuple<String, Integer>> textureUn;
	
	public AnimatedParticleSystem(String smokeFolder, float pps, float speed, float gravityComplient, float lifeLength, float scale) {
		super(pps, speed, gravityComplient, lifeLength, scale);
		File[] files = new File(smokeFolder).listFiles();
		textures = new String[files.length];
		textureUn = new ArrayList<Tuple<String,Integer>>(files.length);
		
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory())
				continue;
			String name = f.getPath();
			String numbers = name.replaceAll("[^0-9]", "");
			textureUn.add(new Tuple<String, Integer>(name, Integer.parseInt(numbers)));
		}
		sortLowToHigh(textureUn);
		for (int i = 0; i < textureUn.size(); i++) {
			textures[i] = textureUn.get(i).getX();
		}
	}
	
	@Override
	protected Particle createParticle(Vector3f center, Vector3f velocity, float gravity, float life, float rotation, float scale) {
		return new AnimatedParticle(textures, center, velocity, gravity, life, rotation, scale);
	}
	
	public static void sortLowToHigh(List<Tuple<String, Integer>> list) {
		for (int i = 1; i < list.size(); i++) {
			Tuple<String, Integer> item = list.get(i);
			if (item.getY() < list.get(i - 1).getY()) {
				sortUpHighToLow(list, i);
			}
		}
	}

	private static void sortUpHighToLow(List<Tuple<String, Integer>> list, int i) {
		Tuple<String, Integer> item = list.get(i);
		int attemptPos = i - 1;
		while (attemptPos != 0 && list.get(attemptPos - 1).getY() > item.getY()) {
			attemptPos--;
		}
		list.remove(i);
		list.add(attemptPos, item);
	}
	
}

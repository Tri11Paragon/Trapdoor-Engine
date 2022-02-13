package com.trapdoor.engine.world.sound;

import java.util.HashMap;
import java.util.Random;

import com.trapdoor.engine.datatypes.sound.SoundFile;
import com.trapdoor.engine.datatypes.sound.SoundInfo;
import com.trapdoor.engine.registry.GameRegistry;
import com.trapdoor.engine.tools.Logging;
import com.trapdoor.engine.tools.SettingsLoader;
import com.trapdoor.engine.world.entities.components.SoundSource;

/**
 * @author brett
 * @date Feb. 12, 2022
 * 
 */
public class SoundSystem {
	
	private static HashMap<SoundSystemType, HashMap<String, SoundInfo>> sounds = new HashMap<SoundSystemType, HashMap<String, SoundInfo>>();
	private static SoundSource musicPlayer;
	private static Random randomness;
	private static long lastPlayed = 0;
	
	public static void init() {
		sounds.put(SoundSystemType.AMBIENT, new HashMap<String, SoundInfo>());
		sounds.put(SoundSystemType.BACKGROUND, new HashMap<String, SoundInfo>());
		sounds.put(SoundSystemType.TRIGGERED, new HashMap<String, SoundInfo>());
		randomness = new Random();
		GameRegistry.registerSoundSource((musicPlayer = new SoundSource(false, false)));
	}
	
	public static void update() {
		// check every 5 minutes if we should play a sound.
		if (System.currentTimeMillis() - lastPlayed > 300 * 1000) {
			// 25% chance to play a sound
			if (randomChance(0.25)) {
				// play one of the random sound infos
				double size = sounds.get(SoundSystemType.BACKGROUND).values().size();
				sounds.get(SoundSystemType.BACKGROUND).values().forEach((info) -> {
					if (randomChance(info.chance / size)) {
						trigger(info);
					}
				});
			}
			lastPlayed = System.currentTimeMillis();
		}
	}
	
	public static boolean randomChance(double chance) {
		double scaled = chance * 10000;
		return scaled > randomness.nextInt(10000);
	}
	
	/**
	 * registers a sound system with the engine.
	 * @param id String name to identifiy the created sound system in future commands
	 * @param type see {@link #SoundSystemType} for details on types
	 * @param info sound files with chance of playing (if music type) to be used with this sound system.
	 */
	public static void registerSoundSystem(String id, SoundSystemType type, SoundInfo info) {
		sounds.get(type).put(id, info);
	}
	
	public static void registerSoundSystem(String id, SoundSystemType type, int chance, SoundFile... soundFiles) {
		sounds.get(type).put(id, new SoundInfo(chance, soundFiles));
	}
	
	public static void registerSoundSystem(String id, SoundSystemType type, SoundFile... soundFiles) {
		sounds.get(type).put(id, new SoundInfo(1.0, soundFiles));
	}
	
	/**
	 * triggers a random sound to be played from the associated sound system. If (any) sound is already being played it will not be triggered
	 * @param type the type of SoundSystem you are triggering. <br>&emsp;&emsp;&emsp; Note: you are allowed to trigger non TRIGGERED type SoundSystems.
	 * @param id the String id associated with the triggerable sound system
	 */
	public static void trigger(SoundSystemType type, String id) {
		trigger(sounds.get(type).get(id));
	}
	
	private static void trigger(SoundInfo info) {
		SoundFile[] files = info.files;
		if (musicPlayer.isPlaying())
			return;
		if (files == null)
			return;
		SoundFile f = files[randomness.nextInt(files.length)];
		Logging.logger.trace("Now playing: " + f);
		musicPlayer.setSound(f);
		musicPlayer.setGain(SettingsLoader.MUSIC_GAIN);
		musicPlayer.play();
	}
	
}

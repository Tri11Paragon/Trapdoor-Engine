package com.trapdoor.engine.datatypes.sound;

/**
 * @author brett
 * @date Feb. 12, 2022
 * 
 */
public class SoundInfo {
	
	public final  SoundFile[] files;
	public final double chance;
	// TODO: locations and other qualities required to get sound
	// not adding them now since this is just for music
	// but we can totally expand this to include more than just a chance trigger type
	
	/**(
	 * @param files files to be played when the event is triggered
	 * @param chance the chance (% in decimal) of the event occurring
	 */
	public SoundInfo(double chance, SoundFile... files) {
		this.files = files;
		this.chance = chance;
	}
	
}

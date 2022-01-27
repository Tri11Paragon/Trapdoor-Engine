package com.trapdoor.engine.datatypes;

import java.io.File;
import java.util.ArrayList;

/**
 * @author brett
 * @date Oct. 18, 2021
 * 
 */
public class Atlas {

	public final int width;
	public final int height;
	public final ArrayList<File> textures;
	
	public Atlas(ArrayList<File> textures, int width, int height) {
		this.textures = textures;
		this.width = width;
		this.height = height;
	}
	
}

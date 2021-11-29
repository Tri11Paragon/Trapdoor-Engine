package com.game.engine.threading;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.game.Main;
import com.game.engine.TextureLoader;
import com.game.engine.datatypes.ogl.Texture;
import com.game.engine.datatypes.ogl.TextureData;
import com.game.engine.tools.Logger;

/**
 * @author brett
 * @date Nov. 28, 2021
 * The purpose of the game registry is to provide a single location for model loading and texture loading
 * so that model and texture loading can be done async at startup instead of being loaded in a single thread at start, or worse
 * during runtime. The game registry also prevents loading of single assets multiple times.
 */
public class GameRegistry {

	private static HashMap<String, String> allowedFormats = new HashMap<String, String>();
	
	public static Map<String, TextureData> textureDatas = Collections.synchronizedMap(new HashMap<String, TextureData>());
	
	public static Map<String, Texture> textures = Collections.synchronizedMap(new HashMap<String, Texture>());
	public static Map<String, Texture> texturesMaterials = Collections.synchronizedMap(new HashMap<String, Texture>());
	public static Map<String, Integer> textureAtlas = Collections.synchronizedMap(new HashMap<String, Integer>());
	public static Map<String, Integer> textureInteralAtlas = Collections.synchronizedMap(new HashMap<String, Integer>());
	
	public static void registerMaterialTexture(String file) {
		// I wonder if this is bad?
		// like accessing memory from threads which is local
		// tbh I don't care
		Threading.execute(new DualExecution(() -> {
			String fd = file;
			// we already loaded the file
			if (GameRegistry.textureDatas.get(fd) != null)
				return;
			if (Main.verbose)
				Logger.writeln("Loading texture: " + fd);
			GameRegistry.textureDatas.put(fd, TextureLoader.decodeTextureToSize(fd, false, true, 0, 0));
		}, () -> {
			String fd = file;
			if (Main.verbose)
				Logger.writeln("Loaded texture: " + fd);
			Texture t = TextureLoader.loadTextureI(fd, GameRegistry.textureDatas.get(fd), TextureLoader.TEXTURE_LOD, GL11.GL_LINEAR, TextureLoader.MINMAG_MIPMAP_FILTER);
			GameRegistry.textures.put(fd, t);
			GameRegistry.texturesMaterials.put(fd, t);
		}));
	}
	
	/**
	 * loads all the textures in a folder and its subfolders
	 */
	public static void registerMaterialTextureFolder(String fold) {
		// fucking cancer way of doing this
		if (allowedFormats.size() == 0) {
			allowedFormats.put("png", "");
			allowedFormats.put("jpg", "");
			allowedFormats.put("jpeg", "");
			allowedFormats.put("bmp", "");
			allowedFormats.put("gif", "");
			allowedFormats.put("tga", "");
		}
		Threading.execute(() -> {
			File folder = new File(fold);
			File[] files = folder.listFiles();
			ArrayList<File> textures = new ArrayList<File>();
			int i = 0;
			while (i < files.length) {
				File f = files[i];
				if (f.isDirectory())
					files = combineArrays(files, f.listFiles());
				else
					if(allowedFormats.get(f.getName().split("\\.")[1]) != null)
						textures.add(f.getAbsoluteFile());
				i++;
			}
			for (i = 0; i < textures.size(); i++) {
				File f = textures.get(i);
				String path = f.getAbsolutePath();
				String[] haha = path.split("resources\\/textures\\/");
				GameRegistry.registerMaterialTexture("resources/textures/" + haha[1]);
			}
		});
	}
	
	/**
	 * Efficiently combines two input arrays, a and b, returning the result.
	 */
	private static File[] combineArrays(File[] a, File[] b) {
		File[] r = new File[a.length + b.length];
		for (int i = 0; i < a.length; i++)
			r[i] = a[i];
		System.arraycopy(b, 0, r, a.length, b.length);
		return r;
	}
	
}

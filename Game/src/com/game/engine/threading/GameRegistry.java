package com.game.engine.threading;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.game.Main;
import com.game.engine.TextureLoader;
import com.game.engine.datatypes.ogl.Texture;
import com.game.engine.datatypes.ogl.TextureData;
import com.game.engine.display.LoadingScreenDisplay;
import com.game.engine.tools.Logger;

/**
 * @author brett
 * @date Nov. 28, 2021
 * The purpose of the game registry is to provide a single location for model loading and texture loading
 * so that model and texture loading can be done async at startup instead of being loaded in a single thread at start, or worse
 * during runtime. The game registry also prevents loading of single assets multiple times.
 */
public class GameRegistry {

	private static Texture errorTexture;
	
	private static HashMap<String, String> allowedFormats = new HashMap<String, String>();
	
	private static Map<String, TextureData> textureDatas = Collections.synchronizedMap(new HashMap<String, TextureData>());
	
	private static Map<String, Texture> textures = Collections.synchronizedMap(new HashMap<String, Texture>());
	private static Map<String, Texture> texturesMaterials = Collections.synchronizedMap(new HashMap<String, Texture>());
	
	private static Map<String, Integer> textureAtlas = Collections.synchronizedMap(new HashMap<String, Integer>());
	private static Map<String, Integer> textureInteralAtlas = Collections.synchronizedMap(new HashMap<String, Integer>());
	
	public static void init() {
		// TODO: this
		//errorTexture = TextureLoader.loadTextureI("resources/textures/error/error3.png", 
		//		new TextureData(ErrorImage.getBuffer(), ErrorImage.width, ErrorImage.height, 3, "resources/textures/error/error3.png"), 
		//		TextureLoader.TEXTURE_LOD, GL11.GL_LINEAR, TextureLoader.MINMAG_MIPMAP_FILTER);
		errorTexture = TextureLoader.loadTexture("error/error3.png");
	}
	
	public static void registerMaterialTexture(String file) {
		LoadingScreenDisplay.max();
		// I wonder if this is bad?
		// like accessing memory from threads which is local
		// tbh I don't care
		Threading.execute(new DualExecution(() -> {
			String fd = file;
			// we already loaded the file
			if (GameRegistry.textureDatas.get(fd) != null)
				return;
			String rt = "Loading texture: " + fd;
			LoadingScreenDisplay.info.getTextState().setText(rt);
			if (Main.verbose)
				Logger.writeln("Loading texture: " + fd);
			GameRegistry.textureDatas.put(fd, TextureLoader.decodeTextureToSize(fd, false, true, 0, 0));
		}, () -> {
			String fd = file;
			String rt = "Loaded texture: " + fd;
			LoadingScreenDisplay.info.getTextState().setText(rt);
			if (Main.verbose)
				Logger.writeln("Loaded texture: " + fd);
			Texture t = TextureLoader.loadTextureI(fd, GameRegistry.textureDatas.get(fd), TextureLoader.TEXTURE_LOD, GL11.GL_LINEAR, TextureLoader.MINMAG_MIPMAP_FILTER);
			GameRegistry.textures.put(fd, t);
			GameRegistry.texturesMaterials.put(fd, t);
			LoadingScreenDisplay.progress();;
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
	 * Loads the specified folder as a 2d texture array. 
	 * It should be noted that every texture in this folder and its subfolders will be loaded into a single array
	 * @param fold the folder and its subfolders to load
	 */
	public static void reigsterMaterialFolderAsArrays(String fold) {
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
			List<TextureData> datas = Collections.synchronizedList(new ArrayList<TextureData>());
			for (i = 0; i < textures.size(); i++) {
				LoadingScreenDisplay.max();
				File f = textures.get(i);
				String path = f.getAbsolutePath();
				String[] haha = path.split("resources\\/textures\\/");
				
				Threading.execute(() -> {
					String fd = "resources/textures/" + haha[1];
					String rt = "Loading texture: " + fd;
					LoadingScreenDisplay.info.getTextState().setText(rt);
					if (Main.verbose)
						Logger.writeln(rt);
					datas.add(TextureLoader.decodeTextureToSize(fd, false, true, 0, 0));
					LoadingScreenDisplay.progress();
				});
			}
			while (datas.size() != textures.size()) {
				try {
					Thread.sleep(16);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			Threading.addToMains(() -> {
				if (Main.verbose)
					Logger.writeln("Loading texture atlas " + fold);
				LoadingScreenDisplay.info.getTextState().setText("Loading texture atlas " + fold);
				int atlas = TextureLoader.loadSpecialTextureATLAS(datas);
				for (int j = 0; j < datas.size(); j++) {
					String rt = "Loaded texture: " + datas.get(j).getName();
					LoadingScreenDisplay.info.getTextState().setText(rt);
					if (Main.verbose)
						Logger.writeln(rt);
					textureAtlas.put(datas.get(j).getName(), atlas);
					textureInteralAtlas.put(datas.get(j).getName(), j);
				}
			});
		});
	}
	
	/**
	 * @param name path to the texture
	 * @return a singular texture which was loaded into memory
	 */
	public static Texture getTexture(String name) {
		Texture t = textures.get(name);
		return t == null ? errorTexture : t;
	}
	
	/**++
	 * @param path
	 * @return the id of the texture array which this image is a part of
	 */
	public static int getTextureArray(String path) {
		return textureAtlas.get(path);
	}
	
	/**
	 * Take for example you have a texture array loaded as follows: <br>
	 * [0] = {"/resources/textures/a1.png"}; <br>
	 * [1] = {"/resources/textures/penis.png"}; <br>
	 * [2] = {"/resources/textures/gaycats.png"}; <br>
	 * [3] = {"/resources/textures/matterjap.png"}; <br>
	 * [4] = {"/resources/textures/subfolder/superman.png"}; <br>
	 * [5] = {"/resources/textures/yeahboi.png"}; <br>
	 * <br>
	 * if path is "/resources/textures/matterjap.png" this function will return 3
	 * <br>
	 * @param path path to texture
	 * @return the int pos of the texture in the texture array
	 */
	public static int getTexturePosInArray(String path) {
		return textureInteralAtlas.get(path);
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

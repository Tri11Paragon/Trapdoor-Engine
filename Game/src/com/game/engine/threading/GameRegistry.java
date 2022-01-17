package com.game.engine.threading;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import com.game.Main;
import com.game.engine.TextureLoader;
import com.game.engine.VAOLoader;
import com.game.engine.datatypes.ogl.Texture;
import com.game.engine.datatypes.ogl.TextureData;
import com.game.engine.datatypes.ogl.assimp.Material;
import com.game.engine.datatypes.ogl.assimp.Model;
import com.game.engine.display.LoadingScreenDisplay;
import com.game.engine.tools.Logger;
import com.game.engine.tools.ScreenShot;
import com.game.engine.tools.input.InputMaster;
import com.game.engine.tools.models.ModelLoader;
import com.spinyowl.legui.style.font.FontRegistry;

/**
 * @author brett
 * @date Nov. 28, 2021
 * The purpose of the game registry is to provide a single location for model loading and texture loading
 * so that model and texture loading can be done async at startup instead of being loaded in a single thread at start, or worse
 * during runtime. The game registry also prevents loading of single assets multiple times.
 */
public class GameRegistry {

	public static final String DEFAULT_EMPTY_NORMAL_MAP = "resources/textures/error/default_normal.png";
	private static Texture errorTexture;
	private static Texture defaultNormalTexture;
	private static Material errorMaterial;
	private static Model errorModel;
	
	private static final HashMap<String, String> allowedFormats = new HashMap<String, String>();
	//private static final ArrayList<IDisplay> registeredDisplays = new ArrayList<IDisplay>();
	
	/*
	 * Data(s) and Lock(s)
	 */
	private static final Map<String, TextureData> textureDatas = Collections.synchronizedMap(new HashMap<String, TextureData>());
	private static final Map<String, Integer> textureLocks = Collections.synchronizedMap(new HashMap<String, Integer>());
	private static final Map<String, Integer> meshLocks = Collections.synchronizedMap(new HashMap<String, Integer>());
	
	/*
	 * important entity related storage
	 */
	private static final Map<String, Texture> textures = Collections.synchronizedMap(new HashMap<String, Texture>());
	private static final Map<String, Material> materials = Collections.synchronizedMap(new HashMap<String, Material>());
	private static final ArrayList<Material> registeredMaterials = new ArrayList<Material>();
	private static final Map<String, Model> meshes = Collections.synchronizedMap(new HashMap<String, Model>());
	
	/*
	 * texture atlases
	 */
	private static final Map<String, Integer> textureAtlas = Collections.synchronizedMap(new HashMap<String, Integer>());
	private static final Map<String, Integer> textureInteralAtlas = Collections.synchronizedMap(new HashMap<String, Integer>());
	
	public static void init() {
		errorTexture = TextureLoader.loadTexture("error/error3.png");
		defaultNormalTexture = TextureLoader.loadTexture("error/default_normal.png");
		errorMaterial = new Material("resources/textures/error/error3.png", DEFAULT_EMPTY_NORMAL_MAP, new Vector3f());
		
		errorMaterial.setDiffuseTexture(errorTexture);
		errorMaterial.setNormalTexture(defaultNormalTexture);
		
		errorModel = ModelLoader.load("resources/models/error.dae", "resources/textures/");
		VAOLoader.loadToVAO(errorModel);
		
		GameRegistry.materials.put("resources/textures/error/error3.png", errorMaterial);
		GameRegistry.materials.put("error/error3.png", errorMaterial);
		
		InputMaster.registerKeyListener(new ScreenShot());
	}
	
	public static void onLoadingComplete() {
		for (Material m : registeredMaterials) {
			m.loadTexturesFromGameRegistry();
		}
	}
	
	public static Material registerMaterial(String diffusePath, String normalPath, Vector3f colorInformation) {
		Material m = materials.get(diffusePath);
		if (m == null) {
			m = new Material(diffusePath, normalPath, colorInformation);
			materials.put(diffusePath, m);
		}
		registeredMaterials.add(m);
		return m;
	}
	
	public static Material registerMaterial2(String diffusePath, String normalPath, Vector3f colorInformation) {
		Material m = new Material(diffusePath, normalPath, colorInformation);
		registeredMaterials.add(m);
		return m;
	}
	
	public static void registerModel(String file) {
		registerModel(file, "resources/textures");
	}
	
	public static void registerModel(String file, String texturesDir) {
		LoadingScreenDisplay.max();
		
		Threading.execute(new DualExecution(() -> {
			String fd = file;
			// we already loaded the file
			if (GameRegistry.meshes.get(fd) != null || GameRegistry.meshLocks.get(fd) != null) {
				LoadingScreenDisplay.progress();
				return;
			}
			GameRegistry.meshLocks.put(fd, 1);
			
			String textureLocation = texturesDir;
			
			char[] tc = textureLocation.toCharArray();
			if (tc[tc.length-1] == '/')
				textureLocation = textureLocation.substring(0, tc.length-1);
			
			String rt = "Loading model: " + fd;
			LoadingScreenDisplay.info.getTextState().setText(rt);
			if (Main.verbose)
				Logger.writeln(rt);
			
			GameRegistry.meshes.put(fd, ModelLoader.load(fd, "resources/textures/"));
		}, () -> {
			String fd = file;
			String rt = "Loaded model: " + fd;
			LoadingScreenDisplay.info.getTextState().setText(rt);
			if (Main.verbose)
				Logger.writeln(rt);
			
			Model m = GameRegistry.meshes.get(fd);
			VAOLoader.loadToVAO(m);
			
			LoadingScreenDisplay.progress();
		}));
	}
	
	public static void registerTexture(String file) {
		if (!file.contains("."))
			return;
		LoadingScreenDisplay.max();
		// I wonder if this is bad?
		// like accessing memory from threads which is local
		// tbh I don't care
		Threading.execute(new DualExecution(() -> {
			String fd = file;
			// we already loaded the file
			if (GameRegistry.textureDatas.get(fd) != null || GameRegistry.textureLocks.get(fd) != null) {
				LoadingScreenDisplay.progress();
				return;
			}
			GameRegistry.textureLocks.put(fd, 1);
			
			String rt = "Loading texture: " + fd;
			LoadingScreenDisplay.info.getTextState().setText(rt);
			if (Main.verbose)
				Logger.writeln(rt);
			GameRegistry.textureDatas.put(fd, TextureLoader.decodeTextureToSize(fd, false, true, 0, 0));
		}, () -> {
			String fd = file;
			
			String rt = "Loaded texture: " + fd;
			LoadingScreenDisplay.info.getTextState().setText(rt);
			if (Main.verbose)
				Logger.writeln(rt);
			
			Texture t = TextureLoader.loadTextureI(fd,GameRegistry.textureDatas.get(fd), TextureLoader.TEXTURE_LOD, GL11.GL_LINEAR_MIPMAP_LINEAR);
			GameRegistry.textures.put(fd, t);
			LoadingScreenDisplay.progress();
		}));
	}
	
	/**
	 * loads all the textures in a folder and its subfolders
	 */
	@Deprecated
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
				GameRegistry.registerTexture("resources/textures/" + haha[1]);
			}
		});
	}
	
	/**
	 * Loads the specified folder as a 2d texture array. 
	 * It should be noted that every texture in this folder and its subfolders will be loaded into a single array
	 * @param fold the folder and its subfolders to load
	 */
	@Deprecated
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
	 * @param file path to the texture
	 * @return a singular texture which was loaded into memory
	 */
	public static Texture getTexture(String file) {
		Texture t = textures.get(file);
		return t == null ? errorTexture : t;
	}
	
	public static Model getModel(String file) {
		Model m = meshes.get(file);
		return m == null ? errorModel : m;
	}
	
	/**
	 * Tries to the the texture, if none is found it will be waited for.
	 * @param name path to the texture
	 * @return a singular texture which has been loaded into gpu memory.
	 */
	public static Texture getTextureBlocking(String name) {
		Texture t = null;
		while ((t = textures.get(name)) == null) {
			try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
		}
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
	 * @param diffuse diffuse texture path for this material
	 * @return the material associated with the specified diffuse texture
	 */
	public static Material getMaterial(String diffuse) {
		Material m = materials.get(diffuse);
		return m == null ? errorMaterial : m;
	}
	
	public static Material getErrorMaterial() {
		return errorMaterial;
	}
	
	public static void registerFont(String name, String path) {
		FontRegistry.registerFont(name, path);
	}
	
	public static Set<Entry<String, Model>> getModelEntries() {
		return meshes.entrySet();
	}
	
	public static Model getErrorModel() {
		return errorModel;
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
	
	/*public static void registerIDisplay(IDisplay display) {
		registeredDisplays.add(display);
	}
	
	public static void doPreRegister() {
		for (int i = 0; i < registeredDisplays.size(); i++)
			registeredDisplays.get(i).preRegister();
	}*/
	
}

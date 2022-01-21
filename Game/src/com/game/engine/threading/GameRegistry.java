package com.game.engine.threading;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import com.game.engine.TextureLoader;
import com.game.engine.VAOLoader;
import com.game.engine.datatypes.ogl.Texture;
import com.game.engine.datatypes.ogl.TextureData;
import com.game.engine.datatypes.ogl.assimp.Material;
import com.game.engine.datatypes.ogl.assimp.Model;
import com.game.engine.display.LoadingScreenDisplay;
import com.game.engine.tools.Logging;
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
	public static final String DEFAULT_EMPTY_DISPLACEMENT_MAP = "resources/textures/error/default_disp.png";
	public static final String DEFAULT_EMPTY_AO_MAP = "resources/textures/error/default_ao.png";
	public static final String DEFAULT_EMPTY_SPEC_MAP = "resources/textures/error/default_spec.png";
	private static Texture errorTexture;
	private static Texture defaultNormalTexture;
	private static Texture defaultDisplacementTexture;
	private static Texture defaultAOTexture;
	private static Texture defaultSpecTexture;
	private static Material errorMaterial;
	private static Model errorModel;
	
	/**
	 * this is the max number of method callers to be printed during an error
	 */
	private static final int MAX_CALLERS_LOG = 5;
	//private static final HashMap<String, String> allowedFormats = new HashMap<String, String>();
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
		defaultDisplacementTexture = TextureLoader.loadTexture("error/default_disp.png");
		defaultAOTexture = TextureLoader.loadTexture("error/default_ao.png");
		defaultSpecTexture = TextureLoader.loadTexture("error/default_spec.png");
		errorMaterial = new Material("resources/textures/error/error3.png", DEFAULT_EMPTY_NORMAL_MAP, 
				DEFAULT_EMPTY_DISPLACEMENT_MAP, DEFAULT_EMPTY_AO_MAP, DEFAULT_EMPTY_SPEC_MAP, new Vector3f(0.0f, 0.0f, 0.0f));
		
		errorMaterial.setDiffuseTexture(errorTexture);
		errorMaterial.setNormalTexture(defaultNormalTexture);
		errorMaterial.setDisplacementTexture(defaultDisplacementTexture);
		errorMaterial.setAmbientOcclusionTexture(defaultAOTexture);
		errorMaterial.setSpecularTexture(defaultSpecTexture);
		
		GameRegistry.textures.put(DEFAULT_EMPTY_NORMAL_MAP, defaultNormalTexture);
		GameRegistry.textures.put(DEFAULT_EMPTY_DISPLACEMENT_MAP, defaultDisplacementTexture);
		GameRegistry.textures.put(DEFAULT_EMPTY_AO_MAP, defaultAOTexture);
		GameRegistry.textures.put(DEFAULT_EMPTY_SPEC_MAP, defaultSpecTexture);
		
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
	
	public static Material registerMaterial2(Material m) {
		registeredMaterials.add(m);
		return m;
	}
	
	public static void registerModel(String file) {
		registerModel(file, "resources/textures");
	}
	
	public static void registerModel(String file, String texturesDir) {
		LoadingScreenDisplay.max();
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		
		Threading.execute(new DualExecution(() -> {
			try {
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
				if (LoadingScreenDisplay.info != null)
					LoadingScreenDisplay.info.getTextState().setText(rt);
				Logging.logger.debug(rt);
				
				GameRegistry.meshes.put(fd, ModelLoader.load(fd, "resources/textures/"));
			} catch (Exception e) {
				Logging.logger.fatal("Error loading entity (" + file +  ")!", e);
				printFatalMethodCallers(stackTraceElements);
				System.exit(-1);
			}
		}, () -> {
			try {
				String fd = file;
				String rt = "Loaded model: " + fd;
				if (LoadingScreenDisplay.info != null)
					LoadingScreenDisplay.info.getTextState().setText(rt);
				Logging.logger.debug(rt);
				
				Model m = GameRegistry.meshes.get(fd);
				VAOLoader.loadToVAO(m);
			} catch (Exception e) {
				Logging.logger.fatal("Error loading entity VAO (" + file + ")!", e);
				printFatalMethodCallers(stackTraceElements);
				System.exit(-1);
			}
			LoadingScreenDisplay.progress();
		}));
	}
	
	public static void registerTexture(String file) {
		if (!file.contains("."))
			return;
		if (!new File(file).exists()) {
			Logging.logger.error("File: " + file + " does not exist!");
			return;
		}
		LoadingScreenDisplay.max();
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		
		Threading.execute(new DualExecution(() -> {
			try {
				String fd = file;
				// we already loaded the file
				if (GameRegistry.textureDatas.get(fd) != null || GameRegistry.textureLocks.get(fd) != null) {
					LoadingScreenDisplay.progress();
					return;
				}
				GameRegistry.textureLocks.put(fd, 1);
				
				String rt = "Loading texture: " + fd;
				if (LoadingScreenDisplay.info != null)
					LoadingScreenDisplay.info.getTextState().setText(rt);
				Logging.logger.debug(rt);
				
				GameRegistry.textureDatas.put(fd, TextureLoader.decodeTextureToSize(fd, false, true, 0, 0));
			} catch (Exception e) {
				Logging.logger.fatal(e.getMessage(), e);
				Logging.logger.fatal("Tried to load texture " + file + ", didn't work!");
				printFatalMethodCallers(stackTraceElements);
				System.exit(-1);
			}
		}, () -> {
			try {
				String fd = file;
				
				String rt = "Loaded texture: " + fd;
				if (LoadingScreenDisplay.info != null)
					LoadingScreenDisplay.info.getTextState().setText(rt);
				Logging.logger.debug(rt);
				
				Texture t = TextureLoader.loadTextureI(fd,GameRegistry.textureDatas.get(fd), TextureLoader.TEXTURE_LOD, GL11.GL_LINEAR_MIPMAP_LINEAR);
				GameRegistry.textures.put(fd, t);
			} catch (Exception e) {
				Logging.logger.fatal(e.getMessage(), e);
				Logging.logger.fatal("Tried to load texture " + file + " to GPU, didn't work!");
				printFatalMethodCallers(stackTraceElements);
				System.exit(-1);
			}
			LoadingScreenDisplay.progress();
		}));
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
	 * prints the method call stack of the local thread. <br>
	 * Note: it will print MAX_CALLERS_LOG ({@value #MAX_CALLERS_LOG})
	 * or the length of the call stack (whatever is smaller)
	 */
	public static void printFatalMethodCallers() {
		printFatalMethodCallers(getStackTraceElements(), MAX_CALLERS_LOG);
	}
	
	/**
	 * prints the method call stack of the local thread. <br>
	 * Note: it will print MAX_CALLERS_LOG ({@value #MAX_CALLERS_LOG})
	 * or the length of the call stack (whatever is smaller)
	 */
	public static void printErrorMethodCallers() {
		printErrorMethodCallers(getStackTraceElements(), MAX_CALLERS_LOG);
	}
	
	/**
	 * prints the method call stack of the local thread. <br>
	 * Note: it will print supplied <b>max</b> number of traces
	 * or the length of the call stack (whatever is smaller)
	 */
	public static void printFatalMethodCallers(int max) {
		printFatalMethodCallers(getStackTraceElements(), max);
	}
	
	/**
	 * prints the method call stack of the local thread. <br>
	 * Note: it will print supplied <b>max</b> number of traces
	 * or the length of the call stack (whatever is smaller)
	 */
	public static void printErrorMethodCallers(int max) {
		printErrorMethodCallers(getStackTraceElements(), max);
	}
	
	/**
	 * prints the method call stack of the provided stack trace. <br>
	 * Note: it will print MAX_CALLERS_LOG ({@value #MAX_CALLERS_LOG})
	 * or the length of the call stack (whatever is smaller)
	 */
	public static void printErrorMethodCallers(StackTraceElement[] stackTraceElements) {
		printErrorMethodCallers(stackTraceElements, MAX_CALLERS_LOG);
	}
	
	/**
	 * prints the method call stack of the provided stack trace. <br>
	 * Note: it will print MAX_CALLERS_LOG ({@value #MAX_CALLERS_LOG})
	 * or the length of the call stack (whatever is smaller)
	 */
	public static void printFatalMethodCallers(StackTraceElement[] stackTraceElements) {
		printFatalMethodCallers(stackTraceElements, MAX_CALLERS_LOG);
	}
	
	/**
	 * prints the method call stack of the provided stack trace. <br>
	 * Note: it will print supplied <b>max</b> callers
	 * or the length of the call stack (whatever is smaller)
	 */
	public static void printErrorMethodCallers(StackTraceElement[] stackTraceElements, int max) {
		for (int i = 0; i < Math.min(stackTraceElements.length, max); i++)
			Logging.logger.error("Method Stack Callers: (" + stackTraceElements[i].getFileName() + ":" + stackTraceElements[i].getLineNumber() + ")");
	}
	
	/**
	 * prints the method call stack of the provided stack trace. <br>
	 * Note: it will print supplied <b>max</b> callers
	 * or the length of the call stack (whatever is smaller)
	 */
	public static void printFatalMethodCallers(StackTraceElement[] stackTraceElements, int max) {
		for (int i = 0; i < Math.min(stackTraceElements.length, max); i++)
			Logging.logger.fatal("Method Stack Callers: (" + stackTraceElements[i].getFileName() + ":" + stackTraceElements[i].getLineNumber() + ")");
	}
	
	/**
	 * returns the local thread's stack trace
	 */
	public static StackTraceElement[] getStackTraceElements() {
		return Thread.currentThread().getStackTrace();
	}
	
}

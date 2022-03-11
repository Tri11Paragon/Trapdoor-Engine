package com.trapdoor.engine.display;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIcon;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

import com.spinyowl.legui.system.context.CallbackKeeper;
import com.trapdoor.Main;
import com.trapdoor.engine.ProjectionMatrix;
import com.trapdoor.engine.TextureLoader;
import com.trapdoor.engine.UBOLoader;
import com.trapdoor.engine.VAOLoader;
import com.trapdoor.engine.registry.GameRegistry;
import com.trapdoor.engine.registry.Threading;
import com.trapdoor.engine.registry.annotations.AnnotationHandler;
import com.trapdoor.engine.renderer.ShaderLookup;
import com.trapdoor.engine.renderer.SyncSave;
import com.trapdoor.engine.renderer.debug.TextureRenderer;
import com.trapdoor.engine.renderer.ui.CommandBox;
import com.trapdoor.engine.renderer.ui.Console;
import com.trapdoor.engine.renderer.ui.DebugInfo;
import com.trapdoor.engine.renderer.ui.UIMaster;
import com.trapdoor.engine.tools.Logging;
import com.trapdoor.engine.tools.SettingsLoader;
import com.trapdoor.engine.tools.icon.GLIcon;
import com.trapdoor.engine.tools.input.InputMaster;
import com.trapdoor.engine.tools.input.Mouse;
import com.trapdoor.engine.world.sound.SoundSystem;

public class DisplayManager {

	public static final String gameVersion = "0.0A";
	public static final String engineVersion = "0.7.4A";
	public static final String gameName = "Rixie";
	public static final String engineName = "Trapdoor";
	public static final String title = gameName + " - V" + gameVersion + " // " + engineName + " V" + engineVersion;
	
	// TODO: make this per display
	public static Vector3f lightDirection = new Vector3f(150, 300, 50).normalize();
	public static Vector3f lightColor = new Vector3f(1.0f);
	public static boolean enableShadows = true;
	//public static Vector3f lightDirection = new Vector3f(0, 0, 0).normalize();
	//public static Vector3f lightColor = new Vector3f(0.0f);
	
	// window
	public static long window;
	public static boolean displayOpen = true;
	public static long mainThreadID = 1;
		
	private static long lastFrameTime;
	private static double delta;
	private static double frameTimeMs,frameTimeS;
	private static double fps;
	private static long lastDebugUpdate;
	private static long lastPUpdate;
	public static long exited = 0;
	public static long createdThreads = 0;
	
	public static int WIDTH = 1280;
	public static int HEIGHT = 720;
	public static int FPS_MAX = 120;
	public static int MAX_SHADER_TEXTURES = 16;
	
	// sound
	public static long device;
    public static long context;
	
	// mouse
	public static double mouseX,mouseY;
	private static int mx, my;
	private static double[] mouseXA = new double[1], mouseYA = new double[1];
	private static double dx, dy, lx, ly;
	public static boolean isMouseGrabbed = false;
	
	// display
	private static IDisplay currentDisplay; 
	private static List<IDisplay> allDisplays = new ArrayList<IDisplay>();
	
	// debugger nonsense
	private static DebugInfo debugInfoLayer;
	
	// display updating
	public static void updateDisplay() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL13.GL_BLEND);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glCullFace(GL11.GL_BACK);
		GL13.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		while(!GLFW.glfwWindowShouldClose(DisplayManager.window)) {
			try {
				long start = getCurrentTime();
				GL11.glClearColor(currentDisplay.getSky1R(), currentDisplay.getSky1G(), currentDisplay.getSky1B(), 1.0f);
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);

				UBOLoader.updateMatrixUBO();
				
				currentDisplay.render();
				
				lx = mouseX;
				ly = mouseY;
				GLFW.glfwGetCursorPos(window, mouseXA, mouseYA);
				mouseX = mouseXA[0];
				mouseY = mouseYA[0];
				
				InputMaster.update();
				
				if (Mouse.isGrabbed()) {
					dx = mouseX - WIDTH/2;
					dy = mouseY - HEIGHT/2;
					// TODO: read this lol
					GLFW.glfwSetCursorPos(window, WIDTH/2, HEIGHT/2);
				} else {
					dx = lx - mouseX;
					dy = ly - mouseY;
				}
				
				UIMaster.render();
				
				glfwSwapBuffers(window);
				glfwPollEvents();
				
				UIMaster.processEvents();
				
				long end = getCurrentTime();
				long time = start - end;
				time = time < 0 ? 0 : time;
				if (FPS_MAX == 0)
					Threading.processMain(16000000-time);
				else
					Threading.processMain((1000000000/FPS_MAX)-time);
					
				SyncSave.sync(FPS_MAX);
				
				long currentFrameTime = getCurrentTime();
				delta = currentFrameTime - lastFrameTime;
				lastFrameTime = currentFrameTime;
				frameTimeMs = delta / 1000000d;
				frameTimeS = delta / 1000000000d;
				fps = 1000/frameTimeMs;
				long currentTime = System.currentTimeMillis();
				if (currentTime - lastDebugUpdate > 1000) {
					lastDebugUpdate = currentTime;
					debugInfoLayer.updateInfo();
				}
				if (currentTime - lastPUpdate > 250) {
					debugInfoLayer.update();
				}
				debugInfoLayer.update();
				//System.out.println(getFrameTimeMilis() + " :: " + 1000/getFrameTimeMilis() + " (" + 0 + ") :: " + 1000);
			} catch (Exception e) {e.printStackTrace(); System.exit(-1);}
		}
	}
	
	// display opening / closing
	
	public static void createDisplay(boolean isUsingFBOs) {
		mainThreadID = Thread.currentThread().getId();
		Logging.infoInsideBox(
				"LWJGL Version: " + Version.getVersion() + "!",
				"Game Version: " + gameVersion,
				"Engine Version: " + engineVersion
				);
		
		GLFWErrorCallback.createPrint(System.err).set();
		
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");
		
		glfwDefaultWindowHints(); 
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); 
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
		
		
		window = glfwCreateWindow(WIDTH, HEIGHT, title, NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");
		
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1);
			IntBuffer pHeight = stack.mallocInt(1);

			glfwGetWindowSize(window, pWidth, pHeight);

			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		}
		
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(SettingsLoader.VSYNC);

		glfwShowWindow(window);
		
		GL.createCapabilities();
		
		glfwWindowHint(GLFW.GLFW_SAMPLES, SettingsLoader.SAMPLES);
		if (SettingsLoader.SAMPLES > 0)
			GL11.glEnable(GL13.GL_MULTISAMPLE);
		
		GLIcon gli = new GLIcon("resources/textures/icon/icontrap16.png", "resources/textures/icon/icontrap32.png");
		glfwSetWindowIcon(window, gli.getBuffer());
		
		String vendor = GL33.glGetString(GL33.GL_VENDOR).toLowerCase();
		
		Logging.logger.info("GPU Vendor is: " + vendor);
		
		ShaderLookup.put(vendor, 1);
		
		UBOLoader.createMatrixUBO();
		
		ProjectionMatrix.updateProjectionMatrix();
		
		// load the texture atlas stuff
		TextureLoader.init("resources/textures/atlas/");
		// init the renderer
		//EntityRenderer.init();
		int[] in = new int[1];
		GL30.glGetIntegerv(GL30.GL_MAX_TEXTURE_IMAGE_UNITS, in);
		MAX_SHADER_TEXTURES = in[0];
		Logging.logger.info("Max allowed shader texures: " + in[0]);
		// this is actually a very big problem if anyone runs into this.
		// we can use 32 but will only use 16 for safety
		if (in[0] < 16) {
			Logging.logger.fatal("Your OpenGL drivers do not support this game. Please report this to the developers.");
			// we cannot recover from this.
			System.exit(-1);
		}
		setMouseGrabbed(isMouseGrabbed);
		try {
			initAudio();
		} catch (Exception e) {
			Logging.logger.fatal(e.getMessage(), e);
			System.exit(-1);
		}
		GameRegistry.init();
		SoundSystem.init();
		
		glfwWindowHint(GLFW.GLFW_DOUBLEBUFFER, GLFW_TRUE);
		
		UIMaster.init(window);
		CallbackKeeper keeper = UIMaster.getInitl().getCallbackKeeper();
		keeper.getChainKeyCallback().add((window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE ) {
				setMouseGrabbed(!isMouseGrabbed);
			}
			if ( action == GLFW_PRESS )
				InputMaster.keyPressed(key);
			if ( action == GLFW_RELEASE )
				InputMaster.keyReleased(key);
		});
		keeper.getChainMouseButtonCallback().add((window, button, action, mods) -> {
			if ( action == GLFW_PRESS )
				InputMaster.mousePressed(button);
			if ( action == GLFW_RELEASE )
				InputMaster.mouseReleased(button);
		});
		keeper.getChainWindowSizeCallback().add((window, x, y) -> {
			DisplayManager.WIDTH = x;
			DisplayManager.HEIGHT = y;
			GL11.glViewport(0, 0, x, y);
			UIMaster.updateScreenSize();
			ProjectionMatrix.updateProjectionMatrix();
			AnnotationHandler.runRescaleEvent(x, y);
		});
		keeper.getChainScrollCallback().add((window, x, y) -> {
			InputMaster.scrollMoved((int)y);
		});
		keeper.getChainCursorPosCallback().add((window, x, y) -> {
			DisplayManager.mx = (int) x;
			DisplayManager.my = (int) y;
		});
		keeper.getChainWindowCloseCallback().add((window) -> {
			displayOpen = false;
		});
		debugInfoLayer = new DebugInfo();
		CommandBox.init();
		InputMaster.registerKeyListener(new Console());
		InputMaster.registerKeyListener(debugInfoLayer);
		InputMaster.registerKeyListener(CommandBox.getInstance());
		
		if (Main.devMode)
			TextureRenderer.init();
	}

	public static void closeDisplay() {
		if (context != NULL)
            ALC11.alcDestroyContext(context);
        if (device != NULL)
            ALC11.alcCloseDevice(device);
        
		for (int i = 0; i < allDisplays.size(); i++)
			allDisplays.get(i).onDestory();
		
		if (Main.devMode)
			TextureRenderer.cleanup();
		
		UIMaster.quit();
		ProjectionMatrix.cleanShaders();
		VAOLoader.cleanUp();
		
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		displayOpen = false;
		
		glfwTerminate();
		glfwSetErrorCallback(null).free();
		Threading.cleanup();
	}
	
	private static void initAudio() {
		device = ALC11.alcOpenDevice((ByteBuffer) null);
        if (device == NULL) {
            throw new IllegalStateException("Failed to open the default OpenAL device.");
        }
        ALCCapabilities deviceCaps = ALC.createCapabilities(device);
        context = ALC11.alcCreateContext(device, (IntBuffer) null);
        if (context == NULL) {
            throw new IllegalStateException("Failed to create OpenAL context.");
        }
        ALC11.alcMakeContextCurrent(context);
        AL.createCapabilities(deviceCaps);
	}

	/*
	 * Display manager functions
	 */
	
	public static void changeDisplay(IDisplay display) {
		IDisplay old = currentDisplay;
		currentDisplay = display;
		if (display != null)
			display.onSwitch();
		if (old != null)
			old.onLeave();
	}
	
	public static void createDisplay(IDisplay display) {
		allDisplays.add(display);
		display.onCreate();
		//ProjectionMatrix.updateProjectionMatrix();
	}
	
	public static IDisplay getCurrentDisplay() {
		return currentDisplay;
	}
	
	/*
	 * Helper functions
	 */
	
	public static void setMouseGrabbed(boolean grabbed) {
		isMouseGrabbed = grabbed;
		glfwSetInputMode(window, GLFW_CURSOR, grabbed ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
	}
	
	// TODO: move to input related class
	public static double getDX() {
		return dx;
	}
	
	public static double getDY() {
		return dy;
	}
	
	public static double getMouseX() {
		return mx;
	}
	
	public static double getMouseY() {
		return my;
	}
	
	public static void setGrabbed(boolean gr) {
		setMouseGrabbed(gr);
	}
	
	private static long getCurrentTime() {
		return System.nanoTime();
	}

	public static double getFrameTimeMilisR() {
		return frameTimeMs;
	}
	
	public static double getFrameTimeMilis() {
		if (Thread.currentThread().getId() == mainThreadID)
			return frameTimeMs;
		else
			return Threading.getFrameTimeMilis();
	}

	public static double getFrameTimeSecondsR() {
		return frameTimeS;
	}
	
	public static double getFrameTimeSeconds() {
		if (Thread.currentThread().getId() == mainThreadID)
			return frameTimeS;
		else
			return Threading.getFrameTimeSeconds();
	}
	
	public static double getFPS() {
		return fps;
	}
	
	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void enableTransparentcy() {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public static void disableTransparentcy() {
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
}

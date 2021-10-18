package com.game.engine.display;

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
import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetInputMode;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowIcon;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.system.MemoryStack;

import com.game.engine.ProjectionMatrix;
import com.game.engine.TextureLoader;
import com.game.engine.tools.Logger;
import com.game.engine.tools.RescaleEvent;
import com.game.engine.tools.SettingsLoader;
import com.game.engine.tools.icon.GLIcon;
import com.game.engine.tools.input.InputMaster;

public class DisplayManager {

	public static final String gameVersion = "0.0A";
	public static final String engineVersion = "0.1A";
	
	// temp color
	private static final float RED = 0.5444f;
	private static final float GREEN = 0.62f;
	private static final float BLUE = 0.69f;
	
	// window
	public static long window;
		
	private static long lastFrameTime;
	private static double delta;
	
	public static int WIDTH = 1280;
	public static int HEIGHT = 720;
	public static int FPS_MAX = 120;
	
	// mouse
	public static double mouseX,mouseY;
	private static double lx, ly;
	public static boolean isMouseGrabbed = false;
	
	// classes needing to change when the window resizes
	public static List<RescaleEvent> rescales = new ArrayList<RescaleEvent>();
	
	// display
	private static IDisplay currentDisplay; 
	private static List<IDisplay> allDisplays = new ArrayList<IDisplay>();
	
	// display updating
	
	public static void updateDisplay() {
		while(!GLFW.glfwWindowShouldClose(DisplayManager.window)) {
			try {
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glClearColor(RED, GREEN, BLUE, 1.0f);
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
				
				currentDisplay.render();
				
				lx = mouseX;
				ly = mouseY;
				InputMaster.update();
				
				glfwSwapBuffers(window);
				glfwPollEvents();
				
				long currentFrameTime = getCurrentTime();
				delta = currentFrameTime - lastFrameTime;
				lastFrameTime = currentFrameTime;
			} catch (Exception e) {e.printStackTrace();}
		}
	}
	
	// display opening / closing
	
	public static void createDisplay(boolean isUsingFBOs) {
		Logger.writeln("LWJGL Version: " + Version.getVersion() + "!");
		Logger.writeln("Game Version: " + gameVersion);
		Logger.writeln("Engine Version: " + engineVersion);
		Logger.writeln();
		
		GLFWErrorCallback.createPrint(System.err).set();
		
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");
		
		glfwDefaultWindowHints(); 
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); 
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		
		
		window = glfwCreateWindow(WIDTH, HEIGHT, "Total Femboy Master Fighters - V" + gameVersion + " // Trapdoor V" + engineVersion, NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");
		
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE ) {
				glfwSetInputMode(window, GLFW_CURSOR, glfwGetInputMode(window, GLFW_CURSOR) == GLFW_CURSOR_DISABLED ? GLFW_CURSOR_NORMAL : GLFW_CURSOR_DISABLED);
				isMouseGrabbed = glfwGetInputMode(window, GLFW_CURSOR) == GLFW_CURSOR_DISABLED ? true : false;
			}
			if ( action == GLFW_PRESS )
				InputMaster.keyPressed(key);
			if ( action == GLFW_RELEASE )
				InputMaster.keyReleased(key);
		});
		
		glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
			if ( action == GLFW_PRESS )
				InputMaster.mousePressed(button);
			if ( action == GLFW_RELEASE )
				InputMaster.mouseReleased(button);
		});
		
		glfwSetWindowSizeCallback(window, (window, x, y) -> {
			DisplayManager.WIDTH = x;
			DisplayManager.HEIGHT = y;
			GL11.glViewport(0, 0, x, y); 
			ProjectionMatrix.updateProjectionMatrix();
			for (int i = 0; i < rescales.size(); i++)
				rescales.get(i).rescale();
		});
		
		glfwSetScrollCallback(window, (window, x, y) -> {
			InputMaster.scrollMoved((int)y);
		});
		
		glfwSetCursorPosCallback(window, (window, x, y) -> {
			DisplayManager.mouseX = x;
			DisplayManager.mouseY = y;
		});
		
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
		
		glfwWindowHint(GLFW_SAMPLES, SettingsLoader.SAMPLES);
		if (SettingsLoader.SAMPLES > 0)
			GL11.glEnable(GL13.GL_MULTISAMPLE);
		
		GLIcon gli = new GLIcon("resources/textures/icon/icon16.png", "resources/textures/icon/icon32.png");
		glfwSetWindowIcon(window, gli.getBuffer());
		ProjectionMatrix.updateProjectionMatrix();
		
		// load the texture atlas stuff
		TextureLoader.init("resources/textures/atlas/");
	}

	public static void closeDisplay() {
		for (int i = 0; i < allDisplays.size(); i++)
			allDisplays.get(i).onDestory();
		
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		
		glfwTerminate();
		glfwSetErrorCallback(null);
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
		ProjectionMatrix.updateProjectionMatrix();
	}
	
	/*
	 * Helper functions
	 */
	
	public static void setMouseGrabbed(boolean grabbed) {
		glfwSetInputMode(window, GLFW_CURSOR, grabbed ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
	}
	
	public static double getDX() {
		return mouseX - lx;
	}
	
	public static double getDY() {
		return mouseY - ly;
	}
	
	public static void setGrabbed(boolean gr) {
		setMouseGrabbed(gr);
	}
	
	private static long getCurrentTime() {
		return System.nanoTime();
	}

	public static double getFrameTimeMilis() {
		return delta / 1000000d;
	}

	public static double getFrameTimeSeconds() {
		return delta / 1000000000d;
	}
	
	public static double getFPS() {
		return 1000000000d / delta;
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

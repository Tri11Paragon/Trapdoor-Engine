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
import static org.lwjgl.openal.AL10.AL_EXTENSIONS;
import static org.lwjgl.openal.AL10.AL_RENDERER;
import static org.lwjgl.openal.AL10.AL_VENDOR;
import static org.lwjgl.openal.AL10.AL_VERSION;
import static org.lwjgl.openal.AL10.alGetString;
import static org.lwjgl.openal.ALC10.ALC_DEFAULT_DEVICE_SPECIFIER;
import static org.lwjgl.openal.ALC10.ALC_DEVICE_SPECIFIER;
import static org.lwjgl.openal.ALC10.ALC_EXTENSIONS;
import static org.lwjgl.openal.ALC10.ALC_MAJOR_VERSION;
import static org.lwjgl.openal.ALC10.ALC_MINOR_VERSION;
import static org.lwjgl.openal.ALC10.ALC_NO_ERROR;
import static org.lwjgl.openal.ALC10.alcGetError;
import static org.lwjgl.openal.ALC10.alcGetInteger;
import static org.lwjgl.openal.ALC10.alcGetString;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;
import static org.lwjgl.openal.ALC11.ALC_CAPTURE_DEFAULT_DEVICE_SPECIFIER;
import static org.lwjgl.openal.ALC11.ALC_CAPTURE_DEVICE_SPECIFIER;
import static org.lwjgl.openal.EXTEfx.ALC_EFX_MAJOR_VERSION;
import static org.lwjgl.openal.EXTEfx.ALC_EFX_MINOR_VERSION;
import static org.lwjgl.openal.EXTEfx.ALC_MAX_AUXILIARY_SENDS;
import static org.lwjgl.openal.EXTEfx.AL_EFFECT_AUTOWAH;
import static org.lwjgl.openal.EXTEfx.AL_EFFECT_CHORUS;
import static org.lwjgl.openal.EXTEfx.AL_EFFECT_COMPRESSOR;
import static org.lwjgl.openal.EXTEfx.AL_EFFECT_DISTORTION;
import static org.lwjgl.openal.EXTEfx.AL_EFFECT_EAXREVERB;
import static org.lwjgl.openal.EXTEfx.AL_EFFECT_ECHO;
import static org.lwjgl.openal.EXTEfx.AL_EFFECT_EQUALIZER;
import static org.lwjgl.openal.EXTEfx.AL_EFFECT_FLANGER;
import static org.lwjgl.openal.EXTEfx.AL_EFFECT_FREQUENCY_SHIFTER;
import static org.lwjgl.openal.EXTEfx.AL_EFFECT_PITCH_SHIFTER;
import static org.lwjgl.openal.EXTEfx.AL_EFFECT_REVERB;
import static org.lwjgl.openal.EXTEfx.AL_EFFECT_RING_MODULATOR;
import static org.lwjgl.openal.EXTEfx.AL_EFFECT_VOCAL_MORPHER;
import static org.lwjgl.openal.EXTEfx.AL_FILTER_BANDPASS;
import static org.lwjgl.openal.EXTEfx.AL_FILTER_HIGHPASS;
import static org.lwjgl.openal.EXTEfx.AL_FILTER_LOWPASS;
import static org.lwjgl.openal.EXTThreadLocalContext.alcSetThreadContext;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.joml.Vector3f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALUtil;
import org.lwjgl.openal.EnumerateAllExt;
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
import com.trapdoor.engine.datatypes.sound.EFXUtil;
import com.trapdoor.engine.registry.GameRegistry;
import com.trapdoor.engine.registry.Threading;
import com.trapdoor.engine.registry.annotations.AnnotationHandler;
import com.trapdoor.engine.renderer.ShaderLookup;
import com.trapdoor.engine.renderer.SyncSave;
import com.trapdoor.engine.renderer.debug.TextureRenderer;
import com.trapdoor.engine.renderer.ui.Console;
import com.trapdoor.engine.renderer.ui.DebugInfo;
import com.trapdoor.engine.renderer.ui.FontAwesomeIcons;
import com.trapdoor.engine.renderer.ui.UIMaster;
import com.trapdoor.engine.renderer.ui.render.ImGuiImplGl3;
import com.trapdoor.engine.tools.Logging;
import com.trapdoor.engine.tools.SettingsLoader;
import com.trapdoor.engine.tools.icon.GLIcon;
import com.trapdoor.engine.tools.input.InputMaster;
import com.trapdoor.engine.tools.input.Mouse;
import com.trapdoor.engine.world.sound.SoundSystem;

import imgui.ImFontConfig;
import imgui.ImFontGlyphRangesBuilder;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.glfw.ImGuiImplGlfw;

public class DisplayManager {

	public static final String gameVersion = "0.0A";
	public static final String engineVersion = "0.8.2A";
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
	
	public static int windowPosX, windowPosY;
		
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
    private static boolean useTLC = false;
	
	// mouse
	public static double mouseX,mouseY;
	private static int mx, my;
	private static double[] mouseXA = new double[1], mouseYA = new double[1];
	private static double dx, dy, lx, ly;
	public static boolean isMouseGrabbed = false;
    
	// display
	private static IDisplay currentDisplay; 
	private static List<IDisplay> allDisplays = new ArrayList<IDisplay>();
	private static ImGuiImplGl3 imguiGL3;
	private static ImGuiImplGlfw imguiGLFW;
	
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
				System.out.flush();
				System.err.flush();
				long start = getCurrentTime();
				GL11.glClearColor(currentDisplay.getSky1R(), currentDisplay.getSky1G(), currentDisplay.getSky1B(), 1.0f);
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
				/**
				 * Dear ImGUI updating
				 */
				imguiGLFW.newFrame();
				ImGui.newFrame();
				
				try (MemoryStack stack = MemoryStack.stackPush()){
					IntBuffer x = stack.callocInt(1);
					IntBuffer y = stack.callocInt(1);
					
					GLFW.glfwGetWindowPos(window, x, y);
					
					windowPosX = x.get();
					windowPosY = y.get();
				}
				
				/**
				 * window stuff // game engine
				 */

				//UBOLoader.updateMatrixUBO();
				
				currentDisplay.render();
				
				debugInfoLayer.updateFrame();
				Console.getInstance().render();
				
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
				
				ImGui.render();
				imguiGL3.renderDrawData(ImGui.getDrawData());
				
				if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
		            final long backupWindowPtr = GLFW.glfwGetCurrentContext();
		            ImGui.updatePlatformWindows();
		            ImGui.renderPlatformWindowsDefault();
		            GLFW.glfwMakeContextCurrent(backupWindowPtr);
		        }
				
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
					lastPUpdate = currentTime;
				}
				//Logging.logger.info(getFrameTimeMilis() + " :: " + 1000/getFrameTimeMilis() + " (" + 0 + ") :: " + 1000);
			} catch (Exception e) {Logging.logger.fatal(e.getMessage(), e); System.out.flush(); System.err.flush(); System.exit(-1);}
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
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5);
		
		
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
		GameRegistry.init();
		try {
			DisplayManager.initAudio();
		} catch (Exception e) {
			Logging.logger.fatal(e.getMessage(), e);
			System.exit(-1);
		}
		SoundSystem.init();
		
		AL11.alDistanceModel(AL11.AL_LINEAR_DISTANCE_CLAMPED);
		
		glfwWindowHint(GLFW.GLFW_DOUBLEBUFFER, GLFW_TRUE);
		
		UIMaster.init(window);
		CallbackKeeper keeper = UIMaster.getInitl().getCallbackKeeper();
		
		/** Dear IMGUI setup */
		
		ImGui.createContext();
		
		final ImGuiIO tio = ImGui.getIO();

		tio.setIniFilename(null);
		tio.addConfigFlags(ImGuiConfigFlags.DockingEnable);
		tio.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
		tio.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
		tio.setConfigViewportsNoTaskBarIcon(true);
        
        imguiGL3 = new ImGuiImplGl3();
        imguiGLFW = new ImGuiImplGlfw();
        
        imguiGLFW.init(window, false);
        imguiGL3.init("#version 330 core");
        
        AnnotationHandler.runPreRegistration();
        
        //tio.getFonts().addFontDefault();
        
        final ImFontGlyphRangesBuilder rangesBuilder = new ImFontGlyphRangesBuilder(); // Glyphs ranges provide
        rangesBuilder.addRanges(tio.getFonts().getGlyphRangesDefault());
        //rangesBuilder.addRanges(tio.getFonts().getGlyphRangesCyrillic());
        //rangesBuilder.addRanges(tio.getFonts().getGlyphRangesJapanese());
        rangesBuilder.addRanges(FontAwesomeIcons._IconRange);
        
        final ImFontConfig fontConfig = new ImFontConfig();
        final ImFontConfig fontConfig2 = new ImFontConfig();
        fontConfig.setMergeMode(false);
        fontConfig2.setMergeMode(true);
        
        final short[] glyphRanges = rangesBuilder.buildRanges();
        
        // TODO:
        
        /*for (int i = 0; i < GameRegistry.getFonts().size(); i++) {
        	if (i > 0)
        		fontConfig.setMergeMode(true);
        	ImFont f = tio.getFonts().addFontFromMemoryTTF(
					loadFromResources(
							GameRegistry.getFonts().get(i)), 
							GameRegistry.getFontSize(
									GameRegistry.getFonts().get(i)), fontConfig, glyphRanges);
        	GameRegistry.regsterImFont(
	        			f, 
	        			GameRegistry.getFontNames().get(i)
        			);
        }*/
        GameRegistry.regsterImFont(
        		tio.getFonts().addFontFromMemoryTTF(loadFromResources("resources/fonts/roboto/Roboto-Regular.ttf"), 18, fontConfig, glyphRanges), 
    			"roboto-regular"
			);
        
        tio.getFonts().addFontFromMemoryTTF(loadFromResources("resources/fonts/fontawesome-free-6.1.0-web/webfonts/fa-regular-400.ttf"), 18, fontConfig2, glyphRanges);
        tio.getFonts().addFontFromMemoryTTF(loadFromResources("resources/fonts/fontawesome-free-6.1.0-web/webfonts/fa-brands-400.ttf"), 18, fontConfig2, glyphRanges);
        tio.getFonts().addFontFromMemoryTTF(loadFromResources("resources/fonts/fontawesome-free-6.1.0-web/webfonts/fa-solid-900.ttf"), 18, fontConfig2, glyphRanges);
        
        tio.getFonts().build();
        
        fontConfig.destroy();
        fontConfig2.destroy();
        
        imguiGL3.updateFontsTexture();
		
		keeper.getChainKeyCallback().add((window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE ) {
				setMouseGrabbed(!isMouseGrabbed);
			}
			if ( action == GLFW_PRESS )
				InputMaster.keyPressed(key);
			if ( action == GLFW_RELEASE )
				InputMaster.keyReleased(key);
			
			imguiGLFW.keyCallback(window, key, scancode, action, mods);
		});
		keeper.getChainMouseButtonCallback().add((window, button, action, mods) -> {
			if ( action == GLFW_PRESS )
				InputMaster.mousePressed(button);
			if ( action == GLFW_RELEASE )
				InputMaster.mouseReleased(button);
			imguiGLFW.mouseButtonCallback(window, button, action, mods);
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

	        imguiGLFW.scrollCallback(window, x, y);
		});
		keeper.getChainCursorPosCallback().add((window, x, y) -> {
			DisplayManager.mx = (int) x;
			DisplayManager.my = (int) y;
		});
		keeper.getChainWindowCloseCallback().add((window) -> {
			displayOpen = false;
		});
		keeper.getChainWindowFocusCallback().add((window, focused) -> {
			imguiGLFW.windowFocusCallback(window, focused);
		});
		keeper.getChainCursorEnterCallback().add((window, entered) -> {
			imguiGLFW.cursorEnterCallback(window, entered);
		});
		keeper.getChainCharCallback().add((window, c) -> {
			imguiGLFW.charCallback(window, c);
		});
		debugInfoLayer = new DebugInfo();
		Console.init();
		InputMaster.registerKeyListener(debugInfoLayer);
		InputMaster.registerKeyListener(Console.getInstance());
		
		if (Main.devMode)
			TextureRenderer.init();
	}

	public static void closeDisplay() {
		imguiGL3.dispose();
		imguiGLFW.dispose();
		ImGui.destroyContext();
        
        if (context != NULL)
            ALC11.alcDestroyContext(context);
        if (device != NULL)
            ALC11.alcCloseDevice(device);
        
        alcMakeContextCurrent(NULL);
        
        if (useTLC) {
            AL.setCurrentThread(null);
        } else {
            AL.setCurrentProcess(null);
        }
        
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
	
	public static void initAudio() {
		device = ALC11.alcOpenDevice((ByteBuffer) null);
        if (device == NULL) {
            throw new IllegalStateException("Failed to open the default OpenAL device.");
        }
        ALCCapabilities deviceCaps = ALC.createCapabilities(device);
        
        
        context = ALC11.alcCreateContext(device, (IntBuffer) null);
        
        checkALCError(device);
        
        useTLC = deviceCaps.ALC_EXT_thread_local_context && alcSetThreadContext(context);
        
        checkALCError(device);
        
        if (context == NULL) {
            throw new IllegalStateException("Failed to create OpenAL context.");
        }
        if (!ALC11.alcMakeContextCurrent(context)) {
     	   throw new IllegalStateException();
        }
        AL.createCapabilities(deviceCaps);
        
        printALCInfo(device, deviceCaps);
        printALInfo();
        if (deviceCaps.ALC_EXT_EFX) {
            printEFXInfo(device);
        }
	}
	
	private static void printALCInfo(long device, ALCCapabilities caps) {
        // we're running 1.1, so really no need to query for the 'ALC_ENUMERATION_EXT' extension
        if (caps.ALC_ENUMERATION_EXT) {
            if (caps.ALC_ENUMERATE_ALL_EXT) {
                printDevices(EnumerateAllExt.ALC_ALL_DEVICES_SPECIFIER, "playback");
            } else {
                printDevices(ALC_DEVICE_SPECIFIER, "playback");
            }
            printDevices(ALC_CAPTURE_DEVICE_SPECIFIER, "capture");
        } else {
            Logging.logger.info("No device enumeration available");
        }

        if (caps.ALC_ENUMERATE_ALL_EXT) {
            Logging.logger.info("Default playback device: " + alcGetString(0, EnumerateAllExt.ALC_DEFAULT_ALL_DEVICES_SPECIFIER));
        } else {
            Logging.logger.info("Default playback device: " + alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER));
        }

        Logging.logger.info("Default capture device: " + alcGetString(0, ALC_CAPTURE_DEFAULT_DEVICE_SPECIFIER));

        Logging.logger.info("ALC device specifier: " + alcGetString(device, ALC_DEVICE_SPECIFIER));

        int majorVersion = alcGetInteger(device, ALC_MAJOR_VERSION);
        int minorVersion = alcGetInteger(device, ALC_MINOR_VERSION);
        checkALCError(device);

        Logging.logger.info("ALC version: " + majorVersion + "." + minorVersion);

        Logging.logger.info("ALC extensions:");
        String[] extensions = Objects.requireNonNull(alcGetString(device, ALC_EXTENSIONS)).split(" ");
        checkALCError(device);
        for (String extension : extensions) {
            if (extension.trim().isEmpty()) {
                continue;
            }
            Logging.logger.info("    " + extension);
        }
    }

    private static void printALInfo() {
        Logging.logger.info("OpenAL vendor string: " + alGetString(AL_VENDOR));
        Logging.logger.info("OpenAL renderer string: " + alGetString(AL_RENDERER));
        Logging.logger.info("OpenAL version string: " + alGetString(AL_VERSION));
        Logging.logger.info("AL extensions:");
        String[] extensions = Objects.requireNonNull(alGetString(AL_EXTENSIONS)).split(" ");
        for (String extension : extensions) {
            if (extension.trim().isEmpty()) {
                continue;
            }
            Logging.logger.info("    " + extension);
        }
        checkALError();
    }

    private static void printEFXInfo(long device) {
        int efxMajor = alcGetInteger(device, ALC_EFX_MAJOR_VERSION);
        int efxMinor = alcGetInteger(device, ALC_EFX_MINOR_VERSION);
        if (alcGetError(device) == ALC_NO_ERROR) {
            Logging.logger.info("EFX version: " + efxMajor + "." + efxMinor);
        }

        int auxSends = alcGetInteger(device, ALC_MAX_AUXILIARY_SENDS);
        if (alcGetError(device) == ALC_NO_ERROR) {
            Logging.logger.info("Max auxiliary sends: " + auxSends);
        }

        Logging.logger.info("Supported filters: ");
        HashMap<String, Integer> filters = new HashMap<>();
        filters.put("Low-pass", AL_FILTER_LOWPASS);
        filters.put("High-pass", AL_FILTER_HIGHPASS);
        filters.put("Band-pass", AL_FILTER_BANDPASS);

        filters.entrySet().stream()
            .filter(entry -> EFXUtil.isFilterSupported(entry.getValue()))
            .forEach(entry -> Logging.logger.info("    " + entry.getKey()));

        Logging.logger.info("Supported effects: ");
        HashMap<String, Integer> effects = new HashMap<>();
        effects.put("EAX Reverb", AL_EFFECT_EAXREVERB);
        effects.put("Reverb", AL_EFFECT_REVERB);
        effects.put("Chorus", AL_EFFECT_CHORUS);
        effects.put("Distortion", AL_EFFECT_DISTORTION);
        effects.put("Echo", AL_EFFECT_ECHO);
        effects.put("Flanger", AL_EFFECT_FLANGER);
        effects.put("Frequency Shifter", AL_EFFECT_FREQUENCY_SHIFTER);
        effects.put("Vocal Morpher", AL_EFFECT_VOCAL_MORPHER);
        effects.put("Pitch Shifter", AL_EFFECT_PITCH_SHIFTER);
        effects.put("Ring Modulator", AL_EFFECT_RING_MODULATOR);
        effects.put("Autowah", AL_EFFECT_AUTOWAH);
        effects.put("Compressor", AL_EFFECT_COMPRESSOR);
        effects.put("Equalizer", AL_EFFECT_EQUALIZER);

        effects.entrySet().stream()
            .filter(e -> EFXUtil.isEffectSupported(e.getValue()))
            .forEach(e -> Logging.logger.info("    " + e.getKey()));
    }
    
    private static void printDevices(int which, String kind) {
        List<String> devices = Objects.requireNonNull(ALUtil.getStringList(NULL, which));
        Logging.logger.info("Available " + kind + " devices: ");
        for (String d : devices) {
            Logging.logger.info("    " + d);
        }
    }

	private static void checkALCError(long device) {
        int err = ALC11.alcGetError(device);
        if (err != ALC11.ALC_NO_ERROR) {
            throw new RuntimeException(ALC11.alcGetString(device, err));
        }
    }

    private static void checkALError() {
        int err = AL11.alGetError();
        if (err != AL11.AL_NO_ERROR) {
            throw new RuntimeException(AL11.alGetString(err));
        }
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
	
	public static void updateFontTextures() {
		imguiGL3.updateFontsTexture();
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
	
	private static byte[] loadFromResources(String path) {
        try {
            return Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
	
}

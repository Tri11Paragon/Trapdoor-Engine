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
import org.lwjgl.glfw.GLFWNativeWin32;
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

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiPlatformIO;
import imgui.ImGuiViewport;
import imgui.ImVec2;
import imgui.callback.ImStrConsumer;
import imgui.callback.ImStrSupplier;
import imgui.flag.ImGuiBackendFlags;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiKey;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiMouseCursor;

public class DisplayManager {
	
	private static final String OS = System.getProperty("os.name", "generic").toLowerCase();
    protected static final boolean IS_WINDOWS = OS.contains("win");
    protected static final boolean IS_APPLE = OS.contains("mac") || OS.contains("darwin");

	public static final String gameVersion = "0.0A";
	public static final String engineVersion = "0.7.5A";
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
	private static int[] windowX = new int[1], windowY = new int[1];
	//private static int[] monitorX = new int[1], monitorY = new int[1];
	private static boolean wantUpdateMonitors = false;
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
	private static double[] mouseXIM = new double[1], mouseYIM = new double[1];
	private static double dx, dy, lx, ly;
	public static boolean isMouseGrabbed = false;
	private static final long[] mouseCursors = new long[ImGuiMouseCursor.COUNT];
    private static final boolean[] mouseJustPressed = new boolean[ImGuiMouseButton.COUNT];
	private static ImVec2 mousePosBackup = new ImVec2();
    
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
				/**
				 * Dear ImGUI updating
				 */
				final ImGuiIO io = ImGui.getIO();
				
		        io.setDeltaTime((float)delta);
		        
		        for (int i = 0; i < ImGuiMouseButton.COUNT; i++) {
		            // If a mouse press event came, always pass it as "mouse held this frame", so we don't miss click-release events that are shorter than 1 frame.
		            io.setMouseDown(i, mouseJustPressed[i] || GLFW.glfwGetMouseButton(window, i) != 0);
		            mouseJustPressed[i] = false;
		        }

		        io.getMousePos(mousePosBackup);
		        io.setMousePos(-Float.MAX_VALUE, -Float.MAX_VALUE);
		        io.setMouseHoveredViewport(0);

		        final ImGuiPlatformIO platformIO = ImGui.getPlatformIO();

		        for (int n = 0; n < platformIO.getViewportsSize(); n++) {
		            final ImGuiViewport viewport = platformIO.getViewports(n);
		            final long windowPtr = viewport.getPlatformHandle();

		            final boolean focused = GLFW.glfwGetWindowAttrib(windowPtr, GLFW.GLFW_FOCUSED) != 0;

		            final long mouseWindowPtr = (window == windowPtr || focused) ? windowPtr : 0;

		            // Update mouse buttons
		            if (focused) {
		                for (int i = 0; i < ImGuiMouseButton.COUNT; i++) {
		                    io.setMouseDown(i, GLFW.glfwGetMouseButton(windowPtr, i) != 0);
		                }
		            }

		            // Set OS mouse position from Dear ImGui if requested (rarely used, only when ImGuiConfigFlags_NavEnableSetMousePos is enabled by user)
		            // (When multi-viewports are enabled, all Dear ImGui positions are same as OS positions)
		            if (io.getWantSetMousePos() && focused) {
		            	GLFW.glfwSetCursorPos(windowPtr, mousePosBackup.x - viewport.getPosX(), mousePosBackup.y - viewport.getPosY());
		            }

		            // Set Dear ImGui mouse position from OS position
		            if (mouseWindowPtr != 0) {
		            	GLFW.glfwGetCursorPos(mouseWindowPtr, mouseXIM, mouseYIM);

		                if (io.hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
		                    // Multi-viewport mode: mouse position in OS absolute coordinates (io.MousePos is (0,0) when the mouse is on the upper-left of the primary monitor)
		                	GLFW.glfwGetWindowPos(windowPtr, windowX, windowY);
		                    io.setMousePos((float) mouseXIM[0] + windowX[0], (float) mouseYIM[0] + windowY[0]);
		                } else {
		                    // Single viewport mode: mouse position in client window coordinates (io.MousePos is (0,0) when the mouse is on the upper-left corner of the app window)
		                    io.setMousePos((float) mouseXIM[0], (float) mouseYIM[0]);
		                }
		            }
		        }
		        
		        final boolean noCursorChange = io.hasConfigFlags(ImGuiConfigFlags.NoMouseCursorChange);
		        final boolean cursorDisabled = GLFW.glfwGetInputMode(window, GLFW_CURSOR) == GLFW_CURSOR_DISABLED;

		        if (noCursorChange || cursorDisabled) {} else {
			        final int imguiCursor = ImGui.getMouseCursor();
	
			        for (int n = 0; n < platformIO.getViewportsSize(); n++) {
			            final long windowPtr = platformIO.getViewports(n).getPlatformHandle();
	
			            if (imguiCursor == ImGuiMouseCursor.None || io.getMouseDrawCursor()) {
			                // Hide OS mouse cursor if imgui is drawing it or if it wants no cursor
			                glfwSetInputMode(windowPtr, GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
			            } else {
			                // Show OS mouse cursor
			                // FIXME-PLATFORM: Unfocused windows seems to fail changing the mouse cursor with GLFW 3.2, but 3.3 works here.
			            	GLFW.glfwSetCursor(windowPtr, mouseCursors[imguiCursor] != 0 ? mouseCursors[imguiCursor] : mouseCursors[ImGuiMouseCursor.Arrow]);
			                glfwSetInputMode(windowPtr, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
			            }
			        }
		        }
		        
		        if (wantUpdateMonitors)
		        	updateMonitors();
				
				/**
				 * window stuff // game engine
				 */
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
			} catch (Exception e) {Logging.logger.fatal(e.getMessage(), e); System.exit(-1);}
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
		
		/** Dear IMGUI setup */
		
		ImGui.createContext();
		
		final ImGuiIO tio = ImGui.getIO();
		
		tio.addBackendFlags(ImGuiBackendFlags.HasMouseCursors | ImGuiBackendFlags.HasSetMousePos | ImGuiBackendFlags.PlatformHasViewports);
		tio.setBackendPlatformName("imgui_java_impl_glfw");
		
        // Keyboard mapping. ImGui will use those indices to peek into the io.KeysDown[] array.
        final int[] keyMap = new int[ImGuiKey.COUNT];
        keyMap[ImGuiKey.Tab] = GLFW.GLFW_KEY_TAB;
        keyMap[ImGuiKey.LeftArrow] = GLFW.GLFW_KEY_LEFT;
        keyMap[ImGuiKey.RightArrow] = GLFW.GLFW_KEY_RIGHT;
        keyMap[ImGuiKey.UpArrow] = GLFW.GLFW_KEY_UP;
        keyMap[ImGuiKey.DownArrow] = GLFW.GLFW_KEY_DOWN;
        keyMap[ImGuiKey.PageUp] = GLFW.GLFW_KEY_PAGE_UP;
        keyMap[ImGuiKey.PageDown] = GLFW.GLFW_KEY_PAGE_DOWN;
        keyMap[ImGuiKey.Home] = GLFW.GLFW_KEY_HOME;
        keyMap[ImGuiKey.End] = GLFW.GLFW_KEY_END;
        keyMap[ImGuiKey.Insert] = GLFW.GLFW_KEY_INSERT;
        keyMap[ImGuiKey.Delete] = GLFW.GLFW_KEY_DELETE;
        keyMap[ImGuiKey.Backspace] = GLFW.GLFW_KEY_BACKSPACE;
        keyMap[ImGuiKey.Space] = GLFW.GLFW_KEY_SPACE;
        keyMap[ImGuiKey.Enter] = GLFW.GLFW_KEY_ENTER;
        keyMap[ImGuiKey.Escape] = GLFW.GLFW_KEY_ESCAPE;
        keyMap[ImGuiKey.KeyPadEnter] = GLFW.GLFW_KEY_KP_ENTER;
        keyMap[ImGuiKey.A] = GLFW.GLFW_KEY_A;
        keyMap[ImGuiKey.C] = GLFW.GLFW_KEY_C;
        keyMap[ImGuiKey.V] = GLFW.GLFW_KEY_V;
        keyMap[ImGuiKey.X] = GLFW.GLFW_KEY_X;
        keyMap[ImGuiKey.Y] = GLFW.GLFW_KEY_Y;
        keyMap[ImGuiKey.Z] = GLFW.GLFW_KEY_Z;

        tio.setKeyMap(keyMap);
        
        tio.setGetClipboardTextFn(new ImStrSupplier() {
            @Override
            public String get() {
                final String clipboardString = GLFW.glfwGetClipboardString(window);
                return clipboardString != null ? clipboardString : "";
            }
        });

        tio.setSetClipboardTextFn(new ImStrConsumer() {
            @Override
            public void accept(final String str) {
                GLFW.glfwSetClipboardString(window, str);
            }
        });
        
        // Mouse cursors mapping. Disable errors whilst setting due to X11.
        final GLFWErrorCallback prevErrorCallback = glfwSetErrorCallback(null);
        mouseCursors[ImGuiMouseCursor.Arrow] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR);
        mouseCursors[ImGuiMouseCursor.TextInput] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_IBEAM_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeAll] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeNS] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_VRESIZE_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeEW] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HRESIZE_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeNESW] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR);
        mouseCursors[ImGuiMouseCursor.ResizeNWSE] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR);
        mouseCursors[ImGuiMouseCursor.Hand] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HAND_CURSOR);
        mouseCursors[ImGuiMouseCursor.NotAllowed] = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR);
        glfwSetErrorCallback(prevErrorCallback);
        
        final ImGuiViewport mainViewport = ImGui.getMainViewport();
        mainViewport.setPlatformHandle(window);

        if (IS_WINDOWS) {
            mainViewport.setPlatformHandleRaw(GLFWNativeWin32.glfwGetWin32Window(window));
        }

        if (tio.hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
        	/*final ImGuiPlatformIO platformIO = ImGui.getPlatformIO();

	        // Register platform interface (will be coupled with a renderer interface)
	        platformIO.setPlatformCreateWindow(new CreateWindowFunction());
	        platformIO.setPlatformDestroyWindow(new DestroyWindowFunction());
	        platformIO.setPlatformShowWindow(new ShowWindowFunction());
	        platformIO.setPlatformGetWindowPos(new GetWindowPosFunction());
	        platformIO.setPlatformSetWindowPos(new SetWindowPosFunction());
	        platformIO.setPlatformGetWindowSize(new GetWindowSizeFunction());
	        platformIO.setPlatformSetWindowSize(new SetWindowSizeFunction());
	        platformIO.setPlatformSetWindowTitle(new SetWindowTitleFunction());
	        platformIO.setPlatformSetWindowFocus(new SetWindowFocusFunction());
	        platformIO.setPlatformGetWindowFocus(new GetWindowFocusFunction());
	        platformIO.setPlatformGetWindowMinimized(new GetWindowMinimizedFunction());
	        platformIO.setPlatformSetWindowAlpha(new SetWindowAlphaFunction());
	        platformIO.setPlatformRenderWindow(new RenderWindowFunction());
	        platformIO.setPlatformSwapBuffers(new SwapBuffersFunction());

	        // Register main window handle (which is owned by the main application, not by us)
	        // This is mostly for simplicity and consistency, so that our code (e.g. mouse handling etc.) can use same logic for main and secondary viewports.
	        final ImGuiViewport mainViewport = ImGui.getMainViewport();
	        final ImGuiViewportDataGlfw data = new ImGuiViewportDataGlfw();
	        data.window = windowPtr;
	        data.windowOwned = false;
	        mainViewport.setPlatformUserData(data);*/
        }
		
		keeper.getChainKeyCallback().add((window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE ) {
				setMouseGrabbed(!isMouseGrabbed);
			}
			if ( action == GLFW_PRESS )
				InputMaster.keyPressed(key);
			if ( action == GLFW_RELEASE )
				InputMaster.keyReleased(key);
			
			 final ImGuiIO io = ImGui.getIO();
			 
			if (key >= 0 && key < 512) {
				if (action == GLFW_PRESS) {
					io.setKeysDown(key, true);
				} else if (action == GLFW_RELEASE) {
					io.setKeysDown(key, false);
				}
			}

			io.setKeyCtrl(io.getKeysDown(GLFW.GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW.GLFW_KEY_RIGHT_CONTROL));
			io.setKeyShift(io.getKeysDown(GLFW.GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW.GLFW_KEY_RIGHT_SHIFT));
			io.setKeyAlt(io.getKeysDown(GLFW.GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW.GLFW_KEY_RIGHT_ALT));
			io.setKeySuper(io.getKeysDown(GLFW.GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW.GLFW_KEY_RIGHT_SUPER));
		});
		keeper.getChainMouseButtonCallback().add((window, button, action, mods) -> {
			if ( action == GLFW_PRESS )
				InputMaster.mousePressed(button);
			if ( action == GLFW_RELEASE )
				InputMaster.mouseReleased(button);
			if (action == GLFW_PRESS && button >= 0 && button < mouseJustPressed.length) {
	            mouseJustPressed[button] = true;
	        }
		});
		keeper.getChainWindowSizeCallback().add((window, x, y) -> {
			int[] fbWidth = new int[1];
			int[] fbHeight = new int[1];
			GLFW.glfwGetFramebufferSize(window, fbWidth, fbHeight);
			DisplayManager.WIDTH = x;
			DisplayManager.HEIGHT = y;
			GL11.glViewport(0, 0, x, y);
			UIMaster.updateScreenSize();
			ProjectionMatrix.updateProjectionMatrix();
			AnnotationHandler.runRescaleEvent(x, y);
			final ImGuiIO io = ImGui.getIO();
			io.setDisplaySize((float) x, (float) y);
			if (x > 0 && x > 0) {
	            final float scaleX = (float) fbWidth[0] / x;
	            final float scaleY = (float) fbHeight[0] / y;
	            io.setDisplayFramebufferScale(scaleX, scaleY);
	        }
			
		});
		keeper.getChainScrollCallback().add((window, x, y) -> {
			InputMaster.scrollMoved((int)y);

	        final ImGuiIO io = ImGui.getIO();
	        io.setMouseWheelH(io.getMouseWheelH() + (float) x);
	        io.setMouseWheel(io.getMouseWheel() + (float) y);
		});
		keeper.getChainCursorPosCallback().add((window, x, y) -> {
			DisplayManager.mx = (int) x;
			DisplayManager.my = (int) y;
		});
		keeper.getChainWindowCloseCallback().add((window) -> {
			displayOpen = false;
		});
		keeper.getChainWindowFocusCallback().add((window, focused) -> {
			ImGui.getIO().addFocusEvent(focused);
		});
		keeper.getChainCursorEnterCallback().add((window, entered) -> {
			
		});
		keeper.getChainCharCallback().add((window, c) -> {
			final ImGuiIO io = ImGui.getIO();
			io.addInputCharacter(c);
		});
		updateMonitors();
		GLFW.glfwSetMonitorCallback((window, i) -> {wantUpdateMonitors = true;});
		debugInfoLayer = new DebugInfo();
		CommandBox.init();
		InputMaster.registerKeyListener(new Console());
		InputMaster.registerKeyListener(debugInfoLayer);
		InputMaster.registerKeyListener(CommandBox.getInstance());
		
		if (Main.devMode)
			TextureRenderer.init();
	}

	public static void closeDisplay() {
		for (int i = 0; i < ImGuiMouseCursor.COUNT; i++)
            GLFW.glfwDestroyCursor(mouseCursors[i]);
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
	
	private static void updateMonitors() {
		/*final ImGuiPlatformIO platformIO = ImGui.getPlatformIO();
		final PointerBuffer monitors = GLFW.glfwGetMonitors();

		platformIO.resizeMonitors(0);

		for (int n = 0; n < monitors.limit(); n++) {
			final long monitor = monitors.get(n);

			GLFW.glfwGetMonitorPos(monitor, monitorX, monitorY);
			final GLFWVidMode vidMode = glfwGetVideoMode(monitor);
			final float mainPosX = monitorX[0];
			final float mainPosY = monitorY[0];
			final float mainSizeX = vidMode.width();
			final float mainSizeY = vidMode.height();

			final boolean glfwHasMonitorWorkArea = f;
			
			if (glfwHasMonitorWorkArea) {
				GLFW.glfwGetMonitorWorkarea(monitor, monitorWorkAreaX, monitorWorkAreaY, monitorWorkAreaWidth,
						monitorWorkAreaHeight);
			}

			float workPosX = 0;
			float workPosY = 0;
			float workSizeX = 0;
			float workSizeY = 0;

			// Workaround a small GLFW issue reporting zero on monitor changes:
			// https://github.com/glfw/glfw/pull/1761
			if (glfwHasMonitorWorkArea && monitorWorkAreaWidth[0] > 0 && monitorWorkAreaHeight[0] > 0) {
				workPosX = monitorWorkAreaX[0];
				workPosY = monitorWorkAreaY[0];
				workSizeX = monitorWorkAreaWidth[0];
				workSizeY = monitorWorkAreaHeight[0];
			}

			// Warning: the validity of monitor DPI information on Windows depends on the
			// application DPI awareness settings,
			// which generally needs to be set in the manifest or at runtime.
			if (glfwHasPerMonitorDpi) {
				glfwGetMonitorContentScale(monitor, monitorContentScaleX, monitorContentScaleY);
			}
			final float dpiScale = monitorContentScaleX[0];

			platformIO.pushMonitors(mainPosX, mainPosY, mainSizeX, mainSizeY, workPosX, workPosY, workSizeX, workSizeY,
					dpiScale);
		}*/

		wantUpdateMonitors = false;
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

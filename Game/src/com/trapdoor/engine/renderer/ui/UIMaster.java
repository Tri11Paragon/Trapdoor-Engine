package com.trapdoor.engine.renderer.ui;

import org.joml.Vector2i;

import com.spinyowl.legui.DefaultInitializer;
import com.spinyowl.legui.animation.AnimatorProvider;
import com.spinyowl.legui.component.Button;
import com.spinyowl.legui.component.Frame;
import com.spinyowl.legui.component.Layer;
import com.spinyowl.legui.event.MouseClickEvent;
import com.spinyowl.legui.listener.MouseClickEventListener;
import com.spinyowl.legui.listener.processor.EventProcessorProvider;
import com.spinyowl.legui.style.border.SimpleLineBorder;
import com.spinyowl.legui.style.color.ColorConstants;
import com.spinyowl.legui.system.context.Context;
import com.spinyowl.legui.system.layout.LayoutManager;
import com.spinyowl.legui.system.renderer.Renderer;
import com.spinyowl.legui.theme.Themes;
import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.registry.GameRegistry;

/**
 * @author laptop
 * @date Dec. 13, 2021
 * 
 */
public class UIMaster {

	private static DefaultInitializer initializer;
	private static Frame frame;
	private static Renderer renderer;
	private static Context context;

	// https://github.com/SpinyOwl/legui/blob/develop/src/main/java/com/spinyowl/legui/demo/SingleClassExampleGuiOverGL.java#L319

	public static void init(long window) {
		frame = new Frame(DisplayManager.WIDTH, DisplayManager.HEIGHT);
		// set the background of the UI frame to transparent
		frame.getContainer().getStyle().getBackground().setColor(ColorConstants.transparent());
		frame.getContainer().setFocusable(false);

		
		Layer layer = new Layer();
		layer.setSize(512, 512);
		Button button = new Button("Add components", 20, 20, 160, 30);
		SimpleLineBorder border = new SimpleLineBorder(ColorConstants.black(), 1);
		button.getStyle().setBorder(border);
		
		button.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {
			System.out.println(frame.getContainer().getSize().x + " :: " + frame.getContainer().getSize().y);
		});
		layer.add(button);
		layer.setEnabled(true);
		
		//frame.addLayer(layer);

		initializer = new DefaultInitializer(window, frame);

		renderer = initializer.getRenderer();
		renderer.initialize();
	    context = initializer.getContext();
	    context.updateGlfwWindow();
	    context.setPixelRatio(1);
	    
	    GameRegistry.registerFont("orbitron-light", "resources/fonts/orbitron-light.otf");
	    GameRegistry.registerFont("orbitron-medium", "resources/fonts/orbitron-medium.otf");
	    
	    GameRegistry.registerFont("mono", "resources/fonts/liberation/LiberationMono-Regular.ttf");
	    
	    Themes.setDefaultTheme(Themes.FLAT_DARK);;
	}

	@Deprecated
	public static DefaultInitializer getInitl() {
		return initializer;
	}
	
	@Deprecated
	/**
	 * Use ImGUI instead. The CPP code should almost directly translate to Java
	 * https://github.com/ocornut/imgui#usage
	 */
	public static Frame getMasterFrame() {
		return frame;
	}

	private static Vector2i windowSize = new Vector2i();
	
	public static void updateScreenSize() {
		windowSize.x = DisplayManager.WIDTH;
		windowSize.y = DisplayManager.HEIGHT;
		context.setWindowSize(windowSize);
		context.setFramebufferSize(windowSize);

		frame.setSize(windowSize.x, windowSize.y);
	}

	public static void render() {
		renderer.render(frame, context);
	}
	
	public static void processEvents() {
		// Now we need to process events. Firstly we need to process system events.
		initializer.getSystemEventProcessor().processEvents(frame, context);

		// When system events are translated to GUI events we need to process them.
		// This event processor calls listeners added to ui components
		EventProcessorProvider.getInstance().processEvents();

		// When everything done we need to relayout components.
		LayoutManager.getInstance().layout(frame);

		// Run animations. Should be also called cause some components use animations
		// for updating state.
		AnimatorProvider.getAnimator().runAnimations();
	}

	public static void quit() {
		
	}

}

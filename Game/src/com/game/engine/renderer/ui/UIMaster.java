package com.game.engine.renderer.ui;

import com.game.engine.display.DisplayManager;
import com.spinyowl.legui.DefaultInitializer;
import com.spinyowl.legui.component.Button;
import com.spinyowl.legui.component.Frame;
import com.spinyowl.legui.style.border.SimpleLineBorder;
import com.spinyowl.legui.style.color.ColorConstants;
import com.spinyowl.legui.system.renderer.Renderer;

/**
 * @author laptop
 * @date Dec. 13, 2021
 * 
 */
public class UIMaster {

	private static final String vertexShaderSource = "#version 330 core\n" + "layout (location = 0) in vec3 aPos;\n" + "\n"
			+ "void main()\n" + "{\n" + "    gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);\n" + "}";
	private static final String fragmentShaderSource = "#version 330 core\n" + "out vec4 FragColor;\n" + "\n" + "void main()\n"
			+ "{\n" + "    FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);\n" + "} ";
	private static final float[] vertices = { -0.5f, -0.5f, 0.0f, 0.5f, -0.5f, 0.0f, 0.0f, 0.5f, 0.0f };
	private static final int[] indices = { // note that we start from 0!
			0, 1, 3, // first Triangle
			1, 2, 3 // second Triangle
	};

	private static DefaultInitializer initializer;
	private static Frame frame;

	// https://github.com/SpinyOwl/legui/blob/develop/src/main/java/com/spinyowl/legui/demo/SingleClassExampleGuiOverGL.java#L319

	public static void init(long window) {
		frame = new Frame(DisplayManager.WIDTH, DisplayManager.HEIGHT);
		// set the background of the UI frame to transparent
		frame.getContainer().getStyle().getBackground().setColor(ColorConstants.transparent());
		frame.getContainer().setFocusable(false);

		Button button = new Button("Add components", 20, 20, 160, 30);
		SimpleLineBorder border = new SimpleLineBorder(ColorConstants.black(), 1);
		button.getStyle().setBorder(border);

		frame.getContainer().add(button);

		initializer = new DefaultInitializer(window, frame);

		Renderer renderer = initializer.getRenderer();
		renderer.initialize();
	}

	public static DefaultInitializer getInitl() {
		return initializer;
	}

	public static void updateScreenSize() {
		frame.setSize(DisplayManager.WIDTH, DisplayManager.HEIGHT);
	}

	public static void render() {

	}

	public static void quit() {

	}

}

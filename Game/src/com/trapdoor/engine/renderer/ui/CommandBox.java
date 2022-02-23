package com.trapdoor.engine.renderer.ui;

import java.util.HashMap;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.lwjgl.glfw.GLFW;

import com.spinyowl.legui.component.Layer;
import com.spinyowl.legui.component.TextArea;
import com.spinyowl.legui.component.TextInput;
import com.spinyowl.legui.style.Style.DisplayType;
import com.trapdoor.engine.datatypes.commands.Command;
import com.trapdoor.engine.datatypes.commands.HelloWorldCommand;
import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.registry.annotations.AnnotatedClass;
import com.trapdoor.engine.registry.annotations.AnnotationHandler;
import com.trapdoor.engine.registry.annotations.ClearScreenEventSubscriber;
import com.trapdoor.engine.tools.input.IKeyState;

/**
 * @author brett
 * @date Feb. 13, 2022
 * 
 */
public class CommandBox implements IKeyState, AnnotatedClass {

	private static CommandBox box;
	private static HashMap<String, Command> commands = new HashMap<String, Command>();
	
	private Layer layer;
	private boolean enabled;
	private boolean closeable = false;
	
	private String commandOutputBuffer;
	private Object lastObject;
	private TextInput in;
	private TextArea label;
	private CommandLineParser parser;
	
	protected CommandBox() {
		this.layer = new Layer();
		this.parser = new DefaultParser();
		
		layer.setSize(DisplayManager.WIDTH, DisplayManager.HEIGHT);
		layer.getStyle().getBackground().getColor().w = 0.0f;
	
		label = new TextArea(30, 30, DisplayManager.WIDTH - 60, DisplayManager.HEIGHT - 120);
		label.getStyle().getBackground().getColor().w = 0.6f;
		label.setFocusable(false);
		label.setHorizontalScrollBarVisible(false);
		label.setEditable(false);
		layer.add(label);
		
		in = new TextInput("", 30, DisplayManager.HEIGHT - 80, DisplayManager.WIDTH - 60, 40);
		in.getStyle().getBackground().getColor().w = 0.6f;
		in.getFocusedStyle().getBackground().getColor().w = 0.8f;
		layer.add(in);
		
		layer.setEnabled(enabled);
		layer.getStyle().setDisplay(enabled == true ? DisplayType.MANUAL : DisplayType.NONE);
		UIMaster.getMasterFrame().addLayer(layer);
	}
	
	@Override
	public void onKeyPressed(int keys) {
		
	}

	@Override
	public void onKeyReleased(int keys) {
		if (keys == GLFW.GLFW_KEY_ENTER) {
			AnnotationHandler.cleanScreen();
			
			if (closeable)
				enabled = !enabled;
			else {
				enabled = true;
				pushCommand();
			}
			if (enabled)
				DisplayManager.setGrabbed(false);
			else
				DisplayManager.setGrabbed(true);
			toggle();
		}
		if (keys == GLFW.GLFW_KEY_ESCAPE) {
			if (enabled)
				DisplayManager.setGrabbed(false);
			enabled = false;
			toggle();
		}
	}
	
	private void toggle() {
		layer.setEnabled(enabled);
		in.setEnabled(enabled);
		in.setEditable(enabled);
		layer.getStyle().setDisplay(enabled == true ? DisplayType.MANUAL : DisplayType.NONE);
	}
	
	private void pushCommand() {
		String line = in.getTextState().getText();
		String trim = line.trim();
		if (line.length() <= 2 && !(trim.isBlank() || trim.isEmpty()) && !trim.contentEquals("") )
			return;
		processLine(trim);
	}
	
	private void processLine(String line) {
		commandOutputBuffer = null;
		String[] indv = line.split(" ");
		Command c = commands.get(indv[0].toLowerCase());
		if (c != null) {
			try {
				lastObject = c.run(parser.parse(c.getOptions(), indv), lastObject);
				commandOutputBuffer = lastObject.toString();
			} catch (ParseException e) {
				commandOutputBuffer = "Unable to run command. Please refer to the help.\nHere is what I know: \n" + e.getMessage();
			}
		} else
			return;
		if (commandOutputBuffer == null || commandOutputBuffer.trim().isBlank() || commandOutputBuffer.trim().isEmpty())
			return;
		String existing = label.getTextState().getText();
		existing += "\n";
		existing += "'" + line + "' : ";
		existing += "\n";
		existing += "\t" + commandOutputBuffer;
		
		in.getTextState().setText("");
		label.getTextState().setText(existing);
	}
	
	public static void registerCommand(String val, Command command) {
		commands.put(val.toLowerCase(), command);
	}
	
	@ClearScreenEventSubscriber
	public static void clearScreen() {
		box.enabled = false;
		box.layer.setEnabled(box.enabled);
		box.layer.getStyle().setDisplay(box.enabled == true ? DisplayType.MANUAL : DisplayType.NONE);
	}
	
	public static void init() {
		box = new CommandBox();
		registerCommand("hello", new HelloWorldCommand());
	}
	
	public static CommandBox getInstance() {
		return box;
	}
	
}

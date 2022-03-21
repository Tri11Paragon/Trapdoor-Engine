package com.trapdoor.engine.renderer.ui;

import java.util.HashMap;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.lwjgl.glfw.GLFW;

import com.trapdoor.engine.datatypes.commands.Command;
import com.trapdoor.engine.datatypes.commands.HelloWorldCommand;
import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.registry.GameRegistry;
import com.trapdoor.engine.registry.annotations.AnnotatedClass;
import com.trapdoor.engine.registry.annotations.AnnotationHandler;
import com.trapdoor.engine.registry.annotations.ClearScreenEventSubscriber;
import com.trapdoor.engine.tools.input.IKeyState;
import com.trapdoor.engine.tools.input.Keyboard;

import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;

/**
 * @author brett
 * @date Feb. 13, 2022
 * 
 */
public class Console implements IKeyState, AnnotatedClass {

	private static Console box;
	private static HashMap<String, Command> commands = new HashMap<String, Command>();
	private static final HashMap<Integer, Character> glfwValidDecode = new HashMap<Integer, Character>();
	private static final HashMap<Integer, Character> glfwValidCaptialDecode = new HashMap<Integer, Character>();
	
	private boolean enabled = false;
	private boolean enabledPrev = false;
	private boolean outputLastFrame = false;
	
	private String commandOutputBuffer;
	private Object lastObject;
	private CommandLineParser parser;
	
	private String line = "";
	private String output = "";
	
	protected Console() {
		this.parser = new DefaultParser();
	}
	
	float winX = DisplayManager.WIDTH/2;
	float winY = DisplayManager.HEIGHT/2;
	
	private float inputY = 18;
	
	public void render() {
		if (!enabled)
			return;
		ImGui.pushFont(GameRegistry.getFont("roboto-regular"));
		
		ImGui.setNextWindowPos(DisplayManager.windowPosX + 5, DisplayManager.windowPosY + 5, ImGuiCond.Appearing);
		ImGui.setNextWindowSize(winX, winY + inputY + 65, ImGuiCond.Once);
		ImGui.begin("Console", ImGuiWindowFlags.NoScrollbar);
		
		winX = ImGui.getWindowSizeX();
		winY = ImGui.getWindowSizeY();
		ImGui.beginChild("Output Buffer", winX, winY - inputY - 46, false, ImGuiWindowFlags.AlwaysVerticalScrollbar);
			if (outputLastFrame) {
				ImGui.setScrollY(winY + ImGui.getScrollMaxY());
				outputLastFrame = false;
			}
			
			ImGui.text(output);
		ImGui.endChild();
		
		
		ImGui.bullet();
		ImGui.text(line);
		
		inputY = ImGui.getItemRectSizeY();
		
		ImGui.end();
		ImGui.popFont();
	}
	
	public void append(String str) {
		output += str;
	}
	
	@Override
	public void onKeyPressed(int keys) {
		if (keys == GLFW.GLFW_KEY_GRAVE_ACCENT) {
			AnnotationHandler.cleanScreen();
			
			if (enabledPrev)
				enabled = false;
			else {
				enabled = true;
			}
			DisplayManager.setGrabbed(!enabled);
		}
		if (!enabled)
			return;
		if (keys == GLFW.GLFW_KEY_ESCAPE) {
			enabled = false;
		} else if (keys == GLFW.GLFW_KEY_ENTER) {
			pushCommand();
		} else if (keys == GLFW.GLFW_KEY_BACKSPACE) {
			if (line.length() > 0) {
				if (Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL)) {
					int li = line.lastIndexOf(' ');
					line = line.substring(0, li);
				} else {
					line = line.substring(0, line.length()-1);
				}
			}
			outputLastFrame = true;
		} else {
			Character c = null;
			if (Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || Keyboard.isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT)) 
				c =  glfwValidCaptialDecode.get(keys);
			else
				c = glfwValidDecode.get(keys);
			
			if (c != null)
				line += c;
			outputLastFrame = true;
		}
		DisplayManager.setGrabbed(!enabled);
	}

	@Override
	public void onKeyReleased(int keys) {
		
	}
	
	private void pushCommand() {
		String trim = line.trim();
		if (line.length() <= 2 && !(trim.isBlank() || trim.isEmpty()) && !trim.contentEquals("") )
			return;
		processLine(trim);
		line = "";
	}
	
	private void processLine(String line) {
		commandOutputBuffer = "";
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
			commandOutputBuffer += "Command not found!";
		if (commandOutputBuffer == null || commandOutputBuffer.trim().isBlank() || commandOutputBuffer.trim().isEmpty())
			return;
		String existing = output;
		existing += "\n";
		existing += "'" + line + "' : ";
		existing += "\n";
		existing += "\t" + commandOutputBuffer;
		
		output = existing;
		outputLastFrame = true;
	}
	
	public static void registerCommand(String val, Command command) {
		commands.put(val.toLowerCase(), command);
	}
	
	@ClearScreenEventSubscriber
	public static void clearScreen() {
		box.enabledPrev = box.enabled;
		box.enabled = false;
	}
	
	public static void init() {
		// I am doing it this way because i want to be able to filter and use special characters
		glfwValidDecode.put(GLFW.GLFW_KEY_0, '0');
		glfwValidDecode.put(GLFW.GLFW_KEY_1, '1');
		glfwValidDecode.put(GLFW.GLFW_KEY_2, '2');
		glfwValidDecode.put(GLFW.GLFW_KEY_3, '3');
		glfwValidDecode.put(GLFW.GLFW_KEY_4, '4');
		glfwValidDecode.put(GLFW.GLFW_KEY_5, '5');
		glfwValidDecode.put(GLFW.GLFW_KEY_6, '6');
		glfwValidDecode.put(GLFW.GLFW_KEY_7, '7');
		glfwValidDecode.put(GLFW.GLFW_KEY_8, '8');
		glfwValidDecode.put(GLFW.GLFW_KEY_9, '9');
		glfwValidDecode.put(GLFW.GLFW_KEY_Q, 'q');
		glfwValidDecode.put(GLFW.GLFW_KEY_W, 'w');
		glfwValidDecode.put(GLFW.GLFW_KEY_E, 'e');
		glfwValidDecode.put(GLFW.GLFW_KEY_R, 'r');
		glfwValidDecode.put(GLFW.GLFW_KEY_T, 't');
		glfwValidDecode.put(GLFW.GLFW_KEY_Y, 'y');
		glfwValidDecode.put(GLFW.GLFW_KEY_U, 'u');
		glfwValidDecode.put(GLFW.GLFW_KEY_I, 'i');
		glfwValidDecode.put(GLFW.GLFW_KEY_O, 'o');
		glfwValidDecode.put(GLFW.GLFW_KEY_P, 'p');
		glfwValidDecode.put(GLFW.GLFW_KEY_A, 'a');
		glfwValidDecode.put(GLFW.GLFW_KEY_S, 's');
		glfwValidDecode.put(GLFW.GLFW_KEY_D, 'd');
		glfwValidDecode.put(GLFW.GLFW_KEY_F, 'f');
		glfwValidDecode.put(GLFW.GLFW_KEY_G, 'g');
		glfwValidDecode.put(GLFW.GLFW_KEY_H, 'h');
		glfwValidDecode.put(GLFW.GLFW_KEY_J, 'j');
		glfwValidDecode.put(GLFW.GLFW_KEY_K, 'k');
		glfwValidDecode.put(GLFW.GLFW_KEY_L, 'l');
		glfwValidDecode.put(GLFW.GLFW_KEY_Z, 'z');
		glfwValidDecode.put(GLFW.GLFW_KEY_X, 'x');
		glfwValidDecode.put(GLFW.GLFW_KEY_C, 'c');
		glfwValidDecode.put(GLFW.GLFW_KEY_V, 'v');
		glfwValidDecode.put(GLFW.GLFW_KEY_B, 'b');
		glfwValidDecode.put(GLFW.GLFW_KEY_N, 'n');
		glfwValidDecode.put(GLFW.GLFW_KEY_M, 'm');
		glfwValidDecode.put(GLFW.GLFW_KEY_LEFT_BRACKET, '[');
		glfwValidDecode.put(GLFW.GLFW_KEY_RIGHT_BRACKET, ']');
		glfwValidDecode.put(GLFW.GLFW_KEY_BACKSLASH, '\\');
		glfwValidDecode.put(GLFW.GLFW_KEY_SEMICOLON, ';');
		glfwValidDecode.put(GLFW.GLFW_KEY_APOSTROPHE, '\'');
		glfwValidDecode.put(GLFW.GLFW_KEY_COMMA, ',');
		glfwValidDecode.put(GLFW.GLFW_KEY_PERIOD, '.');
		glfwValidDecode.put(GLFW.GLFW_KEY_SLASH, '/');
		glfwValidDecode.put(GLFW.GLFW_KEY_SPACE, ' ');
		glfwValidDecode.put(GLFW.GLFW_KEY_MINUS, '-');
		glfwValidDecode.put(GLFW.GLFW_KEY_EQUAL, '=');
		
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_0, ')');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_1, '!');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_2, '@');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_3, '#');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_4, '$');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_5, '%');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_6, '^');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_7, '&');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_8, '*');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_9, '(');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_Q, 'Q');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_W, 'W');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_E, 'E');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_R, 'R');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_T, 'T');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_Y, 'Y');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_U, 'U');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_I, 'I');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_O, 'O');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_P, 'P');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_A, 'A');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_S, 'S');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_D, 'D');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_F, 'F');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_G, 'G');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_H, 'H');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_J, 'J');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_K, 'K');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_L, 'L');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_Z, 'Z');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_X, 'X');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_C, 'C');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_V, 'V');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_B, 'B');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_N, 'N');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_M, 'M');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_LEFT_BRACKET, '{');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_RIGHT_BRACKET, '}');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_BACKSLASH, '|');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_SEMICOLON, ':');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_APOSTROPHE, '"');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_COMMA, '<');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_PERIOD, '>');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_SLASH, '?');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_SPACE, ' ');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_MINUS, '_');
		glfwValidCaptialDecode.put(GLFW.GLFW_KEY_EQUAL, '+');
		box = new Console();
		registerCommand("hello", new HelloWorldCommand());
	}
	
	public static Console getInstance() {
		return box;
	}
	
}

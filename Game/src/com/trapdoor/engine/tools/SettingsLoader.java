package com.trapdoor.engine.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.lwjgl.glfw.GLFW;

import com.trapdoor.Main;
import com.trapdoor.engine.ProjectionMatrix;
import com.trapdoor.engine.TextureLoader;
import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.renderer.shadows.ShadowMap;

/**
*
* @author brett
*
* saves and loads settings from the settings file
*
*/

public class SettingsLoader {

	private static String SETTINGS_LOCATION = "settings.txt";
	private static final HashMap<Integer, String> comments = new HashMap<Integer, String>();
	
	/*
	 * some static variable definitions for classes that need them.
	 */
	public static int KEY_CONSOLE = GLFW.GLFW_KEY_GRAVE_ACCENT;
	public static int KEY_CLEAR = GLFW.GLFW_KEY_F6;
	public static int SAMPLES = 4;
	public static int AF = 4;
	public static double SENSITIVITY_X = 0.5d;
	public static double SENSITIVITY_Y = 0.5d;
	public static int VSYNC = 0;
	public static int RENDER_DISTANCE = 8;
	public static float GAMMA = 2.2f;
	public static float MUSIC_GAIN = 0.5f;
	public static int GRAPHICS_LEVEL = 0;
	
	private static int readLines = 1;
	public static void loadSettings() {
		String set = SETTINGS_LOCATION;
		if (Main.os.toLowerCase().contains("linux")) {
			SETTINGS_LOCATION = Main.user_home + "/.local/share/Trapdoor/" + set;
			new File(Main.user_home + "/.local/share/Trapdoor/").mkdirs();
		} else {
			SETTINGS_LOCATION = Main.user_home + "/Documents/Trapdoor/" + set;
			new File(Main.user_home + "/Documents/Trapdoor/").mkdirs();
		}
		
		Logging.logger.debug("Loading settings!");
		try {
			// load the settings file
			BufferedReader reader = new BufferedReader(new FileReader(SETTINGS_LOCATION));
			String line;
			while((line = reader.readLine()) != null) {
				// don't process lines that are commented.
				// Python, java and Lua comment prefixes.
				if (line.startsWith("#") || line.startsWith("//") || line.startsWith("--")) {
					// put the comments in memory so that way when we save settings
					// it keeps the comments
					comments.put(readLines, line);
					// a marker to what line pos we put the comment at.
					readLines++;
					continue;
				}
				String[] name = line.split(":");
				// make sure we have a good comparison
				name[0] = name[0].toLowerCase();
				if (name[0].equals("fov"))
					ProjectionMatrix.FOV = Float.parseFloat(name[1]);
				if (name[0].equals("fps"))
					DisplayManager.FPS_MAX = (int) Float.parseFloat(name[1]);
				if (name[0].equals("key_console"))
					KEY_CONSOLE = (int) Float.parseFloat(name[1]);
				if (name[0].equals("key_clear"))
					KEY_CLEAR = (int) Float.parseFloat(name[1]);
				if (name[0].equals("sensitivity_x"))
					SENSITIVITY_X = Double.parseDouble(name[1]);
				if (name[0].equals("sensitivity_y"))
					SENSITIVITY_Y = Double.parseDouble(name[1]);
				if (name[0].equals("samples"))
					SAMPLES = (int) Float.parseFloat(name[1]);
				if (name[0].equals("anisotropy"))
					AF = (int) Float.parseFloat(name[1]);
				if (name[0].equals("vsync"))
					VSYNC = (int)Float.parseFloat(name[1]);
				if (name[0].equals("lod"))
					TextureLoader.TEXTURE_LOD = Float.parseFloat(name[1]);
				if (name[0].equals("tscale"))
					TextureLoader.TEXTURE_SCALE = (int) Float.parseFloat(name[1]);
				if (name[0].equals("gamma"))
					GAMMA = Float.parseFloat(name[1]);
				if (name[0].equals("music_gain"))
					MUSIC_GAIN = Float.parseFloat(name[1]);
				if (name[0].equals("graphics"))
					GRAPHICS_LEVEL = (int)Float.parseFloat(name[1]);
				if (name[0].equals("shadowmap"))
					ShadowMap.SHADOW_MAP_HEIGHT = ShadowMap.SHADOW_MAP_WIDTH = (int)Float.parseFloat(name[1]);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			saveSettings();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	public static void saveSettings() {
		try {
			// reads file
			BufferedWriter writer = new BufferedWriter(new FileWriter(SETTINGS_LOCATION));
			// clears the file
			writer.write("");
			// write all the important data that needs saving
			writeLine(writer, "FOV: " + ProjectionMatrix.FOV);
			writeLine(writer, "FPS: " + DisplayManager.FPS_MAX);
			writeLine(writer, "key_console: " + KEY_CONSOLE);
			writeLine(writer, "key_clear: " + KEY_CLEAR);
			writeLine(writer, "sensitivity_x: " + SENSITIVITY_X);
			writeLine(writer, "sensitivity_y: " + SENSITIVITY_Y);
			writeLine(writer, "samples: " + SAMPLES);
			writeLine(writer, "anisotropy: " + AF);
			writeLine(writer, "vsync: " + VSYNC);
			writeLine(writer, "lod: " + TextureLoader.TEXTURE_LOD);
			writeLine(writer, "tscale: " + TextureLoader.TEXTURE_SCALE);
			writeLine(writer, "gamma: " + GAMMA);
			writeLine(writer, "music_gain: " + MUSIC_GAIN);
			writeLine(writer, "graphics: " + GRAPHICS_LEVEL);
			writeLine(writer, "shadowmap: " + ShadowMap.SHADOW_MAP_WIDTH);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static int writeLines = 1;
	private static void writeLine(BufferedWriter writer, String s) throws IOException {
		// get the comment for this current line
		String comment = comments.get(writeLines);
		if (comment != null) {
			// write the comment
			writer.append(comment);
			writer.newLine();
			// Increase the line we are writing
			writeLines++;
			// try to write the line again
			writeLine(writer, s);
			return;
		}
		// write this line
		writer.append(s);
		writer.newLine();
		writeLines++;
	}
	
}

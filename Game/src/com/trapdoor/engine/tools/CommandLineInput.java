package com.trapdoor.engine.tools;

import java.util.ArrayList;
import java.util.HashMap;

public class CommandLineInput {
	
	// map of options to their values
	public static final HashMap<String, String> options = new HashMap<String, String>();
	// list of options supplied
	public static final ArrayList<String> usedOptions = new ArrayList<String>();
	
	public static final HashMap<Character, String> flags = new HashMap<Character, String>();
	public static final ArrayList<Character> usedFlags = new ArrayList<Character>();
	
	private static final HashMap<String, CommandLineInputListener> mapO = new HashMap<String, CommandLineInputListener>();
	private static final HashMap<Character, CommandLineInputListener> mapF = new HashMap<Character, CommandLineInputListener>();

	public static void decode(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i] == null)
				continue;
			if (args[i].startsWith("--")) {
				String option = args[i].substring(2);
				usedOptions.add(option);
				int k = i + 1;
				String data = null;
				if (k < args.length) {
					data = args[k];
				}
				options.put(option, data);
				CommandLineInputListener lis = mapO.get(option);
				if (lis != null)
					lis.event(data);
			}
			if (args[i].startsWith("-")) {
				char option = args[i].substring(1).toCharArray()[0];
				usedFlags.add(option);
				int k = i + 1;
				String data = null;
				if (k < args.length) {
					data = args[k];
				}
				flags.put(option, data);
				CommandLineInputListener lis = mapF.get(option);
				if (lis != null)
					lis.event(data);
			}
		}
	}
	
	public static void callOptionAssigners() {
		for (int i = 0; i < usedOptions.size(); i++)
			if (mapO.get(usedOptions.get(i)) != null)
				mapO.get(usedOptions.get(i)).event(options.get(usedOptions.get(i)));
		for (int i = 0; i < usedFlags.size(); i++)
			if (mapF.get(usedFlags.get(i)) != null)
				mapF.get(usedFlags.get(i)).event(flags.get(usedFlags.get(i)));
	}
	
	public static void registerCommandLineProcessor(String option, CommandLineInputListener listener) {
		mapO.put(option, listener);
	}
	
	public static void registerCommandLineProcessor(Character option, CommandLineInputListener listener) {
		mapF.put(option, listener);
	}
	
	public interface CommandLineInputListener {
		
		public void event(String data);
		
	}
	
}

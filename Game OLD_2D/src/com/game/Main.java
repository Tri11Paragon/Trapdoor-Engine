package com.game;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;

import com.game.engine.display.DisplayManager;
import com.game.engine.display.IDisplay;
import com.game.engine.display.TestDisplay;
import com.game.engine.tools.CommandLineInput;
import com.game.engine.tools.Logger;


public class Main {

	public static boolean isOpen = true;
	public static MemoryMXBean mx;
	public static OperatingSystemMXBean osx;
	public static RuntimeMXBean rnx;
	public static ThreadMXBean thx;
	public static String os;
	public static String os_version;
	public static String os_arch;
	public static String file_separator;
	public static String path_separator;
	public static String line_separator;
	public static String user_name;
	public static String user_home;
	public static String user_workingdir;
	public static String token = null;
	public static String username = null;
	public static String password = null;
	public static boolean verbose = true;
	public static int processors = 8;
	
	public static void main(String[] args) {
		// decode data supplied through the command line
		CommandLineInput.decode(args);
		
		/* register option listeners here */
		CommandLineInput.registerCommandLineProcessor("username", (String data) -> {
			username = data;
		});
		CommandLineInput.registerCommandLineProcessor("password", (String data) -> {
			password = data;
		});
		
		// verbose console output is default. only disable if on potato pc.
		CommandLineInput.registerCommandLineProcessor("verbose", (String data) -> {
			verbose = false;
		});
		
		CommandLineInput.registerCommandLineProcessor('v', (String data) -> {
			verbose = false;
		});
		
		// assign the variables.
		CommandLineInput.callOptionAssigners();
		
		// create the logger instance
		Logger.init("logs.txt");
		
		/**
		 * Assign system variables
		 */
		mx = ManagementFactory.getMemoryMXBean();
		osx = ManagementFactory.getOperatingSystemMXBean();
		rnx = ManagementFactory.getRuntimeMXBean();
		thx = ManagementFactory.getThreadMXBean();
		os = System.getProperty("os.name");
		os_version = System.getProperty("os.version");
		os_arch = System.getProperty("os.arch");
		file_separator = System.getProperty("file.separator");
		path_separator = System.getProperty("path.separator");
		line_separator = System.getProperty("line.separator");
		user_name = System.getProperty("user.name");
		user_home = System.getProperty("user.home");
		user_workingdir = System.getProperty("user.dir");
		processors = Runtime.getRuntime().availableProcessors();
		if (processors < 4)
			processors = 4;
		
		// create the display
		DisplayManager.createDisplay(false);
		
		/**
		 * Display important info about OS (After game info is displayed)
		 */
		Logger.writeln();
		Logger.printBoxYln(new String[] {
				"OS: " + os + " " + os_version + " " + os_arch,
				"User: " + user_name + "@" + user_home,
				"Working Directory: " + user_workingdir,
				"Number of cores: " + processors,
				"Current Thread: " + Thread.currentThread()
				});
		
		IDisplay dis = new TestDisplay();
		DisplayManager.createDisplay(dis);
		DisplayManager.changeDisplay(dis);
		
		// starts the update display loop
		DisplayManager.updateDisplay();
		// delete all glfw / gl stuff
		DisplayManager.closeDisplay();
		
		// close the file stream
		Logger.close();
	}
	
	public static String reverse(String str) {
		return reverseI(str, 0);
	}
	
	public static String reverseI(String str, int i) {
		if (i == (str.length()-1))
			return "" + str.toCharArray()[i];
		return reverseI(str, i + 1) + str.toCharArray()[i];
	}

}

package com.trapdoor;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;

import com.jme3.system.NativeLibraryLoader;
import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.display.IDisplay;
import com.trapdoor.engine.display.LoadingScreenDisplay;
import com.trapdoor.engine.registry.Threading;
import com.trapdoor.engine.registry.annotations.AnnotationHandler;
import com.trapdoor.engine.tools.CommandLineInput;
import com.trapdoor.engine.tools.Logging;
import com.trapdoor.engine.tools.SettingsLoader;


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
	public static boolean devMode = false;
	public static int processors = 8;
	
	public static void main(String[] args) throws InterruptedException {
		Thread.currentThread().setName("Render Thread");
		
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
		
		// create the logger instance
		Logging.init();
		
		NativeLibraryLoader.loadLibbulletjme(true, new File(user_workingdir + "/natives"), "Release", "Sp");
		
		// mvn clean compile assembly:single 
		
		//https://mkyong.com/maven/how-to-create-a-manifest-file-with-maven/
		// https://github.com/SpinyOwl/legui
		
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
		
		// enable developer mode
		CommandLineInput.registerCommandLineProcessor('d', (String data) -> {
			devMode = true;
		});
		
		CommandLineInput.registerCommandLineProcessor("dev", (String data) -> {
			devMode = true;
		});
		
		// assign the variables.
		CommandLineInput.callOptionAssigners();
		
		AnnotationHandler.init();
		
		SettingsLoader.loadSettings();
		Threading.init(processors-1);

		/**
		 * Display important info about OS (After game info is displayed)
		 */
		Logging.infoInsideBox(
				"OS: " + os + " " + os_version + " " + os_arch,
				"Working Directory: " + user_workingdir,
				"Number of cores: " + processors,
				"Current Thread: " + Thread.currentThread() + "\n"
				);
		
		// create the display
		DisplayManager.createDisplay(false);
		
		IDisplay dis = new LoadingScreenDisplay();
		DisplayManager.createDisplay(dis);
		DisplayManager.changeDisplay(dis);
		
		// starts the update display loop
		DisplayManager.updateDisplay();
		// delete all glfw / gl stuff
		DisplayManager.closeDisplay();
		SettingsLoader.saveSettings();
		
		while (DisplayManager.exited < DisplayManager.createdThreads) 
			Thread.sleep(16);
		Logging.logger.info("Goodbye!");
		
		System.exit(0);
	}
	
	public static void gb(ArrayList<String> strarr, String str, int n) {
		if (n == 0) {
			strarr.add(str);
		} else {
			gb(strarr, str + "0", n-1);
			gb(strarr, str + "1", n-1);
		}
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

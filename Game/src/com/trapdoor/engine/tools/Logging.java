package com.trapdoor.engine.tools;

import java.io.PrintStream;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Logging {

	//static {
		//ConfigurationFactory.setConfigurationFactory(new CustomConfigFactory());
	//}
	public static final Logger logger = LogManager.getRootLogger();
	
	public static void init() {
		System.setErr(new PrintStream(new LoggingOutputStream(logger, Level.ERROR)));
		System.setOut(new PrintStream(new LoggingOutputStream(logger, Level.TRACE)));
		logger.debug("Logger Init Successful");
	}
	
	public static void infoInsideBox(String... lines) {
		String[] box = createBox(lines, 0);
		for (int i = 0; i < box.length; i++)
			logger.info(box[i]);
	}
	
	public static void infoInsideBox(int buffer, String... lines) {
		String[] box = createBox(lines, buffer);
		for (int i = 0; i < box.length; i++)
			logger.info(box[i]);
	}
	
	public static void debugInsideBox(String... lines) {
		String[] box = createBox(lines, 0);
		for (int i = 0; i < box.length; i++)
			logger.debug(box[i]);
	}
	
	public static void debugInsideBox(int buffer, String... lines) {
		String[] box = createBox(lines, buffer);
		for (int i = 0; i < box.length; i++)
			logger.debug(box[i]);
	}
	
	public static void traceInsideBox(String... lines) {
		String[] box = createBox(lines, 0);
		for (int i = 0; i < box.length; i++)
			logger.trace(box[i]);
	}
	
	public static void traceInsideBox(int buffer, String... lines) {
		String[] box = createBox(lines, buffer);
		for (int i = 0; i < box.length; i++)
			logger.trace(box[i]);
	}
	
	public static void errorInsideBox(String... lines) {
		String[] box = createBox(lines, 0);
		for (int i = 0; i < box.length; i++)
			logger.error(box[i]);
	}
	
	public static void errorInsideBox(int buffer, String... lines) {
		String[] box = createBox(lines, buffer);
		for (int i = 0; i < box.length; i++)
			logger.error(box[i]);
	}
	
	private static String[] createBox(String[] lines, int buffer) {
		String[] returnLines = new String[lines.length + 2];
		
		int maxLength = 0;
		for (int i = 0; i < lines.length; i++) {
			char[] charArr = lines[i].toCharArray();
			if (charArr.length > maxLength)
				maxLength = charArr.length;
		}
		maxLength += buffer;
		
		StringBuilder linebuilder = new StringBuilder();
		for (int i = 0; i < maxLength; i++)
			linebuilder.append('-');
		
		for (int i = 0; i < lines.length; i++)
			returnLines[i + 1] = lines[i];
		
		returnLines[0] = linebuilder.toString();
		returnLines[returnLines.length - 1] = returnLines[0];
		
		return returnLines;
	}
	
}

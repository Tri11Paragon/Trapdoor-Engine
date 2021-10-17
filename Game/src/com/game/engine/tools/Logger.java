package com.game.engine.tools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

	private static BufferedWriter writer;
	
	public static void init(String file) {
		try {
			writer = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void close() {
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * prints out a box with ------ covering the top and bottom
	 * @param info lines to be printed in the box
	 */
	public static void printBoxY(String[] info) {
		int maxchars = 0;
		for (int i = 0; i < info.length; i++) {
			if (info[i].length() > maxchars)
				maxchars = info[i].length();
		}
		for (int i = 0; i < maxchars; i++)
			write("-");
		writeln();
		for (int i = 0; i < info.length; i++)
			writeln(info[i]);
		for (int i = 0; i < maxchars; i++)
			write("-");
		writeln();
	}
	
	/**
	 * prints out a box with ------ covering the top and bottom plus an extra newline char after the last line
	 * @param info lines to be printed in the box
	 */
	public static void printBoxYln(String[] info) {
		printBoxY(info);
		writeln();
	}
	
	public static void write(String data) {
		try {
			writer.write(data);
			System.out.print(data);
		} catch (Exception e) {}
	}
	
	public static void writeln(String data) {
		try {
			writer.write(data);
			writer.write("\n");
			System.out.println(data);
		} catch (Exception e) {}
	}
	
	public static void writeln() {
		try {
			writer.write("\n");
			System.out.println();
		} catch (Exception e) {}
	}
	
}

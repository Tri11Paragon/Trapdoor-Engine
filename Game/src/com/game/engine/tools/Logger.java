package com.game.engine.tools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Logger {

	private static BufferedWriter writer;
	private static StringBuilder builder = new StringBuilder();
	
	private static String formattedDate;
	private static String t;
	
	public static void init(String file) {
		try {
			writer = new BufferedWriter(new FileWriter(file));
			LocalDate date = LocalDate.now();
			Date d = new Date((long)(System.currentTimeMillis()));
			t = new SimpleDateFormat("HH:mm:ss").format(d);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			formattedDate = date.format(formatter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String formattedDate() {
		return formattedDate;
	}
	
	public static String formattedTime() {
		return t;
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
				maxchars = addDate(info[i]).length();
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
	
	private static String addDate(String data) {
		createDateBuilder();
		builder.append(data);
		return builder.toString();
	}
	
	public static void writeDate() {
		try {
			createDateBuilder();
			writer.write(builder.toString());
			System.out.print(builder.toString());
		} catch (Exception e) {}
	}
	
	public static void writeln(String data) {
		try {
			createDateBuilder();
			builder.append(data);
			builder.append('\n');
			writer.write(builder.toString());
			System.out.print(builder.toString());
		} catch (Exception e) {}
	}
	
	public static void writeln() {
		try {
			writer.write("\n");
			System.out.println();
		} catch (Exception e) {}
	}
	
	private static void createDateBuilder() {
		builder.setLength(0);
		builder.append('[');
		//builder.append(formattedDate);
		//builder.append(' ');
		//builder.append('@');
		//builder.append(' ');
		builder.append(t);
		builder.append(']');
		builder.append(':');
		builder.append(' ');
	}
	
}

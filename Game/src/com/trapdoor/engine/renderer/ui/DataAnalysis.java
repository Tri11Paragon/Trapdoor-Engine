package com.trapdoor.engine.renderer.ui;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

import com.trapdoor.engine.display.DisplayManager;
import com.trapdoor.engine.registry.GameRegistry;
import com.trapdoor.engine.tools.Logging;

import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.type.ImInt;
import imgui.type.ImString;

/**
 * @author brett
 * @date Apr. 5, 2022
 * A simple class for calculating and displaying in game information about various datasets
 */
public class DataAnalysis {
	
	private ArrayList<Double> data;
	
	protected double mean;
	protected double median;
	protected double std;
	protected double min, max;
	protected double pmin = 512, pmax;
	
	private double sum;
	private double values;
	private double sumOfValuesSquared;
	
	private long lastRan = 0;
	private long timeInMs = 500;
	
	private String name;
	
	private int[] lastDataLength = new int[1];
	private float[] lastDatas = new float[1024];
	
	private boolean freeze = false;
	private ImString input = new ImString();
	
	private ImInt minInput = new ImInt(256);
	private ImInt maxInput = new ImInt(2048);
	
	private int autoClear;
	
	public DataAnalysis(String name) {
		this(name, 8192);
	}
	
	public DataAnalysis(String name, int size) {
		this(name, size, 500);
	}
	
	public DataAnalysis(String name, int startingSize, long timeInMs) {
		data = new ArrayList<Double>(startingSize);
		this.timeInMs = timeInMs;
		this.name = name;
		lastDataLength[0] = lastDatas.length;
	}
	
	/**
	 * calculates out all the stats of the current dataset
	 * WILL NOT RUN IF DATA IS FROZEN.
	 */
	public void calcStats() {
		if (freeze)
			return;
		if (data.size() > 0) {
			min = pmin = lastDatas[0];
			max = pmax = lastDatas[0];
			sum = 0;
			sumOfValuesSquared = 0;
			
			int j = 0;
			for (int i = 0; i < data.size(); i++) {
				double val = data.get(i);
				if (i >= data.size()-lastDatas.length) {
					lastDatas[j++] = (float) (val);
					if (val > pmax)
						pmax = val;
					if (val < pmin)
						pmin = val;
				}
				sum += val;
				if (val > max)
					max = val;
				if (val < min)
					min = val;
			}
			mean = sum / data.size();
			
			for (int i = 0; i < data.size(); i++) {
				values = (data.get(i) - mean);
				sumOfValuesSquared += values * values;
			}
			
			double inter = sumOfValuesSquared / data.size();
			std = Math.sqrt(inter);
			
			
			
			double v1 = data.get((int)Math.floor(data.size()/2.0));
			double v2;
			
			try {
				v2 = data.get((int)Math.ceil(data.size()/2.0));
			} catch (Exception e) {v2 = v1;}
			
			median = (v1 + v2) / 2.0;
		}
	}
	
	/**
	 * writes the current dataset to a file specified by:
	 * @param file the file to write to
	 */
	public void writeToFile(String file) {
		String f = file.trim();
		if (f.isEmpty() || f.isBlank() || f.contentEquals(""))
			return;
		Logging.logger.debug("Writing to data file!");
		try {
			if (data.size() <= 0)
				return;
			BufferedWriter writer = new BufferedWriter(new FileWriter(file), 8192 * 16);
			
			writer.write(name + ",mean,median,stdev,min,max\n");
			
			writer.write(data.get(0) + "," + mean + "," + median + "," + std + "," + min + "," + max);
			
			for (int i = 1; i < data.size(); i++) {
				StringBuilder str = new StringBuilder();
				
				str.append(data.get(i));
				str.append("\n");
				
				writer.write(str.toString());
			}
			
			writer.close();
			Logging.logger.debug("Writing to data file complete!");
		} catch (Exception e) {
			Logging.logger.fatal("Error in write data to file!");
		}
	}
	
	/**
	 * draws the window and will calculate stats every timeInMs
	 */
	public void drawWindow() {
		long time = System.currentTimeMillis();
		if (time - lastRan > timeInMs) {
			lastRan = time;
			calcStats();
		}
		
		ImGui.pushFont(GameRegistry.getFont("roboto-regular"));
		ImGui.setNextWindowPos(DisplayManager.windowPosX + DisplayManager.WIDTH - 542, DisplayManager.windowPosY + 5, ImGuiCond.Appearing);
		ImGui.begin(name);
		
		ImGui.text("Mean: " + mean);
		ImGui.text("Median: " + median);
		ImGui.text("STDEV: " + std);
		ImGui.text("Min/Max: " + min + "/" + max);
		ImGui.text("Graph min/max: " + pmin + "/" + pmax);
		
		float mn = (float)(pmin - std*2);
		float mx = (float)(pmax + std*2);
		
		ImGui.plotLines("", lastDatas, lastDatas.length, 0, "", mn, mx, 512, 64, 4);
		ImGui.plotHistogram("", lastDatas, lastDatas.length, 0, "", mn, mx, 512, 64, 4);
		
		if (ImGui.button("Freeze Data (" + data.size() + ")"))
			freeze = !freeze;
		ImGui.sameLine();
		ImGui.nextColumn();
		if (ImGui.button("Clear Dataset"))
			clear();
		
		ImGui.newLine();
		ImGui.inputInt(" : Min Slider", minInput);
		ImGui.inputInt(" : Max Slider", maxInput);
		ImGui.newLine();
		
		if (ImGui.sliderInt(" : Data Size", lastDataLength, minInput.get(), maxInput.get()))
			if (lastDataLength[0] != lastDatas.length)
				lastDatas = new float[lastDataLength[0]];
		
		
		ImGui.newLine();
		ImGui.inputText(" : Filename", input);
		
		if (ImGui.button("Write"))
			writeToFile(input.get());
			
		ImGui.end();
		ImGui.popFont();
		
		if (this.autoClear > 0 && this.data.size() > this.autoClear)
			clear();
	}
	
	/**
	 * will automatically clear the arrays when the count of elements in the array exceeds this value
	 * @param clearCount the value to clear at
	 */
	public void setAutoClear(int clearCount) {
		this.autoClear = clearCount;
	}
	
	/**
	 * clears the internal arrays
	 */
	public void clear() {
		data = new ArrayList<Double>();
		lastDatas = new float[lastDatas.length];
	}

	/**
	 * adds an element of data to this analysis
	 * @param data the data to add
	 */
	public void add(double data){
		this.data.add(data);
	}
	
}

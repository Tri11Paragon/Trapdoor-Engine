package com.trapdoor.engine.tools.timing;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.trapdoor.engine.tools.Logging;

/**
 * @author laptop
 * @date Feb. 19, 2022
 * 
 */
public class TimeSystem {

	private static final DecimalFormat formater = new DecimalFormat("#,###,###,###,##0.0#####");
	private static final DecimalFormat formater2 = new DecimalFormat("#,###,###,###,##00.00");
	private static final int numberOfClasses = 10;
	private static final int maxLines = 20;
	
	private final long[] frequencies = new long[numberOfClasses];
	private final long[] upperLimits = new long[numberOfClasses];
	private final ArrayList<Long> times = new ArrayList<Long>();
	@SuppressWarnings("unused")
	private final ArrayList<Long> sorted = new ArrayList<Long>();
	
	private String name = "Generic Timer";
	
	private long start = 0;
	private long end = 0;
	
	private long highest = 0;
	private long lowest = Long.MAX_VALUE;
	private long classWidth = 0;
	private long highestFrequency;
	private double freqStep;
	
	private long meanNS, medianNS;
	private double sdNS, varNS;
	
	public double mean, median;
	public double sd, variance;
	
	public TimeSystem() {}
	
	public TimeSystem(String name) {
		this.name = name;
	}
	
	public void start() {
		start = System.nanoTime();
	}
	
	public void end() {
		end = System.nanoTime();
		times.add(end - start);
	}
	
	public void calculateTimes() {
		if (times.size() <= 0)
			return;
		for (int i = 0; i < times.size(); i++) {
			long l = times.get(i);
			meanNS+=l;
			if (l > highest)
				highest = l;
			if (l < lowest)
				lowest = l;
		}
		meanNS /= times.size();
		mean = meanNS / 1000000.0;
		
		classWidth = (highest - lowest) / numberOfClasses;
		upperLimits[0] = classWidth;
		for (int i = 1; i < numberOfClasses; i++) {
			upperLimits[i] = upperLimits[i-1] + classWidth;
		}
		for (int i = 0; i < times.size(); i++) {
			long v = times.get(i);
			for (int j = 0; j < numberOfClasses; j++) {
				if (v > upperLimits[j]) {
					frequencies[j]++;
					break;
				}
			}
		}
		
		for (int i = 0; i < frequencies.length; i++)
			if (frequencies[i] > highestFrequency)
				highestFrequency = frequencies[i];
		
		freqStep = highestFrequency / (double) maxLines;
		
		long m1 = times.get((int)Math.floor(times.size()/2));
		long m2 = times.get((int)Math.ceil(times.size()/2));
		medianNS = (m1 + m2)/2;
		median = medianNS / 1000000.0;
		
		long sum = 0;
		for (int i = 0; i < times.size(); i++) {
			long t = times.get(i) - meanNS;
			sum += t * t;
		}
		sum /= times.size();
		varNS = sum;
		variance = varNS / 1000000.0;
		sdNS = Math.sqrt(sum); 
		sd = sdNS / 1000000.0;
		
		Logging.logger.warn("============={Timing Report}=============");
		Logging.logger.warn("Timer Name: " + name);
		Logging.logger.warn("  -------------------------------------  ");
        Logging.logger.warn("Mean time (ns): " + formater.format(meanNS));
        Logging.logger.warn("Median time (ns): " + formater.format(medianNS));
        Logging.logger.warn("Variance (ns): " + formater.format(varNS));
        Logging.logger.warn("Standard Deviation (ns): " + formater.format(sdNS));
        Logging.logger.warn("  -------------------------------------  ");
        Logging.logger.warn("Mean time (ms): " + formater.format(mean));
        Logging.logger.warn("Median time (ms): " + formater.format(median));
        Logging.logger.warn("Variance (ms): " + formater.format(variance));
        Logging.logger.warn("Standard Deviation (ms): " + formater.format(sd));
        //Logging.logger.warn("  -------------------------------------  ");
        //Logging.logger.warn("Histrogram: ");
        //printLines();
        Logging.logger.warn("=========================================");
	}
	
	@SuppressWarnings("unused")
	private void printLines() {
		for (int i = maxLines; i > 0; i--) {
			double currentFreq = (freqStep * i);
			String line = formater2.format(currentFreq) + " | ";
			String line2 = "";
			for (int j = 0; j < frequencies.length; j++) {
				line2 += j;
				line2 += j;
				line2 += j;
			}
			for (int j = 0; j < frequencies.length; j++) {
				if (currentFreq > frequencies[j]) {
					line2 = line2.replace(j + "" + j + "" + j, "___");
					break;
				}
			}
			line += line2.replaceAll("[0-9]", " ");
			Logging.logger.warn(line);
		}
		
	}
	
}

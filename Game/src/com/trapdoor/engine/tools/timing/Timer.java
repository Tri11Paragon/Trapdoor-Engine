package com.trapdoor.engine.tools.timing;

import com.trapdoor.engine.tools.Logging;

/**
 * @author laptop
 * @date Feb. 19, 2022
 * 
 * used to see how long it takes a function to complete
 * 
 */
public class Timer {
    
    private long startTime;
    private long endTime;
    private long elapsedTime;
    
    public Timer() {
        
    }
    
    public Timer(boolean startOnCreation) {
        if(startOnCreation) start();
    }
    
    public void start() {
        startTime = System.nanoTime();
    }
    
    public void stop() {
        endTime = System.nanoTime();
        print();
    }
    
    public void print() {
        elapsedTime = endTime - startTime;
        Logging.logger.warn("============={Timer Report}==============");
        Logging.logger.warn("Start Time: " + startTime);
        Logging.logger.warn("End Time: " + endTime);
        Logging.logger.warn("Elapsed Time(ns): " + elapsedTime);
        Logging.logger.warn("Elapsed Time(ms): " + (elapsedTime / 1000000.0));
        Logging.logger.warn("Elapsed Time(S): " + (elapsedTime / 1000000000.0));
        Logging.logger.warn("========================================");
    }
    
}

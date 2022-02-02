package com.trapdoor.engine.registry.helpers;

/**
 * @author brett
 * @date Nov. 28, 2021
 * The DualExecution class is meant for running threaded tasks which require an
 * ending task to be executed on the main thread.
 * The supplied threadRun will be ran inside the thread pool while the mainRun will be ran on the main thread
 * after the threadRun executes
 */
public class DualExecution {

	private Runnable threadRun;
	private Runnable mainRun;
	
	public DualExecution(Runnable threadRun, Runnable mainRun) {
		this.threadRun = threadRun;
		this.mainRun = mainRun;
	}
	
	public void run() {
		threadRun.run();
	}
	
	public Runnable main() {
		return mainRun;
	}
	
}

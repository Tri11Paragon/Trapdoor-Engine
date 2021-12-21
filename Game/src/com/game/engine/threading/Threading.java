package com.game.engine.threading;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.game.engine.display.DisplayManager;

/**
 * @author brett
 * @date Nov. 28, 2021
 * 
 */
public class Threading {
	
	//private static ThreadPoolExecutor pool;
	private static ExecutorService pool;
	private static Queue<Runnable> mainRuns = new ArrayDeque<Runnable>();
	private static volatile int h = 0;
	
	public static void init(int systemCores) {
		DisplayManager.createdThreads++;
		//pool = new ThreadPoolExecutor(systemCores, systemCores, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		pool = Executors.newCachedThreadPool();
	}
	
	public static void cleanup() {
		System.out.println("Threadpool shutting down! ");
		pool.shutdown();
		pool.shutdownNow();
		pool = null;
		mainRuns = null;
		DisplayManager.exited++;
	}
	
	/**
	 * processes the queue of runnable mains
	 * @param maxTimeNs max time in nanoseconds that can be used by the runnable
	 */
	public static void processMain(long maxTimeNs) {
		if (mainRuns.size() == 0)
			return;
		long start = System.nanoTime();
		while (System.nanoTime() - start < maxTimeNs) {
			Runnable run = mainRuns.poll();
			if (run != null)
				run.run();
			else
				break;
		}
	}
	
	public static boolean isEmpty() {
		// triple check bullshit lol
		return mainRuns.size() == 0 && h <= 0;
	}
	
	public static void d() {
		if (mainRuns.size() == 0) {
			h--;
		}
	}
	
	public static void addToMains(Runnable r) {
		mainRuns.add(r);
	}
	
	public static void execute(DualExecution execute) {
		pool.submit(() -> {
			h++;
			execute.run();
			if (mainRuns != null)
				mainRuns.add(execute.main());
			h--;
		});
	}
	
	public static void execute(Runnable runnable) {
		pool.submit(() -> {
			h++;
			runnable.run();
			h--;
		});
	}
	
}

package com.game.engine.threading;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author brett
 * @date Nov. 28, 2021
 * 
 */
public class Threading {
	
	//private static ThreadPoolExecutor pool;
	private static ExecutorService pool;
	private static Queue<Runnable> mainRuns = new ArrayDeque<Runnable>();
	private static Integer h = 0;
	
	public static void init(int systemCores) {
		//pool = new ThreadPoolExecutor(systemCores, systemCores, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		pool = Executors.newCachedThreadPool();
	}
	
	public static void cleanup() {
		pool.shutdown();
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
		return mainRuns.size() == 0 && h == 0;
	}
	
	public static void d() {
		if (mainRuns.size() == 0) {
			synchronized (h) {
				h--;
			}
		}
	}
	
	public static void execute(DualExecution execute) {
		pool.submit(() -> {
			synchronized (h) {
				h++;
			}
			execute.run();
			mainRuns.add(execute.main());
			synchronized (h) {
				h--;
			}
		});
	}
	
	public static void execute(Runnable runnable) {
		pool.submit(runnable);
	}
	
}

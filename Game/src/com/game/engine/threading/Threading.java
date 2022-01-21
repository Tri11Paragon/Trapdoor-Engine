package com.game.engine.threading;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.game.engine.display.DisplayManager;
import com.game.engine.display.IDisplay;
import com.game.engine.renderer.SyncSave;
import com.game.engine.tools.Logging;

/**
 * @author brett
 * @date Nov. 28, 2021
 * 
 */
public class Threading {
	
	//private static ThreadPoolExecutor pool;
	private static volatile ExecutorService pool;
	private static volatile Queue<Runnable> mainRuns = new ArrayDeque<Runnable>();
	private static final AtomicInteger counter = new AtomicInteger(1);
	
	private static Thread physics;
	private static long lastFrameTime;
	private static double delta;
	private static double frameTimeMs,frameTimeS;
	private static double fps;
	
	public static void init(int systemCores) {
		DisplayManager.createdThreads++;
		//pool = new ThreadPoolExecutor(systemCores, systemCores, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		pool = Executors.newCachedThreadPool(new ThreadFactory() {
			private final AtomicInteger counter = new AtomicInteger(1);
			@Override
			public Thread newThread(Runnable r) {
				Thread th = new Thread(r);
				counter.getAndIncrement();
				th.setName("Misc&. Thread");
				return th;
			}
		});
		physics = new Thread(() -> {
			while (DisplayManager.displayOpen) {
				
				IDisplay dis = DisplayManager.getCurrentDisplay();
				if (dis != null)
					dis.update();
				
				SyncSave.syncPhy(DisplayManager.FPS_MAX);
				
				long currentFrameTime = System.nanoTime();
				delta = currentFrameTime - lastFrameTime;
				lastFrameTime = currentFrameTime;
				frameTimeMs = delta / 1000000d;
				frameTimeS = delta / 1000000000d;
				fps = 1000d/frameTimeMs;
			}
			Logging.logger.info("Physics thread exiting! ");
			DisplayManager.exited++;
		});
		physics.start();
		physics.setName("Phys&. Thread");
		DisplayManager.createdThreads++;
	}
	
	public static void cleanup() {
		Logging.logger.info("Threadpool shutting down! ");
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
		return mainRuns.size() == 0 && counter.get() <= 0;
	}
	
	public static void d() {
		if (mainRuns.size() == 0) {
			counter.decrementAndGet();
		}
	}
	
	public static synchronized void addToMains(Runnable r) {
		mainRuns.add(r);
	}
	
	public static void execute(DualExecution execute) {
		pool.submit(() -> {
			counter.incrementAndGet();
			
			execute.run();
			
			if (mainRuns != null)
				addToMains(execute.main());
			
			counter.decrementAndGet();
		});
	}
	
	public static void execute(Runnable runnable) {
		pool.submit(() -> {
			counter.incrementAndGet();
			runnable.run();
			counter.decrementAndGet();
		});
	}
	
	public static double getFrameTimeMilis() {
		return frameTimeMs;
	}

	public static double getFrameTimeSeconds() {
		return frameTimeS;
	}
	
	public static double getFPS() {
		return fps;
	}
	
}

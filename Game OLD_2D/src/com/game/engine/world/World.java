package com.game.engine.world;

import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Matrix4f;

import com.game.engine.camera.Camera;
import com.game.engine.datatypes.world.Entity;
import com.game.engine.datatypes.world.Player;
import com.game.engine.datatypes.world.Tile;
import com.game.engine.renderer.EntityRenderer;
import com.game.engine.renderer.TileRenderer;
import com.game.engine.renderer.TileTypes;
import com.game.engine.tools.math.Maths;

/**
 * @author brett
 * @date Oct. 25, 2021
 * 
 */
public class World {
	
	private static int lastID = 0;
	private static final ArrayList<Entity> entities = new ArrayList<Entity>();
	private static final HashMap<Integer, Entity> worldEntities = new HashMap<Integer, Entity>();
	private static Thread th;
	
	private static Player localPlayer;
	private static Matrix4f view;
	private static TileRenderer render;
	
	//private static long lastFrameTime;
	private static double delta;
	
	public static void preinit() {
		// WHY THE FUCK DOES THIS ONLY WORK IN PREINIT
		// MAKES NO FUCKING SENSE
		Tile[][] tiles = new Tile[48][48];
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[i].length; j++) {
				if (i == 0 || i == tiles.length-1)
					tiles[i][j] = TileTypes.crowbar;
				else {
					if (i % 2 == 0 || j % 2 == 0)
						tiles[i][j] = TileTypes.grass;
					else 
						tiles[i][j] = TileTypes.clay;
				}
			}
		}
		render = new TileRenderer(tiles);
	}
	
	/**
	 * Creates a world based on the input tile size
	 * @param tx tile size x
	 * @param ty tile size y
	 */
	public static void init() {
		localPlayer = new Player(new Camera(), 64, 64, "emerald.png");
		localPlayer.enable();
		
		
		/*th = new Thread(()-> {
			while (DisplayManager.displayOpen) {
				update();
				SyncSave.sync(60);
				long currentFrameTime = System.nanoTime();
				delta = currentFrameTime - lastFrameTime;
				lastFrameTime = currentFrameTime;
			}
		});
		th.start();*/
	}
	
	public static void render() {
		view = Maths.createViewMatrix(localPlayer.getCamera());
		
		EntityRenderer.render(view, localPlayer.getCamera());
		render.DrawTiles(view);
	}
	
	public static void update() {
		for (int i = 0; i < entities.size(); i++) {
			entities.get(i).onUpdate();
		}
	}
	
	public static boolean checkCollision(Entity e) {
		return false;
	}
	
	public static void addEntity(Entity e) {
		e.setWorldID(lastID);
		worldEntities.put(lastID, e);
		lastID++;
		entities.add(e);
	}
	
	public static void deleteEntity(Entity e) {
		entities.remove(e);
		worldEntities.remove(e.getWorldID());
	}
	
	public static void deleteAllEntities() {
		// stop is deprecated :/
		th.interrupt();
		th = null;
		entities.clear();
		worldEntities.clear();
	}
	
	public static double getFrameTimeMilis() {
		return delta / 1000000d;
	}

	public static double getFrameTimeSeconds() {
		return delta / 1000000000d;
	}
	
}

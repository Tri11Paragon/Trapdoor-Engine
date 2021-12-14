package com.game.engine.display;

import com.game.engine.threading.GameRegistry;
import com.game.engine.threading.Threading;

/**
 * @author brett
 * @date Nov. 28, 2021
 * 
 */
public class LoadingScreenDisplay extends IDisplay {

	public static final int TIME = 0;
	
	public TestDisplay test;
	
	private long time;
	
	@Override
	public void onCreate() {
		GameRegistry.registerMaterialTexture("resources/textures/512.png");
		GameRegistry.registerMaterialTextureFolder("resources/textures/materials/");
		
		
		test = new TestDisplay();
		time = System.currentTimeMillis();
	}

	@Override
	public void onSwitch() {
		
	}
	
	@Override
	public void render() {
		// TODO: splash screen / logo
		
		// make sure we have loaded all assets and the splash screen has existed for some time.
		if ((Threading.isEmpty() && System.currentTimeMillis() - time > TIME)) {
			DisplayManager.createDisplay(test);
			DisplayManager.changeDisplay(test);
		}
		if (System.currentTimeMillis() - time > (TIME+50) * 5)
			Threading.d();
	}

	@Override
	public void onLeave() {
		
	}

	@Override
	public void onDestory() {
		
	}
	
}

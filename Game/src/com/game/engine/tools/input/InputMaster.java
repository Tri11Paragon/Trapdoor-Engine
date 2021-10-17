package com.game.engine.tools.input;

import java.util.ArrayList;
import java.util.List;

public class InputMaster {
	
	public static List<IKeyState> keyboard = new ArrayList<IKeyState>();
	public static List<IMouseState> mouse = new ArrayList<IMouseState>();
	public static List<IScrollState> scroll = new ArrayList<IScrollState>();
	public static boolean state;
	
	public static boolean mouseState;
	public static boolean mouseStateLeft;
	public static boolean mouseStateMiddle;
	public static boolean mouseStateRight;
	
	public static final boolean[] keyDown = new boolean[1024];
	public static final boolean[] mouseDown = new boolean[32];
	public static int lastScrollState = 0;
	public static volatile boolean scrolledLastFrame = false;
	
	public static void registerKeyListener(IKeyState listener) {
		keyboard.add(listener);
	}
	
	public static void registerMouseListener(IMouseState listener) {
		mouse.add(listener);
	}
	
	public static void registerScrollListener(IScrollState listener) {
		scroll.add(listener);
	}
	
	public static void keyPressed(int key) {
		if (key < 0)
			return;
		state = true;
		keyDown[key] = true;
		for (int i = 0; i < keyboard.size(); i++)
			keyboard.get(i).onKeyPressed(key);
	}
	
	public static void keyReleased(int key) {
		if (key < 0)
			return;
		keyDown[key] = false;
		for (int i = 0; i < keyboard.size(); i++)
			keyboard.get(i).onKeyReleased(key);
	}
	
	public static void mousePressed(int button) {
		mouseState = true;
		if (button == Mouse.LEFT)
			mouseStateLeft = true;
		if (button == Mouse.RIGHT)
			mouseStateRight = true;
		if (button == Mouse.MIDDLE)
			mouseStateMiddle = true;
		
		
		mouseDown[button] = true;
		for (int i = 0; i < mouse.size(); i++)
			mouse.get(i).onMousePressed(button);
	}
	
	public static void mouseReleased(int button) {
		mouseDown[button] = false;
		for (int i = 0; i < mouse.size(); i++)
			mouse.get(i).onMouseReleased(button);
	}
	
	public static void scrollMoved(int dir) {
		lastScrollState = dir;
		for (int i = 0; i < scroll.size(); i++)
			scroll.get(i).scroll(dir);
		scrolledLastFrame = true;
	}
	
	public static void update() {
		mouseState = false;
		mouseStateLeft = false;
		mouseStateMiddle = false;
		mouseStateRight = false;
		scrolledLastFrame = false;
		state = false;
	}
	
}

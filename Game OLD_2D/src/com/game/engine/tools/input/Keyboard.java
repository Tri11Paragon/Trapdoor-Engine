package com.game.engine.tools.input;

import org.lwjgl.glfw.GLFW;

public class Keyboard {
	
	/**
	 * @param key
	 * @return true if the specified key is currently being pressed in this frame
	 */
	public static boolean isKeyDown(int key) {
		return InputMaster.keyDown[key];
	}
	
	/**
	 * @param key
	 * @return true if the specified key is currently being pressed in this frame
	 */
	public static boolean keyDown(int key) {
		return InputMaster.keyDown[key];
	}
	
	/**
	 * @return true if a key was pressed in the frame
	 */
	public static boolean state() {
		return InputMaster.state;
	}
	
	// key defs
	public static final int KEY_A = GLFW.GLFW_KEY_A;
	public static final int KEY_B = GLFW.GLFW_KEY_B;
	public static final int KEY_C = GLFW.GLFW_KEY_C;
	public static final int KEY_D = GLFW.GLFW_KEY_D;
	public static final int KEY_E = GLFW.GLFW_KEY_E;
	public static final int KEY_F = GLFW.GLFW_KEY_F;
	public static final int KEY_G = GLFW.GLFW_KEY_G;
	public static final int KEY_H = GLFW.GLFW_KEY_H;
	public static final int KEY_I = GLFW.GLFW_KEY_I;
	public static final int KEY_J = GLFW.GLFW_KEY_J;
	public static final int KEY_K = GLFW.GLFW_KEY_K;
	public static final int KEY_L = GLFW.GLFW_KEY_L;
	public static final int KEY_M = GLFW.GLFW_KEY_M;
	public static final int KEY_N = GLFW.GLFW_KEY_N;
	public static final int KEY_O = GLFW.GLFW_KEY_O;
	public static final int KEY_P = GLFW.GLFW_KEY_P;
	public static final int KEY_Q = GLFW.GLFW_KEY_Q;
	public static final int KEY_R = GLFW.GLFW_KEY_R;
	public static final int KEY_S = GLFW.GLFW_KEY_S;
	public static final int KEY_T = GLFW.GLFW_KEY_T;
	public static final int KEY_U = GLFW.GLFW_KEY_U;
	public static final int KEY_V = GLFW.GLFW_KEY_V;
	public static final int KEY_W = GLFW.GLFW_KEY_W;
	public static final int KEY_X = GLFW.GLFW_KEY_X;
	public static final int KEY_Y = GLFW.GLFW_KEY_Y;
	public static final int KEY_Z = GLFW.GLFW_KEY_Z;

	public static final int A = GLFW.GLFW_KEY_A;
	public static final int B = GLFW.GLFW_KEY_B;
	public static final int C = GLFW.GLFW_KEY_C;
	public static final int D = GLFW.GLFW_KEY_D;
	public static final int E = GLFW.GLFW_KEY_E;
	public static final int F = GLFW.GLFW_KEY_F;
	public static final int G = GLFW.GLFW_KEY_G;
	public static final int H = GLFW.GLFW_KEY_H;
	public static final int I = GLFW.GLFW_KEY_I;
	public static final int J = GLFW.GLFW_KEY_J;
	public static final int K = GLFW.GLFW_KEY_K;
	public static final int L = GLFW.GLFW_KEY_L;
	public static final int M = GLFW.GLFW_KEY_M;
	public static final int N = GLFW.GLFW_KEY_N;
	public static final int O = GLFW.GLFW_KEY_O;
	public static final int P = GLFW.GLFW_KEY_P;
	public static final int Q = GLFW.GLFW_KEY_Q;
	public static final int R = GLFW.GLFW_KEY_R;
	public static final int S = GLFW.GLFW_KEY_S;
	public static final int T = GLFW.GLFW_KEY_T;
	public static final int U = GLFW.GLFW_KEY_U;
	public static final int V = GLFW.GLFW_KEY_V;
	public static final int W = GLFW.GLFW_KEY_W;
	public static final int X = GLFW.GLFW_KEY_X;
	public static final int Y = GLFW.GLFW_KEY_Y;
	public static final int Z = GLFW.GLFW_KEY_Z;

}

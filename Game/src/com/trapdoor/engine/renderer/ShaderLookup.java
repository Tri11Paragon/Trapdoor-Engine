package com.trapdoor.engine.renderer;

import java.util.HashMap;

/**
 * @author laptop
 * @date Mar. 10, 2022
 * 
 */
public class ShaderLookup {
	
	// TODO: make this better. Doing this way as quick and dirty way to making shaodws for laptop
	
	private static final HashMap<String, Object> shaderVariables = new HashMap<String, Object>();
	
	public static void put(String key, Object v) {
		shaderVariables.put(key, v);
	}
	
	public static Object get(String k) {
		return shaderVariables.get(k);
	}
	
}

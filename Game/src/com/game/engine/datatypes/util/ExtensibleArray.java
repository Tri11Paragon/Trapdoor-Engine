package com.game.engine.datatypes.util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author brett
 * @date Jan. 13, 2022
 * 
 */
public class ExtensibleArray<T> {
	
	@SuppressWarnings("unused")
	private ArrayList<T[]> arrays = new ArrayList<T[]>(); 
	@SuppressWarnings("unused")
	private HashMap<Integer, Integer> lengths = new HashMap<Integer, Integer>();
	
	public ExtensibleArray(){
		
	}
	
}

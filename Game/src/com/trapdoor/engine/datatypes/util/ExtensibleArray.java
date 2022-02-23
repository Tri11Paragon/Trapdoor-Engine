package com.trapdoor.engine.datatypes.util;

import com.trapdoor.engine.renderer.DeferredSecondPassShader;

/**
 * @author brett
 * @date Jan. 13, 2022
 * 
 */
public class ExtensibleArray<T> {
	
	private Object[] array;
	
	// pos points to the next open spot, length contains number of elements
	private int pos = 0;
	
	public ExtensibleArray(){
		array = new Object[DeferredSecondPassShader.MAX_LIGHTS];
	}
	
	@SuppressWarnings("unchecked")
	public T get(int i) {
		if (i > pos)
			return null;
		return (T) array[i];
	}
	
	public void add(T t) {
		if (pos >= array.length)
			expandArray();
		array[pos] = t;
				
		this.pos++;
	}
	
	public int size() {
		return pos;
	}
	
	public void clear() {
		this.pos = 0;
	}
	
	private void expandArray() {
		// create the new array
		Object[] newArray = new Object[this.array.length * 2];
		// copy the old array over
		System.arraycopy(array, 0, newArray, 0, array.length);
		// assign to the new array
		this.array = newArray;
	}
	
}

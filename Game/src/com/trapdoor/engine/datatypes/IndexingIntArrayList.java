package com.trapdoor.engine.datatypes;

import java.nio.IntBuffer;
import java.util.Iterator;
import org.lwjgl.system.MemoryUtil;

/**
 * @author laptop
 * @date Mar. 3, 2022
 * 
 */
public class IndexingIntArrayList implements Iterable<Integer> {
	
	private static final int INIT_SIZE = 64;
	
	private int[] arr;
	private boolean additions = true;
	private int[] truncArray;
	private IntBuffer toInt;
	private int size;
	private int lastIndex = 0;
	private int numOfElements = 0;
	
	public IndexingIntArrayList(){
		this(INIT_SIZE);
	}
	
	public IndexingIntArrayList(int init_size) {
		arr = new int[init_size];
		this.size = init_size;
	}
	
	public int[] getRawArray() {
		return arr;
	}
	
	public void generateStructures() {
		if (!additions)
			return;
		
		int[] newArr = new int[size()];
		IntBuffer allocInt = MemoryUtil.memAllocInt(size());
		for (int i = 0; i < size(); i++) {
			newArr[i] = get(i);
			allocInt.put(get(i));
		}
		
		allocInt.flip();
		
		toInt = allocInt;
		truncArray = newArr;
		
		additions = false;
	}
	
	public int[] getTruncatedArray() {
		if (additions)
			generateStructures();
		return truncArray;
	}
	
	public IntBuffer toIntBuffer() {
		if (additions)
			generateStructures();
		return toInt;
	}
	
	/**
	 * adds an entity to this array
	 * @param e the entity to add
	 * @return the index of added entity
	 */
	public int add(int e) {
		additions = true;
		numOfElements++;
		if (lastIndex >= size)
			expand();
		arr[lastIndex++] = e;
		return lastIndex-1;
	} 
	
	/**
	 * gets the entity from the entity array. Now in constant time!
	 * @param i index of the entity
	 * @return the entity at this index (NOTE: this can be null even if in bounds!)
	 */
	public int get(int i) {
		if (i > lastIndex)
			throw new IndexOutOfBoundsException(i);
		return arr[i];
	}
	
	/**
	 * @return the total number of elements in this array
	 */
	public int count() {
		return numOfElements;
	}
	
	/**
	 * @return the last index (ie size occupied) of the array
	 */
	public int size() {
		return lastIndex;
	}
	
	@Override
	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {
			
			int li = 0;
			
			@Override
			public boolean hasNext() {
				return li < size();
			}

			@Override
			public Integer next() {
				return get(li++);
			}
		};
	}
	
	private void expand() {
		int[] n = new int[this.size * 2];
		for (int i = 0; i < this.size; i++)
			n[i] = this.arr[i];
		this.size = n.length;
		this.arr = n;
	}
	
}

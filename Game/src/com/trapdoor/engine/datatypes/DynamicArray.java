package com.trapdoor.engine.datatypes;

import java.util.HashMap;

/**
 * @author brett
 * @date Feb. 10, 2022
 * Specialized advanced data structure for use in high addition / <b>removal</b> situations (mostly removal)
 * It can and does work as a general purpose List however it tends to be out performed in addition when compared to ArrayLists
 */
public class DynamicArray<T> {

	private int size;
	private int lastIndex = 0;
	private int numOfElements = 0;
	
	private T[] entities;
	private HashMap<T, Integer> indexes;
	private LinkedQueue<Integer> openIndices;
	
	
	public DynamicArray() {
		this(128);
	}
	
	@SuppressWarnings("unchecked")
	public DynamicArray(int size) {
		this.size = size;
		this.entities = (T[]) new Object[size];
		this.indexes = new HashMap<T, Integer>(size);
		this.openIndices = new LinkedQueue<Integer>();
	}
	
	/**
	 * adds an entity to this array
	 * @param e the entity to add
	 * @return the index of added entity
	 */
	public int add(T e) {
		Integer ind = this.openIndices.pop();
		numOfElements++;
		if (ind != null) {
			entities[ind] = e;
			indexes.put(e, ind);
			return ind;
		} else {
			if (lastIndex >= size)
				expand();
			indexes.put(e, lastIndex);
			entities[lastIndex++] = e;
			return lastIndex-1;
		}
	} 
	
	/**
	 * gets the entity from the entity array. Now in constant time!
	 * @param i index of the entity
	 * @return the entity at this index (NOTE: this can be null even if in bounds!)
	 */
	public T get(int i) {
		if (i > lastIndex)
			throw new IndexOutOfBoundsException(i);
		return entities[i];
	}
	
	/**
	 * removes an entity from this array. Now in constant time!
	 * @param e entity to remove
	 * @return if entity was inside this array
	 */
	public boolean remove(T e) {
		Integer index = this.indexes.get(e);
		if (index == null)
			return false;
		entities[index] = null;
		this.indexes.remove(e);
		openIndices.add(index);
		numOfElements--;
		return true;
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
	
	@SuppressWarnings("unchecked")
	private void expand() {
		T[] n = (T[]) new Object[this.size * 2];
		for (int i = 0; i < this.size; i++)
			n[i] = this.entities[i];
		this.size = n.length;
		this.entities = n;
	}
	
}

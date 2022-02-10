package com.trapdoor.engine.world;

import java.util.HashMap;

import com.trapdoor.engine.datatypes.LinkedQueue;
import com.trapdoor.engine.world.entities.Entity;

/**
 * @author brett
 * @date Feb. 10, 2022
 * Faster than an ArrayList<Entity>
 */
public class DynamicEntityDatabase {
	
	private int size;
	private int lastIndex = 0;
	private int numOfElements = 0;
	
	private Entity[] entities;
	private HashMap<Entity, Integer> indexes;
	private LinkedQueue<Integer> openIndices;
	
	
	public DynamicEntityDatabase() {
		this(128);
	}
	
	public DynamicEntityDatabase(int size) {
		this.size = size;
		this.entities = new Entity[size];
		this.indexes = new HashMap<Entity, Integer>(size);
		this.openIndices = new LinkedQueue<Integer>();
	}
	
	/**
	 * adds an entity to this array
	 * @param e the entity to add
	 * @return the index of added entity
	 */
	public int add(Entity e) {
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
	public Entity get(int i) {
		if (i > lastIndex)
			throw new IndexOutOfBoundsException(i);
		return entities[i];
	}
	
	/**
	 * removes an entity from this array. Now in constant time!
	 * @param e entity to remove
	 * @return if entity was inside this array
	 */
	public boolean remove(Entity e) {
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
	 * @return the last index (ie size occupied) of the entity array
	 */
	public int size() {
		return lastIndex;
	}
	
	private void expand() {
		Entity[] n = new Entity[this.size * 2];
		for (int i = 0; i < this.size; i++)
			n[i] = this.entities[i];
		this.size = n.length;
		this.entities = n;
	}
	
}

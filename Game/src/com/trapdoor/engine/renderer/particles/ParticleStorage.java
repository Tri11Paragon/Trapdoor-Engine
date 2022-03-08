package com.trapdoor.engine.renderer.particles;

import java.util.HashMap;

import com.trapdoor.engine.datatypes.LinkedQueue;

/**
 * @author brett
 * @date Mar. 6, 2022
 * 
 */
public class ParticleStorage {
	
	public final int MAX_PARTICLES;
	
	private int size;
	private int lastIndex = 0;
	private int numOfElements = 0;
	
	private Particle[] particles;
	private HashMap<Particle, Integer> indexes;
	private LinkedQueue<Integer> openIndices;
	
	public ParticleStorage() {
		// TODO: there is an issue with this rendering at 1/4+ full
		this(100000);
	}
	
	public ParticleStorage(int MAX_PARTICLES) {
		this.size = MAX_PARTICLES;
		this.particles = new Particle[MAX_PARTICLES];
		this.indexes = new HashMap<Particle, Integer>(MAX_PARTICLES);
		this.openIndices = new LinkedQueue<Integer>();
		this.MAX_PARTICLES = MAX_PARTICLES;
	}
	
	/**
	 * adds an entity to this array
	 * @param p the entity to add
	 * @return the index of added entity
	 */
	public int add(Particle p) {
		Integer ind = this.openIndices.pop();
		numOfElements++;
		if (ind != null) {
			particles[ind] = p;
			indexes.put(p, ind);
			return ind;
		} else {
			if (lastIndex >= size)
				lastIndex = 0;
			indexes.put(p, lastIndex);
			particles[lastIndex++] = p;
			return lastIndex-1;
		}
	} 
	
	/**
	 * gets the entity from the entity array. Now in constant time!
	 * @param i index of the entity
	 * @return the entity at this index (NOTE: this can be null even if in bounds!)
	 */
	public Particle get(int i) {
		if (i > lastIndex)
			throw new IndexOutOfBoundsException(i);
		return particles[i];
	}
	
	private void set(int i, Particle p) {
		if (i > lastIndex)
			throw new IndexOutOfBoundsException(i);
		particles[i] = p;
	}
	
	/**
	 * removes an entity from this array. Now in constant time!
	 * @param e entity to remove
	 * @return if entity was inside this array
	 */
	public boolean remove(Particle e) {
		Integer index = this.indexes.get(e);
		if (index == null)
			return false;
		particles[index] = null;
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
	
	public Particle getParticle(int i) {
		return get(i);
	}
	
	public void addParticle(Particle p) {
		add(p);
	}
	
	public void deleteParticle(Particle p) {
		remove(p);
	}
	
	public int getSize() {
		return size();
	}
	
	/**
	 * Sorts the list of particles so that the particles with the highest distance
	 * from the camera are first, and the particles with the shortest distance
	 * are last.
	 * 
	 */
	public void insertionSort() {  
		removeNulls();
		
		// insertion sort
        int n = size();  
        for (int j = 1; j < n; j++) {  
            Particle key = get(j);  
            int i = j-1;  
            while ( (i > -1) && ( get(i).getDistance() < key.getDistance() ) ) {  
                set(i+1, get(i));  
                i--;  
            }  
            set(i+1, key);  
        }  
    }  
	
	private void removeNulls() {
		// remove all the null
		int end = 0;
		openIndices = new LinkedQueue<Integer>();
		this.indexes = new HashMap<Particle, Integer>(MAX_PARTICLES);
		
		for (int i = 0; i < size(); i++) {
			if (particles[i] == null) {
				for (int j = i + 1; j < size(); j++) {
					if (particles[j] != null) {
						particles[i] = particles[j];
						particles[j] = null;
						break;
					}
				}
			}
			if (particles[i] != null) {
				end = i;
			}
			this.indexes.put(particles[i], i);
		}
		this.lastIndex = end + 1;
	}
	
	public void quickSort() {
		
	}

	
}

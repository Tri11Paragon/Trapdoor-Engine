package com.trapdoor.engine.datatypes;

import java.nio.ByteBuffer;
import java.util.Iterator;
import org.lwjgl.BufferUtils;

/**
 * @author laptop
 * @date Mar. 3, 2022
 * 
 */
public class IndexingFloatArrayList implements Iterable<Float> {
	
	private static final int INIT_SIZE = 64;
	
	private float[] arr;
	private boolean additionsSinceArray = true;
	private boolean additionsSinceByte = true;
	private float[] truncArray;
	private ByteBuffer toByte;
	private int size;
	private int lastIndex = 0;
	private int numOfElements = 0;
	
	public IndexingFloatArrayList(){
		this(INIT_SIZE);
	}
	
	public IndexingFloatArrayList(int init_size) {
		arr = new float[init_size];
		this.size = init_size;
	}
	
	public float[] getRawArray() {
		return arr;
	}
	
	public void generateStructures() {
		if (!additionsSinceArray && !additionsSinceByte)
			return;
		
		float[] newArr = new float[size()];
		ByteBuffer allocByte = BufferUtils.createByteBuffer(size() * 4);
		for (int i = 0; i < size(); i++) {
			newArr[i] = get(i);
			allocByte.putFloat(get(i));
		}
		
		allocByte.flip();
		
		toByte = allocByte;
		truncArray = newArr;
		
		additionsSinceArray = false;
		additionsSinceByte = false;
	}
	
	public float[] getTruncatedArray() {
		if (!additionsSinceArray)
			return truncArray;
		float[] newArr = new float[size()];
		for (int i = 0; i < size(); i++)
			newArr[i] = arr[i];
		additionsSinceArray = false;
		truncArray = newArr;
		return newArr;
	}
	
	public ByteBuffer toByteBuffer() {
		if (!additionsSinceByte)
			return toByte;
		ByteBuffer alloc = null;
			alloc = BufferUtils.createByteBuffer(size() * 4);
			for (int i = 0; i < size(); i++)
				alloc.putFloat(get(i));
		additionsSinceByte = false;
		alloc.flip();
		toByte = alloc;
		return alloc;
	}
	
	/**
	 * adds an entity to this array
	 * @param e the entity to add
	 * @return the index of added entity
	 */
	public int add(float e) {
		additionsSinceArray = true;
		additionsSinceByte = true;
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
	public float get(int i) {
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
	public Iterator<Float> iterator() {
		return new Iterator<Float>() {
			
			int li = 0;
			
			@Override
			public boolean hasNext() {
				return li < size();
			}

			@Override
			public Float next() {
				return get(li++);
			}
		};
	}
	
	private void expand() {
		float[] n = new float[this.size * 2];
		for (int i = 0; i < this.size; i++)
			n[i] = this.arr[i];
		this.size = n.length;
		this.arr = n;
	}
	
}

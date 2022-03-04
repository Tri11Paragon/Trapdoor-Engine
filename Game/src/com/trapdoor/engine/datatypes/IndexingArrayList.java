package com.trapdoor.engine.datatypes;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Iterator;
import org.lwjgl.BufferUtils;

/**
 * @author laptop
 * @date Mar. 3, 2022
 * 
 */
@SuppressWarnings("unchecked")
public class IndexingArrayList<T> implements Iterable<T> {
	
	private static final int INIT_SIZE = 64;
	
	private T[] arr;
	private boolean additionsSinceArray = true;
	private boolean additionsSinceInt = true;
	private boolean additionsSinceByte = true;
	private T[] truncArray;
	private ByteBuffer toByte;
	private IntBuffer toInt;
	private int size;
	private int lastIndex = 0;
	private int numOfElements = 0;
	
	public IndexingArrayList(){
		this(INIT_SIZE);
	}
	
	public IndexingArrayList(int init_size) {
		arr = (T[]) new Object[init_size];
	}
	
	public T[] getRawArray() {
		return arr;
	}
	
	public T[] getTruncatedArray() {
		if (!additionsSinceArray)
			return truncArray;
		T[] newArr = (T[]) new Object[size()];
		for (int i = 0; i < size(); i++)
			newArr[i] = arr[i];
		additionsSinceArray = false;
		truncArray = newArr;
		return newArr;
	}
	
	public IntBuffer toIntBuffer() {
		if (!additionsSinceInt)
			return toInt;
		T check = (T) new Object();
		IntBuffer alloc = null;
		if (check instanceof Integer) {
			alloc = BufferUtils.createIntBuffer(size());
			for (int i = 0; i < size(); i++)
				alloc.put((Integer)get(i));
		}
		additionsSinceInt = false;
		toInt = alloc;
		return alloc;
	}
	
	public ByteBuffer toByteBuffer() {
		if (!additionsSinceByte)
			return toByte;
		T check = (T) new Object();
		ByteBuffer alloc = null;
		if (check instanceof Float) {
			alloc = BufferUtils.createByteBuffer(size() * 4);
			for (int i = 0; i < size(); i++)
				alloc.putFloat((Float)get(i));
		} else if (check instanceof Integer) {
			alloc = BufferUtils.createByteBuffer(size() * 4);
			for (int i = 0; i < size(); i++)
				alloc.putInt((Integer)get(i));
		} else if (check instanceof Short) {
			alloc = BufferUtils.createByteBuffer(size() * 2);
			for (int i = 0; i < size(); i++)
				alloc.putShort((Short)get(i));
		} else if (check instanceof Byte) {
			alloc = BufferUtils.createByteBuffer(size());
			for (int i = 0; i < size(); i++)
				alloc.put((Byte)get(i));
		}
		additionsSinceByte = false;
		toByte = alloc;
		return alloc;
	}
	
	/**
	 * adds an entity to this array
	 * @param e the entity to add
	 * @return the index of added entity
	 */
	public int add(T e) {
		additionsSinceArray = true;
		additionsSinceByte = true;
		additionsSinceInt = true;
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
	public T get(int i) {
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
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			
			int li = 0;
			
			@Override
			public boolean hasNext() {
				return li < size();
			}

			@Override
			public T next() {
				return get(li++);
			}
		};
	}
	
	private void expand() {
		T[] n = (T[]) new Object[this.size * 2];
		for (int i = 0; i < this.size; i++)
			n[i] = this.arr[i];
		this.size = n.length;
		this.arr = n;
	}
	
}

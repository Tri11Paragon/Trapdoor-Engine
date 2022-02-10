package com.trapdoor.engine.datatypes;

/**
 * @author brett
 * @date Feb. 10, 2022
 * 
 */
public class LinkedQueue<T> {

	private Node head;
	private Node tail;
	
	public LinkedQueue() {
		
	}
	
	public void add(T data) {
		Node ne = new Node(data, null);
		if (head == null)
			head = ne;
		if (tail == null)
			tail = ne;
		if (tail != null) {
			tail.prev = ne;
			tail = ne;
		}
	}
	
	public T pop() {
		if (head == null)
			return null;
		T val = head.data;
		
		if (head.prev != null)
			head = head.prev;
		
		return val;
	}
	
	public T peek() {
		return head != null ? head.data : null;
	}
	
	private class Node{
		private T data;
		private Node prev;
		
		public Node(T data, Node prev) {
			this.data = data;
			this.prev = prev;
		}
	}
	
}

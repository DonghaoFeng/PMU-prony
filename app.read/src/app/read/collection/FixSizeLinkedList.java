package app.read.collection;

import java.util.LinkedList;

/**
 * @author Donghao
 *
 * @param <T>
 */
public class FixSizeLinkedList<T> extends LinkedList<T> {	
	private int capacity;
 
	public FixSizeLinkedList(int capacity) {
		super();
		this.capacity = capacity;
	}
 
	@Override
	public boolean add(T e) {
		if (size() + 1 > capacity) {
			super.removeFirst();
		}
		return super.add(e);
	}
}
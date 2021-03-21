package org.delmesoft.crazyblocks.utils.datastructure;

import java.util.Iterator;

public class LinkedList<T> {

	private Node head;
	private int size;

	public LinkedList() {

	}

	public void add(T e) {

		Node node = new Node();
		node.e = e;
		if(size == 0) {
			head = node;
			head.last = head;
		} else {
			head.last.next = node;
			node.last = head.last;
			head.last = node;
		}
		size++;
	}

	private void remove(Node n) {
		if(n == head) {
			if(head.next != null) {
				head.next.last = head.last;
			}
			head = head.next;
		} else {
			if(n.next != null) {
				n.next.last = n.last;
			}
			n.last.next = n.next;
		}
		size--;
	}

	public T pop() {
		size--;
		T tmp = head.last.e;
		if(size > 0) {
			head.last = head.last.last;
			head.last.next = null;
		} else {
			head = null;
		}
		return tmp;
	}

	public T poll() {
		size--;
		T tmp = head.e;
		if(size > 0) {
			head.next.last = head.last;
			head = head.next;
		} else {
			head = null;
		}
		return tmp;
	}

	public int size() {
		return size;
	}

	public void clear() {
		head = null;
		size = 0;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		LinkedList<T>.Node tmp = head;
		sb.append('[');
		if(size > 0) {
			while(true) {
				sb.append(tmp.e);
				tmp = tmp.next;
				if(tmp != null) {
					sb.append(", ");
				} else {
					break;
				}
			}
		}
		sb.append(']');
		return sb.toString();
	}

	private class Node {
		T e;
		Node last, next;
	}

	public Iterator<T> iterator() {
		return new MyIterator();
	}

	private class MyIterator implements Iterator<T> {

		Node result = null;
		Node current = head;

		@Override
		public boolean hasNext() {
			return current != null;
		}

		@Override
		public T next() {
			result = current;
			current = current.next;
			return result.e;
		}

		@Override
		public void remove() {
			LinkedList.this.remove(result);
		}
	}

}

package org.delmesoft.crazyblocks.utils.datastructure;

import com.badlogic.gdx.utils.Sort;

import java.util.Comparator;

public class Array<T> {

	public T[] items;
	public int size;

	public Array() {
		this(8);
	}

	@SuppressWarnings("unchecked")
	public Array(int initialCapacity) {
		items = (T[]) new Object[initialCapacity];
	}

	public void add(T e) {
		if (size == items.length) {
			resize(Math.max(8, (int) (size * 1.75f)));
		}
		items[size++] = e;
	}

	public void set(int index, T v) {
		items[index] = v;
	}

	public T get(int index) {
		return items[index];
	}

	public boolean contains(T e) {
		final T[] items = this.items;
		for (int i = 0, n = size; i < n; i++) {
			if (e == items[i]) {
				return true;
			}
		}

		return false;
	}
	
	public boolean containsEquals(T e) {
		final T[] items = this.items;
		for (int i = 0, n = size; i < n; i++) {
			if (e.equals(items[i])) {
				return true;
			}
		}

		return false;
	}

	public int indexOf(T e) {
		final T[] items = this.items;
		for (int i = 0, n = size; i < n; i++) {
			if (e == items[i]) {
				return i;
			}
		}

		return -1;
	}

	public boolean removeValue(T e) {

		final T[] items = this.items;

		for (int i = 0, n = size; i < n; i++) {
			if (e == items[i]) {
				removeIndex(i);
				return true;
			}
		}

		return false;
	}

	public T removeIndex(int index) {

		T e = items[index];

		--size;
		System.arraycopy(items, index + 1, items, index, size - index);

		items[size] = null;

		return e;
	}

	@SuppressWarnings("unchecked")
	private T[] resize(int newCapacity) {

		T[] tmp = (T[]) java.lang.reflect.Array.newInstance(items.getClass().getComponentType(), newCapacity);
		System.arraycopy(items, 0, tmp, 0, size);
		return items = tmp;
	}

	public T pop() {
		/*T e = items[--size];
		items[size] = null;
		return e;*/
		return items[--size];
	}

	public void trim() {
		if (size < items.length) {
			resize(size);
		}
	}

	public void reverse() {

		T[] items = this.items;

		T tmp;
		
		int i = 0, j = size - 1;
		
		while(j > i) {
			
			tmp = items[i];
			items[i] = items[j];
			items[j] = tmp;
			
			i++;
			j--;
			
		}

	}
	
	public void sort() {
		Sort.instance().sort(items, 0, size);
	}
	
	public void sort(int fromIndex, int toIndex) {
		Sort.instance().sort(items, fromIndex, toIndex);
	}
	
	public void sort(Comparator<T> comparator) {
		Sort.instance().sort(items, comparator, 0, size);
	}

	public void clear() {
		for (int i = 0, n = size; i < n; i++)
			items[i] = null;
		size = 0;
	}

	public void addAll(Array<? extends T> array) {
		addAll(array, 0, array.size);
	}

	public void addAll(Array<? extends T> array, int start, int count) {
		addAll(array.items, start, count);
	}

	public void addAll(T[] array, int start, int count) {
		T[] items = this.items;
		int sizeNeeded = size + count;
		if (sizeNeeded > items.length)
			items = resize(Math.max(8, (int) (sizeNeeded * 1.75f)));
		System.arraycopy(array, start, items, size, count);
		size += count;
	}
	
	public void insertAll(Array<? extends T> array) {
		insertAll(array, 0, array.size);
	}

	public void insertAll(Array<? extends T> array, int start, int count) {
		insertAll(array.items, start, count);
	}

	@SuppressWarnings("unchecked")
	public void insertAll(T[] array, int start, int count) {
		int sizeNeeded = size + count;			
		T[] items = (T[]) new Object[sizeNeeded];
		System.arraycopy(array, start, items, 0, count);
		System.arraycopy(this.items, 0, items, count, size);
		this.items = items;
		size += count;
	}

	public void insert (int index, T value) {
		
		if (index > size) throw new IndexOutOfBoundsException("index can't be > size: " + index + " > " + size);
		T[] items = this.items;
		if (size == items.length) items = resize(Math.max(8, (int)(size * 1.75f)));

		System.arraycopy(items, index, items, index + 1, size - index);
		
		size++;
		items[index] = value;
	}

	public Array<T> subArray(int start, int end) {
		int n = end - start;
		Array tmp = new Array<T>(n);
		System.arraycopy(items, start, tmp.items, 0, n);
		tmp.size = n;
		return tmp;
	}

}

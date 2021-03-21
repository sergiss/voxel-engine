package org.delmesoft.crazyblocks.world.blocks.utils;


import java.util.Arrays;

import javax.xml.crypto.Data;

public class DataPointArray {
	
	public DataPoint[] items;
	public int size;
	
	public DataPointArray() {
		this(8);
	}
	
	public DataPointArray(int initialCapacity) {
		items = new DataPoint[initialCapacity];
	}
	
	public void add(DataPoint lightPoint) {
		ensureCapacity();
		items[size++] = lightPoint;
	}

	private void ensureCapacity() {
		if(size == items.length) {
			DataPoint[] tmp = new DataPoint[(int) (size * 1.75f)];
			System.arraycopy(items, 0, tmp, 0, size);
			items = tmp;
		}
	}

	public void addFirst(DataPoint dataPoint) {
		ensureCapacity();
		System.arraycopy(items, 0, items, 1, size++);
		items[0] = dataPoint;
	}
	
	public DataPoint pop() {
		return items[--size];
	}

	public DataPoint poll() {
		DataPoint d = items[0];
		System.arraycopy(items, 1, items, 0, --size);
		return d;
	}

	public void clear() {
		size = 0;
	}

	public DataPoint[] toArray() {
		DataPoint[] tmp = new DataPoint[size];
		System.arraycopy(items, 0, tmp, 0, size);
		return tmp;
	}
}
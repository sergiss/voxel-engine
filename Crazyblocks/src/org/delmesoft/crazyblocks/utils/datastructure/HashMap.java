package org.delmesoft.crazyblocks.utils.datastructure;

import org.delmesoft.crazyblocks.math.MathHelper;

public class HashMap {

	private transient Entry[] entries;
	private transient int size;
	
	private int threshold = 12;
	private final float loadFactor = 0.75F;

	public HashMap() {
		entries = new Entry[16];
	}
	
	public HashMap(int initialCapacity) {
		
		initialCapacity = MathHelper.nextPowerOf2(initialCapacity);
		
		entries = new Entry[initialCapacity];
		this.threshold = (int) ((float) initialCapacity * this.loadFactor);
		
	}
	
	private static int hashCode(long l) {
		return hash((int) (l ^ (l >>> 32)));
	}

	private static int hash(int h) {
		h ^= (h >>> 20) ^ (h >>> 12);
		return h ^ (h >>> 7) ^ (h >>> 4);
	}

	private static int indexFor(int h, int lenght) {
		return h & lenght - 1;
	}

	public int size() {
		return this.size;
	}

	public Object get(long key) {

		Entry entry = entries[indexFor(hashCode(key), entries.length)];

		while (entry != null) {
			if (entry.key == key) {
				return entry.object;
			}
			entry = entry.next;
		}

		return null;
	}

	public boolean contains(long key) {
		return get(key) != null;
	}

	final Entry getEntry(long key) {

		Entry entry = entries[indexFor(hashCode(key), entries.length)];

		while (entry != null) {
			if (entry.key == key) {
				return entry;
			}
			entry = entry.next;
		}

		return null;
	}

	public void put(long key, Object object) {

		int hashCode = hashCode(key);
		int index = indexFor(hashCode, entries.length);

		Entry entry = entries[index];

		while (entry != null) {
			if (entry.key == key) {
				entry.object = object;
				return;
			}
			entry = entry.next;
		}

		put(hashCode, key, object, index);

	}

	private void resize(int newCapacity) {

		if (entries.length == 1073741824) {
			this.threshold = Integer.MAX_VALUE;
		} else {

			Entry[] newEntries = new Entry[newCapacity];
			
			this.transfer(newEntries);
			this.entries = newEntries;
			this.threshold = (int) ((float) newCapacity * this.loadFactor);
		}

	}

	private void transfer(Entry[] newEntries) {

		Entry[] src = this.entries;
		int newCapacity = newEntries.length;

		for (int j = 0; j < src.length; ++j) {
			Entry entry = src[j];

			if (entry != null) {
				src[j] = null;

				Entry next;

				do {

					next = entry.next;
					int index = indexFor(entry.hash, newCapacity);

					entry.next = newEntries[index];
					newEntries[index] = entry;
					entry = next;

				} while (next != null);
			}
		}

	}

	public Object remove(long key) {

		Entry entry = removeEntryForKey(key);

		return entry == null ? null : entry.object;
	}

	final Entry removeEntryForKey(long key) {

		int hash = hashCode(key);
		int index = indexFor(hash, this.entries.length);
		Entry prev = this.entries[index];

		Entry e;
		Entry next;

		for (e = prev; e != null; e = next) {
			next = e.next;
			if (e.key == key) {

				--this.size;
				if (prev == e) {
					this.entries[index] = next;
				} else {
					prev.next = next;
				}

				return e;
			}

			prev = e;
		}

		return e;
	}

	private void put(int hash, long key, Object object, int index) {
		Entry entry = this.entries[index];
		this.entries[index] = new Entry(hash, key, object, entry);
		if (this.size++ >= this.threshold) {
			this.resize(entries.length << 1);
		}
	}
	
	public void iterate(ObjectIterator it) {
		
		final Entry[] entries = this.entries;
		
		Entry entry;
		
		for (int j = 0; j < entries.length; ++j) {
			
			entry = entries[j];

			while (entry != null) {

				it.next(entry.object);

				entry = entry.next;

			}

		}

	}
	
	public void iterate(EntryIterator it) {
		
		final Entry[] entries = this.entries;
		
		Entry entry;
		
		for (int j = 0; j < entries.length; ++j) {
			
			entry = entries[j];

			while (entry != null) {

				it.next(entry);

				entry = entry.next;

			}

		}

	}
	
	public void clear() {

		Entry[] entries = this.entries;
		for(int i = 0, n = entries.length; i < n; i++) 
			entries[i] = null;

		size = 0;

	}
	
	public class Entry {

	    public final long key;
	    public Object object;
	    Entry next;
	    final int hash;

		public Entry(int hash, long key, Object object, Entry next) {
			
			this.hash = hash;
			this.key = key;
			this.object = object;
			this.next = next;
		}

	}
	
	public interface ObjectIterator {
		void next(Object o);
	}
	
	public interface EntryIterator {
		void next(Entry e);
	}

}

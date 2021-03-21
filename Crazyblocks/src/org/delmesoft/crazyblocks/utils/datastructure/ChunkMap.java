package org.delmesoft.crazyblocks.utils.datastructure;

import org.delmesoft.crazyblocks.world.blocks.Chunk;

public class ChunkMap {

	private static final int[] primeCapacities = { 3, 5, 7, 11, 17, 23, 31, 37, 43, 47, 67, 79, 89, 97, 137, 163, 179,
			197, 277, 311, 331, 359, 379, 397, 433, 557, 599, 631, 673, 719, 761, 797, 877, 953, 1039, 1117, 1201, 1277,
			1361, 1439, 1523, 1597, 1759, 1907, 2081, 2237, 2411, 2557, 2729, 2879, 3049, 3203, 3527, 3821, 4177, 4481,
			4831, 5119, 5471, 5779, 6101, 6421, 7057, 7643, 8363, 8963, 9677, 10243, 10949, 11579, 12203, 12853, 14143,
			15287, 16729, 17929, 19373, 20507, 21911, 23159, 24407, 25717, 28289, 30577, 33461, 35863, 38747, 41017,
			43853, 46327, 48817, 51437, 56591, 61169, 66923, 71741, 77509, 82037, 87719, 92657, 97649, 102877, 113189,
			122347, 133853, 143483, 155027, 164089, 175447, 185323, 195311, 205759, 226379, 244703, 267713, 286973,
			310081, 328213, 350899, 370661, 390647, 411527, 452759, 489407, 535481, 573953, 620171, 656429, 701819,
			741337, 781301, 823117, 905551, 978821, 1070981, 1147921, 1240361, 1312867, 1403641, 1482707, 1562611,
			1646237, 1811107, 1957651, 2141977, 2295859, 2480729, 2625761, 2807303, 2965421, 3125257, 3292489, 3622219,
			3915341, 4283963, 4591721, 4961459, 5251529, 5614657, 5930887, 6250537, 6584983, 7244441, 7830701, 8567929,
			9183457, 9922933, 10503061, 11229331, 11861791, 12501169, 13169977, 14488931, 15661423, 17135863, 18366923,
			19845871, 21006137, 22458671, 23723597, 25002389, 26339969, 28977863, 31322867, 34271747, 36733847,
			39691759, 42012281, 44917381, 47447201, 50004791, 52679969, 57955739, 62645741, 68543509, 73467739,
			79383533, 84024581, 89834777, 94894427, 100009607, 105359939, 115911563, 125291483, 137087021, 146935499,
			158767069, 168049163, 179669557, 189788857, 200019221, 210719881, 231823147, 250582987, 274174111,
			293871013, 317534141, 336098327, 359339171, 379577741, 400038451, 421439783, 463646329, 501165979,
			548348231, 587742049, 635068283, 672196673, 718678369, 759155483, 800076929, 842879579, 927292699,
			1002331963, 1096696463, 1175484103, 1270136683, 1344393353, 1437356741, 1518310967, 1600153859, 1685759167,
			1854585413, 2004663929, 2147483647 };

	private static final int largestPrime = primeCapacities[primeCapacities.length - 1];

	private static int nextPrime(int desiredCapacity) {
		int i = java.util.Arrays.binarySearch(primeCapacities, desiredCapacity);
		if (i < 0) {
			i = -i - 1;
		}
		return primeCapacities[i];
	}

	/**
	 * The number of distinct associations in the map; its "size()".
	 */
	protected int distinct;

	/**
	 * The table capacity c=table.length always satisfies the invariant
	 * <tt>c * minLoadFactor <= s <= c * maxLoadFactor</tt>, where s=size() is
	 * the number of associations currently contained. The term "c *
	 * minLoadFactor" is called the "lowWaterMark", "c * maxLoadFactor" is
	 * called the "highWaterMark". In other words, the table capacity (and
	 * proportionally the memory used by this class) oscillates within these
	 * constraints. The terms are precomputed and cached to avoid recalculating
	 * them each time put(..) or removeKey(...) is called.
	 */
	protected int lowWaterMark;
	protected int highWaterMark;

	/**
	 * The minimum load factor for the hashtable.
	 */
	protected double minLoadFactor;

	/**
	 * The maximum load factor for the hashtable.
	 */
	protected double maxLoadFactor;

	protected static final int defaultCapacity = 277;
	protected static final double defaultMinLoadFactor = 0.2;
	protected static final double defaultMaxLoadFactor = 0.5;

	/**
	 * The hash table keys.
	 * 
	 * @serial
	 */
	protected int table[];

	/**
	 * The hash table values.
	 * 
	 * @serial
	 */
	protected Chunk values[];

	/**
	 * The state of each hash table entry (FREE, FULL, REMOVED).
	 * 
	 * @serial
	 */
	protected byte state[];

	/**
	 * The number of table entries in state==FREE.
	 * 
	 * @serial
	 */
	protected int freeEntries;

	protected static final byte FREE = 0;
	protected static final byte FULL = 1;
	protected static final byte REMOVED = 2;

	protected DistinctListener distinctListener;

	/**
	 * Constructs an empty map with default capacity and default load factors.
	 */
	public ChunkMap() {
		this(defaultCapacity);
	}

	/**
	 * Constructs an empty map with the specified initial capacity and default
	 * load factors.
	 *
	 * @param initialCapacity
	 *            the initial capacity of the map.
	 * @throws IllegalArgumentException
	 *             if the initial capacity is less than zero.
	 */
	public ChunkMap(int initialCapacity) {
		this(initialCapacity, defaultMinLoadFactor, defaultMaxLoadFactor);
	}

	/**
	 * Constructs an empty map with the specified initial capacity and the
	 * specified minimum and maximum load factor.
	 *
	 * @param initialCapacity
	 *            the initial capacity.
	 * @param minLoadFactor
	 *            the minimum load factor.
	 * @param maxLoadFactor
	 *            the maximum load factor.
	 * @throws IllegalArgumentException
	 *             if
	 *             <tt>initialCapacity < 0 || (minLoadFactor < 0.0 || minLoadFactor >= 1.0) || (maxLoadFactor <= 0.0 || maxLoadFactor >= 1.0) || (minLoadFactor >= maxLoadFactor)</tt>.
	 */
	public ChunkMap(int initialCapacity, double minLoadFactor, double maxLoadFactor) {
		setUp(initialCapacity, minLoadFactor, maxLoadFactor);
	}

	/**
	 * Removes all (key,value) associations from the receiver. Implicitly calls
	 * <tt>trimToSize()</tt>.
	 */
	public void clear() {

		for (int i = 0, n = state.length; i < n; ++i) {
			state[i] = FREE;
			values[i] = null;
		}

		this.distinct = 0;
		this.freeEntries = table.length; // delta
		trimToSize();
	}

	public Chunk get(int key) {
		int i = indexOfKey(key);
		if (i < 0)
			return null; // not contained
		return values[i];
	}

	/**
	 * @param key
	 *            the key to be added to the receiver.
	 * @return the index where the key would need to be inserted, if it is not
	 *         already contained. Returns -index-1 if the key is already
	 *         contained at slot index. Therefore, if the returned index < 0,
	 *         then it is already contained at slot -index-1. If the returned
	 *         index >= 0, then it is NOT already contained and should be
	 *         inserted at slot index.
	 */
	protected int indexOfInsertion(int key) {
		final int tab[] = table;
		final byte stat[] = state;
		final int length = tab.length;

		final int hash = key & 0x7FFFFFFF;
		int i = hash % length;
		int decrement = (hash / length) % length;
		if (decrement == 0)
			decrement = 1;

		// stop if we find a removed or free slot, or if we find the key itself
		// do NOT skip over removed slots (yes, open addressing is like that...)
		while (stat[i] == FULL && tab[i] != key) {
			i -= decrement;
			// hashCollisions++;
			if (i < 0)
				i += length;
		}

		if (stat[i] == REMOVED) {
			// stop if we find a free slot, or if we find the key itself.
			// do skip over removed slots (yes, open addressing is like that...)
			// assertion: there is at least one FREE slot.
			int j = i;
			while (stat[i] != FREE && (stat[i] == REMOVED || tab[i] != key)) {
				i -= decrement;
				// hashCollisions++;
				if (i < 0)
					i += length;
			}
			if (stat[i] == FREE)
				i = j;
		}

		if (stat[i] == FULL) {
			// key already contained at slot i.
			// return a negative number identifying the slot.
			return -i - 1;
		}
		// not already contained, should be inserted at slot i.
		// return a number >= 0 identifying the slot.
		return i;
	}

	/**
	 * @param key
	 *            the key to be searched in the receiver.
	 * @return the index where the key is contained in the receiver, returns -1
	 *         if the key was not found.
	 */
	protected int indexOfKey(int key) {
		final int tab[] = table;
		final byte stat[] = state;
		final int length = tab.length;

		final int hash = key & 0x7FFFFFFF;
		int i = hash % length;
		int decrement = (hash / length) % length;
		if (decrement == 0)
			decrement = 1;

		// stop if we find a free slot, or if we find the key itself.
		// do skip over removed slots (yes, open addressing is like that...)
		while (stat[i] != FREE && (stat[i] == REMOVED || tab[i] != key)) {
			i -= decrement;
			// hashCollisions++;
			if (i < 0)
				i += length;
		}

		if (stat[i] == FREE)
			return -1; // not found
		return i; // found, return index where key is contained
	}

	public boolean put(int key, Chunk value) {
		int i = indexOfInsertion(key);
		if (i < 0) { // already contained
			i = -i - 1;
			this.values[i] = value;
			return false;
		}

		if (this.distinct > this.highWaterMark) {
			int newCapacity = chooseGrowCapacity(this.distinct + 1, this.minLoadFactor, this.maxLoadFactor);
			rehash(newCapacity);
			return put(key, value);
		}

		this.table[i] = key;
		this.values[i] = value;
		if (this.state[i] == FREE)
			this.freeEntries--;
		this.state[i] = FULL;
		this.distinct++;

		if (this.freeEntries < 1) { // delta
			int newCapacity = chooseGrowCapacity(this.distinct + 1, this.minLoadFactor, this.maxLoadFactor);
			rehash(newCapacity);
		}

		if(distinctListener != null) {
			distinctListener.event(distinct);
		}

		return true;
	}

	protected int chooseGrowCapacity(int size, double minLoad, double maxLoad) {
		return nextPrime(Math.max(size + 1, (int) (((size << 2) / (3 * minLoad + maxLoad)))));
	}

	/**
	 * Returns new high water mark threshold based on current capacity and
	 * maxLoadFactor.
	 * 
	 * @return int the new threshold.
	 */
	protected int chooseHighWaterMark(int capacity, double maxLoad) {
		return Math.min(capacity - 2, (int) (capacity * maxLoad)); // makes sure
																	// there is
																	// always at
																	// least one
																	// FREE slot
	}

	/**
	 * Returns new low water mark threshold based on current capacity and
	 * minLoadFactor.
	 * 
	 * @return int the new threshold.
	 */
	protected int chooseLowWaterMark(int capacity, double minLoad) {
		return (int) (capacity * minLoad);
	}

	/**
	 * Chooses a new prime table capacity optimized for shrinking that
	 * (approximately) satisfies the invariant
	 * <tt>c * minLoadFactor <= size <= c * maxLoadFactor</tt> and has at least
	 * one FREE slot for the given size.
	 */
	protected int chooseShrinkCapacity(int size, double minLoad, double maxLoad) {
		return nextPrime(Math.max(size + 1, (int) (((size << 2) / (minLoad + 3 * maxLoad)))));
	}

	/**
	 * Rehashes the contents of the receiver into a new table with a smaller or
	 * larger capacity. This method is called automatically when the number of
	 * keys in the receiver exceeds the high water mark or falls below the low
	 * water mark.
	 */
	protected void rehash(int newCapacity) {

		int oldCapacity = table.length;

		int[] oldTable    = table;
		Chunk[] oldValues = values;
		byte[] oldState   = state;

		int[] newTable    = new int[newCapacity];
		Chunk[] newValues = new Chunk[newCapacity];
		byte[] newState   = new byte[newCapacity];

		lowWaterMark  = chooseLowWaterMark(newCapacity , minLoadFactor);
		highWaterMark = chooseHighWaterMark(newCapacity, maxLoadFactor);

		table  = newTable;
		values = newValues;
		state  = newState;
		freeEntries = newCapacity - distinct; // delta

		for (int i = oldCapacity; i-- > 0;) {
			if (oldState[i] == FULL) {
				int element = oldTable[i];
				int index   = indexOfInsertion(element);
				newTable[index]  = element;
				newValues[index] = oldValues[i];
				newState[index]  = FULL;
			}
		}

	}

	/**
	 * Removes the given key with its associated element from the receiver, if
	 * present.
	 *
	 * @param key
	 *            the key to be removed from the receiver.
	 * @return <tt>true</tt> if the receiver contained the specified key,
	 *         <tt>false</tt> otherwise.
	 */
	public boolean removeKey(int key) {
		int i = indexOfKey(key);
		if (i < 0)
			return false; // key not contained

		this.state[i] = REMOVED;
		this.values[i] = null; // delta
		this.distinct--;

		if (this.distinct < this.lowWaterMark) {
			int newCapacity = chooseShrinkCapacity(this.distinct, this.minLoadFactor, this.maxLoadFactor);
			rehash(newCapacity);
		}

		return true;
	}

	/**
	 * Initializes the receiver.
	 *
	 * @param initialCapacity
	 *            the initial capacity of the receiver.
	 * @param minLoadFactor
	 *            the minLoadFactor of the receiver.
	 * @param maxLoadFactor
	 *            the maxLoadFactor of the receiver.
	 * @throws IllegalArgumentException
	 *             if
	 *             <tt>initialCapacity < 0 || (minLoadFactor < 0.0 || minLoadFactor >= 1.0) || (maxLoadFactor <= 0.0 || maxLoadFactor >= 1.0) || (minLoadFactor >= maxLoadFactor)</tt>.
	 */
	protected void setUp(int initialCapacity, double minLoadFactor, double maxLoadFactor) {

		if (initialCapacity < 0)
			throw new IllegalArgumentException("Initial Capacity must not be less than zero: " + initialCapacity);
		if (minLoadFactor < 0.0 || minLoadFactor >= 1.0)
			throw new IllegalArgumentException("Illegal minLoadFactor: " + minLoadFactor);
		if (maxLoadFactor <= 0.0 || maxLoadFactor >= 1.0)
			throw new IllegalArgumentException("Illegal maxLoadFactor: " + maxLoadFactor);
		if (minLoadFactor >= maxLoadFactor)
			throw new IllegalArgumentException(
					"Illegal minLoadFactor: " + minLoadFactor + " and maxLoadFactor: " + maxLoadFactor);

		int capacity = nextPrime(initialCapacity);

		if (capacity == 0)
			capacity = 1; // open addressing needs at least one FREE slot at any time.

		this.table = new int[capacity];
		this.values = new Chunk[capacity];
		this.state = new byte[capacity];

		// memory will be exhausted long before this pathological case happens,
		// anyway.
		this.minLoadFactor = minLoadFactor;
		if (capacity == largestPrime)
			this.maxLoadFactor = 1.0;
		else
			this.maxLoadFactor = maxLoadFactor;

		this.distinct = 0;
		this.freeEntries = capacity; // delta

		// lowWaterMark will be established upon first expansion.
		// establishing it now (upon instance construction) would immediately
		// make the table shrink upon first put(...).
		// After all the idea of an "initialCapacity" implies violating
		// lowWaterMarks when an object is young.
		// See ensureCapacity(...)
		this.lowWaterMark = 0;
		this.highWaterMark = chooseHighWaterMark(capacity, this.maxLoadFactor);
	}

	/**
	 * Trims the capacity of the receiver to be the receiver's current size.
	 * Releases any superfluous internal memory. An application can use this
	 * operation to minimize the storage of the receiver.
	 */
	public void trimToSize() {
		// * 1.2 because open addressing's performance exponentially degrades
		// beyond that point so that even rehashing the table can take very long
		int newCapacity = nextPrime((int) (1 + 1.2 * distinct));
		if (table.length > newCapacity) {
			rehash(newCapacity);
		}
	}

	public int size() {
		return distinct;
	}

	public DistinctListener getDistinctListener() {
		return distinctListener;
	}

	public void setDistinctListener(DistinctListener distinctListener) {
		this.distinctListener = distinctListener;
	}

	public void iterate(Iterator it) {
		Chunk[] val  = values;
		byte[]  stat = state;
		int idx = 0;
		do {
			if (stat[idx] != FREE && stat[idx] != REMOVED) {
				if (val[idx] != null) {
					it.next(val[idx]);
				}
			}
		} while (++idx < stat.length);
	}

	public interface Iterator {
		void next(Chunk chunk);
	}

	public interface DistinctListener {
		void event(int size);
	}

}

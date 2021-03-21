package org.delmesoft.crazyblocks.math;

public class FastRandom {
	
	private long seed;
	
	public FastRandom(long seed) {
		this.seed = seed;
	}
	
	public FastRandom() {
		this.seed = System.nanoTime();
	}
	
	public void setSeed(long seed) {
		this.seed = seed;
	}
	
	public long nextLong() {
		seed ^= (seed << 21);
		seed ^= (seed >>> 35);
		seed ^= (seed << 4);
		return seed;
	}
	
	public double nextAbsDouble() {
		return (nextLong() >>> 1) / (0x7fffffffffffffffL - 1D);
	}

	public float nextAbsFloat() {
		return (nextLong() >>> 1) / (0x7fffffffffffffffL - 1F);
	}
	
	public double nextDouble() {
		return nextLong() / (0x7fffffffffffffffL - 1D);
	}

	public float nextFloat() {
		return nextLong() / (0x7fffffffffffffffL - 1F);
	}
	
    public int nextInt() {
        return (int) nextLong();
    }

    public int nextInt(int range) {
    	
    	int val = (int) nextLong();
    	    	
        return (val < 0 ? -val : val) % range;
    }

}

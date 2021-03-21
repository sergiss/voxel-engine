package org.delmesoft.crazyblocks.world.blocks.utils;

public class DataPoint {

	public int x, y, z;

	public byte data;

	public DataPoint(int x, int y, int z, byte data) {
		this.x = x; 
		this.y = y; 
		this.z = z;
		this.data = data;
	}
	
	public DataPoint(int x, int y, int z) {
		this.x = x; 
		this.y = y; 
		this.z = z;
	}

	public DataPoint() {}

	public void setPoint(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void set(int x, int y, int z, byte data) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.data = data;
	}

	@Override
	public String toString() {
		return "DataPoint{" +
				"x=" + x +
				", y=" + y +
				", z=" + z +
				", data=" + data +
				'}';
	}
}

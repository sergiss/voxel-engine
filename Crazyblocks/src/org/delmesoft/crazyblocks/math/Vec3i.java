package org.delmesoft.crazyblocks.math;

public class Vec3i {

	public int x, y, z;
	
	public Vec3i() {}

	public Vec3i(int x, int y, int z) {

		this.x = x;
		this.y = y;
		this.z = z;

	}

	public Vec3i(Vec3i v) {

		this.x = v.x;
		this.y = v.y;
		this.z = v.z;

	}

	public Vec3i set(int x, int y, int z) {

		this.x = x;
		this.y = y;
		this.z = z;

		return this;
	}

	public Vec3i set(Vec3i v) {

		this.x = v.x;
		this.y = v.y;
		this.z = v.z;

		return this;
	}

	public Vec3i copy() {
		return new Vec3i(this);
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Vec3i) {
			Vec3i v = (Vec3i) o;
			return v.x == x && v.y == y && v.z == z;
		}
		return false;
	}

	@Override
	public String toString() {
		return x + ", " + y + ", " + z;
	}

}

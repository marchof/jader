package jader.math;

import static jader.math.Vec2.vec2;

public record Vec3(float x, float y, float z) {

	public static Vec3 vec3(float x, float y, float z) {
		return new Vec3(x, y, z);
	}

	public static Vec3 vec3(double x, double y, double z) {
		return new Vec3((float) x, (float) y, (float) z);
	}

	public Vec3 add(Vec3 other) {
		return new Vec3(this.x + other.x, this.y + other.y, this.z + other.z);
	}

	public Vec3 nor() {
		float len = len();
		if (len != 0) {
			return new Vec3(x / len, y / len, z / len);
		}
		return this;
	}

	public Vec3 direction(Vec3 to) {
		float dx = x - to.x;
		float dy = y - to.y;
		float dz = z - to.z;
		float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
		if (len != 0) {
			return new Vec3(dx / len, dy / len, dz / len);
		}
		return new Vec3(1, 0, 0);
	}

	public Vec3 sub(Vec3 other) {
		return new Vec3(this.x - other.x, this.y - other.y, this.z - other.z);
	}

	public Vec3 mul(float scalar) {
		return new Vec3(this.x * scalar, this.y * scalar, this.z * scalar);
	}

	public Vec3 mul(Vec3 other) {
		return new Vec3(x * other.x, y * other.y, z * other.z);
	}

	public Vec3 mulAdd(Vec3 vec, float scalar) {
		return new Vec3(this.x + vec.x * scalar, this.y + vec.y * scalar, this.z + vec.z * scalar);
	}

	public Vec3 mulSub(Vec3 vec, float scalar) {
		return new Vec3(this.x - vec.x * scalar, this.y - vec.y * scalar, this.z - vec.z * scalar);
	}

	public float len() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	public float dist2(final Vec3 to) {
		float dx = to.x - x;
		float dy = to.y - y;
		float dz = to.z - z;
		return dx * dx + dy * dy + dz * dz;
	}

	public float dist(Vec3 to) {
		return (float) Math.sqrt(dist2(to));
	}

	public float dot(Vec3 vector) {
		return x * vector.x + y * vector.y + z * vector.z;
	}

	public Vec3 cross(Vec3 vector) {
		return new Vec3( //
				y * vector.z - z * vector.y, //
				z * vector.x - x * vector.z, //
				x * vector.y - y * vector.x);
	}
	
	public Vec2 xy() {
		return vec2(x, y);
	}

	public Vec2 xz() {
		return vec2(x, z);
	}
	
	public Vec2 yz() {
		return vec2(y, z);
	}
	
}
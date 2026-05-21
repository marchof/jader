package jader.math;

/// A ray in 3 dimensions described as a starting point and a direction.
public record Ray3(Vec3 start, Vec3 direction) {

	public Ray3 {
		direction = direction.nor();
	}

	public static Ray3 ray3(Vec3 start, Vec3 direction) {
		return new Ray3(start, direction);
	}

	public Vec3 pointAt(float distance) {
		return start.mulAdd(direction, distance);
	}

}

package jader.math;

/// A ray in 3 dimensions described as a starting point and a direction.
public record Ray3(Vec3 start, Vec3 direction) {

	public Ray3 {
		direction = direction.nor();
	}

	public static Ray3 ray3(Vec3 start, Vec3 direction) {
		return new Ray3(start, direction);
	}

	public static Ray3 ray3To(Vec3 start, Vec3 target) {
		return new Ray3(start, target.sub(start));
	}
	
	public Vec3 pointAt(float distance) {
		return start.mulAdd(direction, distance);
	}

}

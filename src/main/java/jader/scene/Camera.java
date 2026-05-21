package jader.scene;

import static jader.math.Ray3.ray3;
import static jader.math.Vec3.vec3;

import jader.math.Ray3;
import jader.math.Vec3;

public record Camera(

		Vec3 position, //
		Vec3 direction, //
		Vec3 right, //
		Vec3 up, //
		float focalLength

) {

	public static final Vec3 DEFAULT_UP = vec3(0, 1, 0);
	public static final float DEFAULT_FOCAL_LENGTH = 1.0f;

	public Camera {
		direction = direction.nor();
		right = right.nor();
		up = up.nor();
	}

	public static Camera direction(Vec3 position, Vec3 direction, Vec3 worldUp, float focalLength) {
		var right = direction.cross(worldUp);
		var up = right.cross(direction);
		return new Camera(position, direction, right, up, focalLength);
	}

	public static Camera direction(Vec3 position, Vec3 direction) {
		return direction(position, direction, DEFAULT_UP, DEFAULT_FOCAL_LENGTH);
	}

	public Camera withUp(Vec3 worldUp) {
		return direction(position, direction, worldUp, focalLength);
	}

	public Camera withFocalLength(float focalLength) {
		return new Camera(position, direction, right, up, focalLength);
	}

	/// Calculates the view direction of this camera for a given camera image coordinate (typically pixels).
	/// 
	/// @param x x-coordinate on the camera image (top to bottom)
	/// @param y y-coordinate on the camera image (left to right)
	/// @param width camera width
	/// @param height camera height
	/// @return ray at the given screen position
	public Ray3 project(float x, float y, float width, float height) {
		var ratio = width / height;
		var ux = (x / width - 0.5f) * ratio / focalLength;
		var uy = (-y / height + 0.5f) / focalLength;
		return ray3(position, direction.mulAdd(right, ux).mulAdd(up(), uy));
	}

}

package jader.scene;

import static jader.math.Vec3.vec3;

import jader.math.Vec3;

public record Camera(

		Vec3 position,
		Vec3 direction,
		Vec3 right,
		Vec3 up,
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

}

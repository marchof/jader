package jader.scene;

import static jader.math.Vec3.vec3;

import jader.math.Vec3;

public record Camera(

		Vec3 position,
		Vec3 direction,
		Vec3 right,
		Vec3 up,
		float f
		
) {
	
	public static final Vec3 DEFAULT_UP = vec3(0, 1, 0);
	public static final float DEFAULT_FOCALLENGTH = 1.0f;
	
	public Camera {
		direction = direction.nor();
		right = right.nor();
		up = up.nor();
	}
	
	public static Camera direction(Vec3 position, Vec3 direction, Vec3 worldup, float focalLength) {
		var right = direction.cross(worldup);
		var up = right.cross(direction);
		return new Camera(position, direction, right, up, focalLength);
	}
	
	public static Camera direction(Vec3 position, Vec3 direction) {
		return direction(position, direction, DEFAULT_UP, DEFAULT_FOCALLENGTH);
	}
	
	public Camera withUp(Vec3 worldup) {
		return direction(position, direction, worldup, f);
	}
	
	public Camera withFocalLength(float f) {
		return new Camera(position, direction, right, up, f);
	}

}

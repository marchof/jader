package jader.shader;

import static jader.math.Vec3.vec3;

import jader.math.Vec3;
import jader.shape.Material;
import jader.shape.Shape;

public record RayMarch(

		Vec3 hitPoint, //
		Shape shape, //
		float distanceRatio, //
		Shape.Distance closestDist

) {

	private static final float MIN_SURFACE_DIST = 0.000001f;
	private static final int MAX_STEPS = 400;

	private static final Vec3 DELTA_X = vec3(0.001f, 0f, 0f);
	private static final Vec3 DELTA_Y = vec3(0f, 0.001f, 0f);
	private static final Vec3 DELTA_Z = vec3(0f, 0f, 0.001f);

	public static RayMarch from(Vec3 start, Vec3 direction, Shape shape, float maxMarchDistance) {

		Shape.Distance closestDistance = null;
		var minLength = Float.MAX_VALUE;
		var minDistanceRatio = 1f;
		var marchDist = 0f;

		for (var steps = 0;; steps++) {
			var p = start.mulAdd(direction, marchDist);
			var distance = shape.distance(p);
			var len = distance.length();
			if (len < minLength) {
				closestDistance = distance;
				minLength = len;
			} else {
				// When we move away from a surface we reset the step counter.
				// Otherwise near-misses will influence the ray marching towards
				// objects behind those misses.
				steps = 0;
			}
			if (marchDist > 0f) {
				var dr = len / marchDist;
				if (dr < minDistanceRatio) {
					minDistanceRatio = dr;
				}
			}
			if (len < MIN_SURFACE_DIST || steps > MAX_STEPS) {
				return new RayMarch(p, shape, minDistanceRatio, closestDistance);
			}
			if ((marchDist += len) > maxMarchDistance) {
				return new RayMarch(null, null, minDistanceRatio, null);
			}
		}
	}

	public boolean isHit() {
		return hitPoint != null;
	}

	// The calculation in the following methods are expensive and should only
	// be called once when the value is actually required.

	public Material material() {
		return closestDist.material();
	}

	public Vec3 hitNormal() {
		var dX = hitPoint.add(DELTA_X);
		var dY = hitPoint.add(DELTA_Y);
		var dZ = hitPoint.add(DELTA_Z);
		return vec3(shape.distance(dX).length(), shape.distance(dY).length(), shape.distance(dZ).length()).nor();
	}

	public static Vec3 hoverHitPoint(Vec3 hitPoint, Vec3 hitNormal) {
		return hitPoint.mulAdd(hitNormal, 2 * MIN_SURFACE_DIST);
	}

}

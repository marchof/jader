package jader.shader;

import jader.math.Vec3;
import jader.shape.Shape;

/**
 * The result of a ray marching operation.
 */
record RayMarch(

		Vec3 hitPoint, //
		float distanceRatio, //
		Shape.Distance closestDist

) {

	static final float MIN_SURFACE_DIST = 0.000001f;

	private static final int MAX_STEPS = 400;

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
				return new RayMarch(p, minDistanceRatio, closestDistance);
			}
			if ((marchDist += len) > maxMarchDistance) {
				return new RayMarch(null, minDistanceRatio, null);
			}
		}
	}

	public boolean isHit() {
		return hitPoint != null;
	}

}

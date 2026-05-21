package jader.shader;

import jader.math.Ray3;
import jader.math.Vec3;
import jader.shape.Material;
import jader.shape.Shape;

/// The result of a ray marching operation.
sealed interface RayMarch {

	/// Successful ray march that hits a surface.
	record Hit(Vec3 point, Shape.Distance closestDistance, float distance) implements RayMarch {
		
		public Material material() {
			return closestDistance.material();
		}
	}

	/// Ray march that exceeds the maximum distance without a hit.
	record Miss(float distanceRatio) implements RayMarch {
	}

	static final float MIN_SURFACE_DIST = 0.000001f;

	static final int MAX_STEPS = 400;

	public static RayMarch from(Ray3 ray, Shape shape, float maxMarchDistance) {

		Shape.Distance closestDistance = null;
		var minLength = Float.MAX_VALUE;
		var minDistanceRatio = 1f;
		var marchDist = 0f;

		for (var steps = 0;; steps++) {
			var p = ray.pointAt(marchDist);
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
				return new Hit(p, closestDistance, marchDist);
			}
			if ((marchDist += len) > maxMarchDistance) {
				return new Miss(minDistanceRatio);
			}
		}
	}

}

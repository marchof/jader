package jader.shader;

import jader.math.Ray3;
import jader.math.Vec3;
import jader.shape.Shape;

/// The result of a ray marching operation.
sealed interface RayMarch {

	/// Successful ray march that hits a surface.
	record Hit(Vec3 point, float distance) implements RayMarch {
	}

	/// Ray march that exceeds the maximum distance without a hit.
	record Miss(float distanceRatio) implements RayMarch {
	}

	static final float MIN_SURFACE_DIST = 0.000001f;

	static final int MAX_STEPS = 400;

	public static RayMarch from(Ray3 ray, Shape shape, float maxMarchDistance) {

		var minDistance = Float.MAX_VALUE;
		var minDistanceRatio = 1f;
		var marchDist = 0f;

		for (var steps = 0;; steps++) {
			var p = ray.pointAt(marchDist);
			var distance = shape.distance(p);
			if (distance < minDistance) {
				minDistance = distance;
			} else {
				// When we move away from a surface we reset the step counter.
				// Otherwise near-misses will influence the ray marching towards
				// objects behind those misses.
				steps = 0;
			}
			if (marchDist > 0f) {
				var dr = distance / marchDist;
				if (dr < minDistanceRatio) {
					minDistanceRatio = dr;
				}
			}
			if (distance < MIN_SURFACE_DIST || steps > MAX_STEPS) {
				return new Hit(p, marchDist);
			}
			if ((marchDist += distance) > maxMarchDistance) {
				return new Miss(minDistanceRatio);
			}
		}
	}

}

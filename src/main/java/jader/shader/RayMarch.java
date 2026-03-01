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
	
	// Pre-calculated tetrahedron edges for normal calculation
	private static final float TSIZE = 0.00001f;
	private static final Vec3 T1 = vec3(TSIZE, -TSIZE, -TSIZE);
	private static final Vec3 T2 = vec3(-TSIZE, -TSIZE, TSIZE);
	private static final Vec3 T3 = vec3(-TSIZE, TSIZE, -TSIZE);
	private static final Vec3 T4 = vec3(TSIZE, TSIZE, TSIZE);

	public Vec3 hitNormal() {
		// Tetrahedron technique for smooth normals with 4 distance calculations
		// as described in https://iquilezles.org/articles/normalsSDF/
		var d1 = shape.distance(hitPoint.add(T1)).length();
		var d2 = shape.distance(hitPoint.add(T2)).length();
		var d3 = shape.distance(hitPoint.add(T3)).length();
		var d4 = shape.distance(hitPoint.add(T4)).length();
		return vec3( //
				+d1 - d2 - d3 + d4, //
				-d1 - d2 + d3 + d4, //
				-d1 + d2 - d3 + d4).nor();
	}

	public static Vec3 hoverHitPoint(Vec3 hitPoint, Vec3 hitNormal) {
		return hitPoint.mulAdd(hitNormal, 2 * MIN_SURFACE_DIST);
	}

}

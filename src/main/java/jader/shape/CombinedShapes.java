package jader.shape;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

import jader.math.Vec3;
import jader.shape.Shape.Distance;

public class CombinedShapes {

	public static Shape union(Shape... shapes) {
		return p -> minSDF(p, shapes);
	}

	public static Shape intersect(Shape... shapes) {
		return p -> maxSDF(p, shapes);
	}

	public static Shape subtract(Shape a, Shape b) {
		return p -> {
			var dista = a.distance(p);
			var distb = b.distance(p);
			return (dista.length() > -distb.length()) ? dista : distb.scaledDistance(-1f);
		};
	}

	public static Shape smoothUnion(Shape shape1, Shape shape2, float k) {
		var k4 = k * 4.0f;
		return p -> {
			var dist1 = shape1.distance(p);
			var dist2 = shape2.distance(p);
			float l1 = dist1.length();
			float l2 = dist2.length();
			float h = max(k4 - abs(l1 - l2), 0.0f);
			var d = min(l1, l2) - h * h * 0.25f / k4;
			return new BlendedMaterialDistance(d, dist1, dist2, l1 / (l1 + l2));
		};
	}
	
	public static Shape smoothSubtract(Shape shape1, Shape shape2, float k) {
		var k4 = k * 4.0f;
		return p -> {
			var dist1 = shape1.distance(p);
			var dist2 = shape2.distance(p);
			float l1 = -dist1.length();
			float l2 = dist2.length();
			float h = max(k4 - abs(l1 - l2), 0.0f);
			var d = min(l1, l2) - h * h * 0.25f / k4;
			return new BlendedMaterialDistance(-d, dist1, dist2, l1 / (l1 + l2));
		};
	}
	
	private static record BlendedMaterialDistance(float length, Distance d1, Distance d2, float f) implements Shape.Distance {
		@Override
		public Material material() {
			return d1.material().blend(d2.material(), f);
		}

		@Override
		public Distance scaledDistance(float f) {
			return new BlendedMaterialDistance(length * f, d1, d2, f);
		}
	}

	// While the following methods could be nicely implemented with streams, the
	// performance drawback is too large for these critical-path operations.
	// Therefore, we use these allocation-free implementations.

	private static Distance minSDF(Vec3 p, Shape... shapes) {
		var mindist = Float.MAX_VALUE;
		Distance result = null;
		for (var s : shapes) {
			var d = s.distance(p);
			var dist = d.length();
			if (dist < mindist) {
				mindist = dist;
				result = d;
			}
		}
		return result;
	}

	private static Distance maxSDF(Vec3 p, Shape... shapes) {
		var maxdist = -Float.MAX_VALUE;
		Distance result = null;
		for (var s : shapes) {
			var d = s.distance(p);
			var dist = d.length();
			if (dist > maxdist) {
				maxdist = dist;
				result = d;
			}
		}
		return result;
	}

}

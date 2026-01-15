package jader.shape;

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
			return (dista.length() > -distb.length()) ? dista : distb.negdist();
		};
	}

	// While the following methods could be nicely implements with streams, the
	// performance drawback is too big for this critical path operations. Therefore
	// we use these allocation free implementations.

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

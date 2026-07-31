package jader.shape;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

import jader.math.Vec3;

public class CombinedShapes {

	public static Shape union(Shape... shapes) {
		record MinShape(Shape[] shapes) implements Shape {

			@Override
			public float distance(Vec3 p) {
				var mindist = Float.MAX_VALUE;
				for (var s : shapes) {
					var dist = s.distance(p);
					if (dist < mindist) {
						mindist = dist;
					}
				}
				return mindist;
			}

			@Override
			public Material material(Vec3 p) {
				var mindist = Float.MAX_VALUE;
				Shape minDistShape = null;
				for (var s : shapes) {
					var dist = s.distance(p);
					if (dist < mindist) {
						mindist = dist;
						minDistShape = s;
					}
				}
				return minDistShape.material(p);
			}

		}
		return new MinShape(shapes);
	}

	public static Shape intersect(Shape... shapes) {
		record MaxShape(Shape[] shapes) implements Shape {

			@Override
			public float distance(Vec3 p) {
				var maxdist = -Float.MAX_VALUE;
				for (var s : shapes) {
					var dist = s.distance(p);
					if (dist > maxdist) {
						maxdist = dist;
					}
				}
				return maxdist;
			}

			@Override
			public Material material(Vec3 p) {
				var maxdist = -Float.MAX_VALUE;
				Shape maxDistShape = null;
				for (var s : shapes) {
					var dist = s.distance(p);
					if (dist > maxdist) {
						maxdist = dist;
						maxDistShape = s;
					}
				}
				return maxDistShape.material(p);
			}

		}
		return new MaxShape(shapes);
	}

	public static Shape subtract(Shape a, Shape b) {
		record SubShape(Shape a, Shape b) implements Shape {

			@Override
			public float distance(Vec3 p) {
				var da = a.distance(p);
				var db = b.distance(p);
				return (da > -db) ? da : -db;
			}

			@Override
			public Material material(Vec3 p) {
				var da = a.distance(p);
				var db = b.distance(p);
				return (da > -db) ? a.material(p) : b.material(p);
			}
			
		};
		return new SubShape(a, b);
	}

	public static Shape smoothUnion(Shape shape1, Shape shape2, float k) {
		record BlendedShape(Shape shape1, Shape shape2, float k4) implements Shape {

			@Override
			public float distance(Vec3 p) {
				var d1 = shape1.distance(p);
				var d2 = shape2.distance(p);
				var h = max(k4 - abs(d1 - d2), 0.0f);
				return min(d1, d2) - h * h * 0.25f / k4;
			}

			@Override
			public Material material(Vec3 p) {
				var d1 = shape1.distance(p);
				var d2 = shape2.distance(p);
				return shape1.material(p).blend(shape2.material(p), d1 / (d1 + d2));
			}
			
		}
		return new BlendedShape(shape1, shape2, k * 4.0f);
	}

	public static Shape smoothSubtract(Shape shape1, Shape shape2, float k) {
		record BlendedShape(Shape shape1, Shape shape2, float k4) implements Shape {

			@Override
			public float distance(Vec3 p) {
				var d1 = -shape1.distance(p);
				var d2 = shape2.distance(p);
				var h = max(k4 - abs(d1 - d2), 0.0f);
				return h * h * 0.25f / k4 - min(d1, d2);
			}

			@Override
			public Material material(Vec3 p) {
				var d1 = -shape1.distance(p);
				var d2 = shape2.distance(p);
				return shape1.material(p).blend(shape2.material(p), d1 / (d1 + d2));
			}
			
		}
		return new BlendedShape(shape1, shape2, k * 4.0f);
	}

}

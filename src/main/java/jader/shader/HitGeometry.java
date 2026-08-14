package jader.shader;

import static jader.math.Ray3.ray3;
import static jader.math.Ray3.ray3To;
import static jader.math.Vec3.vec3;
import static jader.shader.RayMarch.MIN_SURFACE_DIST;

import jader.math.Ray3;
import jader.math.Vec3;
import jader.shape.Shape;

/// Geometric information about the hit point on a surface.
value record HitGeometry(

		/// Point just over the hit point so that the surface does not interfere with ray marching
		Vec3 hover,

		/// Ray from the surface point in surface normal direction
		Ray3 normalRay,

		/// Ray from the hover point in direction of the reflection given the provided view direction
		Ray3 reflectionRay) {

	// Pre-calculated tetrahedron edges for normal calculation
	private static final float TSIZE = 0.00001f;
	private static final Vec3 T1 = vec3(TSIZE, -TSIZE, -TSIZE);
	private static final Vec3 T2 = vec3(-TSIZE, -TSIZE, TSIZE);
	private static final Vec3 T3 = vec3(-TSIZE, TSIZE, -TSIZE);
	private static final Vec3 T4 = vec3(TSIZE, TSIZE, TSIZE);

	public static HitGeometry calculate(Shape shape, Vec3 point, Vec3 viewDirection) {

		// Tetrahedron technique for smooth normals with 4 distance calculations
		// as described in https://iquilezles.org/articles/normalsSDF/
		var d1 = shape.distance(point.add(T1));
		var d2 = shape.distance(point.add(T2));
		var d3 = shape.distance(point.add(T3));
		var d4 = shape.distance(point.add(T4));
		var normal = vec3( //
				+d1 - d2 - d3 + d4, //
				-d1 - d2 + d3 + d4, //
				-d1 + d2 - d3 + d4).nor();

		var surfaceNormal = ray3(point, normal);
		var hover = surfaceNormal.pointAt(2 * MIN_SURFACE_DIST);
		var relectionRay = ray3(hover, viewDirection.mulSub(normal, viewDirection.dot(normal) * 2f));

		return new HitGeometry(hover, surfaceNormal, relectionRay);
	}

	public Ray3 rayTo(Vec3 target) {
		return ray3To(hover, target);
	}

}
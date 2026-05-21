package jader.shader;

import static jader.math.Vec3.vec3;
import static jader.shader.RayMarch.MIN_SURFACE_DIST;

import jader.math.Ray3;
import jader.math.Vec3;
import jader.shape.Shape;

/// Geometric information about the hit point on a surface.
record HitGeometry(
		
		/// Hit point on the surface 
		Vec3 point,
		
		/// Surface normal at the hit point
		Vec3 normal,
		
		/// Point just over the hit point so that the surface does not interfere with ray marching
		Vec3 hover,
		
		/// Direction of the reflection on the surface from the provided view direction
		Vec3 reflectionDirection) {

	// Pre-calculated tetrahedron edges for normal calculation
	private static final float TSIZE = 0.00001f;
	private static final Vec3 T1 = vec3(TSIZE, -TSIZE, -TSIZE);
	private static final Vec3 T2 = vec3(-TSIZE, -TSIZE, TSIZE);
	private static final Vec3 T3 = vec3(-TSIZE, TSIZE, -TSIZE);
	private static final Vec3 T4 = vec3(TSIZE, TSIZE, TSIZE);

	public static HitGeometry calculate(Shape shape, Vec3 point, Vec3 viewDirection) {

		// Tetrahedron technique for smooth normals with 4 distance calculations
		// as described in https://iquilezles.org/articles/normalsSDF/
		var d1 = shape.distance(point.add(T1)).length();
		var d2 = shape.distance(point.add(T2)).length();
		var d3 = shape.distance(point.add(T3)).length();
		var d4 = shape.distance(point.add(T4)).length();
		var normal = vec3( //
				+d1 - d2 - d3 + d4, //
				-d1 - d2 + d3 + d4, //
				-d1 + d2 - d3 + d4).nor();

		var hover = hover(point, normal, 2 * MIN_SURFACE_DIST);
		var reflectionDirection = viewDirection.mulSub(normal, viewDirection.dot(normal) * 2f);

		return new HitGeometry(point, normal, hover, reflectionDirection);
	}

	public Ray3 reflectionRay() {
		return Ray3.ray3(hover, reflectionDirection);
	}
	
	public Vec3 hover(float dist) {
		return hover(point, normal, dist);
	}
	
	private static Vec3 hover(Vec3 point, Vec3 normal, float dist) {
		return point.mulAdd(normal, dist);
	}

}
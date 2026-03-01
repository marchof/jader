package jader.shader;

import static jader.math.Vec3.vec3;

import jader.math.Vec3;
import jader.shape.Shape;

record SurfaceGeometry(Vec3 normal, Vec3 hover) {

	// Pre-calculated tetrahedron edges for normal calculation
	private static final float TSIZE = 0.00001f;
	private static final Vec3 T1 = vec3(TSIZE, -TSIZE, -TSIZE);
	private static final Vec3 T2 = vec3(-TSIZE, -TSIZE, TSIZE);
	private static final Vec3 T3 = vec3(-TSIZE, TSIZE, -TSIZE);
	private static final Vec3 T4 = vec3(TSIZE, TSIZE, TSIZE);

	public static SurfaceGeometry calculate(Shape shape, Vec3 point) {

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

		var hover = point.mulAdd(normal, 2 * RayMarch.MIN_SURFACE_DIST);

		return new SurfaceGeometry(normal, hover);
	}

}
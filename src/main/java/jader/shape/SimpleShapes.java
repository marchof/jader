package jader.shape;

import static jader.math.Vec2.vec2;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.acos;
import static java.lang.Math.atan2;
import static java.lang.Math.clamp;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;

import jader.math.Vec3;

public final class SimpleShapes {

	public static ShapeBuilder planeXZ(float y) {
		return ShapeBuilder.of(p -> p.y() - y, //
				p -> vec2(absmod(p.x(), 1f), absmod(p.z(), 1f)));
	}

	public static ShapeBuilder sphere(Vec3 center, float radius) {
		return ShapeBuilder.of(p -> center.dist(p) - radius, //
				p -> {
					var r = p.sub(center);
					return vec2((float) (atan2(r.x(), r.z()) / (2 * PI)) + 1f,
							(float) ((1 + acos(r.y()) / 2)));
				});
	}

	public static ShapeBuilder box(Vec3 center, Vec3 size, float radius) {
		return ShapeBuilder.of(p -> {
			var qx = abs(p.x() - center.x()) - size.x() + radius;
			var qy = abs(p.y() - center.y()) - size.y() + radius;
			var qz = abs(p.z() - center.z()) - size.z() + radius;
			var d1 = min(max(qx, max(qy, qz)), 0);
			qx = max(0, qx);
			qy = max(0, qy);
			qz = max(0, qz);
			var d2 = (float) sqrt(qx * qx + qy * qy + qz * qz);
			return d1 + d2 - radius;
		});
	}

	public static ShapeBuilder box(Vec3 center, Vec3 size) {
		return box(center, size, 0.0f);
	}

	public static ShapeBuilder cylinder(Vec3 center, float radius, float height) {
		return ShapeBuilder.of(p -> {
			var dx = p.xz().dist(center.xz()) - radius;
			var dy = abs(p.y() - center.y()) - height;
			return min(max(dx, dy), 0f) + vec2(dx, dy).clampMax(0f).len();
		});
	}

	public static ShapeBuilder cone(Vec3 center, float radius, float height) {
		return ShapeBuilder.of(p -> {
			var pp = vec2(p.xz().dist(center.xz()) - radius, p.y() - center.y() + height);
			var e = vec2(-radius, 2.0f * height);
			var q = pp.mulSub(e, clamp(pp.dot(e) / e.dot(e), 0.0f, 1.0f));
			var d = q.len();
			if (max(q.x(), q.y()) > 0.0) {
				return d;
			}
			return -min(d, pp.y());
		});
	}

	public static ShapeBuilder torus(Vec3 center, float r1, float r2) {
		return ShapeBuilder.of(p -> {
			var q = vec2(p.xz().dist(center.xz()) - r1, p.y() - center.y());
			return q.len() - r2;
		});
	}

	private static float absmod(float v, float m) {
		var r = v % m;
		if (r < 0) {
			r += m;
		}
		return r;
	}

}

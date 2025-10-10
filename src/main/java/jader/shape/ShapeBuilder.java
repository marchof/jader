package jader.shape;

import static jader.math.Vec2.vec2;

import jader.math.Vec2;
import jader.math.Vec3;
import jader.shape.Shape.Distance;

public record ShapeBuilder(SDF sdf, UVMapper mapper) {

	/**
	 * Signed distance function to describe the geometry of a shape.
	 */
	public interface SDF {
		float dist(Vec3 p);
	}

	/**
	 * Maps point on the 3D surface of a shape to 2-dimensional point (u, v) with u
	 * and v in the range [0, 1]. Used to apply {@link Surface} definitions on a
	 * specific shape.
	 */
	public interface UVMapper {
		Vec2 uv(Vec3 p);
	}

	public static ShapeBuilder of(SDF sdf) {
		return of(sdf, p -> vec2(0, 0));
	}

	public static ShapeBuilder of(SDF sdf, UVMapper mapper) {
		return new ShapeBuilder(sdf, mapper);
	}

	public Shape with(Material material) {
		return p -> new MaterialDistance(sdf.dist(p), material);
	}

	public Shape with(Surface surface) {
		return p -> new SurfaceDistance(sdf.dist(p), p, mapper, surface);
	}

	private static record MaterialDistance(float length, Material material) implements Shape.Distance {

		@Override
		public Distance negdist() {
			return new MaterialDistance(-length, material);
		}
	}

	private static record SurfaceDistance(float length, Vec3 p, UVMapper uvMapper, Surface surface)
			implements Shape.Distance {
		@Override
		public Material material() {
			return surface.material(uvMapper.uv(p));
		}

		@Override
		public Distance negdist() {
			return new SurfaceDistance(-length, p, uvMapper, surface);
		}
	}

}

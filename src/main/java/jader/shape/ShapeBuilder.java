package jader.shape;

import static jader.math.Vec2.vec2;

import jader.math.Vec2;
import jader.math.Vec3;
import jader.shape.Shape.Distance;

public record ShapeBuilder(SDF sdf, UVMapper mapper) {

	/// Signed distance function describing the geometry of a shape.
	public interface SDF {
		float dist(Vec3 p);
	}

	/// Maps a point on a shape's 3D surface to a 2D point (u, v), with both
	/// coordinates in the range [0, 1]. This is used to apply {@link Surface}
	/// definitions to a specific shape.
	public interface UVMapper {
		Vec2 uv(Vec3 p);
	}

	public static ShapeBuilder of(SDF sdf) {
		return of(sdf, _ -> vec2(0, 0));
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
		public Distance scaledDistance(float f) {
			return new MaterialDistance(length * f, material);
		}
	}

	private static record SurfaceDistance(float length, Vec3 p, UVMapper uvMapper, Surface surface)
			implements Shape.Distance {
		@Override
		public Material material() {
			return surface.material(uvMapper.uv(p));
		}

		@Override
		public Distance scaledDistance(float f) {
			return new SurfaceDistance(length * f, p, uvMapper, surface);
		}
	}

}

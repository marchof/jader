package jader.shape;

import static jader.math.Vec2.vec2;

import jader.math.Vec2;
import jader.math.Vec3;

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
		record UniformMaterialShape(SDF sdf, Material material) implements Shape {

			@Override
			public float distance(Vec3 p) {
				return sdf.dist(p);
			}

			@Override
			public Material material(Vec3 p) {
				return material;
			}

		}
		return new UniformMaterialShape(sdf, material);
	}

	public Shape with(Surface surface) {
		record SurfaceMaterialShape(SDF sdf, UVMapper mapper, Surface surface) implements Shape {

			@Override
			public float distance(Vec3 p) {
				return sdf.dist(p);
			}

			@Override
			public Material material(Vec3 p) {
				return surface.material(mapper.uv(p));
			}

		}
		return new SurfaceMaterialShape(sdf, mapper, surface);
	}

}

package jader.shape;

import jader.math.Trans3;
import jader.math.Vec3;

/// A shape is defined by a signed distance function (SDF).
public interface Shape {

	/// Returns the closest distance to the shape from the given point.
	float distance(Vec3 p);
	
	/// Returns the material at the given point.
	Material material(Vec3 p);

	/// Applies the given transformations to the shape
	default Shape transform(Trans3... transformations) {
		record TransformedShape(Shape original, Trans3 inv, float scale) implements Shape {

			@Override
			public float distance(Vec3 p) {
				return original.distance(inv.apply(p)) * scale;
			}

			@Override
			public Material material(Vec3 p) {
				return original.material(inv.apply(p));
			}
			
		}
		var t = Trans3.of(transformations);
		return new TransformedShape(this, t.inverse(), t.getScale());
	}
	

}

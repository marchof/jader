package jader.shape;

import jader.math.Trans3;
import jader.math.Vec3;

/// A shape is defined by a signed distance function (SDF).
public interface Shape {

	/// A distance describes the shortest distance to the shape and the material at
	/// the closest point on the surface.
	interface Distance {

		/// The length to the closest point.
		float length();

		/// The material at the closest point. This operation might be expensive
		/// and should only be called the the material is actually needed.
		Material material();

		/// Returns a copy of the distance with the given length.
		Distance withLength(float l);

	}

	/// Returns the closest distance to the shape from the given point.
	Distance distance(Vec3 p);

	/// Applies the given transformations to the shape
	default Shape transform(Trans3... transformations) {
		var t = Trans3.of(transformations);
		var inv = t.inverse();
		var scale = t.getScale();
		if (Math.abs(1.0f - scale) < 0.0001) {
			return p -> this.distance(inv.apply(p));
		}
		return p -> {
			var d = this.distance(inv.apply(p));
			return d.withLength(d.length() * scale);
		};
	}

}

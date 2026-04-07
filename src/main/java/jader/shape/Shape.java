package jader.shape;

import jader.math.Trans3;
import jader.math.Vec3;

/// A shape is defined by a signed distance function (SDF).
public interface Shape {

	/// A distance describes the shortest distance to the shape and the material at
	/// the closest point on the surface.
	interface Distance {

		float length();

		Material material();

		Distance scaledDistance(float f);

	}

	/// Returns the closest distance to the shape from the given point.
	Distance distance(Vec3 p);

	default Shape transform(Trans3... transformations) {
		var t = Trans3.of(transformations);
		var inv = t.inverse();
		var scale = t.getScale();
		return p -> this.distance(inv.apply(p)).scaledDistance(scale);
	}

}

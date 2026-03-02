package jader.shape;

import jader.math.Trans3;
import jader.math.Vec3;

/// A shape is defined by a Signed Distance Function (SDF).
public interface Shape {

	/// A distance describes the shortest distance to a shape and the material at
	/// that closest point of the shape.
	interface Distance {

		float length();

		Material material();

		default Distance scaleddist(float f) {
			var delegate = this;
			var dist = delegate.length() * f;
			return new Distance() {

				public Material material() {
					return delegate.material();
				}

				@Override
				public float length() {
					return dist;
				}
			};
		}

	}

	/// Returns the closest distance to the shape from the given point.
	Distance distance(Vec3 p);

	default Shape transform(Trans3... transformations) {
		var t = Trans3.of(transformations);
		var inv = t.inverse();
		var scale = t.getScale();
		return p -> this.distance(inv.apply(p)).scaleddist(scale);
	}

}

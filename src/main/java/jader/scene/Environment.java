package jader.scene;

import jader.shape.Color;

/// Abstraction of the environment like background color or fog.
public interface Environment {

	/// Calculates the final color considering the environment. 
	/// 
	/// @param object color of the object hit or {@code null} if no object was hit
	/// @param distance distance to the object
	/// @return final color
	///	 
	public Color getColor(Color object, float distance);

	public static Environment background(Color color) {
		return (object, _) -> object == null ? color : object;
	}

	public static Environment fog(Color color, float mindist, float maxdist) {
		var span = maxdist - mindist;
		return (object, distance) -> {
			if (object == null || distance >= maxdist) {
				return color;
			}
			if (distance <= mindist) {
				return object;
			}
			return object.blend(color, (distance - mindist) / span);
		};
	}

	
}

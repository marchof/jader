package jader.scene;

import java.util.List;

import jader.shape.Color;
import jader.shape.Shape;

public record Scene(

		Shape shape, //
		List<Light> lights, //
		Camera camera, //
		Color background

) {
}

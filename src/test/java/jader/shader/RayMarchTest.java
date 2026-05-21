package jader.shader;

import static jader.math.Ray3.ray3;
import static jader.math.Vec3.vec3;
import static jader.shape.SimpleShapes.sphere;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import jader.shader.RayMarch.Hit;
import jader.shape.Material;

public class RayMarchTest {

	@Test
	void should_hit_sphere() {
		should_hit_sphere(0.0);
		should_hit_sphere(0.1 * PI);
		should_hit_sphere(0.2 * PI);
		should_hit_sphere(0.3 * PI);
		should_hit_sphere(0.4 * PI);
		should_hit_sphere(0.49 * PI);
		should_hit_sphere(0.5 * PI);
	}

	void should_hit_sphere(double alpha) {
		
		var sphere = sphere(vec3(0, 0, 0), 1).with(Material.DEFAULT);

		var x = cos(alpha);
		var y = sin(alpha);
		var ray = ray3(vec3(2, y, 0), vec3(-1, 0, 0));

		var rm = RayMarch.from(ray, sphere, 10f);

		if (rm instanceof Hit hit) {
			var expectedHit = vec3(x, y, 0);
			var surfaceDistance = hit.point().dist(expectedHit);
			assertTrue(surfaceDistance < 0.005, "Actual Distance: " + surfaceDistance);
			var expectedNormal = vec3(x, y, 0);

			var details = HitGeometry.calculate(sphere, hit.point(), ray.direction());
			var normalDeviation = details.normal().dot(expectedNormal);
			assertTrue(normalDeviation > 0.99999, "Actual Deviation: " + normalDeviation);
		} else {
			fail("Hit expected");
		}
	}

}

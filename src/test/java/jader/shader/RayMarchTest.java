package jader.shader;

import static jader.math.Vec3.vec3;
import static jader.shape.SimpleShapes.sphere;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

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
		
		var hit = RayMarch.from(vec3(2, y, 0), vec3(-1, 0, 0), sphere, 10f);

		assertTrue(hit.isHit());
		var expectedHit = vec3(x, y, 0);
		var surfaceDistance = hit.hitPoint().dist(expectedHit);
		assertTrue(surfaceDistance < 0.005, "Actual Distance: " + surfaceDistance);
		var expectedNormal = vec3(x, y, 0);
		
		var details = SurfaceGeometry.calculate(sphere, hit.hitPoint());
		var normalDeviation = details.normal().dot(expectedNormal);
		assertTrue(normalDeviation > 0.99999, "Actual Deviation: " + normalDeviation);
	}

}

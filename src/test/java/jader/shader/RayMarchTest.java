package jader.shader;

import static jader.math.Vec3.vec3;
import static jader.shape.SimpleShapes.sphere;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import jader.shape.Material;

public class RayMarchTest {

	@Test
	void should_hit_sphere() {
		should_hit_sphere(0.0);
		should_hit_sphere(0.1 * Math.PI);
		should_hit_sphere(0.2 * Math.PI);
		should_hit_sphere(0.3 * Math.PI);
		should_hit_sphere(0.4 * Math.PI);
		should_hit_sphere(0.49 * Math.PI);
		should_hit_sphere(0.5 * Math.PI);
	}
	
	void should_hit_sphere(double alpha) {
		var sphere = sphere(vec3(0, 0, 0), 1).with(Material.DEFAULT);

		var x = Math.cos(alpha);
		var y = Math.sin(alpha);
		
		var hit = RayMarch.from(vec3(2, y, 0), vec3(-1, 0, 0), sphere, 10f);

		assertTrue(hit.isHit());
		var expectedHit = vec3(x, y, 0);
		var surfaceDistance = hit.hitPoint().dist(expectedHit);
		assertTrue(surfaceDistance < 0.005, "Actual Distance: " + surfaceDistance);
		var expectedNormal = vec3(x, y, 0);
		var normalDeviation = hit.hitNormal().dot(expectedNormal);
		assertTrue(normalDeviation > 0.99999, "Actual Deviation: " + normalDeviation);

	}

}

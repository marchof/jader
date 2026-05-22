package jader.shader;

import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.pow;

import jader.math.Ray3;
import jader.math.Vec3;
import jader.scene.Light;
import jader.scene.Scene;
import jader.shader.RayMarch.Hit;
import jader.shader.RayMarch.Miss;
import jader.shape.Color;
import jader.shape.Material;

public class Shader {

	private static final float MAX_MARCH_DIST = 20f;
	private static final int MAX_REFLECTION_COUNT = 16;

	private static final int AO_STEPS = 5;
	private static final float AO_DECAY = 0.9f;

	private final Scene scene;

	public Shader(Scene scene) {
		this.scene = scene;
	}

	public Color getColor(float x, float y, float width, float height) {
		return getColor(scene.camera().project(x, y, width, height));
	}

	public Color getColor(Ray3 ray) {
		return getColor(ray, 0);
	}

	private Color getColor(Ray3 ray, int reflectionCount) {
		var rm = RayMarch.from(ray, scene.shape(), MAX_MARCH_DIST);
		var env = scene.environment();
		return switch (rm) {
		case Hit hit ->
			env.getColor(getColor(ray.direction(), hit.point(), hit.material(), reflectionCount), hit.distance());
		case Miss _ -> env.getColor(null, 0);
		};
	}

	private Color getColor(Vec3 viewDirection, Vec3 hitPoint, Material material, int reflectionCount) {

		var hitGeometry = HitGeometry.calculate(scene.shape(), hitPoint, viewDirection);

		var color = material.emissiveColor();

		// Lights:
		for (var light : scene.lights()) {
			color = switch (light) {
			case Light.Ambient ambient when ambient.hasAO() -> //
				applyAmbientOcclusionLight(ambient, material, hitGeometry, color);
			case Light.Ambient ambient -> //
				applySimpleAmbientLight(ambient, material, color);
			case Light.Point point -> //
				applyPointLight(point, material, hitGeometry, color);
			};
		}

		// Reflections from the scene:
		if (material.isReflective() && reflectionCount < MAX_REFLECTION_COUNT) {
			var reflected = getColor(hitGeometry.reflectionRay(), reflectionCount + 1);
			color = color.mulAdd(material.reflectiveColor(), reflected);
		}

		return color;
	}

	private Color applyAmbientOcclusionLight(Light.Ambient ambient, Material material, HitGeometry hitGeometry,
			Color color) {
		var ao = getAmbientOcclusion(ambient, hitGeometry.normalRay());
		return color.mulAdd(material.diffuseColor(), ambient.color(), ao);
	}

	private Color applySimpleAmbientLight(Light.Ambient ambient, Material material, Color color) {
		return color.mulAdd(material.diffuseColor(), ambient.color());
	}

	private Color applyPointLight(Light.Point point, Material material, HitGeometry hitGeometry, Color color) {
		var lightRay = hitGeometry.rayTo(point.position());
		var lightDistance = lightRay.start().dist(point.position());
		var rm = RayMarch.from(lightRay, scene.shape(), lightDistance);
		if (rm instanceof Miss miss) {
			var blur = min(1.0f, miss.distanceRatio() / (point.radius() / lightDistance));
			
			// Diffuse light reflection:
			var diffuseFactor = hitGeometry.normalRay().direction().dot(lightRay.direction());
			color = color.mulAdd(material.diffuseColor(), point.color(), diffuseFactor * blur);
			
			// Specular light reflection:
			if (material.specularColor().isNonBlack()) {
				var specularFactor = (float) pow(abs(hitGeometry.reflectionRay().direction().dot(lightRay.direction())),
						material.shininess());
				color = color.mulAdd(material.specularColor(), point.color(), specularFactor * blur);
			}
		}
		return color;
	}

	private float getAmbientOcclusion(Light.Ambient ambient, Ray3 surfaceNormal) {
		var stepsize = ambient.aoRange() / AO_STEPS;
		var occ = 0.0f;
		var total = 0.0f;
		var f = 1.0f;
		for (var i = 1; i <= AO_STEPS; ++i) {
			var dist = stepsize * i;
			total += dist * f;
			occ += (dist - scene.shape().distance(surfaceNormal.pointAt(dist)).length()) * f;
			f *= AO_DECAY;
		}
		return 1.0f - ambient.ao() * occ / total;
	}

}

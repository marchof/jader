package jader.shader;

import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.pow;

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
		var direction = screenToDirection(x, y, width, height);
		var position = scene.camera().position();
		return getColor(position, direction);
	}

	public Color getColor(Vec3 start, Vec3 direction) {
		return getColor(start, direction, 0);
	}

	private Color getColor(Vec3 start, Vec3 viewDirection, int reflectionCount) {
		var rm = RayMarch.from(start, viewDirection, scene.shape(), MAX_MARCH_DIST);
		return switch (rm) {
		case Hit hit -> getColor(viewDirection, hit.point(), hit.distance().material(), reflectionCount);
		case Miss _ -> scene.background();
		};
	}

	private Color getColor(Vec3 viewDirection, Vec3 hitPoint, Material material, int reflectionCount) {

		var hitGeometry = SurfaceGeometry.calculate(scene.shape(), hitPoint);
		var hitNormal = hitGeometry.normal();
		var hitHoverPoint = hitGeometry.hover();

		var reflectionDirection = getReflection(viewDirection, hitNormal);

		var color = material.emissiveColor();

		// Lights:
		for (var light : scene.lights()) {
			color = switch (light) {
			case Light.Ambient ambient when ambient.hasAO() -> //
				applyAmbientOcclusionLight(ambient, material, color, hitPoint, hitNormal);
			case Light.Ambient ambient -> //
				applySimpleAmbientLight(ambient, material, color);
			case Light.Point point -> //
				applyPointLight(point, material, hitPoint, hitHoverPoint, hitNormal, reflectionDirection, color);
			};
		}

		// Reflections from the scene:
		if (material.isReflective() && reflectionCount < MAX_REFLECTION_COUNT) {
			var reflected = getColor(hitHoverPoint, reflectionDirection, reflectionCount + 1);
			color = color.mulAdd(material.reflectiveColor(), reflected);
		}

		return color;
	}

	private Color applyAmbientOcclusionLight(Light.Ambient ambient, Material material, Color color, Vec3 hitPoint,
			Vec3 hitNormal) {
		float ao = getAmbientOcclusion(ambient, hitPoint, hitNormal);
		return color.mulAdd(material.diffuseColor(), ambient.color(), ao);
	}

	private Color applySimpleAmbientLight(Light.Ambient ambient, Material material, Color color) {
		return color.mulAdd(material.diffuseColor(), ambient.color());
	}

	private Color applyPointLight(Light.Point point, Material material, Vec3 hitPoint, Vec3 hitHoverPoint,
			Vec3 hitNormal, Vec3 reflectionDirection, Color color) {
		var ln = point.position().direction(hitPoint);
		var ld = hitHoverPoint.dist(point.position());
		var rm = RayMarch.from(hitHoverPoint, ln, scene.shape(), ld);
		if (rm instanceof Miss miss) {
			var blur = min(1.0f, miss.distanceRatio() / (point.radius() / ld));
			var diffuseFactor = hitNormal.dot(ln);
			color = color.mulAdd(material.diffuseColor(), point.color(), diffuseFactor * blur);
			if (material.specularColor().isNonBlack()) {
				var specularFactor = (float) pow(abs(reflectionDirection.dot(ln)), material.shininess());
				color = color.mulAdd(material.specularColor(), point.color(), specularFactor * blur);
			}
		}
		return color;
	}

	private Vec3 getReflection(Vec3 viewDirection, Vec3 surfaceNormal) {
		return viewDirection.mulSub(surfaceNormal, viewDirection.dot(surfaceNormal) * 2f);
	}

	private float getAmbientOcclusion(Light.Ambient ambient, Vec3 hitPoint, Vec3 hitNormal) {
		float stepsize = ambient.aoRange() / AO_STEPS;
		float occ = 0.0f;
		float total = 0.0f;
		float f = 1.0f;
		for (int i = 1; i <= AO_STEPS; ++i) {
			float dist = stepsize * i;
			total += dist * f;
			occ += (dist - scene.shape().distance(hitPoint.mulAdd(hitNormal, dist)).length()) * f;
			f *= AO_DECAY;
		}
		return 1.0f - ambient.ao() * occ / total;
	}

	private Vec3 screenToDirection(float x, float y, float width, float height) {
		var cam = scene.camera();
		var ratio = width / height;
		var ux = (x / width - 0.5f) * ratio / cam.focalLength();
		var uy = (-y / height + 0.5f) / cam.focalLength();
		return cam.direction().mulAdd(cam.right(), ux).mulAdd(cam.up(), uy).nor();
	}

}

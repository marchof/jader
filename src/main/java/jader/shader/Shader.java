package jader.shader;

import jader.math.Vec3;
import jader.scene.Light;
import jader.scene.Scene;
import jader.shape.Color;
import jader.shape.Shape;

public class Shader {

	private static final float MAX_MARCH_DIST = 20f;
	private static final int MAX_REFELECTION_COUNT = 16;

	private static final int AO_STEPS = 5;
	private static final float OA_DECAY = 0.9f;

	private Scene scene;

	public Shader(Scene scene) {
		this.scene = scene;
	}

	public Color getColor(float x, float y, float width, float height) {
		var direction = screenToDirection(x, y, width, height);
		var position = scene.camera().position();
		return getColor(position, direction);
	}

	public Color getColor(Vec3 start, Vec3 direction) {
		var rm = RayMarch.from(start, direction, scene.shape(), MAX_MARCH_DIST);
		if (rm.isHit()) {
			return getColor(direction, rm, 0);
		} else {
			return scene.background();
		}
	}

	private Color getColor(Vec3 viewDirection, RayMarch hit, int reflectionCount) {

		var color = Color.BLACK;
		var material = hit.material();
		var hitPoint = hit.hitPoint();
		var hitNormal = hit.hitNormal();
		var hoverHitPoint = RayMarch.hoverHitPoint(hitPoint, hitNormal);
		var reflectionDirection = getReflection(viewDirection, hitNormal);

		// Lights:
		for (var light : scene.lights()) {
			switch (light) {
			case Light.Ambient ambient:
				if (ambient.hasAO()) {
					float ao = getAmbientOcclusion(ambient, scene.shape(), hitPoint, hitNormal);
					color = color.mulAdd(material.diffuseColor(), ambient.color(), ao);
				} else {
					color = color.mulAdd(material.diffuseColor(), ambient.color());
				}
				break;
			case Light.Point point:
				var ln = point.position().direction(hitPoint);
				var ld = hoverHitPoint.dist(point.position());
				var rm = RayMarch.from(hoverHitPoint, ln, scene.shape(), ld);
				if (!rm.isHit()) {
					var blur = Math.min(1.0f, rm.distanceRatio() / (point.radius() / ld));
					var diffuseFactor = hitNormal.dot(ln);
					color = color.mulAdd(material.diffuseColor(), point.color(), diffuseFactor * blur);
					if (material.specularColor().isNonBlack()) {
						var specularFactor = (float) Math.pow(Math.abs(reflectionDirection.dot(ln)), material.shinyness());
						color = color.mulAdd(material.specularColor(), point.color(), specularFactor * blur);
					}
				}
				break;
			}
		}

		// Reflections from the scene:
		if (material.isReflective()) {
			var rm = RayMarch.from(hoverHitPoint, reflectionDirection, scene.shape(), MAX_MARCH_DIST);
			var reflected = Color.BLACK;
			if (rm.isHit()) {
				if (reflectionCount < MAX_REFELECTION_COUNT) {
					reflected = getColor(reflectionDirection, rm, reflectionCount + 1);
				}
			} else {
				reflected = scene.background();
			}
			color = color.mulAdd(material.reflectiveColor(), reflected);
		}

		return color;
	}

	private Vec3 getReflection(Vec3 viewDirection, Vec3 surfaceNormal) {
		return viewDirection.mulSub(surfaceNormal, viewDirection.dot(surfaceNormal) * 2f);
	}

	private float getAmbientOcclusion(Light.Ambient ambient, Shape shape, Vec3 hitPoint, Vec3 hitNormal) {
		float stepsize = ambient.aoRange() / AO_STEPS;
		float occ = 0.0f;
		float total = 0.0f;
		float f = 1.0f;
		for (int i = 1; i <= AO_STEPS; ++i) {
			float dist = stepsize * i;
			total += dist * f;
			occ += (dist - shape.distance(hitPoint.mulAdd(hitNormal, dist)).length()) * f;
			f *= OA_DECAY;
		}
		return 1.0f - ambient.oa() * occ / total;
	}

	private Vec3 screenToDirection(float x, float y, float width, float height) {
		var cam = scene.camera();
		var ratio = width / height;
		var ux = (x / width - 0.5f) * ratio / cam.f();
		var uy = (-y / height + 0.5f) / cam.f();
		return cam.direction().mulAdd(cam.right(), ux).mulAdd(cam.up(), uy).nor();
	}

}

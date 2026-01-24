package jader.ui;

import static jader.math.Vec3.vec3;
import static jader.scene.Light.ambient;
import static jader.scene.Light.point;
import static jader.shape.Color.BLACK;
import static jader.shape.Color.rgb;
import static jader.shape.CombinedShapes.intersect;
import static jader.shape.CombinedShapes.smoothSubtract;
import static jader.shape.CombinedShapes.smoothUnion;
import static jader.shape.CombinedShapes.subtract;
import static jader.shape.CombinedShapes.union;
import static jader.shape.Material.diffuse;
import static jader.shape.Material.glossy;
import static jader.shape.SimpleShapes.box;
import static jader.shape.SimpleShapes.sphere;
import static jader.shape.Surface.grid;

import java.util.List;

import jader.scene.Camera;
import jader.scene.Scene;
import jader.shape.Color;
import jader.shape.Material;
import jader.shape.Shape;
import jader.shape.SimpleShapes;
import jader.shape.Surface;

public class ExampleScenes {

	public static Scene scene1() {
		return standardSetting( //
				sphere(vec3(-0.3, -0.2, 0.6), 0.3f) //
						.with(grid(0.05f, diffuse(rgb(200, 0, 0)), glossy(rgb(33, 66, 240), 0.05f, 0.5f)) //
								.scale(32f, 32f)), //
				sphere(vec3(0.3, 0.2, -0.4), 0.7f) //
						.with(glossy(rgb(10, 10, 10), 0.8f, 0.8f)), //
				box(vec3(0.4, -0.4, 0.4), vec3(0.3, 0.1, 0.3), 0.04f).with(Material.COOPER));
	}

	public static Scene scene2() {
		return standardSetting( //
				box(vec3(-0.6, -0.15, -0.5), vec3(0.35, 0.35, 0.35), 0.05f).with(Material.DEFAULT), //
				box(vec3(0.55, -0.35, 0.2), vec3(0.15, 0.15, 0.15), 0.05f).with(Material.DEFAULT));
	}

	public static Scene scene3() {
		return standardSetting( //
				sphere(vec3(-0.3, -0.2, 0.4), 0.2f).with(diffuse(rgb(0, 0, 192))), //
				subtract( //
						box(vec3(0.0, -0.35, -0.1), vec3(0.80, 0.15, 0.50), 0.05f).with(diffuse(rgb(160, 0, 0))), //
						sphere(vec3(0.3, -0.2, 0.4), 0.2f).with(diffuse(rgb(0, 0, 192)))));
	}

	public static Scene scene4() {
		return standardSetting( //
				intersect( //
						sphere(vec3(-1.0, -0.2, -0.3), 0.4f).with(Material.GOLD), //
						box(vec3(-1.0, -0.2, -0.3), vec3(0.3, 0.3, 0.3)).with(diffuse(rgb(192, 64, 64)))), //
				union( //
						sphere(vec3(0.8, -0.1, -0.6), 0.4f).with(Material.GOLD), //
						box(vec3(0.8, -0.1, -0.6), vec3(0.3, 0.3, 0.3)).with(diffuse(rgb(192, 64, 64)))), //
				subtract( //
						sphere(vec3(-0.2, 0.0, -1), 0.5f).with(Material.GOLD), //
						box(vec3(-0.2, 0.0, -1), vec3(0.4, 0.4, 0.4)).with(diffuse(rgb(192, 64, 64)))));
	}

	public static Scene scene5() {
		return standardSetting( //
				smoothUnion( //
						box(vec3(-0.45, -0.4, 0.4), vec3(0.3, 0.1, 0.3), 0.05f).with(diffuse(rgb(200, 0, 0))),
						sphere(vec3(-0.45, -0.35, 0.4), 0.2f).with(diffuse(rgb(0, 160, 0))), 0.02f), //
				smoothSubtract( //
						box(vec3(0.45, -0.4, 0.4), vec3(0.3, 0.1, 0.3), 0.05f).with(diffuse(rgb(200, 0, 0))),
						sphere(vec3(0.45, -0.3, 0.4), 0.2f).with(diffuse(rgb(0, 160, 0))), 0.02f));
	}

	public static Scene scene6() {
		return standardSetting( //
				smoothUnion( //
						smoothUnion( //
								sphere(vec3(-0.5, -0.2, 0.6), 0.3f) //
										.with(grid(0.08f, diffuse(Color.WHITE), diffuse(rgb(240, 66, 33))) //
												.scale(32f, 32f)), //
								sphere(vec3(0, -0.2, 0.6), 0.25f) //
										.with(Material.GOLD),
								0.04f), //
						sphere(vec3(0.5, -0.2, 0.6), 0.3f) //
								.with(grid(0.08f, diffuse(Color.WHITE), diffuse(rgb(33, 66, 240))) //
										.scale(32f, 32f)), //
						0.04f));
	}

	public static Scene scene7() {
		return new Scene( //
				union(SimpleShapes.planeXZ(-0.5f).with(Material.DEFAULT), //
						box(vec3(-0.9, -0.2, 0.2), vec3(0.1, 0.3, 0.1)).with(Material.DEFAULT), //
						box(vec3(-0.5, -0.1, 0.0), vec3(0.1, 0.4, 0.1)).with(Material.DEFAULT), //
						box(vec3(-0.1, 0.0, -0.2), vec3(0.1, 0.5, 0.1)).with(Material.DEFAULT), //
						subtract( //
								box(vec3(0.5, -0.2, 0.5), vec3(0.3, 0.3, 0.1)).with(Material.DEFAULT),
								box(vec3(0.5, -0.2, 0.5), vec3(0.2, 0.2, 0.2)).with(Material.DEFAULT))), //
				List.of( //
						ambient(0.6f).withAO(0.7f, 0.15f), //
						point(vec3(-5, 5, -4), rgb(200, 200, 200), 1.0f), //
						point(vec3(5, 5, 2), rgb(128, 128, 128), 1.0f)), //
				Camera.direction(vec3(0, 0, 2), vec3(0, 0, -1)), //
				Color.WHITE);
	}

	private static Scene standardSetting(Shape... shapes) {
		return new Scene( //
				union(concat(SimpleShapes.planeXZ(-0.5f)
						.with(Surface.checker(glossy(BLACK, 0.3f, 0.3f), glossy(rgb(210, 210, 210), 0.3f, 0.3f))
								.move(0.75f, 0.0f)),
						shapes)), //
				List.of( //
						ambient(rgb(64, 64, 64)), //
						point(vec3(-5, 5, 2), 0.6f), //
						point(vec3(5, 5, 2), 0.6f)), //
				Camera.direction(vec3(0, 0, 2), vec3(0, 0, -1)), //
				Color.WHITE);
	}

	private static Shape[] concat(Shape shape1, Shape... moreshapes) {
		var shapes = new Shape[moreshapes.length + 1];
		shapes[0] = shape1;
		System.arraycopy(moreshapes, 0, shapes, 1, moreshapes.length);
		return shapes;
	}

}

package jader.ui;

import static jader.math.Vec3.vec3;
import static jader.scene.Light.ambient;
import static jader.scene.Light.point;
import static jader.shape.Color.rgb;
import static jader.shape.CombinedShapes.union;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.imageio.ImageIO;

import jader.scene.Camera;
import jader.scene.Scene;
import jader.shader.Rasterer;
import jader.shape.Color;
import jader.shape.Material;
import jader.shape.SimpleShapes;
import jader.shape.Surface;

public class MaterialGallery {

	private static final int WIDTH = 320;
	private static final int HEIGHT = 320;
	private static final int OVERSAMPLING = 4;
	
	private static final Path EXPORT_FOLDER = Path.of("target/export/materials");

	private static Scene getSzene(Material material) {
		return new Scene( //
				union( //
						SimpleShapes.planeXZ(-0.3f).with(Surface.grid(0.05f, Material.diffuse(Color.BLACK), Material.diffuse(Color.rgb(96, 96, 96))).scale(5, 5)), //
						SimpleShapes.sphere(vec3(0, 0, 0.3), 0.3f).with(material), //
						SimpleShapes.box(vec3(0, -0.25, 0.3), vec3(0.3, 0.05, 0.3), 0.03f).with(material)), //
				List.of( //
						ambient(rgb(64, 64, 64)), //
						point(vec3(-2, 5, 0), 1.0f), //
						point(vec3(5, 5, -2), 1.0f)), //
				Camera.direction(vec3(0, 0, -0.75), vec3(0, 0, 1)), //
				Color.BLACK);
	}

	private static BufferedImage render(Material material) {
		var rasterer = new Rasterer(OVERSAMPLING, true);
		return rasterer.render(getSzene(material), WIDTH, HEIGHT);
	}

	private static void exportSzene(Material material, String file) throws IOException {
		ImageIO.write(render(material), "png", EXPORT_FOLDER.resolve(file).toFile());
	}

	public static void main(String[] args) throws IOException {
		Files.createDirectories(EXPORT_FOLDER);
		exportSzene(Material.ALUMINIUM, "material-aluminium.png");
		exportSzene(Material.BRASS, "material-brass.png");
		exportSzene(Material.COOPER, "material-cooper.png");
		exportSzene(Material.GOLD, "material-gold.png");
		exportSzene(Material.SILVER, "material-silver.png");
	}

}

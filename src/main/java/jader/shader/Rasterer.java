package jader.shader;

import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

import jader.scene.Scene;
import jader.shape.Color;

/// Fills a raster image with the rendered scene.
public class Rasterer {

	private final int oversampling;
	private final boolean multithreaded;

	public Rasterer(int oversampling, boolean multithreaded) {
		this.oversampling = oversampling;
		this.multithreaded = multithreaded;
	}

	public Rasterer() {
		this(1, true);
	}

	public BufferedImage render(Scene scene, int width, int height) {
		var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		render(scene, image);
		return image;
	}

	public void render(Scene scene, BufferedImage image) {
		var shader = new Shader(scene);
		if (multithreaded) {
			renderMultiThreaded(shader, image);
		} else {
			renderSingleThreaded(shader, image);
		}
	}

	private void renderSingleThreaded(Shader shader, BufferedImage image) {
		for (int y = 0; y < image.getHeight(); y++) {
			renderRow(shader, y, image);
		}
	}

	private void renderMultiThreaded(Shader shader, BufferedImage image) {
		IntStream.range(0, image.getHeight()).parallel().forEach(y -> {
			renderRow(shader, y, image);
		});
	}

	private void renderRow(Shader shader, int y, BufferedImage image) {
		var width = image.getWidth();
		var height = image.getHeight();
		for (int x = 0; x < width; x++) {
			var c = oversampling == 1 ? shader.getColor(x, y, width, height)
					: getOversampledColor(shader, x, y, width, height);
			image.setRGB(x, y, c.toRGB());
		}
	}

	private Color getOversampledColor(Shader shader, int x, int y, int width, int height) {
		float r = 0f, g = 0f, b = 0f;
		for (int dy = 0; dy < oversampling; dy++) {
			for (int dx = 0; dx < oversampling; dx++) {
				var c = shader.getColor(x * oversampling + dx, y * oversampling + dy, width * oversampling,
						height * oversampling);
				r += c.red();
				g += c.green();
				b += c.blue();
			}
		}
		var samplecount = oversampling * oversampling;
		return Color.color(r / samplecount, g / samplecount, b / samplecount);
	}

}

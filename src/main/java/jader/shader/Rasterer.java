package jader.shader;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
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
		render(scene, image.getRaster());
		return image;
	}
	
	public void render(Scene scene, WritableRaster raster) {
		var shader = new Shader(scene);
		var width = raster.getWidth();
		var height = raster.getHeight();
		if (multithreaded) {
			renderMultiThreaded(shader, raster, width, height);
		} else {
			renderSingleThreaded(shader, raster, width, height);
		}
	}

	private void renderSingleThreaded(Shader shader, WritableRaster raster, int width, int height) {
		var buffer = new int[3];
		for (int y = 0; y < height; y++) {
			renderRow(shader, y, width, height, buffer, raster);
		}
	}

	private void renderMultiThreaded(Shader shader, WritableRaster raster, int width, int height) {
		IntStream.range(0, height).parallel().forEach(y -> {
			renderRow(shader, y, width, height, new int[3], raster);
		});
	}
	
	private void renderRow(Shader shader, int y, int width, int height, int[] buffer, WritableRaster raster) {
		for (int x = 0; x < width; x++) {
			var c = oversampling == 1 ? shader.getColor(x, y, width, height)
					: getOversampledColor(shader, x, y, width, height);
			c.fillRGB(buffer);
			raster.setPixel(x, y, buffer);
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

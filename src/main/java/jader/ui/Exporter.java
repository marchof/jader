package jader.ui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import jader.scene.Scene;
import jader.shader.Rasterer;

public class Exporter {
	
	private final Scene scene;
	private final int oversampling;

	public Exporter(Scene scene, int oversampling) {
		this.scene = scene;
		this.oversampling = oversampling;
	}
	
	private BufferedImage render(int width, int height) {
		var rasterer = new Rasterer(oversampling, true);
		return rasterer.render(scene, width, height);
	}
	
	public void exportPNG(int width, int height, Path file) throws IOException {
		ImageIO.write(render(width, height), "png", file.toFile());
	}
	
	public static void main(String[] args) throws IOException {
		new Exporter(ExampleScenes.scene1(), 4).exportPNG(1280, 960, Path.of("export/scene1.png"));
	}

}

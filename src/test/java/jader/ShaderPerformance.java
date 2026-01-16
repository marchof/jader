package jader;

import java.awt.image.BufferedImage;

import jader.scene.Scene;
import jader.shader.Rasterer;
import jader.ui.ExampleScenes;

public class ShaderPerformance {

	static final Scene SCENE = ExampleScenes.scene1();

	static final int WIDTH = 800;
	static final int HEIGHT = 600;

	static final int RUNS = 5;

	public static void main(String... args) throws Exception {
		var raster = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB).getRaster();
		var rasterer = new Rasterer(1, false);
		var info = PerfInfo.run(() -> rasterer.render(SCENE, raster), 3);
		System.out.println(info);
	}

}

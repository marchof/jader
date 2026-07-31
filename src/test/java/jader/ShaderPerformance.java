package jader;

import java.awt.image.BufferedImage;

import jader.scene.Scene;
import jader.shader.Rasterer;
import jader.ui.ExampleScenes;

public class ShaderPerformance {

	static final Scene SCENE = ExampleScenes.scene1();

	static final int WIDTH = 800;
	static final int HEIGHT = 600;

	public static void main(String... args) throws Exception {
		var image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		var rasterer = new Rasterer(1, false);
		var info = PerfInfo.run(() -> rasterer.render(SCENE, image), 1);
		System.out.println(info);
	}

}

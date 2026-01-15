package jader;

import java.awt.image.BufferedImage;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;

import jader.scene.Scene;
import jader.shader.Rasterer;
import jader.ui.ExampleScenes;

public class ShaderPerformance {

	static final Scene SCENE = ExampleScenes.scene1();

	static final int WIDTH = 800;
	static final int HEIGHT = 600;

	static final int RUNS = 5;

	public static void main(String... args) {
		var raster = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB).getRaster();
		var rasterer = new Rasterer(1, false);
		var threadMXBean = (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();

		for (int i = 1; i <= RUNS; i++) {
			var start = Instant.now();
			var startBytes = threadMXBean.getCurrentThreadAllocatedBytes();
			rasterer.render(SCENE, raster);
			var stop = Instant.now();
			var stopBytes = threadMXBean.getCurrentThreadAllocatedBytes();
			System.out.printf("Run %s: duration=%s allocation=%,d%n", i, Duration.between(start, stop),
					stopBytes - startBytes);
		}

	}

}

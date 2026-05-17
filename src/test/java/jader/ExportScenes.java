package jader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import jader.scene.Scene;
import jader.ui.ExampleScenes;
import jader.ui.Exporter;

public class ExportScenes {

	static final int WIDTH = 640;
	static final int HEIGHT = 360;

	static final Path OUTPUT_FOLDER = Path.of("target/export/referencescenes");

	static void export(Scene scene, String filename) throws Exception {
		Files.createDirectories(OUTPUT_FOLDER);
		var info = PerfInfo.run(() -> {
			try {
				new Exporter(scene, 4).exportPNG(WIDTH, HEIGHT, OUTPUT_FOLDER.resolve(filename));
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		System.out.println("Exported " + filename + " with " + info);
	}

	public static void main(String[] args) throws Exception {
		export(ExampleScenes.scene1(), "scene1.png");
		export(ExampleScenes.scene2(), "scene2.png");
		export(ExampleScenes.scene3(), "scene3.png");
		export(ExampleScenes.scene4(), "scene4.png");
		export(ExampleScenes.scene5(), "scene5.png");
		export(ExampleScenes.scene6(), "scene6.png");
		export(ExampleScenes.scene7(), "scene7.png");
	}

}

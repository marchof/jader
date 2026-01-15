package jader;

import java.nio.file.Files;
import java.nio.file.Path;

import jader.scene.Scene;
import jader.ui.ExampleScenes;
import jader.ui.Exporter;

public class ExportScenes {

	static final int WIDTH = 480;
	static final int HEIGHT = 320;
	
	static final Path OUTPUT_FOLDER = Path.of("target/referencescenes");
	
	static void export(Scene scene, String filename) throws Exception {
		Files.createDirectories(OUTPUT_FOLDER);
		new Exporter(scene, 4).exportPNG(WIDTH, HEIGHT, OUTPUT_FOLDER.resolve(filename));
	}

	public static void main(String[] args) throws Exception {
		export(ExampleScenes.scene1(), "scene1.png");
		export(ExampleScenes.scene2(), "scene2.png");
		export(ExampleScenes.scene3(), "scene3.png");
		export(ExampleScenes.scene4(), "scene4.png");
		export(ExampleScenes.scene5(), "scene5.png");
	}

}

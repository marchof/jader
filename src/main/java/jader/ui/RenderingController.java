package jader.ui;

import static java.time.Duration.between;
import static java.time.Instant.now;

import java.awt.image.BufferedImage;
import java.time.Duration;

import javax.swing.SwingWorker;

import jader.scene.Scene;
import jader.shader.Rasterer;

/// This class encapsulates the asynchronous shading calculation outside the
/// event dispatching thread to keep the UI responsive.
class RenderingController {

	interface ImageOutput {
		void show(BufferedImage image, Duration renderingTime);
	}

	private final ImageOutput output;

	private RenderingWorker currentWorker;

	RenderingController(ImageOutput output) {
		this.output = output;
		this.currentWorker = null;
	}

	void schedule(int width, int height, Scene scene) {
		if (currentWorker == null || currentWorker.width != width || currentWorker.height != height
				|| !currentWorker.scene.equals(scene)) {
			if (currentWorker != null) {
				currentWorker.cancelRendering();
			}
			currentWorker = new RenderingWorker(width, height, scene);
			currentWorker.execute();
		}
	}

	class RenderingWorker extends SwingWorker<Void, Void> {

		private final int width;
		private final int height;
		private final Scene scene;

		private final BufferedImage buffer;
		private final Rasterer rasterer;

		private Duration duration;

		RenderingWorker(int width, int height, Scene scene) {
			this.width = width;
			this.height = height;
			this.scene = scene;
			this.buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			this.rasterer = new Rasterer();
		}

		@Override
		protected Void doInBackground() throws Exception {
			var start = now();
			rasterer.render(scene, buffer);
			duration = between(start, now());
			return null;
		}

		void cancelRendering() {
			cancel(true);
			rasterer.cancel();
		}

		@Override
		protected void done() {
			if (!isCancelled()) {
				output.show(buffer, duration);
			}
		}
	}

}

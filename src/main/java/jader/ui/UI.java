package jader.ui;

import static java.time.Instant.now;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import jader.scene.Scene;
import jader.shader.Rasterer;

public class UI {

	private static final int HIDPI_FACTOR = 2;

	private Scene scene = ExampleScenes.scene1();
	
	private Rasterer rasterer = new Rasterer();

	private BufferedImage buffer;

	public void start() {
		var frame = new JFrame("Jader");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(640, 480);
		frame.getContentPane().add(createShaderPanel(frame::setTitle));
		frame.setVisible(true);
	}

	private Component createShaderPanel(Consumer<String> frameinfo) {
		var panel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				var buffer = getBuffer(this.getWidth() * HIDPI_FACTOR, this.getHeight() * HIDPI_FACTOR);
				var start = now();
				rasterer.render(scene, buffer.getRaster());
				var info = "Jader - %sx%spx %sms".formatted(buffer.getWidth(), buffer.getHeight(),
						Duration.between(start, now()).toMillis());
				frameinfo.accept(info);
				g2d.drawImage(buffer, 0, 0, buffer.getWidth() / HIDPI_FACTOR, buffer.getHeight() / HIDPI_FACTOR, null);
				repaint();
			}
		};
		return panel;
	}

	private BufferedImage getBuffer(int width, int height) {
		if (buffer != null && buffer.getWidth() == width && buffer.getHeight() == height) {
			return buffer;
		} else {
			return buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		}
	}

	public static void main(String[] args) {
		new UI().start();
	}

}

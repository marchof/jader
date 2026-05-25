package jader.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.time.Duration;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import jader.scene.Scene;
import jader.ui.RenderingController.ImageOutput;

public class UI implements ImageOutput {

	private Scene scene = ExampleScenes.scene1();

	private JFrame frame;

	private Component shaderPanel;

	private BufferedImage buffer;

	private RenderingController controller;

	public void start() {
		frame = new JFrame("Jader");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(640, 400);
		shaderPanel = createShaderPanel();
		frame.getContentPane().add(shaderPanel);
		frame.setVisible(true);
		controller = new RenderingController(this);
		shaderPanel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				schedule();
			}
		});
		schedule();
	}

	private void schedule() {
		// Adjust rendered pixels to physical resolution for retina support:
		var t = shaderPanel.getGraphicsConfiguration().getDefaultTransform();
		var pixelsX = (int) (shaderPanel.getWidth() * t.getScaleX());
		var pixelsY = (int) (shaderPanel.getHeight() * t.getScaleY());
		controller.schedule(pixelsX, pixelsY, scene);
	}

	private Component createShaderPanel() {
		var panel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				if (buffer != null) {
					g.drawImage(buffer, 0, 0, this.getWidth(), this.getHeight(), this);
				}
			}
		};
		return panel;
	}

	@Override
	public void show(BufferedImage buffer, Duration renderingTime) {
		var info = "Jader - %sx%spx %sms".formatted(buffer.getWidth(), buffer.getHeight(), renderingTime.toMillis());
		frame.setTitle(info);
		this.buffer = buffer;
		this.shaderPanel.repaint();
	}

	public static void main(String[] args) {
		new UI().start();
	}

}

package jader.shape;

import static java.lang.Math.clamp;
import static java.lang.Math.pow;

/// Color in linear space for shading calculation. Conversion from/to RGB
/// includes gamma correction.
public record Color(float red, float green, float blue) {

	private static final double GAMMA = 2.2;

	public static final Color BLACK = new Color(0.0f, 0.0f, 0.0f);
	public static final Color WHITE = new Color(1.0f, 1.0f, 1.0f);

	public Color {
		red = clamp(red, 0f, 1f);
		green = clamp(green, 0f, 1f);
		blue = clamp(blue, 0f, 1f);
	}

	public static Color color(float r, float g, float b) {
		return new Color(r, g, b);
	}

	/// Creates a gamma corrected color instance from RGB values.
	public static Color rgb(int r, int g, int b) {
		return new Color( //
				(float) pow(r / 255.0, GAMMA), //
				(float) pow(g / 255.0, GAMMA), //
				(float) pow(b / 255.0, GAMMA));
	}

	public boolean isNonBlack() {
		return red > 0f || green > 0f || blue > 0f;
	}

	public Color add(Color c) {
		return new Color(this.red + c.red, this.green + c.green, this.blue + c.blue);
	}

	public Color mulAdd(Color c, float scalar) {
		return new Color(this.red + c.red * scalar, this.green + c.green * scalar, this.blue + c.blue * scalar);
	}

	public Color mulAdd(Color c1, Color c2) {
		return new Color(this.red + c1.red * c2.red, this.green + c1.green * c2.green, this.blue + c1.blue * c2.blue);
	}

	public Color mulAdd(Color c1, Color c2, float scalar) {
		return new Color(this.red + c1.red * c2.red * scalar, this.green + c1.green * c2.green * scalar,
				this.blue + c1.blue * c2.blue * scalar);
	}

	public Color mul(float scalar) {
		return new Color(this.red * scalar, this.green * scalar, this.blue * scalar);
	}

	public Color mul(Color c) {
		return new Color(this.red * c.red, this.green * c.green, this.blue * c.blue);
	}

	public Color blend(Color other, float f) {
		var nf = 1.0f - f;
		return new Color( //
				this.red * nf + other.red * f, //
				this.green * nf + other.green * f, //
				this.blue * nf + other.blue * f);
	}

	/// Fills gamma corrected RGB components into the given array.
	public void fillRGB(int[] components) {
		components[0] = (int) (pow(red, 1.0 / GAMMA) * 255f);
		components[1] = (int) (pow(green, 1.0 / GAMMA) * 255f);
		components[2] = (int) (pow(blue, 1.0 / GAMMA) * 255f);
	}

}

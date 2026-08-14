package jader.shape;

import static java.lang.Math.clamp;
import static java.lang.Math.pow;

/// Color in linear space for shading calculations. Conversion to and from RGB
/// includes gamma correction.
public value record Color(float red, float green, float blue) {

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

	/// Creates a gamma-corrected color instance from RGB values.
	public static Color rgb(int r, int g, int b) {
		return new Color(decode(r), decode(g), decode(b));
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

	/// Returns the gamma-corrected 8-bit RGB components encoded in an {@code int}. 
	public int toRGB() {
		return encode(red) << 16 | encode(green) << 8 | encode(blue);
	}
	
	private static float decode(int component) {
		return (float) pow(component / 255.0, GAMMA);
	}

	private static int encode(float value) {
		return (int) (pow(value, 1.0 / GAMMA) * 255f);
	}

}

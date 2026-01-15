package jader.shape;

import static jader.math.Vec2.vec2;

import jader.math.Vec2;

public interface Surface {

	Material material(Vec2 at);

	default Surface scale(float f) {
		return scale(f, f);
	}

	default Surface scale(float fx, float fy) {
		return at -> this.material(vec2((at.x() * fx) % 1f, (at.y() * fy) % 1f));
	}

	public static Surface uniform(Material material) {
		return p -> material;
	}

	public static Surface checker(Material m1, Material m2) {
		return p -> p.x() > 0.5f ^ p.y() > 0.5f ? m1 : m2;
	}

	public static Surface grid(float linewidth, Material lines, Material background) {
		return p -> p.x() <= linewidth || p.y() <= linewidth ? lines : background;
	}
	
}

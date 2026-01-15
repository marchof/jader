package jader.math;

public record Vec2(float x, float y) {
	
	public static Vec2 vec2(float x, float y) {
		return new Vec2(x, y);
	}

}

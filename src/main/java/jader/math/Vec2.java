package jader.math;

public record Vec2(float x, float y) {

	public static Vec2 vec2(float x, float y) {
		return new Vec2(x, y);
	}

	public float len() {
		return (float) Math.sqrt(x * x + y * y);
	}

	public float dot(Vec2 other) {
		return x * other.x + y * other.y;
	}

	public Vec2 sub(Vec2 other) {
		return new Vec2(this.x - other.x, this.y - other.y);
	}

	public Vec2 mul(float scalar) {
		return new Vec2(this.x * scalar, this.y * scalar);
	}
	
	public Vec2 mulAdd(Vec2 vec, float scalar) {
		return new Vec2(this.x + vec.x * scalar, this.y + vec.y * scalar);
	}

	public Vec2 mulSub(Vec2 vec, float scalar) {
		return new Vec2(this.x - vec.x * scalar, this.y - vec.y * scalar);
	}

	public float dist2(final Vec2 to) {
		float dx = to.x - x;
		float dy = to.y - y;
		return dx * dx + dy * dy;
	}

	public float dist(Vec2 to) {
		return (float) Math.sqrt(dist2(to));
	}

	public Vec2 max(float limit) {
		return new Vec2(Math.max(x, limit), Math.max(y, limit));
	}

}

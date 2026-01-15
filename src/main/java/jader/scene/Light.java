package jader.scene;

import jader.math.Vec3;
import jader.shape.Color;

public sealed interface Light {

	public record Ambient(Color color, float oa, float aoRange) implements Light {
		
		public boolean hasAO() {
			return oa > 0.0f;
		}
		
		public Ambient withAO(float oa, float oaRange) {
			return new Ambient(color, oa, oaRange);
		}
		
	}

	public static Ambient ambient(Color color) {
		return new Ambient(color, 0.0f, 0.0f);
	}

	public static Ambient ambient(float brightness) {
		return ambient(Color.WHITE.mul(brightness));
	}
	
	public record Point(Vec3 position, Color color, float radius) implements Light {
	}
	
	public static Light.Point point(Vec3 position, Color color, float radius) {
		return new Point(position, color, radius);
	}

	public static Light.Point point(Vec3 position, Color color) {
		return point(position, color, 0.0f);
	}

	public static Light.Point point(Vec3 position, float brightness) {
		return point(position, Color.WHITE.mul(brightness));
	}
	
}

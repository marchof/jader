package jader.math;

import static jader.math.Vec3.vec3;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Description affine transformation composed from rotations, translations and
 * scalings. Note that non-uniform scaling is not possible with with SDFs as it
 * dilates spaces.
 */
public record Trans3( //
		float m00, float m01, float m02, float m03, //
		float m10, float m11, float m12, float m13, //
		float m20, float m21, float m22, float m23 //
) implements Function<Vec3, Vec3> {

	private static final Trans3 IDENTITY = new Trans3( //
			1, 0, 0, 0, //
			0, 1, 0, 0, //
			0, 0, 1, 0);

	public static Trans3 identity() {
		return IDENTITY;
	}

	public static Trans3 of(final Trans3... transformations) {
		return Arrays.stream(transformations).reduce(Trans3.identity(), Trans3::mul);
	}

	public static Trans3 rot(float x, float y, float z, float rad) {
		return rot(vec3(x, y, z), rad);
	}

	public static Trans3 rot(double x, double y, double z, float rad) {
		return rot(vec3(x, y, z), rad);
	}

	public static Trans3 rot(Vec3 axis, float rad) {
		axis = axis.nor();
		float x = axis.x(), y = axis.y(), z = axis.z();
		float c = (float) Math.cos(rad), s = (float) Math.sin(rad), mc = 1.0f - c;
		return new Trans3( //
				x * x * mc + c, //
				x * y * mc + z * s, //
				x * z * mc - y * s, //
				0, //
				x * y * mc - z * s, //
				y * y * mc + c, //
				y * z * mc + x * s, //
				0, //
				x * z * mc + y * s, //
				y * z * mc - x * s, //
				z * z * mc + c, //
				0);
	}

	public static Trans3 rotdeg(float x, float y, float z, float rad) {
		return rotdeg(vec3(x, y, z), rad);
	}

	public static Trans3 rotdeg(double x, double y, double z, float rad) {
		return rotdeg(vec3(x, y, z), rad);
	}

	public static Trans3 rotdeg(Vec3 axis, float deg) {
		return rot(axis, (float) (deg * Math.PI / 180.0f));
	}

	public static Trans3 trans(float x, float y, float z) {
		return new Trans3( //
				1, 0, 0, x, //
				0, 1, 0, y, //
				0, 0, 1, z);
	}

	public static Trans3 trans(double x, double y, double z) {
		return trans((float) x, (float) y, (float) z);
	}

	public static Trans3 trans(Vec3 t) {
		return trans(t.x(), t.y(), t.z());
	}

	public static Trans3 scale(double s) {
		return scale((float) s);
	}

	public static Trans3 scale(float s) {
		return new Trans3( //
				s, 0, 0, 0, //
				0, s, 0, 0, //
				0, 0, s, 0);
	}

	public float getScale() {
		return (float) Math.sqrt(m00 * m00 + m10 * m10 + m20 * m20);
	}

	@Override
	public Vec3 apply(Vec3 p) {
		return vec3( //
				lc(m00, p.x(), m01, p.y(), m02, p.z()) + m03, //
				lc(m10, p.x(), m11, p.y(), m12, p.z()) + m13, //
				lc(m20, p.x(), m21, p.y(), m22, p.z()) + m23);
	}

	public Trans3 mul(Trans3 other) {
		return new Trans3( //
				lc(this.m00, other.m00, this.m01, other.m10, this.m02, other.m20), //
				lc(this.m00, other.m01, this.m01, other.m11, this.m02, other.m21), //
				lc(this.m00, other.m02, this.m01, other.m12, this.m02, other.m22), //
				lc(this.m00, other.m03, this.m01, other.m13, this.m02, other.m23) + this.m03, //

				lc(this.m10, other.m00, this.m11, other.m10, this.m12, other.m20), //
				lc(this.m10, other.m01, this.m11, other.m11, this.m12, other.m21), //
				lc(this.m10, other.m02, this.m11, other.m12, this.m12, other.m22), //
				lc(this.m10, other.m03, this.m11, other.m13, this.m12, other.m23) + this.m13, //

				lc(this.m20, other.m00, this.m21, other.m10, this.m22, other.m20), //
				lc(this.m20, other.m01, this.m21, other.m11, this.m22, other.m21), //
				lc(this.m20, other.m02, this.m21, other.m12, this.m22, other.m22), //
				lc(this.m20, other.m03, this.m21, other.m13, this.m22, other.m23) + this.m23);
	}

	public Trans3 inverse() {
		float det = det(m00, m01, m02, m10, m11, m12, m20, m21, m22);
		float invDet = 1.0f / det;
		return new Trans3(//
				invDet * det(m11, m12, m21, m22), //
				-invDet * det(m01, m02, m21, m22), //
				invDet * det(m01, m02, m11, m12), //
				-invDet * det(m01, m02, m03, m11, m12, m13, m21, m22, m23), //

				-invDet * det(m10, m12, m20, m22), //
				invDet * det(m00, m02, m20, m22), //
				-invDet * det(m00, m02, m10, m12), //
				invDet * det(m00, m02, m03, m10, m12, m13, m20, m22, m23), //

				invDet * det(m10, m11, m20, m21), //
				-invDet * det(m00, m01, m20, m21), //
				invDet * det(m00, m01, m10, m11), //
				-invDet * det(m00, m01, m03, m10, m11, m13, m20, m21, m23));
	}

	private static float lc(float a1, float b1, float a2, float b2, float a3, float b3) {
		return a1 * b1 + a2 * b2 + a3 * b3;
	}

	private static float det(final float a00, final float a01, final float a10, final float a11) {
		return a00 * a11 - a01 * a10;
	}

	private static float det(final float a00, final float a01, final float a02, final float a10, final float a11,
			final float a12, final float a20, final float a21, final float a22) {
		return a00 * a11 * a22 //
				+ a01 * a12 * a20 //
				+ a02 * a10 * a21 //
				- a00 * a12 * a21 //
				- a01 * a10 * a22 //
				- a02 * a11 * a20;
	}

}

package jader.math;

import static jader.math.Trans3.rotdeg;
import static jader.math.Trans3.scale;
import static jader.math.Trans3.trans;
import static jader.math.Vec3.vec3;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class Trans3Test {

	static final float EPSILON = 0.00001f;

	@Test
	void should_apply_translate() {
		var t = trans(1f, 2f, 3f);
		assertEqualsVec(vec3(11, 22, 33), t.apply(vec3(10, 20, 30)));
	}

	@Test
	void should_apply_uniform_scale() {
		var t = scale(0.5f);
		assertEqualsVec(vec3(5, 10, 15), t.apply(vec3(10, 20, 30)));
	}

	@Test
	void should_invert_symmetrically() {
		var t = Trans3.of(trans(15.0f, -7.7f, 66.0f), scale(0.75f), rotdeg(1, 1, 1, 75));
		assertEqualsTransform(t, t.inverse().inverse());
	}

	@Test
	void should_calculate_scale() {
		var t = Trans3.of(trans(15.0f, -7.7f, 66.0f), scale(0.5f), rotdeg(1, 1, 1, 75), scale(0.5f));
		assertEquals(0.25, t.getScale(), EPSILON);
	}

	
	static void assertEqualsVec(Vec3 expected, Vec3 actual) {
		assertEquals(expected.x(), actual.x(), EPSILON, "x");
		assertEquals(expected.y(), actual.y(), EPSILON, "y");
		assertEquals(expected.z(), actual.z(), EPSILON, "z");
	}

	static void assertEqualsTransform(Trans3 expected, Trans3 actual) {
		assertEquals(expected.m00(), actual.m00(), EPSILON, "m00");
		assertEquals(expected.m01(), actual.m01(), EPSILON, "m01");
		assertEquals(expected.m02(), actual.m02(), EPSILON, "m02");
		assertEquals(expected.m03(), actual.m03(), EPSILON, "m03");
		assertEquals(expected.m10(), actual.m10(), EPSILON, "m10");
		assertEquals(expected.m11(), actual.m11(), EPSILON, "m11");
		assertEquals(expected.m12(), actual.m12(), EPSILON, "m12");
		assertEquals(expected.m13(), actual.m13(), EPSILON, "m13");
		assertEquals(expected.m20(), actual.m20(), EPSILON, "m20");
		assertEquals(expected.m21(), actual.m21(), EPSILON, "m21");
		assertEquals(expected.m22(), actual.m22(), EPSILON, "m22");
		assertEquals(expected.m23(), actual.m23(), EPSILON, "m23");
	}

}

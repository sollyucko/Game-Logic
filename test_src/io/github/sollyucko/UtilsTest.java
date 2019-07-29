package io.github.sollyucko;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class UtilsTest {
	@Test
	void test2DCartesianProductSquare() {
		assertIterableEquals(Arrays.asList(0, 1, 10, 2, 11, 20, 3, 12, 21, 30, 13, 22, 31, 23, 32, 33),
		                     Utils.cartesianProduct((a, b) -> 10 * a + b,
		                                            Arrays.asList(0, 1, 2, 3),
		                                            Arrays.asList(0, 1, 2, 3)));
	}
	
	@Test
	void test2DCartesianProductTallRectangle() {
		assertIterableEquals(Arrays.asList(0, 1, 10, 2, 11, 20, 3, 12, 21, 13, 22, 23),
		                     Utils.cartesianProduct((a, b) -> 10 * a + b,
		                                            Arrays.asList(0, 1, 2),
		                                            Arrays.asList(0, 1, 2, 3)));
	}
	
	@Test
	void test2DCartesianProductWideRectangle() {
		assertIterableEquals(Arrays.asList(0, 1, 10, 2, 11, 20, 12, 21, 30, 22, 31, 32),
		                     Utils.cartesianProduct((a, b) -> 10 * a + b,
		                                            Arrays.asList(0, 1, 2, 3),
		                                            Arrays.asList(0, 1, 2)));
	}
}
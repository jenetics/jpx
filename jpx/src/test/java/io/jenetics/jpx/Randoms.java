/*
 * Java GPX Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.jpx;

import static java.lang.Math.nextDown;
import static java.lang.String.format;

import java.util.Random;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public final class Randoms {
	private Randoms() {}

	public static byte nextByte(final Random random) {
		return (byte) nextInt(Byte.MIN_VALUE, Byte.MAX_VALUE + 1, random);
	}

	public static char nextChar(final Random random) {
		char c = '\0';
		do {
			c = (char)nextInt(
				Character.MIN_VALUE,
				Character.MAX_VALUE + 1,
				random
			);
		} while (!Character.isLetterOrDigit(c));

		return c;
	}

	public static short nextShort(final Random random) {
		return (short) nextInt(Short.MIN_VALUE, Short.MAX_VALUE + 1, random);
	}

	/**
	 * Returns a pseudo-random, uniformly distributed int value between origin
	 * (included) and bound (excluded).
	 *
	 * @param origin the origin (inclusive) of each random value
	 * @param bound the bound (exclusive) of each random value
	 * @param random the random engine to use for calculating the random
	 *        int value
	 * @return a random integer greater than or equal to {@code min} and
	 *         less than or equal to {@code max}
	 * @throws IllegalArgumentException if {@code origin >= bound}
	 */
	public static int nextInt(
		final int origin,
		final int bound,
		final Random random
	) {
		if (origin >= bound) {
			throw new IllegalArgumentException(format(
				"origin >= bound: %d >= %d", origin, bound
			));
		}

		final int value;

		if (origin < bound) {
			int n = bound - origin;
			if (n > 0) {
				value = random.nextInt(n) + origin;
			} else {
				int r;
				do {
					r = random.nextInt();
				} while (r < origin || r >= bound);
				value = r;
			}
		} else {
			value = random.nextInt();
		}

		return value;
	}

	/**
	 * Returns a pseudo-random, uniformly distributed double value between
	 * min (inclusively) and max (exclusively).
	 *
	 * @param min lower bound for generated float value (inclusively)
	 * @param max upper bound for generated float value (exclusively)
	 * @param random the random engine used for creating the random number.
	 * @return a random float greater than or equal to {@code min} and less
	 *         than to {@code max}
	 * @throws NullPointerException if the given {@code random}
	 *         engine is {@code null}.
	 */
	public static float nextFloat(
		final float min,
		final float max,
		final Random random
	) {
		if (min >= max) {
			throw new IllegalArgumentException(format(
				"min >= max: %f >= %f.", min, max
			));
		}

		float value = random.nextFloat();
		if (min < max) {
			value = value*(max - min) + min;
			if (value >= max) {
				value = nextDown(value);
			}
		}

		return value;
	}

	/**
	 * Returns a pseudo-random, uniformly distributed double value between
	 * min (inclusively) and max (exclusively).
	 *
	 * @param min lower bound for generated double value (inclusively)
	 * @param max upper bound for generated double value (exclusively)
	 * @param random the random engine used for creating the random number.
	 * @return a random double greater than or equal to {@code min} and less
	 *         than to {@code max}
	 * @throws NullPointerException if the given {@code random}
	 *         engine is {@code null}.
	 */
	public static double nextDouble(
		final double min,
		final double max,
		final Random random
	) {
		if (min >= max) {
			throw new IllegalArgumentException(format(
				"min >= max: %f >= %f.", min, max
			));
		}

		double value = random.nextDouble();
		if (min < max) {
			value = value*(max - min) + min;
			if (value >= max) {
				value = nextDown(value);
			}
		}

		return value;
	}

	public static String nextASCIIString(final int length, final Random random) {
		final char[] chars = new char[length];
		for (int i = 0; i < length; ++i) {
			chars[i] = (char)nextInt(32, 127, random);
		}

		return new String(chars);
	}

	public static String nextASCIIString(final Random random) {
		return nextASCIIString(nextInt(5, 20, random), random);
	}

}

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
package io.jenetics.jpx.geom;

import static java.lang.Double.doubleToRawLongBits;

/**
 * Mathematical helper functions.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 2.1
 * @since 2.1
 */
final class MathUtils {

	private static final long POSITIVE_ZERO_BITS = doubleToRawLongBits(+0.0);
	private static final long NEGATIVE_ZERO_BITS = doubleToRawLongBits(-0.0);

	private MathUtils() {
	}

	/**
	 * Returns {@code true} if the given {@code double} values are equal within
	 * the range of allowed ULP error (inclusive). The values are considered
	 * equal if there are maximal {@code (ulps - 1)} {@code double} values
	 * between them.
	 *
	 * @param x first value to compare
	 * @param y second value to compare
	 * @param ulps the maximal ULP distance (epsilon)
	 * @return {@code true} if there are fewer than {@code ulps}values between
	 *         {@code x} and {@code y}, {@code false} otherwise
	 */
	static boolean equal(final double x, final double y, final int ulps) {
		long a = doubleToRawLongBits(x);
		long b = doubleToRawLongBits(y);
		if (a < b) {
			final long t = a; a = b; b = t;
		}

		final boolean equal;
		if ((a ^ b) < 0) { // a and b have opposite sign.
			final long diffPositive = a - POSITIVE_ZERO_BITS;
			final long diffNegative = b - NEGATIVE_ZERO_BITS;
			equal = diffPositive <= ulps && diffNegative <= (ulps - diffPositive);
		} else {         // a and b have same sign.
			equal = a - b <= ulps;
		}

		return equal && !Double.isNaN(x) && !Double.isNaN(y);
	}

}

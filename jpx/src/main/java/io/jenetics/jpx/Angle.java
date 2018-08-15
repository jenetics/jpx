/*
 * Java Genetic Algorithm Library (@__identifier__@).
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

/**
 * Helper class for angle calculations
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class Angle {

	private Angle() {
	}

	/**
	 * Splits the given angle (in degrees) into an int array containing degrees,
	 * minutes and seconds.
	 *
	 * @param angle the angle to split up
	 * @return the split angle
	 */
	static int[] split(final double angle) {
		final double value = Math.abs(angle);
		final int sign = Double.compare(angle, 0.0) < 0 ? -1 : 1;

		int degrees = (int)Math.floor(value);
		int seconds = (int)Math.round((value - degrees)*60*60);

		int minutes = seconds/60;
		if (minutes == 60) {
			minutes = 0;
			degrees++;
		}
		seconds = seconds%60;

		degrees = degrees*sign;
		minutes = minutes*sign;
		seconds = seconds*sign;

		return new int[] { degrees, minutes, seconds };
	}

	static double merge(final int[] components) {
		final double degrees = Math.abs(components[0]) +
			Math.abs(components[1]/60.0) +
			Math.abs(components[2]/(60.0*60.0));

		return components[0] >= 0 ? degrees : -degrees;
	}

}

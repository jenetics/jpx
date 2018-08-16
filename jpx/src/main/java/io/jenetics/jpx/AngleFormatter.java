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

import static java.lang.Math.abs;
import static java.lang.Math.floor;
import static java.util.Objects.requireNonNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.function.Supplier;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
abstract class AngleFormatter {

	final Supplier<NumberFormat> _format;

	private AngleFormatter(final Supplier<NumberFormat> format) {
		_format = requireNonNull(format);
	}

	abstract String format(final double degrees);


	static AngleFormatter ofDegrees(final String pattern) {
		return DegreesFormatter.ofPattern(pattern);
	}

	static AngleFormatter ofMinutes(final String pattern) {
		return MinutesFormatter.ofPattern(pattern);
	}

	static AngleFormatter ofSeconds(final String pattern) {
		return SecondsFormatter.ofPattern(pattern);
	}


	private final static class DegreesFormatter extends AngleFormatter {

		private DegreesFormatter(final Supplier<NumberFormat> format) {
			super(format);
		}

		String format(final double degrees) {
			final double dd = abs(degrees);
			return _format.get().format(dd);
		}

		static DegreesFormatter ofPattern(final String pattern) {
			return new DegreesFormatter(() -> new DecimalFormat(pattern));
		}

	}

	private final static class MinutesFormatter extends AngleFormatter {

		private MinutesFormatter(final Supplier<NumberFormat> format) {
			super(format);
		}

		String format(final double degrees) {
			final double dd = abs(degrees);
			final double minutes = (dd - floor(dd))*60.0;
			return _format.get().format(minutes);
		}

		static MinutesFormatter ofPattern(final String pattern) {
			return new MinutesFormatter(() -> new DecimalFormat(pattern));
		}
	}

	private final static class SecondsFormatter extends AngleFormatter {

		private SecondsFormatter(final Supplier<NumberFormat> format) {
			super(format);
		}

		String format(final double degrees) {
			final double dd = abs(degrees);
			final double d = floor(dd);
			final double m = floor((dd - d)*60.0);
			final double s = (dd - d - m/60.0)*3600.0;
			return _format.get().format(s);
		}

		static SecondsFormatter ofPattern(final String pattern) {
			return new SecondsFormatter(() -> new DecimalFormat(pattern));
		}

	}

}

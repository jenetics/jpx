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
package io.jenetics.jpx.format;

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
abstract class AngleFormat implements Format<Double> {

	final Supplier<NumberFormat> _format;

	private AngleFormat(final Supplier<NumberFormat> format) {
		_format = requireNonNull(format);
	}

	static AngleFormat ofDegrees(final String pattern) {
		return DegreesFormatter.ofPattern(pattern);
	}

	static AngleFormat ofMinutes(final String pattern) {
		return MinutesFormatter.ofPattern(pattern);
	}

	static AngleFormat ofSeconds(final String pattern) {
		return SecondsFormatter.ofPattern(pattern);
	}

	static AngleFormat ofPattern(final String pattern) {
		switch (Character.toLowerCase(pattern.charAt(0))) {
			case 'd': return ofDegrees(toDecimalPattern(pattern));
			case 'm': return ofMinutes(toDecimalPattern(pattern));
			case 's': return ofSeconds(toDecimalPattern(pattern));
			default: throw new IllegalArgumentException(String.format(
				"Invalid pattern: %s", pattern
			));
		}
	}

	private static String toDecimalPattern(final String pattern) {
		final StringBuilder out = new StringBuilder();
		for (int i = 0; i < pattern.length(); ++i) {
			final char c = pattern.charAt(i);
			switch (c) {
				case '.':
				case ',':
					out.append(c);
					break;
				default:
					out.append('0');
					break;
			}
		}

		return out.toString();
	}

	private final static class DegreesFormatter extends AngleFormat {

		private DegreesFormatter(final Supplier<NumberFormat> format) {
			super(format);
		}

		@Override
		public String format(final Double degrees) {
			final double dd = abs(degrees);
			return _format.get().format(dd);
		}

		static DegreesFormatter ofPattern(final String pattern) {
			return new DegreesFormatter(() -> new DecimalFormat(pattern));
		}

	}

	private final static class MinutesFormatter extends AngleFormat {

		private MinutesFormatter(final Supplier<NumberFormat> format) {
			super(format);
		}

		@Override
		public String format(final Double degrees) {
			final double dd = abs(degrees);
			final double minutes = (dd - floor(dd))*60.0;
			return _format.get().format(minutes);
		}

		static MinutesFormatter ofPattern(final String pattern) {
			return new MinutesFormatter(() -> new DecimalFormat(pattern));
		}
	}

	private final static class SecondsFormatter extends AngleFormat {

		private SecondsFormatter(final Supplier<NumberFormat> format) {
			super(format);
		}

		@Override
		public String format(final Double degrees) {
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

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
 * Implementations of format objects for <em>degrees</em>, <em>minutes</em>,
 * <em>seconds</em> and <em>heights</em>.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
abstract class ValueFormat implements Format<Double> {

	final Supplier<NumberFormat> _format;

	private ValueFormat(final Supplier<NumberFormat> format) {
		_format = requireNonNull(format);
	}

	/**
	 * Return the appropriate value format object for the given pattern:
	 * {@code DD}, {@code ss.sss} or {@code HHHH.H}.
	 *
	 * @param pattern the location part pattern
	 * @return the appropriate format for the given pattern
	 */
	static Format<Double> ofPattern(final String pattern) {
		switch (Character.toLowerCase(pattern.charAt(0))) {
			case 'd': return new DegreesFormat(toNumberFormat(pattern));
			case 'm': return new MinutesFormat(toNumberFormat(pattern));
			case 's': return new SecondsFormat(toNumberFormat(pattern));
			case 'h': return ele -> new DecimalFormat(pattern).format(ele);
			case '+': return new FixSignFormat();
			default: throw new IllegalArgumentException(String.format(
				"Invalid pattern: %s", pattern
			));
		}
	}

	private static Supplier<NumberFormat> toNumberFormat(final String pattern) {
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

		return () -> new DecimalFormat(out.toString());
	}

	/**
	 * Degree format implementation.
	 */
	private final static class DegreesFormat extends ValueFormat {
		private DegreesFormat(final Supplier<NumberFormat> format) {
			super(format);
		}
		@Override
		public String format(final Double degrees) {
			final double dd = abs(degrees);
			return _format.get().format(dd);
		}
	}

	/**
	 * Minute format implementation.
	 */
	private final static class MinutesFormat extends ValueFormat {
		private MinutesFormat(final Supplier<NumberFormat> format) {
			super(format);
		}
		@Override
		public String format(final Double degrees) {
			final double dd = abs(degrees);
			final double minutes = (dd - floor(dd))*60.0;
			return _format.get().format(minutes);
		}
	}

	/**
	 * Second format implementation.
	 */
	private final static class SecondsFormat extends ValueFormat {
		private SecondsFormat(final Supplier<NumberFormat> format) {
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
	}

	private static final class FixSignFormat implements Format<Double> {
		@Override
		public String format(final Double value) {
			return Double.compare(value, 0.0) >= 0 ? "+" : "-";
		}
	}

}

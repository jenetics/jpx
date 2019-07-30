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
package io.jenetics.jpx.format;

import static java.util.Objects.requireNonNull;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Optional;
import java.util.function.Supplier;

import io.jenetics.jpx.format.Location.Field;

/**
 * This class formats a given location field (latitude, longitude or elevation)
 * with the given double value format. E.g. {@code DD}, {@code ss.sss} or
 * {@code HHHH.H}.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.4
 * @since 1.4
 */
final class LocationFieldFormat implements Format<Location> {

	private final Field _field;
	private final Supplier<NumberFormat> _format;

	private LocationFieldFormat(
		final Field field,
		final Supplier<NumberFormat> format
	) {
		_field = requireNonNull(field);
		_format = requireNonNull(format);
	}

	@Override
	public Optional<String> format(final Location location) {
		return _field.apply(location).map(v -> _format.get().format(v));
	}

	static LocationFieldFormat of(final Field field, final String pattern) {
		// Fast fail. Will throw an IAE if the pattern is invalid.
		new DecimalFormat(pattern);
		return new LocationFieldFormat(field, () -> new DecimalFormat(pattern));
	}

	@Override
	public String toString() {
		final NumberFormat format = _format.get();
		final int minValueDigit = format.getMinimumIntegerDigits();
		final int minFractionDigit = format.getMinimumFractionDigits();
		final int maxFractionDigit = format.getMaximumFractionDigits();

		final StringBuilder out = new StringBuilder();
		for (int i = 0; i < minValueDigit; ++i) {
			out.append(_field.type());
		}
		if (minFractionDigit != 0 || maxFractionDigit != 0) {
			out.append('.');
		}
		for (int i = 0; i < minFractionDigit; ++i) {
			out.append(_field.type());
		}

		return out.toString();
	}

	static LocationFieldFormat of(
		final Field field,
		final Supplier<NumberFormat> format
	) {
		return new LocationFieldFormat(field, format);
	}

}

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

import static java.util.Objects.requireNonNull;

import java.util.Optional;

/**
 * This class formats a given location field (latitude, longitude or elevation)
 * with the given double value format. E.g. {@code DD}, {@code ss.sss} or
 * {@code HHHH.H}.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class LocationFieldFormat implements Format<Location> {

	private final LocationField _field;
	private final Format<Double> _format;
	private final boolean _optional;

	private LocationFieldFormat(
		final LocationField field,
		final Format<Double> format,
		final boolean optional
	) {
		_field = requireNonNull(field);
		_format = requireNonNull(format);
		_optional = optional;
	}

	@Override
	public String format(final Location location) {
		final Optional<String> text = _field.value(location)
			.map(_format::format);

		if (!text.isPresent() && !_optional) {
			throw new IllegalArgumentException(String.format(
				"No '%s' value.", _field.fieldName())
			);
		}

		return text.orElseThrow(AssertionError::new);
	}

	/**
	 * Return a new location field format object form the given pattern:
	 * {@code DD}, {@code ss.sss} or {@code HHHH.H}.
	 *
	 * @param pattern the location field pattern
	 * @param optional marks this field format as optional
	 * @return a new format object from the given pattern
	 */
	static LocationFieldFormat
	ofPattern(final String pattern, final boolean optional) {
		return new LocationFieldFormat(
			LocationField.ofPattern(pattern),
			ValueFormat.ofPattern(pattern),
			optional
		);
	}

	/**
	 * Return a new location field format object form the given pattern:
	 * {@code DD}, {@code ss.sss} or {@code HHHH.H}.
	 *
	 * @param pattern the location field pattern
	 * @return a new format object from the given pattern
	 */
	static LocationFieldFormat ofPattern(final String pattern) {
		return ofPattern(pattern, false);
	}

}

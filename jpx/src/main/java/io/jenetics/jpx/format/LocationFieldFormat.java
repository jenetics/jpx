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

	private LocationFieldFormat(
		final LocationField field,
		final Format<Double> format
	) {
		_field = requireNonNull(field);
		_format = requireNonNull(format);
	}

	@Override
	public String format(final Location location) {
		final double value = _field.value(location)
			.orElseThrow(() -> new IllegalArgumentException(String.format(
				"No '%s' value.", _field.fieldName())));

		return _format.format(value);
	}

	/**
	 * Return a new location field format object form the given pattern:
	 * {@code DD}, {@code ss.sss} or {@code HHHH.H}.
	 *
	 * @param pattern the location field pattern
	 * @return a new format object from the given pattern
	 */
	static LocationFieldFormat ofPattern(final String pattern) {
		final LocationField field = LocationField.ofType(pattern.charAt(0));
		return new LocationFieldFormat(field, ValueFormat.ofPattern(pattern));
	}

}

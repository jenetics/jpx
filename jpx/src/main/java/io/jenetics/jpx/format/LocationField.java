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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static io.jenetics.jpx.Length.Unit.METER;

import java.util.Optional;
import java.util.function.Function;

import io.jenetics.jpx.Latitude;
import io.jenetics.jpx.Longitude;

/**
 * Represents one of the existing location fields: latitude, longitude and
 * elevation.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
enum LocationField {

	LATITUDE(
		"latitude",
		loc -> loc.latitude().map(Latitude::toDegrees)
	),

	LONGITUDE(
		"longitude",
		loc -> loc.longitude().map(Longitude::toDegrees)
	),

	ELEVATION(
		"elevation",
		loc -> loc.elevation().map(l -> l.to(METER))
	);

	private final String _name;
	private final Function<Location, Optional<Double>> _value;

	LocationField(
		final String name,
		final Function<Location, Optional<Double>> value
	) {
		_name = requireNonNull(name);
		_value = requireNonNull(value);
	}

	/**
	 * Return the name of the location field.
	 *
	 * @return the name of the location field
	 */
	String fieldName() {
		return _name;
	}

	/**
	 * Extracts the (double) value from the given location field.
	 *
	 * @param location the location
	 * @return the value of the location field
	 */
	Optional<Double> value(final Location location) {
		return _value.apply(requireNonNull(location));
	}

	/**
	 * Return the location field for the given location pattern:
	 * {@code DD}, {@code ss.sss} or {@code HHHH.H}.
	 *
	 * @param pattern the location pattern
	 * @return the location field for the given location pattern
	 */
	static LocationField ofPattern(final String pattern) {
		switch (pattern.charAt(0)) {
			case 'H':
				return ELEVATION;
			case 'D': case 'M': case 'S': case 'X':
				return LATITUDE;
			case 'd': case 'm': case 's': case 'x':
				return LONGITUDE;
			default: throw new IllegalArgumentException(format(
				"Unknown field type: %s", pattern
			));
		}
	}

}

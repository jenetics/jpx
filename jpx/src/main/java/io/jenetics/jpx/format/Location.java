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
import io.jenetics.jpx.Length;
import io.jenetics.jpx.Longitude;
import io.jenetics.jpx.Point;
import io.jenetics.jpx.WayPoint;

/**
 * Aggregation of the three location components: latitude, longitude and
 * elevation.
 *
 * @see Point
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Location {
	private final Latitude _latitude;
	private final Longitude _longitude;
	private final Length _elevation;

	private Location(
		final Latitude latitude,
		final Longitude longitude,
		final Length elevation
	) {
		_latitude = latitude;
		_longitude = longitude;
		_elevation = elevation;
	}

	/**
	 * Return the <em>latitude</em> of {@code this} location.
	 *
	 * @return the <em>latitude</em> of {@code this} location, or
	 *         {@link Optional#empty()} if not available
	 */
	public Optional<Latitude> latitude() {
		return Optional.ofNullable(_latitude);
	}

	/**
	 * Return the <em>longitude</em> of {@code this} location.
	 *
	 * @return the <em>longitude</em> of {@code this} location, or
	 *         {@link Optional#empty()} if not available
	 */
	public Optional<Longitude> longitude() {
		return Optional.ofNullable(_longitude);
	}

	/**
	 * Return the <em>elevation</em> of {@code this} location.
	 *
	 * @return the <em>elevation</em> of {@code this} location, or
	 *         {@link Optional#empty()} if not available
	 */
	public Optional<Length> elevation() {
		return Optional.ofNullable(_elevation);
	}

	/**
	 * Return a new {@link Point} from {@code this} location. If the
	 * {@link #latitude()} or the {@link #longitude()} is not given,
	 * {@link Optional#empty()} is returned
	 *
	 * @return a new {@link Point} if the latitude and longitude is given,
	 *         {@link Optional#empty()} otherwise
	 */
	public Optional<Point> toPoint() {
		return latitude().flatMap(lat ->
			longitude()
				.map(lon -> WayPoint.of(lat, lon, _elevation, null))
		);
	}

	/**
	 * Create a new location form the given GPS point.
	 *
	 * @param point the GPS point
	 * @return a new location form the given GPS point
	 * @throws NullPointerException if the given {@code point} is {@code null}
	 */
	public static Location of(final Point point) {
		requireNonNull(point);
		return of(
			point.getLatitude(),
			point.getLongitude(),
			point.getElevation().orElse(null)
		);
	}

	/**
	 * Create a new <em>location</em> object from the given {@code latitude},
	 * {@code longitude} and {@code elevation}.
	 *
	 * @param latitude the latitude of the location, maybe {@code null}
	 * @param longitude the longitude of the location, maybe {@code null}
	 * @param elevation the elevation if the location, maybe {@code null}
	 * @return a new location object from the given input data
	 */
	public static Location of(
		final Latitude latitude,
		final Longitude longitude,
		final Length elevation
	) {
		return new Location(latitude, longitude, elevation);
	}

	/**
	 * Create a new <em>location</em> object from the given {@code latitude}
	 * and {@code longitude}.
	 *
	 * @param latitude the latitude of the location, maybe {@code null}
	 * @param longitude the longitude of the location, maybe {@code null}
	 * @return a new location object from the given input data
	 */
	public static Location of(final Latitude latitude, final Longitude longitude) {
		return new Location(latitude, longitude, null);
	}

	/**
	 * Create a new <em>location</em> object from the given {@code latitude}.
	 *
	 * @param latitude the latitude of the location, maybe {@code null}
	 * @return a new location object from the given input data
	 */
	public static Location of(final Latitude latitude) {
		return new Location(latitude, null, null);
	}

	/**
	 * Create a new <em>location</em> object from the given {@code longitude}.
	 *
	 * @param longitude the longitude of the location, maybe {@code null}
	 * @return a new location object from the given input data
	 */
	public static Location of(final Longitude longitude) {
		return new Location(null, longitude, null);
	}

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
				case 'E':
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
}

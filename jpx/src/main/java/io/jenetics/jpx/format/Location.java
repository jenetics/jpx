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

import static java.lang.Math.abs;
import static java.lang.Math.floor;
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
 * @version 1.4
 * @since 1.4
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
			longitude().map(lon ->
				WayPoint.of(lat, lon, _elevation, null)
			)
		);
	}

	@Override
	public String toString() {
		return format(
			"[lat=%s, lon=%s, ele=%s]",
			_latitude, _longitude, _elevation
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
	 * Create a new <em>location</em> object from the given {@code elevation}.
	 *
	 * @param elevation the elevation of the location, maybe {@code null}
	 * @return a new location object from the given input data
	 */
	public static Location of(final Length elevation) {
		return new Location(null, null, elevation);
	}


	/* *************************************************************************
	 * Inner classes.
	 * ************************************************************************/

	/**
	 * Represents one of the existing location fields: latitude, longitude and
	 * elevation.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
	 * @version 1.4
	 * @since 1.4
	 */
	public enum Field {

		/**
		 * This field allows to access the latitude value of a given location
		 * object. The latitude value is returned in degrees.
		 */
		LATITUDE(
			"latitude", 'L',
			loc -> loc.latitude().map(Latitude::toDegrees)
		),

		/**
		 * This field allows to access the absolute value of the latitude
		 * degrees of a given location. If you need to extract the signed
		 * latitude degrees, use {@link #LATITUDE} instead.
		 */
		DEGREE_OF_LATITUDE(
			"latitude", 'D',
			loc -> loc.latitude()
				.map(Latitude::toDegrees)
				.map(Field::toDegrees)
		),

		/**
		 * This field allows to access the absolute value of the minute part of
		 * the latitude of a given location.
		 */
		MINUTE_OF_LATITUDE(
			"latitude", 'M',
			loc -> loc.latitude()
				.map(Latitude::toDegrees)
				.map(Field::toMinutes)
		),

		/**
		 * This field allows to access the absolute value of the second part of
		 * the latitude of a given location.
		 */
		SECOND_OF_LATITUDE(
			"latitude", 'S',
			loc -> loc.latitude()
				.map(Latitude::toDegrees)
				.map(Field::toSeconds)
		),

		/**
		 * This field allows to access the longitude value of a given location
		 * object. The longitude value is returned in degrees.
		 */
		LONGITUDE(
			"longitude", 'l',
			loc -> loc.longitude().map(Longitude::toDegrees)
		),

		/**
		 * This field allows to access the absolute value of the longitude
		 * degrees of a given location. If you need to extract the signed
		 * longitude degrees, use {@link #LONGITUDE} instead.
		 */
		DEGREE_OF_LONGITUDE(
			"longitude", 'd',
			loc -> loc.longitude()
				.map(Longitude::toDegrees)
				.map(Field::toDegrees)
		),

		/**
		 * This field allows to access the absolute value of the minute part of
		 * the longitude of a given location.
		 */
		MINUTE_OF_LONGITUDE(
			"longitude", 'm',
			loc -> loc.longitude()
				.map(Longitude::toDegrees)
				.map(Field::toMinutes)
		),

		/**
		 * This field allows to access the absolute value of the second part of
		 * the longitude of a given location.
		 */
		SECOND_OF_LONGITUDE(
			"longitude", 's',
			loc -> loc.longitude()
				.map(Longitude::toDegrees)
				.map(Field::toSeconds)
		),

		/**
		 * This field allows to access the elevation (in meter) of a given
		 * location.
		 */
		ELEVATION(
			"elevation", 'E',
			loc -> loc.elevation().map(l -> l.to(METER))
		),

		/**
		 * This field allows to access the absolute elevation (in meter) of a
		 * given location.
		 */
		METER_OF_ELEVATION(
			"elevation", 'H',
			loc -> loc.elevation().map(l -> abs(l.to(METER)))
		);

		private final String _name;
		private final char _type;
		private final Function<Location, Optional<Double>> _accessor;

		Field(
			final String name,
			final char type,
			final Function<Location, Optional<Double>> accessor
		) {
			_name = requireNonNull(name);
			_type = type;
			_accessor = requireNonNull(accessor);
		}

		/**
		 * Return the name of the location field.
		 *
		 * @return the name of the location field
		 */
		String fieldName() {
			return _name;
		}

		char type() {
			return _type;
		}

		/**
		 * Extracts the (double) value from the given location field.
		 *
		 * @param location the location
		 * @return the value of the location field
		 */
		Optional<Double> apply(final Location location) {
			return _accessor.apply(requireNonNull(location));
		}

		private static double toDegrees(final double degrees) {
			return abs(degrees);
		}

		private static double toMinutes(final double degrees) {
			final double dd = abs(degrees);
			return (dd - floor(dd))*60.0;
		}

		private static double toSeconds(final double degrees) {
			final double dd = abs(degrees);
			final double d = floor(dd);
			final double m = floor((dd - d)*60.0);
			return (dd - d - m/60.0)*3600.0;
		}

		static Optional<Field> ofPattern(final String pattern) {
			for (int i = 0; i < pattern.length(); ++i) {
				for (Field field : Field.values()) {
					if (field.type() == pattern.charAt(i)) {
						return Optional.of(field);
					}
				}
			}

			return Optional.empty();
		}

		String toDecimalPattern(final String pattern) {
			return pattern.replace(type(), '0');
		}

		boolean isLatitude() {
			return this == LATITUDE ||
				this == DEGREE_OF_LATITUDE ||
				this == MINUTE_OF_LATITUDE ||
				this == SECOND_OF_LATITUDE;
		}

		boolean isLongitude() {
			return this == LONGITUDE ||
				this == DEGREE_OF_LONGITUDE ||
				this == MINUTE_OF_LONGITUDE ||
				this == SECOND_OF_LONGITUDE;
		}

		boolean isElevation() {
			return this == ELEVATION || this == METER_OF_ELEVATION;
		}

	}

}

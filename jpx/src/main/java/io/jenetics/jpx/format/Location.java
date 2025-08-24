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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;

import io.jenetics.jpx.Latitude;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.Longitude;
import io.jenetics.jpx.Point;
import io.jenetics.jpx.WayPoint;

/**
 * Aggregation of the three location parts: latitude, longitude and elevation.
 *
 * @see Point
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 2.2
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
	public int hashCode(){
		return Objects.hash(_latitude, _longitude, _elevation);
	}

	@Override
	public boolean equals(final Object other) {
		return other == this ||
			other instanceof Location loc &&
			Objects.equals(_latitude, loc._latitude) &&
			Objects.equals(_longitude, loc._longitude) &&
			Objects.equals(_elevation, loc._elevation);
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
	 * @return a new location from the given GPS point
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

}

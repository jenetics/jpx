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
package io.jenetics.jpx;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Optional;

import io.jenetics.jpx.geom.Geoid;

/**
 * A geographic point with optional elevation and time.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.6
 * @since 1.0
 */
public interface Point {

	/**
	 * The latitude of the point, WGS84 datum.
	 *
	 * @return the latitude of the point
	 */
	Latitude getLatitude();

	/**
	 * The longitude of the point, WGS84 datum.
	 *
	 * @return the longitude of the point
	 */
	Longitude getLongitude();

	/**
	 * The elevation (in meters) of the point.
	 *
	 * @return the elevation (in meters) of the point
	 */
	default Optional<Length> getElevation() {
		return Optional.empty();
	}

	/**
	 * Creation/modification timestamp for the point.
	 *
	 * @see #getInstant()
	 *
	 * @return creation/modification timestamp for the point
	 */
	default Optional<ZonedDateTime> getTime() {
		return Optional.empty();
	}

	/**
	 * Creation/modification instant of the given point.
	 *
	 * @implNote
	 * The default implementation of this method forwards the call to the
	 * {@link #getTime()} method. If you want to override this method, do it via
	 * the {@link #getTime()}. Otherwise, you have to re-implement this method as
	 * well.
	 *
	 * @since 1.6
	 *
	 * @see #getTime()
	 *
	 * @return creation/modification instant for the point
	 */
	default Optional<Instant> getInstant() {
		return getTime().map(t -> t.toOffsetDateTime().toInstant());
	}

	/**
	 * Calculate the distance between points on the default ellipsoidal earth
	 * model
	 * <a href="https://en.wikipedia.org/wiki/World_Geodetic_System#A_new_World_Geodetic_System:_WGS_84">
	 * WGS-84</a>.
	 *
	 * @see <a href="http://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf">DIRECT AND
	 *               INVERSE SOLUTIONS OF GEODESICS 0 THE ELLIPSOID
	 *               WITH APPLICATION OF NESTED EQUATIONS</a>
	 * @see <a href="http://www.movable-type.co.uk/scripts/latlong-vincenty.html">
	 *     Vincenty solutions of geodesics on the ellipsoid</a>
	 *
	 * @param end the end point
	 * @return the distance between {@code this} and {@code end} in meters
	 * @throws NullPointerException if the {@code end} point is {@code null}
	 */
	default Length distance(final Point end) {
		return Geoid.DEFAULT.distance(this, end);
	}

}

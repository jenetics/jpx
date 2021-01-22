/*
 * Java GPX Library (@__identifier__@).
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
 */
package io.jenetics.jpx.format;

import io.jenetics.jpx.Latitude;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.Longitude;

/**
 * @version 2.2
 * @since 2.2
 */
final class LocationBuilder {

	private int latitudeSign = +1;
	private Double latitude = null; // degrees
	private int longitudeSign = +1;
	private Double longitude = null; // degrees
	private Double elevation = null; // meters
	private final Length.Unit elevationUnit = Length.Unit.METER;

	LocationBuilder copy() {
		LocationBuilder c = new LocationBuilder();
		c.latitudeSign = latitudeSign;
		c.latitude = latitude;
		c.longitudeSign = longitudeSign;
		c.longitude = longitude;
		c.elevation = elevation;
		return c;
	}

	void copy(final LocationBuilder from) {
		latitudeSign = from.latitudeSign;
		latitude = from.latitude;
		longitudeSign = from.longitudeSign;
		longitude = from.longitude;
		elevation = from.elevation;
	}

	void setLatitudeSign(final int i) {
		latitudeSign = i;
	}

	void addLatitude(final double d) {
		if (latitude == null) {
			latitude = 0.0;
		}
		latitude += d;
	}

	void addLatitudeMinute(final double d) {
		addLatitude(d/60.0);
	}

	void addLatitudeSecond(final double d) {
		addLatitude( d/3600.0);
	}

	void setLongitudeSign(final int i) {
		longitudeSign = i;
	}

	void addLongitude(final double d) {
		if (longitude == null) {
			longitude = 0.0;
		}
		longitude += d;
	}

	void addLongitudeMinute(final double d) {
		addLongitude(d/60.0);
	}

	void addLongitudeSecond(final double d) {
		addLongitude(d/3600.0);
	}

	void setElevation(final double d) {
		elevation = d;
	}

	Location build() {
		final var lat = latitude == null
			? null
			: Latitude.ofDegrees(latitudeSign*latitude);
		final var lon = longitude == null
			? null
			: Longitude.ofDegrees(longitudeSign*longitude);
		final var ele = elevation == null
			? null
			: Length.of(elevation, elevationUnit);

		return Location.of(lat, lon, ele);
	}

}

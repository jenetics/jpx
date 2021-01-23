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

	private Double _latitude = null;
	private int _latitudeSign = +1;

	private Double _longitude = null;
	private int _longitudeSign = +1;

	private Double elevation = null;

	LocationBuilder copy() {
		LocationBuilder c = new LocationBuilder();
		c._latitudeSign = _latitudeSign;
		c._latitude = _latitude;
		c._longitudeSign = _longitudeSign;
		c._longitude = _longitude;
		c.elevation = elevation;
		return c;
	}

	void copy(final LocationBuilder from) {
		_latitudeSign = from._latitudeSign;
		_latitude = from._latitude;
		_longitudeSign = from._longitudeSign;
		_longitude = from._longitude;
		elevation = from.elevation;
	}

	void setLatitudeSign(final int sign) {
		_latitudeSign = sign;
	}

	void addLatitude(final double degrees) {
		if (degrees < 0.0) {
			_latitudeSign = -1;
		}
		if (_latitude == null) {
			_latitude = 0.0;
		}
		_latitude += Math.abs(degrees);
	}

	void addLatitudeMinute(final double minutes) {
		addLatitude(minutes/60.0);
	}

	void addLatitudeSecond(final double seconds) {
		addLatitude( seconds/3600.0);
	}

	void setLongitudeSign(final int sign) {
		_longitudeSign = sign;
	}

	void addLongitude(final double degrees) {
		if (degrees < 0.0) {
			_longitudeSign = -1;
		}
		if (_longitude == null) {
			_longitude = 0.0;
		}
		_longitude += Math.abs(degrees);
	}

	void addLongitudeMinute(final double minutes) {
		addLongitude(minutes/60.0);
	}

	void addLongitudeSecond(final double seconds) {
		addLongitude(seconds/3600.0);
	}

	void setElevation(final double meters) {
		elevation = meters;
	}

	Location build() {
		final var lat = _latitude == null
			? null
			: Latitude.ofDegrees(_latitudeSign*_latitude);
		final var lon = _longitude == null
			? null
			: Longitude.ofDegrees(_longitudeSign*_longitude);
		final var ele = elevation == null
			? null
			: Length.of(elevation, Length.Unit.METER);

		return Location.of(lat, lon, ele);
	}

}

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

import static io.jenetics.jpx.Length.Unit.METER;

import io.jenetics.jpx.Latitude;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.Longitude;

abstract class Fixture {

	protected LocationFormatter f;

	protected Location latitude(double d) {
		return Location.of(Latitude.ofDegrees(d));
	}

	protected Location latitude(double d, double m) {
		return latitude(d + m / 60.0);
	}

	protected Location latitude(double d, double m, double s) {
		return latitude(d + m / 60.0 + s / 3600.0);
	}

	protected Location longitude(double d) {
		return Location.of(Longitude.ofDegrees(d));
	}

	protected Location longitude(double d, double m) {
		return longitude(d + m / 60.0);
	}

	protected Location longitude(double d, double m, double s) {
		return longitude(d + m / 60.0 + s / 3600.0);
	}

	protected Location elevation(double e) {
		return Location.of(Length.of(e, METER));
	}

	protected Location location(double lat, double lon, double ele) {
		return Location.of(Latitude.ofDegrees(lat), Longitude.ofDegrees(lon), Length.of(ele, METER));
	}

}

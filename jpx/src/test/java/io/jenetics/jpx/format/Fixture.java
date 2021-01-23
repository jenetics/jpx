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

	protected Location latitude(final double d) {
		return Location.of(Latitude.ofDegrees(d));
	}

	protected Location latitude(final double d, final double m) {
		final var sign = d >= 0.0 ? 1 : -1;
		return latitude((Math.abs(d) + m/60.0)*sign);
	}

	protected Location latitude(final double d, final double m, final double s) {
		final var sign = d >= 0.0 ? 1 : -1;
		return latitude((Math.abs(d) + m/60.0 + s/3600.0)*sign);
	}

	protected Location longitude(final double d) {
		return Location.of(Longitude.ofDegrees(d));
	}

	protected Location longitude(final double d, final double m) {
		final var sign = d >= 0.0 ? 1 : -1;
		return longitude((Math.abs(d) + m/60.0*sign));
	}

	protected Location longitude(final double d, final double m, final double s) {
		final var sign = d >= 0.0 ? 1 : -1;
		return longitude((Math.abs(d) + m/60.0 + s/3600.0)*sign);
	}

	protected Location elevation(final double e) {
		return Location.of(Length.of(e, METER));
	}

	protected Location location(final double lat, final double lon, final double ele) {
		return Location.of(
			Latitude.ofDegrees(lat),
			Longitude.ofDegrees(lon),
			Length.of(ele, METER)
		);
	}

}

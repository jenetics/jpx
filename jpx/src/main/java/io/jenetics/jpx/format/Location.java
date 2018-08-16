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

import java.util.Optional;

import io.jenetics.jpx.Latitude;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.Longitude;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class Location {
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

	Optional<Latitude> latitude() {
		return Optional.ofNullable(_latitude);
	}

	Optional<Longitude> longitude() {
		return Optional.ofNullable(_longitude);
	}

	Optional<Length> elevation() {
		return Optional.ofNullable(_elevation);
	}

	static Location of(
		final Latitude latitude,
		final Longitude longitude,
		final Length elevation
	) {
		return new Location(latitude, longitude, elevation);
	}

}

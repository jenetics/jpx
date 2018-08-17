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

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.jenetics.jpx.Latitude;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.Longitude;

/**
 * DD°MM''SS.SSS"X
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class LocationFormatter {

	private final List<Format<Location>> _formats;

	public LocationFormatter(final List<Format<Location>> formats) {
		_formats = requireNonNull(formats);
	}

	public LocationFormatter() {
		_formats = new ArrayList<>();
	}

	String format(final Location location) {
		requireNonNull(location);

		return _formats.stream()
			.map(format -> format.format(location))
			.collect(Collectors.joining());
	}


	public static LocationFormatter ofPattern(final String pattern) {
		final StringBuilder out = new StringBuilder();

		for (int i = 0; i < pattern.length(); ++i) {
			final char c = pattern.charAt(i);
			switch (c) {
				case 'D':
				case 'M':
				case 'S':
				case 'd':
				case 'm':
				case 's':
				case 'H':
				case '.':
				case ',':
				default:
			}
		}

		return null;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private final List<Format<Location>> _formats = new ArrayList<>();

		private Builder() {
		}

		public Builder appendLiteral(final String literal) {
			_formats.add(ConstFormat.of(literal));
			return this;
		}

		public Builder appendFieldFormat(final String pattern) {
			_formats.add(LocationFieldFormat.ofPattern(pattern));
			return this;
		}

		public LocationFormatter build() {
			return new LocationFormatter(new ArrayList<>(_formats));
		}

	}


	public static final LocationFormatter ISO_HUMAN_LONG = builder()
		.appendFieldFormat("DD")
		.appendLiteral("°")
		.appendFieldFormat("MM")
		.appendLiteral("'")
		.appendFieldFormat("SS")
		.appendLiteral("\"")
		.build();


	public String format(final Latitude lat, final Longitude lon, final Length ele) {
		return format(Location.of(lat, lon, ele));
	}

	public String format(final Latitude lat, final Longitude lon) {
		return format(lat, lon, null);
	}

	public String format(final Latitude lat) {
		return format(lat, null, null);
	}

	public String format(final Longitude lon) {
		return format(null, lon, null);
	}

	public String format(final Length ele) {
		return format(null, null, ele);
	}

}

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

import static java.lang.String.format;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Year;

/**
 * Some helper methods for parsing GPS values.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.2
 * @since 1.0
 */
final class Format {

	private Format() {
	}

	static Double parseDouble(final String value) {
		final String d = Strings.trim(value);
		return d != null ? Double.parseDouble(d) : null;
	}

	/**
	 * Convert the given {@code object} into a duration value. If the
	 * {@code object} is {@code null}, {@code null} is returned.
	 *
	 * @param value the object to convert
	 * @return the converted object
	 */
	static Duration parseDuration(final String value) {
		final String duration = Strings.trim(value);
		return duration != null
			? Duration.ofSeconds(Long.parseLong(duration))
			: null;
	}

	static String durationString(final Duration duration) {
		return duration != null ? Long.toString(duration.getSeconds()) : null;
	}

	/**
	 * Convert the given {@code object} into a year value. If the
	 * {@code object} is {@code null}, {@code null} is returned.
	 *
	 * @param value the string value to parse
	 * @return the converted object
	 */
	static Year parseYear(final String value) {
		final String year = Strings.trim(value);
		return year != null ? Year.of(Integer.parseInt(year)) : null;
	}

	static String yearString(final Year year) {
		return year != null ? Integer.toString(year.getValue()) : null;
	}

	/**
	 * Convert the given {@code object} into a URI value. If the
	 * {@code object} is {@code null}, {@code null} is returned.
	 *
	 * @param value the string to convert
	 * @return the converted object
	 */
	static URI parseURI(final String value) {
		try {
			final String uri = Strings.trim(value);
			return uri != null ? new URI(uri) : null;
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(format(
				"Invalid URI value: '%s'.", value
			));
		}
	}

	static String uriString(final URI uri) {
		return uri != null ? uri.toString() : null;
	}

	static String doubleString(final Number number) {
		return number != null ? Double.toString(number.doubleValue()) : null;
	}

	static String intString(final Number number) {
		return number != null ? Integer.toString(number.intValue()) : null;
	}

}

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
 * @version !__version__!
 * @since 1.0
 */
final class Format {

	private Format() {
	}

	/**
	 * Convert the given {@code object} into a duration value. If the
	 * {@code object} is {@code null}, {@code null} is returned.
	 *
	 * @param string the object to convert
	 * @return the converted object
	 */
	static Duration parseDuration(final String string) {
		return string != null
			? Duration.ofSeconds(Long.parseLong(string))
			: null;
	}

	static String durationString(final Duration duration) {
		return duration != null
			? Long.toString(duration.getSeconds())
			: null;
	}

	/**
	 * Convert the given {@code object} into a year value. If the
	 * {@code object} is {@code null}, {@code null} is returned.
	 *
	 * @param string the string value to parse
	 * @return the converted object
	 */
	static Year parseYear(final String string) {
		return string != null ? Year.of(Integer.parseInt(string)) : null;
	}

	static String yearString(final Year year) {
		return year != null ? Integer.toString(year.getValue()) : null;
	}

	/**
	 * Convert the given {@code object} into a URI value. If the
	 * {@code object} is {@code null}, {@code null} is returned.
	 *
	 * @param string the string to convert
	 * @return the converted object
	 */
	static URI parseURI(final String string) {
		try {
			return string != null ? new URI(string) : null;
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(format(
				"Invalid URI value: %s.", string
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

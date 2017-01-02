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
import java.util.function.Function;

import javax.xml.stream.XMLStreamException;

/**
 * Some helper methods for parsing GPS values.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 1.0
 * @since 1.0
 */
final class Parsers {

	private Parsers() {
	}

	static <T> T parse(final Object object, final Function<Object, T> parser) {
		return object != null
			? parser.apply(object)
			: null;
	}

	/**
	 * Convert the given object to a string. The {@link Object#toString()}
	 * method is used for converting the string. If the object is {@code null}
	 * or the string {@code isEmpty}, {@code null} is returned.
	 *
	 * @param object the object to convert
	 * @return the given object as string, or {@code null} if the object is
	 *         {@code null} or the converted string is empty
	 */
	static String toString(final Object object) {
		String string = null;
		if (object != null && !object.toString().isEmpty()) {
			string = object.toString();
		}

		return string;
	}

	static String toMandatoryString(final Object object, final String property)
		throws XMLStreamException
	{
		final String value = toString(object);
		if (value == null) {
			throw new XMLStreamException(
				format("Empty or null string for '%s'.", property)
			);
		}

		return value;
	}

	/**
	 * Convert the given {@code object} into a double value. If the
	 * {@code object} is {@code null}, {@code null} is returned.
	 *
	 * @param object the object to convert
	 * @param property the property name of the object. Needed for error message.
	 * @return the converted object
	 * @throws XMLStreamException if the object doesn't represent a valid double
	 *         value
	 */
	static Double toDouble(final Object object, final String property)
		throws XMLStreamException
	{
		Double value = null;
		if (object instanceof Number) {
			value = ((Number)object).doubleValue();
		} else {
			final String string = toString(object);
			if (string != null) {
				try {
					value = Double.valueOf(string);
				} catch (NumberFormatException e) {
					throw new XMLStreamException(
						format("Invalid value for '%s': %s.", property, string)
					);
				}
			}
		}

		return value;
	}

	/**
	 * Convert the given {@code object} into a double value. If the
	 * {@code object} is {@code null} a {@link XMLStreamException} is thrown
	 *
	 * @param object the object to convert
	 * @param property the property name of the object. Needed for error message.
	 * @return the converted object
	 * @throws XMLStreamException if the object doesn't represent a valid double
	 *         value or the object is {@code null}
	 */
	static Double toMandatoryDouble(final Object object, final String property)
		throws XMLStreamException
	{
		final Double value = toDouble(object, property);
		if (value == null) {
			throw new XMLStreamException(
				format("Property '%s' is mandatory.", property)
			);
		}

		return value;
	}

	/**
	 * Convert the given {@code object} into an int value. If the
	 * {@code object} is {@code null}, {@code null} is returned.
	 *
	 * @param object the object to convert
	 * @param property the property name of the object. Needed for error message.
	 * @return the converted object
	 * @throws XMLStreamException if the object doesn't represent a valid int
	 *         value
	 */
	static Integer toInt(final Object object, final String property)
		throws XMLStreamException
	{
		Integer value = null;
		if (object instanceof Number) {
			value = ((Number)object).intValue();
		} else {
			final String string = toString(object);
			if (string != null) {
				try {
					value = Integer.valueOf(string);
				} catch (NumberFormatException e) {
					throw new XMLStreamException(
						format("Invalid value for '%s': %s.", property, string)
					);
				}
			}
		}

		return value;
	}

	/**
	 * Convert the given {@code object} into a long value. If the
	 * {@code object} is {@code null}, {@code null} is returned.
	 *
	 * @param object the object to convert
	 * @param property the property name of the object. Needed for error message.
	 * @return the converted object
	 * @throws XMLStreamException if the object doesn't represent a valid long
	 *         value
	 */
	static Long toLong(final Object object, final String property)
		throws XMLStreamException
	{
		Long value = null;
		if (object instanceof Number) {
			value = ((Number)object).longValue();
		} else {
			final String string = toString(object);
			if (string != null) {
				try {
					value = Long.valueOf(string);
				} catch (NumberFormatException e) {
					throw new XMLStreamException(
						format("Invalid value for '%s': %s.", property, string)
					);
				}
			}
		}

		return value;
	}

	/**
	 * Convert the given {@code object} into a duration value. If the
	 * {@code object} is {@code null}, {@code null} is returned.
	 *
	 * @param object the object to convert
	 * @param property the property name of the object. Needed for error message.
	 * @return the converted object
	 * @throws XMLStreamException if the object doesn't represent a valid duration
	 *         value
	 */
	static Duration toDuration(final Object object, final String property)
		throws XMLStreamException
	{
		Duration duration = null;
		final Long value = toLong(object, property);
		if (value != null) {
			duration = Duration.ofSeconds(value);
		}

		return duration;
	}

	/**
	 * Convert the given {@code object} into a year value. If the
	 * {@code object} is {@code null}, {@code null} is returned.
	 *
	 * @param object the object to convert
	 * @param property the property name of the object. Needed for error message.
	 * @return the converted object
	 * @throws XMLStreamException if the object doesn't represent a valid year
	 *         value
	 */
	static Year parseYear(final Object object, final String property)
		throws XMLStreamException
	{
		Year year = null;
		final Integer value = toInt(object, property);
		if (value != null) {
			year = Year.of(value);
		}

		return year;
	}

	/**
	 * Convert the given {@code object} into a URI value. If the
	 * {@code object} is {@code null}, {@code null} is returned.
	 *
	 * @param object the object to convert
	 * @param property the property name of the object. Needed for error message.
	 * @return the converted object
	 * @throws XMLStreamException if the object doesn't represent a valid URI
	 *         value
	 */
	static URI toURI(final Object object, final String property)
		throws XMLStreamException
	{
		URI uri = null;
		final String value = toString(object);
		if (value != null) {
			try {
				uri = new URI(value);
			} catch (URISyntaxException e) {
				throw new XMLStreamException(format(
					"Invalid URI value for '%s': %s.", property, object
				));
			}
		}

		return uri;
	}

	/**
	 * Convert the given {@code object} into a URI value. If the
	 * {@code object} is {@code null}, {@code null} is returned.
	 *
	 * @param object the object to convert
	 * @param property the property name of the object. Needed for error message.
	 * @return the converted object
	 * @throws XMLStreamException if the object doesn't represent a valid URI
	 *         value
	 */
	static URI toMandatoryURI(final Object object, final String property)
		throws XMLStreamException
	{
		final URI uri = toURI(object, property);
		if (uri == null) {
			throw new XMLStreamException(
				format("Property '%s' is mandatory.", property)
			);
		}

		return uri;
	}

}

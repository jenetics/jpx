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
import java.time.ZonedDateTime;

import javax.xml.stream.XMLStreamException;

import io.jenetics.jpx.Length.Unit;

/**
 * Some helper methods for parsing GPS values.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.0
 * @since 1.0
 */
final class Parsers {

	private Parsers() {
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

	/**
	 * Convert the given object into a non-null string.
	 *
	 * @param object the object to convert
	 * @return the given object as string
	 */
	static String toMandatoryString(final Object object, final String property) {
		final String value = toString(object);
		if (value == null) {
			throw new NullPointerException(
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
	 */
	static Double toDouble(final Object object, final String property) {
		Double value = null;
		if (object instanceof Number) {
			value = ((Number)object).doubleValue();
		} else {
			final String string = toString(object);
			if (string != null) {
				try {
					value = Double.valueOf(string);
				} catch (NumberFormatException e) {
					throw new NumberFormatException(
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
	 */
	private static Double toMandatoryDouble(
		final Object object,
		final String property
	) {
		final Double value = toDouble(object, property);
		if (value == null) {
			throw new NullPointerException(
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
	 */
	private static Integer toInteger(final Object object, final String property) {
		Integer value = null;
		if (object instanceof Number) {
			value = ((Number)object).intValue();
		} else {
			final String string = toString(object);
			if (string != null) {
				try {
					value = Integer.valueOf(string);
				} catch (NumberFormatException e) {
					throw new NumberFormatException(
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
	 */
	private static Long toLong(final Object object, final String property) {
		Long value = null;
		if (object instanceof Number) {
			value = ((Number)object).longValue();
		} else {
			final String string = toString(object);
			if (string != null) {
				try {
					value = Long.valueOf(string);
				} catch (NumberFormatException e) {
					throw new NumberFormatException(
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
	 */
	static Duration toDuration(final Object object, final String property) {
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
	 */
	static Year toYear(final Object object, final String property) {
		Year year = null;
		final Integer value = toInteger(object, property);
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
	 */
	static URI toURI(final Object object, final String property) {
		URI uri = null;
		final String value = toString(object);
		if (value != null) {
			try {
				uri = new URI(value);
			} catch (URISyntaxException e) {
				throw new IllegalArgumentException(format(
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
	 */
	static URI toMandatoryURI(final Object object, final String property) {
		final URI uri = toURI(object, property);
		if (uri == null) {
			throw new NullPointerException(
				format("Property '%s' is mandatory.", property)
			);
		}

		return uri;
	}

	/**
	 * Parses the given object.
	 *
	 * @param object the object to convert
	 * @param property the property name of the object. Needed for error message.
	 * @return the converted object
	 */
	static Degrees toDegrees(final Object object, final String property) {
		Degrees degrees = null;
		final Double value = toDouble(object, property);
		if (value != null) {
			if (value < 0 || value >= 360) {
				throw new IllegalArgumentException(format(
					"%f not in the range [0, 360) for %s.", value, property
				));
			}

			degrees = Degrees.ofDegrees(value);
		}

		return degrees;
	}

	/**
	 * Parses the given object.
	 *
	 * @param object the object to parse
	 * @return the parsed object
	 */
	static DGPSStation toDGPSStation(final Object object, final String property) {
		DGPSStation station = null;
		final Integer value = toInteger(object, property);
		if (value != null) {
			if (value < 0 || value > 1023) {
				throw new IllegalArgumentException(format(
					"%d is out of range [0, 1023] for '%s'.", value, property
				));
			}

			station = DGPSStation.of(value);
		}

		return station;
	}

	/**
	 * Parses the given object.
	 *
	 * @param object the object to convert
	 * @param property the property name of the object. Needed for error message.
	 * @return the converted object
	 */
	static Fix toFix(final Object object, final String property) {
		final String value = toString(object);
		return value != null
			? Fix.ofName(value).orElseThrow(() -> new IllegalArgumentException(format(
				"Invalid value for '%s': %s.", property, value)))
			: null;
	}

	/**
	 * Try to parse the given object into a {@code Latitude} object.
	 *
	 * @param object the object to convert
	 * @param property the property name of the object. Needed for error message.
	 * @return the converted object
	 */
	static Latitude toLatitude(final Object object, final String property) {
		final double value = toMandatoryDouble(object, property);
		if (value < -90 || value > 90) {
			throw new IllegalArgumentException(format(
				"%f is not in range [-90, 90] for '%s'.", value, property
			));
		}

		return Latitude.ofDegrees(value);
	}

	/**
	 * Parses the given object.
	 *
	 * @param object the object to convert
	 * @param property the property name of the object. Needed for error message.
	 * @return the converted object
	 */
	static Length toLength(final Object object, final String property) {
		final Double value = toDouble(object, property);
		return value != null ? Length.of(value, Unit.METER) : null;
	}

	/**
	 * Try to parse the given object into a {@code Longitude} object. If the
	 * given {@code object} is {@code null}, {@code null} is returned.
	 *
	 * @param object the object to parse
	 * @return the parsed object, or {@code null} if the argument is {@code null}
	 */
	static Longitude toLongitude(final Object object, final String property)
	{
		final double value = toMandatoryDouble(object, property);
		if (value < -180 || value > 180) {
			throw new IllegalArgumentException(format(
				"%f is not in range [-180, 180] for '%s'.", value, property
			));
		}

		return Longitude.ofDegrees(value);
	}

	/**
	 * Parses the given object.
	 *
	 * @param object the object to convert
	 * @param property the property name of the object. Needed for error message.
	 * @return the converted object
	 */
	static Speed toSpeed(final Object object, final String property) {
		Speed speed = null;
		final Double value = toDouble(object, property);
		if (value != null) {
			speed = Speed.of(value, Speed.Unit.METERS_PER_SECOND);
		}

		return speed;
	}

	/**
	 * Parses the given object.
	 *
	 * @param object the object to convert
	 * @param property the property name of the object. Needed for error message.
	 * @return the converted object
	 */
	static UInt toUInt(final Object object, final String property) {
		UInt uint = null;
		final Integer value = toInteger(object, property);
		if (value != null) {
			if (value < 0) {
				throw new IllegalArgumentException(
					format("Invalid value for '%s': %s.", property, object)
				);
			}

			uint = UInt.of(value);
		}

		return uint;
	}

	/**
	 * Parses the given object to a zoned data time object.
	 *
	 * @param time the string to parse
	 * @return the parsed object
	 */
	static ZonedDateTime toZonedDateTime(final String time) {
		return time != null
			? ZonedDateTimeFormat.parseOptional(time).orElseThrow(() ->
				new IllegalArgumentException(
					format("Can't parse time: %s'", time)))
			: null;
	}
}

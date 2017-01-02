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
import static java.util.Objects.requireNonNull;

import java.util.Optional;

import javax.xml.stream.XMLStreamException;

/**
 * Type of GPS fix. {@code none} means GPS had no fix. To signify "the fix info
 * is unknown, leave out {@code Fix} entirely. {@code pps} = military signal
 * used.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.0
 * @since 1.0
 */
public enum Fix {

	NONE("none"),
	DIM_2("2d"),
	DIM_3("3d"),
	DGPS("dgps"),
	PPS("pps");

	private final String _value;

	Fix(final String value) {
		_value = requireNonNull(value);
	}

	/**
	 * Return the string representation of the GPS {@code Fix}. {@code none},
	 * {@code 2d}. {@code 3d}, {@code dgps} or {@code pps}.
	 *
	 * @return the string representation of the GPS {@code Fix}
	 */
	public String getValue() {
		return _value;
	}

	/**
	 * Return the {@code Fix} constant for the given fix {@code value}.
	 *
	 * @param name the GPS fix names
	 * @return the GPS fix for the given value, or {@code Optional.empty()} if
	 *         the given {@code name} is invalid
	 */
	public static Optional<Fix> ofName(final String name) {
		switch (name) {
			case "none": return Optional.of(Fix.NONE);
			case "2d": return Optional.of(Fix.DIM_2);
			case "3d": return Optional.of(Fix.DIM_3);
			case "dgps": return Optional.of(Fix.DGPS);
			case "pps": return Optional.of(Fix.PPS);
			default: return Optional.empty();
		}
	}

	/**
	 * Parses the given object.
	 *
	 * @param object the object to convert
	 * @param property the property name of the object. Needed for error message.
	 * @return the converted object
	 * @throws XMLStreamException if the object doesn't represent a valid double
	 *         value
	 */
	static Fix parse(final Object object, final String property)
		throws XMLStreamException
	{
		final String value = Parsers.toString(object);
		return value != null
			? ofName(value).orElseThrow(() -> new XMLStreamException(format(
				"Invalid value for '%s': %s.", property, value)))
			: null;
	}

}

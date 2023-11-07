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
package io.jenetics.jpx.format;

import java.text.ParsePosition;
import java.util.Optional;

/**
 * Base interface for formatting and parsing a location.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since 1.4
 */
sealed interface Format
	permits
		CompositeFormat,
		ConstFormat,
		Field,
		LatitudeNS,
		LongitudeEW,
		OptionalFormat,
		Plus
{

	/**
	 * Formats the given {@code value} to its string representation. If it is not
	 * possible to convert the {@code value} to a string, {@link Optional#empty()}
	 * is returned.
	 *
	 * @param value the value which is converted to a string.
	 * @return the converted value, or {@link Optional#empty()} if the format
	 *         fails
	 */
	Optional<String> format(final Location value);

	/**
	 * Parses the given input value, {@code in}.
	 *
	 * @param in the input string to parse
	 * @param pos the current parse position
	 * @param builder the location builder
	 * @throws ParseException it the parsing fails
	 * @throws NullPointerException if one of the given parameters is {@code null}
	 */
	void parse(
		final CharSequence in,
		final ParsePosition pos,
		final LocationBuilder builder
	);

	/**
	 * Return a string representation of the format pattern.
	 *
	 * @return a string representation of the format pattern
	 */
	String toPattern();

}

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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.jenetics.jpx.format.Location.Field;

/**
 * Formatter for printing and parsing geographic location objects.
 * <h3 id="patterns">Patterns for Formatting and Parsing</h3>
 * Patterns are based on a simple sequence of letters and symbols. A pattern is
 * used to create a Formatter using the {@code #ofPattern(String)} and
 * {@code #ofPattern(String, Locale)} methods.
 * For example, {@code DD°MM'SS.SSS"X} will format to, for example,
 * {@code 60°15'59.613"N}. A formatter created from a pattern can be used as
 * many times as necessary, it is immutable and is thread-safe.
 * <table class="striped">
 * <caption><b>Pattern Letters and Symbols</b></caption>
 * <thead>
 *  <tr><th scope="col">Symbol</th>   <th scope="col">Meaning</th>         <th scope="col">Examples</th>
 * </thead>
 * <tbody>
 *   <tr><th scope="row">D</th>       <td>degree part of latitude</td>     <td>34; 23.2332</td>
 *   <tr><th scope="row">M</th>       <td>minute part of latitude</td>     <td>45; 45.6</td>
 *   <tr><th scope="row">S</th>       <td>second part of latitude</td>     <td>7; 07</td>
 *   <tr><th scope="row">X</th>       <td>hemisphere (N or S)</td>         <td>N; S</td>
 *   <tr><th scope="row">d</th>       <td>degree part of longitude</td>    <td>34; 23.2332</td>
 *   <tr><th scope="row">m</th>       <td>minute part of longitude</td>    <td>45; 45.6</td>
 *   <tr><th scope="row">s</th>       <td>second part of longitude</td>    <td>7; 07</td>
 *   <tr><th scope="row">x</th>       <td>hemisphere (E or W)</td>         <td>E; W</td>
 *   <tr><th scope="row">E</th>       <td>elevation in meters</td>         <td>234; 1023</td>
 *   <tr><th scope="row">'</th>       <td>escape for text</td>             <td></td>
 *   <tr><th scope="row">''</th>      <td>single quote</td>                <td>'</td>
 *   <tr><th scope="row">[</th>       <td>optional section start</td>      <td></td>
 *   <tr><th scope="row">]</th>       <td>optional section end</td>        <td></td>
 * </tbody>
 * </table>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class LocationFormatter {

	/**
	 * Latitude formatter with the pattern {@code DD°MM'SS.SSS"X}. Example:
	 * {@code 16°27'59.180"N}.
	 */
	public static final LocationFormatter ISO_HUMAN_LAT_LONG = builder()
		.append(Field.DEGREE_OF_LATITUDE, "00")
		.appendLiteral("°")
		.append(Field.MINUTE_OF_LATITUDE, "00")
		.appendLiteral("'")
		.append(Field.SECOND_OF_LATITUDE, "00.000")
		.appendLiteral("\"")
		.appendNorthSouthHemisphere()
		.build();

	/**
	 * Longitude formatter with the pattern {@code dd°mm'ss.sss"x}. Example:
	 * {@code 16°27'59.180"E}.
	 */
	public static final LocationFormatter ISO_HUMAN_LON_LONG = builder()
		.append(Field.DEGREE_OF_LONGITUDE, "00")
		.appendLiteral("°")
		.append(Field.MINUTE_OF_LONGITUDE, "00")
		.appendLiteral("'")
		.append(Field.SECOND_OF_LONGITUDE, "00.000")
		.appendLiteral("\"")
		.appendEastWestHemisphere()
		.build();

	/**
	 * Elevation formatter with the pattern {@code E.Em}. Example: {@code 2045m}.
	 */
	public static final LocationFormatter ISO_HUMAN_ELE_LONG = builder()
		.append(Field.ELEVATION, "#.##")
		.appendLiteral("m")
		.build();

	/**
	 * Elevation formatter with the pattern
	 * {@code DD°MM'SS.SSS"X dd°mm'ss.sss"x[ E.Em]}.
	 * Example: {@code 50°03′46.461″S 125°48′26.533″E 978.90m}.
	 */
	public static final LocationFormatter ISO_HUMAN_LONG = builder()
		.append(ISO_HUMAN_LAT_LONG)
		.appendLiteral(" ")
		.append(ISO_HUMAN_LON_LONG)
		.append(
			builder()
				.appendLiteral(" ")
				.append(ISO_HUMAN_ELE_LONG)
				.build(),
			true)
		.build();

	private final List<Format<Location>> _formats;

	private LocationFormatter(final List<Format<Location>> formats) {
		_formats = requireNonNull(formats);
	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder to create location formatters. This allows a
	 * {@code LocationFormatter} to be created. All location formatters are
	 * created ultimately using this builder.
	 *
	 * @implNote
	 * This class is a mutable builder intended for use from a single thread.
	 */
	public static final class Builder {
		private final List<Format<Location>> _formats = new ArrayList<>();

		private Builder() {
		}

		/**
		 * Appends all the elements of a formatter to the builder. This method
		 * has the same effect as appending each of the constituent parts of the
		 * formatter directly to this builder.
		 *
		 * @param formatter the formatter to add, not {@code null}
		 * @param optional optional flag
		 * @return {@code this}, for chaining, not {@code null}
		 * @throws NullPointerException if the given {@code formatter} is
		 *         {@code null}
		 */
		public Builder append(final LocationFormatter formatter, final boolean optional) {
			final Format<Location> formats = new CompositeFormat<>(formatter._formats);
			_formats.add(optional ? new OptionalFormat<>(formats) :  formats);
			return this;
		}

		public Builder append(final LocationFormatter formatter) {
			return append(formatter, false);
		}

		public Builder append(
			final Location.Field field,
			final Supplier<NumberFormat> format
		) {
			_formats.add(new LocationFieldFormat(field, format));
			return this;
		}

		public Builder append(final Location.Field field, final String pattern) {
			return append(field, () -> new DecimalFormat(pattern));
		}

		public Builder appendLatitudeSign() {
			_formats.add(new LatitudeSignFormat());
			return this;
		}

		public Builder appendLongitudeSign() {
			_formats.add(new LongitudeSignFormat());
			return this;
		}

		public Builder appendNorthSouthHemisphere() {
			_formats.add(new NorthSouthFormat());
			return this;
		}

		public Builder appendEastWestHemisphere() {
			_formats.add(new EastWestFormat());
			return this;
		}

		public Builder appendLiteral(final String literal) {
			_formats.add(new ConstFormat<>(literal));
			return this;
		}

		public LocationFormatter build() {
			return new LocationFormatter(new ArrayList<>(_formats));
		}

	}


	public String format(final Location location) {
		requireNonNull(location);
		DateTimeFormatter f;

		return _formats.stream()
			.map(format -> format.format(location)
				.orElseThrow(() -> new IllegalArgumentException("Invalid location format.")))
			.collect(Collectors.joining());
	}


//	public String format(final Latitude lat, final Longitude lon, final Length ele) {
//		return format(Location.of(lat, lon, ele));
//	}
//
//	public String format(final Latitude lat, final Longitude lon) {
//		return format(lat, lon, null);
//	}
//
//	public String format(final Latitude lat) {
//		return format(lat, null, null);
//	}
//
//	public String format(final Longitude lon) {
//		return format(null, lon, null);
//	}
//
//	public String format(final Length ele) {
//		return format(null, null, ele);
//	}
//
//	public Location parse(final CharSequence text, final ParsePosition pos) {
//		return null;
//	}
//
//	public Location parse(final CharSequence text) {
//		return null;
//	}
//
//	public LocationFormatter withLocal(final Locale locale) {
//		return this;
//	}

}

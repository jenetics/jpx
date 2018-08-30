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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.jenetics.jpx.Latitude;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.Longitude;
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
 *   <tr><th scope="row">L</th>       <td>the latitude value</td>          <td>-34.4334; 23.2332</td>
 *   <tr><th scope="row">D</th>       <td>(absolute) degree part of latitude</td>     <td>34; 23.2332</td>
 *   <tr><th scope="row">M</th>       <td>minute part of latitude</td>     <td>45; 45.6</td>
 *   <tr><th scope="row">S</th>       <td>second part of latitude</td>     <td>7; 07</td>
 *   <tr><th scope="row">X</th>       <td>hemisphere (N or S)</td>         <td>N; S</td>
 *   <tr><th scope="row">l</th>       <td>the longitude value</td>         <td>34; -23.2332</td>
 *   <tr><th scope="row">d</th>       <td>(absolute) degree part of longitude</td>    <td>34; 23.2332</td>
 *   <tr><th scope="row">m</th>       <td>minute part of longitude</td>    <td>45; 45.6</td>
 *   <tr><th scope="row">s</th>       <td>second part of longitude</td>    <td>7; 07</td>
 *   <tr><th scope="row">x</th>       <td>hemisphere (E or W)</td>         <td>E; W</td>
 *   <tr><th scope="row">E</th>       <td>elevation in meters</td>         <td>234; 1023; -12</td>
 *   <tr><th scope="row">H</th>       <td>(absolute) elevation in meters</td> <td>234; 1023; 12</td>
 *   <tr><th scope="row">'</th>       <td>escape for text</td>             <td></td>
 *   <tr><th scope="row">''</th>      <td>single quote</td>                <td>'</td>
 *   <tr><th scope="row">[</th>       <td>optional section start</td>      <td></td>
 *   <tr><th scope="row">]</th>       <td>optional section end</td>        <td></td>
 * </tbody>
 * </table>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.4
 * @since 1.4
 */
public final class LocationFormatter {

	static final Set<Character> PROTECTED_CHARS = new HashSet<>(Arrays.asList(
		'L', 'D', 'M', 'S', 'l', 'd', 'm', 's', 'E', 'H', 'X', 'x', '+', '[', ']'
	));

	/**
	 * Latitude formatter with the pattern <em>{@code DD°MM''SS.SSS"X}</em>.
	 * Example: <em>{@code 16°27'59.180"N}</em>.
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
	 * Longitude formatter with the pattern <em>{@code dd°mm''ss.sss"x}</em>.
	 * Example: <em>{@code 16°27'59.180"E}</em>.
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
	 * Elevation formatter with the pattern <em>{@code E.EE'm'}</em>. Example:
	 * <em>{@code 2045m}</em>.
	 */
	public static final LocationFormatter ISO_HUMAN_ELE_LONG = builder()
		.append(Field.ELEVATION, "0.00")
		.appendLiteral("m")
		.build();

	/**
	 * Elevation formatter with the pattern
	 * <em>{@code DD°MM''SS.SSS"X dd°mm''ss.sss"x[ E.EE'm']}</em>.
	 * Example: <em>{@code 50°03′46.461″S 125°48′26.533″E 978.90m}</em>.
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

	/**
	 * ISO 6709 conform latitude format, short: <em>{@code +DD.DD}</em>.
	 */
	public static final LocationFormatter ISO_LAT_SHORT = builder()
		.appendLatitudeSign()
		.append(Field.DEGREE_OF_LATITUDE, "00.00")
		.build();

	/**
	 * ISO 6709 conform latitude format, medium: <em>{@code +DDMM.MMM}</em>.
	 */
	public static final LocationFormatter ISO_LAT_MEDIUM = builder()
		.appendLatitudeSign()
		.append(Field.DEGREE_OF_LATITUDE, "00")
		.append(Field.MINUTE_OF_LATITUDE, "00.000")
		.build();

	/**
	 * ISO 6709 conform latitude format, long: <em>{@code +DDMMSS.SS}</em>.
	 */
	public static final LocationFormatter ISO_LAT_LONG = builder()
		.appendLatitudeSign()
		.append(Field.DEGREE_OF_LATITUDE, "00")
		.append(Field.MINUTE_OF_LATITUDE, "00")
		.append(Field.SECOND_OF_LATITUDE, "00.00")
		.build();


	/**
	 * ISO 6709 conform longitude format, short: <em>{@code +ddd.dd}</em>.
	 */
	public static final LocationFormatter ISO_LON_SHORT = builder()
		.appendLongitudeSign()
		.append(Field.DEGREE_OF_LONGITUDE, "000.00")
		.build();

	/**
	 * ISO 6709 conform longitude format, medium: <em>{@code +dddmm.mmm}</em>.
	 */
	public static final LocationFormatter ISO_LON_MEDIUM = builder()
		.appendLongitudeSign()
		.append(Field.DEGREE_OF_LONGITUDE, "000")
		.append(Field.MINUTE_OF_LONGITUDE, "00.000")
		.build();

	/**
	 * ISO 6709 conform longitude format, long: <em>{@code +dddmmss.ss}</em>.
	 */
	public static final LocationFormatter ISO_LON_LONG = builder()
		.appendLongitudeSign()
		.append(Field.DEGREE_OF_LONGITUDE, "000")
		.append(Field.MINUTE_OF_LONGITUDE, "00")
		.append(Field.SECOND_OF_LONGITUDE, "00.00")
		.build();

	/**
	 * ISO 6709 conform elevation format, short: <em>{@code +E'CRS'}</em>.
	 */
	public static final LocationFormatter ISO_ELE_SHORT = builder()
		.appendElevationSign()
		.append(Field.METER_OF_ELEVATION, "0")
		.appendLiteral("CRS")
		.build();

	/**
	 * ISO 6709 conform elevation format, medium: <em>{@code +E.E'CRS'}</em>.
	 */
	public static final LocationFormatter ISO_ELE_MEDIUM = builder()
		.appendElevationSign()
		.append(Field.METER_OF_ELEVATION, "0.0")
		.appendLiteral("CRS")
		.build();

	/**
	 * ISO 6709 conform elevation format, long: <em>{@code +E.EE'CRS'}</em>.
	 */
	public static final LocationFormatter ISO_ELE_LONG = builder()
		.appendElevationSign()
		.append(Field.METER_OF_ELEVATION, "0.00")
		.appendLiteral("CRS")
		.build();

	/**
	 * ISO 6709 conform location format, short:
	 * <em>{@code +DD.DD+ddd.dd[+E'CRS']}</em>.
	 */
	public static final LocationFormatter ISO_SHORT = builder()
		.append(ISO_LAT_SHORT)
		.append(ISO_LON_SHORT)
		.append(ISO_ELE_SHORT, true)
		.build();

	/**
	 * ISO 6709 conform location format, medium:
	 * <em>{@code +DDMM.MMM+ddmm.mmm[+E.E'CRS']}</em>.
	 */
	public static final LocationFormatter ISO_MEDIUM = builder()
		.append(ISO_LAT_MEDIUM)
		.append(ISO_LON_MEDIUM)
		.append(ISO_ELE_MEDIUM, true)
		.build();

	/**
	 * ISO 6709 conform location format, medium:
	 * <em>{@code +DDMMSS.SS+ddmmss.ss[+E.EE'CRS']}</em>.
	 */
	public static final LocationFormatter ISO_LONG = builder()
		.append(ISO_LAT_LONG)
		.append(ISO_LON_LONG)
		.append(ISO_ELE_LONG, true)
		.build();



	private final List<Format<Location>> _formats;

	private LocationFormatter(final List<Format<Location>> formats) {
		_formats = requireNonNull(formats);
	}

	/**
	 * Return a new formatter builder instance.
	 *
	 * @return a new formatter builder instance
	 */
	public static Builder builder() {
		return new Builder();
	}


	/**
	 * Formats the given {@code location} using {@code this} formatter.
	 *
	 * @param location the location to format
	 * @return the format string
	 * @throws NullPointerException if the given {@code location} is {@code null}
	 * @throws FormatterException if the formatter tries to format a non-existing,
	 *         non-optional location fields.
	 */
	public String format(final Location location) {
		requireNonNull(location);
		return _formats.stream()
			.map(format -> format.format(location)
							.orElseThrow(() -> toError(location)))
			.collect(Collectors.joining());
	}

	private FormatterException toError(final Location location) {
		return new FormatterException(String.format(
			"Invalid format '%s' for location %s.",
			toPattern(), location
		));
	}

	/**
	 * Return the pattern string represented by this formatter.
	 *
	 * @see #ofPattern(String)
	 *
	 * @return the pattern string of {@code this} formatter
	 */
	public String toPattern() {
		return _formats.stream()
			.map(Objects::toString)
			.collect(Collectors.joining());
	}

	/**
	 * Formats the given location elements using {@code this} formatter.
	 *
	 * @see #format(Location)
	 *
	 * @param lat the latitude part of the location
	 * @param lon the longitude part of the location
	 * @param ele the elevation part of the location
	 * @return the format string
	 * @throws FormatterException if the formatter tries to format a non-existing,
	 *         non-optional location fields.
	 */
	public String format(final Latitude lat, final Longitude lon, final Length ele) {
		return format(Location.of(lat, lon, ele));
	}

	/**
	 * Formats the given location elements using {@code this} formatter.
	 *
	 * @see #format(Location)
	 *
	 * @param lat the latitude part of the location
	 * @param lon the longitude part of the location
	 * @return the format string
	 * @throws FormatterException if the formatter tries to format a non-existing,
	 *         non-optional location fields.
	 */
	public String format(final Latitude lat, final Longitude lon) {
		return format(lat, lon, null);
	}

	/**
	 * Formats the given location elements using {@code this} formatter.
	 *
	 * @see #format(Location)
	 *
	 * @param lat the latitude part of the location
	 * @return the format string
	 * @throws FormatterException if the formatter tries to format a non-existing,
	 *         non-optional location fields.
	 */
	public String format(final Latitude lat) {
		return format(lat, null, null);
	}

	/**
	 * Formats the given location elements using {@code this} formatter.
	 *
	 * @see #format(Location)
	 *
	 * @param lon the longitude part of the location
	 * @return the format string
	 * @throws FormatterException if the formatter tries to format a non-existing,
	 *         non-optional location fields.
	 */
	public String format(final Longitude lon) {
		return format(null, lon, null);
	}

	/**
	 * Formats the given location elements using {@code this} formatter.
	 *
	 * @see #format(Location)
	 *
	 * @param ele the elevation part of the location
	 * @return the format string
	 * @throws FormatterException if the formatter tries to format a non-existing,
	 *         non-optional location field.
	 */
	public String format(final Length ele) {
		return format(null, null, ele);
	}
//
//	public Location parse(final CharSequence text, final ParsePosition pos) {
//		return null;
//	}
//
//	public Location parse(final CharSequence text) {
//		return null;
//	}


	@Override
	public String toString() {
		return String.format("LocationFormat[%s]", toPattern());
	}


	/* *************************************************************************
	 * Static factory methods.
	 * ************************************************************************/

	/**
	 * Creates a formatter using the specified pattern.
	 *
	 * @see #toPattern()
	 *
	 * @param pattern the formatter pattern
	 * @return the location-formatter of the given {@code pattern}
	 * @throws NullPointerException if the given {@code pattern} is {@code null}
	 * @throws IllegalArgumentException if the given {@code pattern} is invalid
	 */
	public static LocationFormatter ofPattern(final String pattern) {
		return builder()
			.appendPattern(pattern)
			.build();
	}


	/* *************************************************************************
	 * Inner classes.
	 * ************************************************************************/

	/**
	 * Builder to create location formatters. This allows a
	 * {@code LocationFormatter} to be created. All location formatters are
	 * created ultimately using this builder. The following example will create
	 * a formatter for the latitude field:
	 * <pre>{@code
	 * final LocationFormatter formatter = LocationFormatter.builder()
	 *     .append(Location.Field.DEGREE_OF_LATITUDE, "00")
	 *     .appendLiteral("°")
	 *     .append(Location.Field.MINUTE_OF_LATITUDE, "00")
	 *     .appendLiteral("'")
	 *     .append(Location.Field.SECOND_OF_LATITUDE, "00.000")
	 *     .appendLiteral("\"")
	 *     .appendNorthSouthHemisphere()
	 *     .build();
	 * }</pre>
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
		 * @param optional optional flag. If {@code true}, the created formatter
		 *        will allow missing location fields.
		 * @return {@code this}, for chaining, not {@code null}
		 * @throws NullPointerException if the given {@code formatter} is
		 *         {@code null}
		 */
		public Builder append(
			final LocationFormatter formatter,
			final boolean optional
		) {
			_formats.add(optional
				? OptionalFormat.of(formatter._formats)
				:  CompositeFormat.of(formatter._formats)
			);
			return this;
		}

		/**
		 * Appends all the elements of a formatter to the builder. This method
		 * has the same effect as appending each of the constituent parts of the
		 * formatter directly to this builder.
		 *
		 * @param formatter the formatter to add, not {@code null}
		 * @return {@code this}, for chaining, not {@code null}
		 * @throws NullPointerException if the given {@code formatter} is
		 *         {@code null}
		 */
		public Builder append(final LocationFormatter formatter) {
			return append(formatter, false);
		}

		/**
		 * Append a formatter for the given location field, which will be
		 * formatted using the given number format objects.
		 *
		 * @param field the location field to format
		 * @param format the number formatter used for formatting the defined
		 *        location field
		 * @return {@code this}, for chaining, not {@code null}
		 * @throws NullPointerException if one of the arguments is {@code null}
		 */
		public Builder append(
			final Location.Field field,
			final Supplier<NumberFormat> format
		) {
			_formats.add(LocationFieldFormat.of(field, format));
			return this;
		}

		/**
		 * Append a formatter for the given location field, which will be
		 * formatted using the given decimal format pattern, as used in the
		 * {@link DecimalFormat} object
		 *
		 * @see DecimalFormat
		 *
		 * @param field the location field to format
		 * @param pattern the decimal format pattern
		 * @return {@code this}, for chaining, not {@code null}
		 * @throws NullPointerException if one of the arguments is {@code null}
		 */
		public Builder append(final Location.Field field, final String pattern) {
			return append(field, () -> new DecimalFormat(pattern));
		}

		/**
		 * Append a formatter for the sign of the latitude value ('+' or '-').
		 *
		 * @return {@code this}, for chaining, not {@code null}
		 */
		public Builder appendLatitudeSign() {
			_formats.add(LatitudeSignFormat.INSTANCE);
			return this;
		}

		/**
		 * Append a formatter for the sign of the longitude value ('+' or '-').
		 *
		 * @return {@code this}, for chaining, not {@code null}
		 */
		public Builder appendLongitudeSign() {
			_formats.add(LongitudeSignFormat.INSTANCE);
			return this;
		}

		/**
		 * Append a formatter for the sign of the elevation value ('+' or '-').
		 *
		 * @return {@code this}, for chaining, not {@code null}
		 */
		public Builder appendElevationSign() {
			_formats.add(ElevationSignFormat.INSTANCE);
			return this;
		}

		/**
		 * Append a formatter for the north-south hemisphere ('N' or 'S'). The
		 * appended formatter will access the <em>latitude</em> value of the
		 * location.
		 *
		 * @return {@code this}, for chaining, not {@code null}
		 */
		public Builder appendNorthSouthHemisphere() {
			_formats.add(NorthSouthFormat.INSTANCE);
			return this;
		}

		/**
		 * Append a formatter for the east-west hemisphere ('E' or 'W'). The
		 * appended formatter will access the <em>longitude</em> value of the
		 * location.
		 *
		 * @return {@code this}, for chaining, not {@code null}
		 */
		public Builder appendEastWestHemisphere() {
			_formats.add(EastWestFormat.INSTANCE);
			return this;
		}

		/**
		 * Appends a string literal to the formatter. This string will be output
		 * during a format. If the literal is empty, nothing is added to the
		 * formatter.
		 *
		 * @param literal the {@code literal} to append, not null
		 * @return {@code this}, for chaining, not {@code null}
		 * @throws NullPointerException if one of the {@code literal} is
		 *         {@code null}
		 */
		public Builder appendLiteral(final String literal) {
			if (!literal.isEmpty()) {
				_formats.add(ConstFormat.of(literal));
			}
			return this;
		}

		/**
		 * Appends the elements defined by the specified pattern to the builder.
		 *
		 * @param pattern the pattern to add
		 * @return {@code this}, for chaining, not {@code null}
		 * @throws NullPointerException if the given {@code pattern} is
		 *         {@code null}
		 * @throws IllegalArgumentException if the given {@code pattern} is
		 *         invalid
		 */
		public Builder appendPattern(final String pattern) {
			parsePattern(pattern);
			return this;
		}

		/**
		 * Completes this builder by creating the {@code LocationFormatter}.
		 *
		 * @return a new location-formatter object
		 */
		public LocationFormatter build() {
			return new LocationFormatter(new ArrayList<>(_formats));
		}

		private void parsePattern(final String pattern) {
			requireNonNull(pattern);
			final List<Format<Location>> formats = new ArrayList<>();

			boolean optional = false;
			int signs = 0;
			boolean quote = false;

			List<Format<Location>> fmt;
			for (Tokens tokens = new Tokens(tokenize(pattern)); tokens.hasNext();) {
				final String token = tokens.next();
				switch (token) {
					case "X":
						fmt = optional ? formats : _formats;
						for (int i = 0; i < signs; ++i) {
							fmt.add(LatitudeSignFormat.INSTANCE);
						}
						signs = 0;
						fmt.add(NorthSouthFormat.INSTANCE);
						break;
					case "x":
						fmt = optional ? formats : _formats;
						for (int i = 0; i < signs; ++i) {
							fmt.add(LongitudeSignFormat.INSTANCE);
						}
						signs = 0;
						fmt.add(EastWestFormat.INSTANCE);
						break;
					case "+":
						++signs;
						break;
					case "[":
						if (optional) {
							throw new IllegalArgumentException(
								"No nesting '[' (optional) allowed."
							);
						}
						if (signs > 0) {
							throw new IllegalArgumentException(
								"No '[' after '+' allowed."
							);
						}
						optional = true;
						break;
					case "]":
						if (!optional) {
							throw new IllegalArgumentException(
								"Missing open '[' bracket."
							);
						}
						optional = false;

						_formats.add(OptionalFormat.of(formats));
						formats.clear();
						break;
					case "'":
						fmt = optional ? formats : _formats;
						if (tokens.after().filter("'"::equals).isPresent()) {
							fmt.add(ConstFormat.of("'"));
							tokens.next();
							break;
						}
						if (quote) {
							if (tokens.before().isPresent()) {
								fmt.add(ConstFormat.of(
									tokens.before()
										.orElseThrow(AssertionError::new)
								));
							}
							quote = false;
						} else {
							quote = true;
						}
						break;
					default:
						fmt = optional ? formats : _formats;
						if (!quote) {
							final Optional<Field> field = Field.ofPattern(token);
							if (field.isPresent()) {
								if (signs > 0) {
									if (field.get().isLatitude()) {
										for (int i = 0; i < signs; ++i) {
											fmt.add(LatitudeSignFormat.INSTANCE);
										}
									} else if (field.get().isLongitude()) {
										for (int i = 0; i < signs; ++i) {
											fmt.add(LongitudeSignFormat.INSTANCE);
										}
									} else if (field.get().isElevation()) {
										for (int i = 0; i < signs; ++i) {
											fmt.add(ElevationSignFormat.INSTANCE);
										}
									}
								}
								fmt.add(LocationFieldFormat.of(
									field.get(),
									() -> new DecimalFormat(
										field.get().toDecimalPattern(token))
								));
							} else {
								fmt.add(ConstFormat.of(token));
							}
						}
						signs = 0;
						break;
				}
			}

			if (optional) {
				throw new IllegalArgumentException("No closing ']' found.");
			}
			if (quote) {
				throw new IllegalArgumentException(
					"Missing closing ' character."
				);
			}

			_formats.addAll(formats);
		}


		static List<String> tokenize(final String pattern) {
			final List<String> tokens = new ArrayList<>();
			final StringBuilder token = new StringBuilder();

			boolean quote = false;
			char pc = '\0';
			for (int i = 0; i < pattern.length(); ++i) {
				final char c = pattern.charAt(i);
				switch (c) {
					case '\'':
						quote = !quote;
						if (token.length() > 0) {
							tokens.add(token.toString());
							token.setLength(0);
						}
						tokens.add(Character.toString(c));
						break;
					case 'x':
					case 'X':
					case '+':
					case '[':
					case ']':
						if (quote) {
							token.append(c);
						} else {
							if (token.length() > 0) {
								tokens.add(token.toString());
								token.setLength(0);
							}
							tokens.add(Character.toString(c));
						}
						break;
					case 'L':
					case 'D':
					case 'M':
					case 'S':
					case 'l':
					case 'd':
					case 'm':
					case 's':
					case 'E':
					case 'H':
						if (c != pc && pc != '\0' && pc != '.' &&
							pc != ',' && !quote)
						{
							if (token.length() > 0) {
								tokens.add(token.toString());
								token.setLength(0);
							}
						}
						token.append(c);
						break;
					case ',':
					case '.':
						token.append(c);
						break;
					default:
						if (PROTECTED_CHARS.contains(pc) || pc == '\'') {
							if (token.length() > 0) {
								tokens.add(token.toString());
								token.setLength(0);
							}
						}
						token.append(c);
						break;
				}

				pc = c;
			}

			if (token.length() > 0) {
				tokens.add(token.toString());
			}

			return tokens;
		}

	}

	private static final class Tokens implements Iterator<String> {
		private final List<String> _tokens;

		private int _index = 0;

		private Tokens(final List<String> tokens) {
			_tokens = requireNonNull(tokens);
		}

		@Override
		public boolean hasNext() {
			return _index < _tokens.size();
		}

		@Override
		public String next() {
			return _tokens.get(_index++);
		}

		Optional<String> before() {
			return _index - 1 > 0
				? Optional.of(_tokens.get(_index - 2))
				: Optional.empty();
		}

		Optional<String> after() {
			return hasNext()
				? Optional.of(_tokens.get(_index))
				: Optional.empty();
		}

	}

}

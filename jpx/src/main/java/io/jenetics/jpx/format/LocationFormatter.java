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

import static java.util.Objects.requireNonNull;

import java.text.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import io.jenetics.jpx.Latitude;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.Longitude;

/**
 * Formatter for printing and parsing geographic location objects.
 * <p>
 * <b>Patterns for Formatting and Parsing</b>
 * <p>
 * Patterns are based on a simple sequence of letters and symbols. A pattern is
 * used to create a Formatter using the {@code #ofPattern(String)} and
 * {@code #ofPattern(String, Locale)} methods.
 * For example, {@code DD°MM'SS.SSS"X} will format to
 * {@code 60°15'59.613"N}. A formatter created from a pattern can be used as
 * many times as necessary, it is immutable and is thread-safe.
 * <table class="striped">
 * <caption><b>Pattern Letters and Symbols</b></caption>
 * <thead>
 *  <tr><th scope="col">Symbol</th>   <th scope="col">Meaning</th>         <th scope="col">Examples</th>
 * </thead>
 * <tbody>
 *   <tr><th scope="row">L</th>       <td>latitude in degrees</td>         <td>-34.4334; 23.2332</td>
 *   <tr><th scope="row">D</th>       <td>absolute degree part of latitude</td>     <td>34; 23.2332</td>
 *   <tr><th scope="row">M</th>       <td>minute part of latitude</td>     <td>45; 45.6</td>
 *   <tr><th scope="row">S</th>       <td>second part of latitude</td>     <td>7; 07</td>
 *   <tr><th scope="row">X</th>       <td>hemisphere (N or S)</td>         <td>N; S</td>
 *   <tr><th scope="row">l</th>       <td>longitude in degrees</td>        <td>34; -23.2332</td>
 *   <tr><th scope="row">d</th>       <td>absolute degree part of longitude</td>    <td>34; 23.2332</td>
 *   <tr><th scope="row">m</th>       <td>minute part of longitude</td>    <td>45; 45.6</td>
 *   <tr><th scope="row">s</th>       <td>second part of longitude</td>    <td>7; 07</td>
 *   <tr><th scope="row">x</th>       <td>hemisphere (E or W)</td>         <td>E; W</td>
 *   <tr><th scope="row">E</th>       <td>elevation in meters</td>         <td>234; 1023; -12</td>
 *   <tr><th scope="row">H</th>       <td>absolute elevation in meters</td> <td>234; 1023; 12</td>
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
// TODO explain the use of + versus X,x
// TODO explain behaviour of D,d: round or truncate
// TODO explain behaviour of M,m: round or truncate
public final class LocationFormatter {

	static final Set<Character> PROTECTED_CHARS = Set.of(
		'L', 'D', 'M', 'S', 'l', 'd', 'm', 's', 'E', 'H', 'X', 'x', '+', '[', ']'
	);

	/**
	 * Latitude formatter with the pattern <em>{@code D°MM''SS.SSS"X}</em>.
	 * Example: <em>{@code 16°27'59.180"N}</em>.
	 */
	public static final LocationFormatter ISO_HUMAN_LAT_LONG = ofPattern("D°MM''SS.SSS\"X");

	/**
	 * Longitude formatter with the pattern <em>{@code dd°mm''ss.sss"x}</em>.
	 * Example: <em>{@code 16°27'59.180"E}</em>.
	 */
	public static final LocationFormatter ISO_HUMAN_LON_LONG = ofPattern("dd°mm''ss.sss\"x");

	/**
	 * Elevation formatter with the pattern <em>{@code E.EE'm'}</em>. Example:
	 * <em>{@code 2045m}</em>.
	 */
	public static final LocationFormatter ISO_HUMAN_ELE_LONG = ofPattern("E.EE'm'");

	/**
	 * Elevation formatter with the pattern
	 * <em>{@code DD°MM''SS.SSS"X dd°mm''ss.sss"x[ E.EE'm']}</em>.
	 * Example: <em>{@code 50°03′46.461″S 125°48′26.533″E 978.90m}</em>.
	 */
	public static final LocationFormatter ISO_HUMAN_LONG = ofPattern("DD°MM''SS.SSS\"X dd°mm''ss.sss\"x[ E.EE'm']");

	/**
	 * ISO 6709 conform latitude format, short: <em>{@code +DD.DD}</em>.
	 */
	public static final LocationFormatter ISO_LAT_SHORT = ofPattern("+DD.DD");

	/**
	 * ISO 6709 conform latitude format, medium: <em>{@code +DDMM.MMM}</em>.
	 */
	public static final LocationFormatter ISO_LAT_MEDIUM = ofPattern("+DDMM.MMM");

	/**
	 * ISO 6709 conform latitude format, long: <em>{@code +DDMMSS.SS}</em>.
	 */
	public static final LocationFormatter ISO_LAT_LONG = ofPattern("+DDMMSS.SS");

	/**
	 * ISO 6709 conform longitude format, short: <em>{@code +ddd.dd}</em>.
	 */
	public static final LocationFormatter ISO_LON_SHORT = ofPattern("+ddd.dd");

	/**
	 * ISO 6709 conform longitude format, medium: <em>{@code +dddmm.mmm}</em>.
	 */
	public static final LocationFormatter ISO_LON_MEDIUM = ofPattern("+dddmm.mmm");

	/**
	 * ISO 6709 conform longitude format, long: <em>{@code +dddmmss.ss}</em>.
	 */
	public static final LocationFormatter ISO_LON_LONG = ofPattern("+dddmmss.ss");

	/**
	 * ISO 6709 conform elevation format, short: <em>{@code +H'CRS'}</em>.
	 */
	public static final LocationFormatter ISO_ELE_SHORT = ofPattern("+H'CRS'");

	/**
	 * ISO 6709 conform elevation format, medium: <em>{@code +H.H'CRS'}</em>.
	 */
	public static final LocationFormatter ISO_ELE_MEDIUM = ofPattern("+H.H'CRS'");

	/**
	 * ISO 6709 conform elevation format, long: <em>{@code +H.HH'CRS'}</em>.
	 */
	public static final LocationFormatter ISO_ELE_LONG = ofPattern("+H.HH'CRS'");

	/**
	 * ISO 6709 conform location format, short:
	 * <em>{@code +DD.DD+ddd.dd[+H'CRS']}</em>.
	 */
	public static final LocationFormatter ISO_SHORT = ofPattern("+DD.DD+ddd.dd[+H'CRS']");

	/**
	 * ISO 6709 conform location format, medium:
	 * <em>{@code +DDMM.MMM+ddmm.mmm[+H.H'CRS']}</em>.
	 */
	public static final LocationFormatter ISO_MEDIUM = ofPattern("+DDMM.MMM+ddmm.mmm[+H.H'CRS']");

	/**
	 * ISO 6709 conform location format, medium:
	 * <em>{@code +DDDMMSS.SS+dddmmss.ss[+H.HH'CRS']}</em>.
	 */
	public static final LocationFormatter ISO_LONG = ofPattern("+DDDMMSS.SS+dddmmss.ss[+H.HH'CRS']");

	private final List<Format<Location>> _formats;

	private LocationFormatter(List<Format<Location>> formats) {
		_formats = requireNonNull(formats);
	}

	/**
	 * Return a new formatter builder instance.
	 *
	 * @return a new formatter builder instance
	 */
	static Builder builder() { return new Builder(); }

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
			.map( f -> f.toString() ) // using toString() to return pattern
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

	/** Parses the text using this formatter, providing control over the text position.
	 *
	 * This parses the text without requiring the parse to start from the beginning of the string or finish at the end.
	 *
	 * The text will be parsed from the specified start ParsePosition.
	 * The entire length of the text does not have to be parsed,
	 * the ParsePosition will be updated with the index at the end of parsing.
	 *
	 * If the formatter parses the same field more than once with different values, the result will be an error.
	 *
	 * @param text the text to parse, not null
	 * @param pos the position to parse from, updated with length parsed and the index of any error, not null
	 * @return the parsed Location, not null
	 * @throws ParseException - if unable to parse the requested result
	 * @throws IndexOutOfBoundsException - if the position is invalid
	 * */
	public Location parse(CharSequence text, ParsePosition pos) throws ParseException {
		requireNonNull(text);
		requireNonNull(pos);
		if(pos.getIndex() < 0 || text.length() <= pos.getIndex())
			throw new IndexOutOfBoundsException(pos.getIndex());

		LocationBuilder builder = new LocationBuilder();
		for( Format<Location> f : _formats )
			f.parse(text, pos, builder);

		Location location = builder.build();
		return location;
	}

	/** Fully parses the text producing a location.
	 *
	 * This parses the entire text producing a location.
	 * It is typically more useful to use parse(CharSequence, TemporalQuery).
	 *
	 * If the parse completes without reading the entire length of the text,
	 * or a problem occurs during parsing or merging, then an exception is thrown.
	 *
	 * @param text the text to parse, not null
	 * @return the parsed temporal object, not null
	 * @throws ParseException - if unable to parse the requested result
	 * */
	public Location parse(CharSequence text) throws ParseException {
		ParsePosition pos = new ParsePosition(0);
		Location loc = parse(text, pos); // ParseException
		if(pos.getIndex()!=text.length())
			throw new ParseException("Not used all input", text, pos.getIndex());
		return loc;
	}

	@Override public String toString() { return String.format("LocationFormat[%s]", toPattern()); }

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
		return builder().appendPattern(pattern).build();
	}

	/* *************************************************************************
	 * Inner classes.
	 * ************************************************************************/

	/**
	 * Builder to create location formatters. This allows a
	 * {@code LocationFormatter} to be created. All location formatters are
	 * created ultimately using this builder.
	 *
	 * @implNote
	 * This class is a mutable builder intended for use from a single thread.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
	 * @version 1.4
	 * @since 1.4
	 */
	static class Builder {

		private List<Format<Location>> _formats = new ArrayList<>();

		private Builder() { }

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
		Builder appendPattern(final String pattern) { parsePattern(pattern);return this; }

		/**
		 * Completes this builder by creating the {@code LocationFormatter}.
		 *
		 * @return a new location-formatter object
		 */
		LocationFormatter build() {
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
						for (int i = 0; i < signs; ++i)
							fmt.add(LatitudeSignFormat.INSTANCE);
						signs = 0;
						fmt.add(NorthSouthFormat.INSTANCE);
						break;

					case "x":
						fmt = optional ? formats : _formats;
						for (int i = 0; i < signs; ++i)
							fmt.add(LongitudeSignFormat.INSTANCE);
						signs = 0;
						fmt.add(EastWestFormat.INSTANCE);
						break;

					case "+":
						++signs;
						break;

					case "[":
						if (optional)
							throw new IllegalArgumentException("No nesting '[' (optional) allowed.");

						if (signs > 0)
							throw new IllegalArgumentException("No '[' after '+' allowed.");

						optional = true;
						break;

					case "]":
						if (!optional)
							throw new IllegalArgumentException("Missing open '[' bracket.");

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
								Field f = field.get();

								// Maybe first add some sign formats.
								if (signs > 0) {
									if (f.isLatitude()) // only really makes sense for D
									{
										for (int i = 0; i < signs; ++i)
											fmt.add(LatitudeSignFormat.INSTANCE);
									}

									else if (f.isLongitude()) // only really makes sense for d
									{
										for (int i = 0; i < signs; ++i)
											fmt.add(LongitudeSignFormat.INSTANCE);
									}

									else if (f.isElevation()) // only really makes sense for H
									{
										for (int i = 0; i < signs; ++i)
											fmt.add(ElevationSignFormat.INSTANCE);
									}
								}

								fmt.add(f);
							} else {
								fmt.add(ConstFormat.of(token));
							}
						}
						signs = 0;
						break;
				}
			}

			if (optional)
				throw new IllegalArgumentException("No closing ']' found.");

			if (quote)
				throw new IllegalArgumentException("Missing closing ' character.");

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

		@Override public boolean hasNext() {
			return _index < _tokens.size();
		}

		@Override public String next() {
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

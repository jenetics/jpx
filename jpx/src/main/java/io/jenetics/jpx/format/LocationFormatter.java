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

import java.text.ParsePosition;
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
 *
 * For example, {@code D°MM'SS.SSS"X} will format to {@code 60°15'59.613"N}.
 *
 * A formatter created from a pattern can be used as many times as necessary, it
 * is immutable and is thread-safe.
 *
 * <table class="striped">
 * <caption><b>Pattern Letters and Symbols</b></caption>
 * <thead>
 *  <tr><th scope="col">Symbol</th><th scope="col">Meaning</th><th scope="col">Examples</th>
 * </thead>
 * <tbody>
 *   <tr><th scope="row">L</th><td>deprecated synonym for 'D'</td></tr>
 *   <tr>
 *       <th scope="row">D</th>
 *       <td>Latitude in degrees. Values in {@code -90 <= d <= +90}. See examples.</td>
 *       <td>34; 23.2332</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">M</th>
 *       <td>Latitude minutes. Values in {@code 0 <= m < 60}. See examples.</td>
 *       <td>45; 45.6</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">S</th>
 *       <td>Latitude seconds. Values in {@code 0 <= s < 60}. See examples.</td>
 *       <td>7; 07</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">X</th>
 *       <td>hemisphere (N or S)</td>
 *       <td>N; S</td>
 *   </tr>
 *   <tr><th scope="row">l</th><td>deprecated synonym for 'd'</td></tr>
 *   <tr>
 *       <th scope="row">d</th>
 *       <td>Longitude degrees. Values in {@code -180 <= d <= +180}. Similar to Latitude degrees.</td>
 *       <td>34; 23.2332</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">m</th>
 *       <td>Longitude minutes. Similar to latitude minutes.</td>
 *       <td>45; 45.6</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">s</th>
 *       <td>Longitude seconds. Similar to Latitude seconds.</td>
 *       <td>7; 07</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">x</th>
 *       <td>hemisphere (E or W)</td>
 *       <td>E; W</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">E</th>
 *       <td>Elevation in meters. See examples.</td>
 *       <td>234; 1023; -12</td>
 *   </tr>
 *   <tr><th scope="row">H</th><td>deprecated synonym for 'E'</td></tr>
 *   <tr>
 *       <th scope="row">'</th>
 *       <td>escape for text</td>
 *       <td></td>
 *   </tr>
 *   <tr>
 *       <th scope="row">''</th>
 *       <td>single quote</td>
 *       <td>'</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">[</th>
 *       <td>optional section start</td>
 *       <td></td>
 *   </tr>
 *   <tr>
 *       <th scope="row">]</th>
 *       <td>optional section end</td>
 *       <td></td>
 *   </tr>
 * </tbody>
 * </table>
 *
 * <table class="striped">
 * <caption><b>Examples</b></caption>
 * <thead>
 *  <tr>
 *      <th scope="col">Pattern</th>
 *      <th scope="col">Meaning</th>
 *      <th scope="col">Examples</th>
 * </thead>
 * <tbody>
 *   <tr>
 *       <th scope="row">+D</th>
 *       <td>Latitude sign indicated by prefix +/-</td>
 *       <td>+47; -24</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">D X</th>
 *       <td>Latitude sign indicated by X</td>
 *       <td>47 N; 24 S</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">D.DD</th>
 *       <td>Latitude degrees with decimal faction</td>
 *       <td>47.50</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">D M</th>
 *       <td>Latitude in degrees and minutes</td>
 *       <td>47 30</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">D</th>
 *       <td>Latitude degrees in variable width</td>
 *       <td>9; 47</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">DD</th>
 *       <td>Latitude degrees in fixed width</td>
 *       <td>09; 47</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">D M.MM</th>
 *       <td>Latitude minutes with decimal fraction</td>
 *       <td>47 2.25</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">D M S</th>
 *       <td>Latitude minutes and seconds</td>
 *       <td>47 2 15</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">D M</th>
 *       <td>Latitude minutes in variable width</td>
 *       <td>47 2; 46 12</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">D MM</th>
 *       <td>Latitude minutes in fixed width</td>
 *       <td>47 02; 46 12</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">D M S</th>
 *       <td>Latitude seconds in variable width</td>
 *       <td>47 2 3; 46 2 13</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">D M SS</th>
 *       <td>Latitude seconds in fixed width</td>
 *       <td>47 2 03; 46 2 13</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">D M S.SS</th>
 *       <td>Latitude seconds with decimal fraction</td>
 *       <td>46 2 13.54</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">+d</th>
 *       <td>Longitude sign indicated by prefix +/-</td>
 *       <td>+147; -124</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">d x</th>
 *       <td>Latitude sign indicated by x</td>
 *       <td>147 E; 124 W</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">d</th>
 *       <td>Longitude degrees in variable width</td>
 *       <td>9; 47; 175</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">ddd</th>
 *       <td>Longitude degrees in fixed width</td>
 *       <td>009; 047; 175</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">d m.mm</th>
 *       <td>Longitude minutes with decimal fraction</td>
 *       <td>47 2.25</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">d m s</th>
 *       <td>Longitude minutes and seconds</td>
 *       <td>47 2 15</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">d m</th>
 *       <td>Longitude minutes in variable width</td>
 *       <td>47 2; 46 12</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">d mm</th>
 *       <td>Longitude minutes in fixed width</td>
 *       <td>47 02; 46 12</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">d m s</th>
 *       <td>Longitude seconds in variable width</td>
 *       <td>47 2 3; 46 2 13</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">d m ss</th>
 *       <td>Longitude seconds in fixed width</td>
 *       <td>47 2 03; 46 2 13</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">d m s.ss</th>
 *       <td>Longitude seconds with decimal fraction</td>
 *       <td>46 2 13.54</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">E</th>
 *       <td>Elevation without positive sign</td>
 *       <td>9; -9</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">+E</th>
 *       <td>Elevation with positive or negative sign</td>
 *       <td>+9; -9</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">E</th>
 *       <td>Elevation in variable width</td>
 *       <td>9; 19; 190</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">EEE</th>
 *       <td>Elevation in fixed width</td>
 *       <td>009; 019; 190</td>
 *   </tr>
 *   <tr>
 *       <th scope="row">E.EEE</th>
 *       <td>Elevation with decimal fraction</td>
 *       <td>9.123</td>
 *   </tr>
 * </tbody>
 * </table>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 2.2
 * @since 1.4
 */
public final class LocationFormatter {

	static final Set<Character> PROTECTED_CHARS = Set.of(
		'L', 'D', 'M', 'S', 'l', 'd', 'm', 's', 'E', 'H', 'X', 'x', '+', '[', ']'
	);

	/**
	 * Latitude formatter with the pattern <em>{@code D°MM''SS.SSS"X}</em>.
	 * Example: <em>{@code 16°27'59.180"N}</em>.
	 */
	public static final LocationFormatter ISO_HUMAN_LAT_LONG =
		ofPattern("D°MM''SS.SSS\"X");

	/**
	 * Longitude formatter with the pattern <em>{@code d°mm''ss.sss"x}</em>.
	 * Example: <em>{@code 16°27'59.180"E}</em>.
	 */
	public static final LocationFormatter ISO_HUMAN_LON_LONG =
		ofPattern("d°mm''ss.sss\"x");

	/**
	 * Elevation formatter with the pattern <em>{@code E.EE'm'}</em>. Example:
	 * <em>{@code 2045m}</em>.
	 */
	public static final LocationFormatter ISO_HUMAN_ELE_LONG =
		ofPattern("E.EE'm'");

	/**
	 * Elevation formatter with the pattern
	 * <em>{@code D°MM''SS.SSS"X d°mm''ss.sss"x[ E.EE'm']}</em>.
	 * Example: <em>{@code 50°03′46.461″S 125°48′26.533″E 978.90m}</em>.
	 */
	public static final LocationFormatter ISO_HUMAN_LONG =
		ofPattern("D°MM''SS.SSS\"X d°mm''ss.sss\"x[ E.EE'm']");

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
	 * ISO 6709 conform elevation format, short: <em>{@code +E'CRS'}</em>.
	 */
	public static final LocationFormatter ISO_ELE_SHORT = ofPattern("+E'CRS'");

	/**
	 * ISO 6709 conform elevation format, medium: <em>{@code +E.E'CRS'}</em>.
	 */
	public static final LocationFormatter ISO_ELE_MEDIUM = ofPattern("+E.E'CRS'");

	/**
	 * ISO 6709 conform elevation format, long: <em>{@code +E.EE'CRS'}</em>.
	 */
	public static final LocationFormatter ISO_ELE_LONG = ofPattern("+E.EE'CRS'");

	/**
	 * ISO 6709 conform location format, short:
	 * <em>{@code +DD.DD+ddd.dd[+E'CRS']}</em>.
	 */
	public static final LocationFormatter ISO_SHORT =
		ofPattern("+DD.DD+ddd.dd[+E'CRS']");

	/**
	 * ISO 6709 conform location format, medium:
	 * <em>{@code +DDMM.MMM+dddmm.mmm[+E.E'CRS']}</em>.
	 */
	public static final LocationFormatter ISO_MEDIUM =
		ofPattern("+DDMM.MMM+dddmm.mmm[+E.E'CRS']");

	/**
	 * ISO 6709 conform location format, medium:
	 * <em>{@code +DDMMSS.SS+dddmmss.ss[+E.EE'CRS']}</em>.
	 */
	public static final LocationFormatter ISO_LONG =
		ofPattern("+DDMMSS.SS+dddmmss.ss[+E.EE'CRS']");

	private final List<Format> _formats;

	private LocationFormatter(final List<Format> formats) {
		_formats = List.copyOf(formats);
	}

	/**
	 * Formats the given {@code location} using {@code this} formatter.
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
			"Invalid format '%s' for location %s.", toPattern(), location
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
			.map(Format::toPattern)
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
	public String format(
		final Latitude lat,
		final Longitude lon,
		final Length ele
	) {
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

	/**
	 * Parses the text using this formatter, providing control over the text
	 * position. This parses the text without requiring the parse to start from
	 * the beginning of the string or finish at the end. The text will be parsed
	 * from the specified start ParsePosition. The entire length of the text
	 * does not have to be parsed, the {@link ParsePosition} will be updated with
	 * the index at the end of parsing.
	 *
	 * @param text the text to parse, not null
	 * @param pos the position to parse from, updated with length parsed and the
	 *            index of any error, not null
	 * @return the parsed Location, not null
	 * @throws ParseException - if unable to parse the requested result
	 * @throws IndexOutOfBoundsException - if the position is invalid
	 */
	public Location parse(final CharSequence text, final ParsePosition pos) {
		requireNonNull(text);
		requireNonNull(pos);

		if (pos.getIndex() < 0 || text.length() <= pos.getIndex()) {
			throw new IndexOutOfBoundsException(pos.getIndex());
		}

		final var builder = new LocationBuilder();
		for (var format : _formats) {
			format.parse(text, pos, builder);
		}

		return builder.build();
	}

	/**
	 * Fully parses the text producing a location. This parses the entire text
	 * producing a location. It is typically more useful to use
	 * {@link #parse(CharSequence, ParsePosition)}. If the parse completes
	 * without reading the entire length of the text, or a problem occurs during
	 * parsing or merging, then an exception is thrown.
	 *
	 * @param text the text to parse, not null
	 * @return the parsed temporal object, not null
	 * @throws ParseException if unable to parse the requested result
	 */
	public Location parse(final CharSequence text) {
		requireNonNull(text);

		final var pos = new ParsePosition(0);
		final var location = parse(text, pos);
		if (pos.getIndex() != text.length()) {
			throw new ParseException("Not all input used", text, pos.getIndex());
		}

		return location;
	}

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
		requireNonNull(pattern);

		return new Builder()
			.appendPattern(pattern)
			.build();
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

		/**
		 * The formats that will go into the LocationFormatter.
		 */
		private final List<Format> _formats = new ArrayList<>();

		private Builder() {
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
		Builder appendPattern(final String pattern) {
			parsePattern(pattern);
			return this;
		}

		/**
		 * Completes this builder by creating the {@code LocationFormatter}.
		 *
		 * @return a new location-formatter object
		 * @throws IllegalArgumentException invalid pattern
		 */
		LocationFormatter build() {
			validate();
			return new LocationFormatter(_formats);
		}

		private void validate(){
			LatitudeDegree D = null;
			LatitudeMinute M = null;
			LatitudeSecond S = null;
			LatitudeNS X = null;
			LongitudeDegree d = null;
			LongitudeMinute m = null;
			LongitudeSecond s = null;
			LongitudeEW x = null;
			Elevation E = null;

			for (var format : _formats) {
				if (format instanceof LatitudeDegree) {
					if (D == null) {
						D = (LatitudeDegree)format;
					} else {
						throw iae("Only one 'D' pattern allowed.");
					}
				} else if (format instanceof LatitudeMinute) {
					if (M == null) {
						M = (LatitudeMinute)format;
					} else {
						throw iae("Only one 'M' pattern allowed.");
					}
				} else if (format instanceof LatitudeSecond) {
					if (S == null) {
						S = (LatitudeSecond)format;
					} else {
						throw iae("Only one 'S' pattern allowed.");
					}
				} else if (format instanceof LatitudeNS && X==null) {
					X = (LatitudeNS)format;
				} else if (format instanceof LongitudeDegree) {
					if (d == null) {
						d = (LongitudeDegree)format;
					} else {
						throw iae("Only one 'd' pattern allowed.");
					}
				} else if (format instanceof LongitudeMinute) {
					if (m == null) {
						m = (LongitudeMinute)format;
					} else {
						throw iae("Only one 'm' pattern allowed.");
					}
				} else if (format instanceof LongitudeSecond) {
					if (s == null) {
						s = (LongitudeSecond)format;
					} else {
						throw iae("Only one 's' pattern allowed.");
					}
				} else if (format instanceof LongitudeEW && x == null) {
					x = (LongitudeEW) format;
				} else if (format instanceof Elevation){
					if (E == null) {
						E = (Elevation)format;
					} else {
						throw iae("Only one 'E' pattern allowed.");
					}
				}
			}

			// Validating latitude /////////////////////////////////////////////

			if (D == null && M != null) {
				throw iae("No 'M' without 'D'.");
			}
			if (M == null && S != null) {
				throw iae("No 'S' without 'M'.");
			}

			// If X, D without sign.
			if (X != null && D != null && D.isPrefixSign()) {
				throw iae("If 'X' in pattern, 'D' must be without '+'.");
			}

			// If D has fractional, no M or S
			if (D != null &&
				0 < D._numberFormat.getMinimumFractionDigits()
				&& M != null)
			{
				throw iae("If 'D' has fraction, no 'M' or 'S' allowed.");
			}

			// If M has fractional, no S
			if (M != null &&
				0 < M._numberFormat.getMinimumFractionDigits() &&
				S != null)
			{
				throw iae("If 'M' has fraction, no 'S' allowed.");
			}

			// Validating longitude ////////////////////////////////////////////

			if (d == null && m != null) {
				throw iae("No 'm' without 'd'.");
			}
			if (m == null && s != null) {
				throw iae("No 's' without 'm'.");
			}

			// If x, d without sign.
			if (x != null && d != null && d.isPrefixSign()) {
				throw iae("If 'x' in pattern, 'd' must be without '+'.");
			}

			// If d has fractional, no m or s
			if (d != null &&
				0 < d._numberFormat.getMinimumFractionDigits() &&
				m != null)
			{
				throw iae("If 'd' has fraction, no 'm' or 's' allowed.");
			}

			// If m has fractional, no s.
			if (m != null &&
				0 < m._numberFormat.getMinimumFractionDigits() &&
				s != null)
			{
				throw iae("If 'm' has fraction, no 's' allowed.");
			}

			// This is still construction, not a validity check. ///////////////

			if (X != null && D != null) {
				D.setAbsolute(true);
			}
			if (M != null) {
				D.setTruncate(true);
			}
			if (S != null) {
				M.setTruncate(true);
			}

			if (x != null && d != null) {
				d.setAbsolute(true);
			}
			if (m != null) {
				d.setTruncate(true);
			}
			if (s != null) {
				m.setTruncate(true);
			}
		}

		private static IllegalArgumentException iae(final String message) {
			return new IllegalArgumentException(message);
		}

		private void parsePattern(final String pattern) {
			requireNonNull(pattern);

			// The formats we've collected and that are not yet added to
			// _formats. They may be added to _formats directly or be bundled
			// into an Optional first.
			final List<Format> formats = new ArrayList<>();

			boolean optional = false; // Inside [ ] ?
			int signs = 0; // How many unprocessed '+' ?
			boolean quote = false; // last was ' ?

			final var tokens = new Tokens(tokenize(pattern));
			while (tokens.hasNext()) {
				var token = tokens.next();
				switch (token) {
					case "X": {
						List<Format> fs = optional ? formats : _formats;
						for (int i = 0; i < signs; ++i) fs.add(Plus.INSTANCE);
						signs = 0;
						fs.add(LatitudeNS.INSTANCE);
						break;
					}
					case "x": {
						List<Format> fs = optional ? formats : _formats;
						for (int i = 0; i < signs; ++i) fs.add(Plus.INSTANCE);
						signs = 0;
						fs.add(LongitudeEW.INSTANCE);
						break;
					}
					case "+":
						++signs;
						break;
					case "[": {
						if (optional) {
							throw iae("No nesting '[' (optional) allowed.");
						}
						for (int i = 0; i < signs; i++) {
							_formats.add(Plus.INSTANCE);
						}
						signs = 0;
						optional = true;
						break;
					}
					case "]": {
						if (!optional) {
							throw iae("Missing open '[' bracket.");
						}
						// Formats will be bundled into Optional and added to
						// _formats.
						for (int i = 0; i < signs; i++) {
							formats.add(Plus.INSTANCE);
						}
						signs = 0;
						optional = false;
						_formats.add(OptionalFormat.of(formats));
						formats.clear();
						break;
					}
					case "'": {
						List<Format> fs = optional ? formats : _formats;
						for (int i = 0; i < signs; ++i)
							fs.add(Plus.INSTANCE);

						if (tokens.after().filter("'"::equals).isPresent()) {
							fs.add(ConstFormat.of("'"));
							tokens.next();
							break;
						}
						if (quote) {
							if (tokens.before().isPresent()) {
								fs.add(ConstFormat.of(
									tokens.before()
										.orElseThrow(AssertionError::new)
								));
							}
							quote = false;
						} else {
							quote = true;
						}
						break;
					}
					default: {
						List<Format> fs = optional ? formats : _formats;
						if (!quote) {
							final var field = Field.ofPattern(token);
							if (field.isPresent()) {
								final var f = field.get();

								// Maybe first add some sign formats.
								if (0 < signs) {
									// One goes to the field.
									f.setPrefixSign(true);
									for (int i = 1; i < signs; i++) {
										// The rest will be Plus.
										fs.add(Plus.INSTANCE);
									}
								}

								fs.add(f);
							} else {
								fs.add(ConstFormat.of(token));
							}
						}
						signs = 0;
						break;
					}
				}
			}

			// Maybe there are still signs left over.
			for (int i = 0; i < signs; i++) {
				formats.add(Plus.INSTANCE);
			}

			if (optional) {
				throw iae("No closing ']' found.");
			}
			if (quote) {
				throw iae("Missing closing ' character.");
			}

			_formats.addAll(formats);
		}

		static List<String> tokenize(final String pattern) {
			final var tokens = new ArrayList<String>();
			final var token = new StringBuilder();

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
						if (c != pc &&
							pc != '\0' &&
							pc != '.' &&
							pc != ',' &&
							!quote)
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
			_tokens = List.copyOf(tokens);
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

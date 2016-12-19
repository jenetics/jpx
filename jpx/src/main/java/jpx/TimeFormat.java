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
package jpx;

import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static java.util.Objects.requireNonNull;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DecimalStyle;
import java.time.format.ResolverStyle;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
enum  TimeFormat {

	// http://books.xmlschemata.org/relaxng/ch19-77049.html

	// 2001-10-26T21:32:52
	ISO_DATE_TIME(
		"^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$",
		new DateTimeFormatterBuilder()
			.append(ISO_LOCAL_DATE_TIME)
			.optionalStart()
			.appendOffsetId()
			.toFormatter()
			.withZone(UTC)
	),

	// 2001-10-26T21:32:52+02:00
	ISO_DATE_TIME2(
		"\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}([+-]\\d{2}:\\d{2})",
		new DateTimeFormatterBuilder()
			.append(ISO_LOCAL_DATE_TIME)
			.optionalStart()
			.appendOffsetId()
			.toFormatter()
	),

	// 2001-10-26T21:32:52Z
	ISO_DATE_TIME3(
		"\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z",
			new DateTimeFormatterBuilder()
			.append(ISO_LOCAL_DATE_TIME)
			.optionalStart()
			.appendOffsetId()
			.toFormatter()
			.withZone(UTC)
	),

	// 2001-10-26T21:32:52Z
	ISO_DATE_TIME4(
		"\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{1,9}",
			new DateTimeFormatterBuilder()
			.append(ISO_LOCAL_DATE_TIME)
			.optionalStart()
			.appendOffsetId()
			.toFormatter()
			.withZone(UTC)
	)

	;



	/*
	ISO_TIMESTAMP_OFFSET(
		"^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}([+-]\\d{4})$",
		DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	),



	ISO_DATE_TIME2(
		"^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{1}$",
		DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.S")
	),

	ISO_DATE_TIME3(
		"^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{2}$",
		DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS")
	),

	ISO_DATE_TIME4(
		"^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}$",
		DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
	),

	ISO_TIMESTAMP_OFFSET2(
		"^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}([+-]\\d{4}\\[*?\\])$",
		DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	);
	*/

//	/**
//	 * ISO-8601 time format: <i>HH:mm:ss.SSSZ</i>
//	 */
//	ISO_TIME_OFFSET(
//		"^\\d{2}:\\d{2}:\\d{2}.\\d{3}([+-]\\d{4}|Z)$",
//		"HH:mm:ss.SSSZ"
//	),
//
//	/**
//	 * ISO-8601 date time format: <i>yyyy-MM-dd'T'HH:mm:ss</i>
//	 */
//	ISO_DATE_TIME(
//		"^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$",
//		"yyyy-MM-dd'T'HH:mm:ss"
//	),
//
//	/**
//	 * ISO-8601 date time format: <i>yyyy-MM-dd'T'HH:mm:ssZ</i>
//	 */
//	ISO_DATE_TIME_OFFSET(
//		"^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}([+-]\\d{4}|Z)$",
//		"yyyy-MM-dd'T'HH:mm:ssZ"
//	),
//
//	/**
//	 * ISO-8601 date time format: <i>yyyy-MM-dd'T'HH:mm:ss'Z'</i>
//	 * UTC (Zulu) Timezone
//	 */
//	ISO_DATE_TIME_ZULU(
//		"^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z$",
//		"yyyy-MM-dd'T'HH:mm:ss'Z'"
//	),
//
//	/**
//	 * ISO-8601 timestamp format: <i>yyyy-MM-dd'T'HH:mm:ss.SSS</i>
//	 */
//	ISO_TIMESTAMP(
//		"^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}$",
//		"yyyy-MM-dd'T'HH:mm:ss.SSS"
//	),
//
//	/**
//	 * Number date format only: <i>yyyMMdd</i>
//	 */
//	ISO_NUMERIC_DATE(
//		"^\\d{8}$",
//		"yyyyMMdd"
//	),
//
//	/**
//	 * Number date time format only: <i>yyyMMddHHmmss</i>
//	 */
//	ISO_NUMERIC_DATE_TIME(
//		"^\\d{14}$",
//		"yyyyMMddHHmmss"
//	),
//
//	JAVASCRIPT_DATE_TIME(
//		"^[a-zA-Z]{3} [a-zA-Z]{3} \\d{2} \\d{4} \\d{2}:\\d{2}:\\d{2} GMT[+-]{1}\\d{4}$",
//		"EEE MMM dd yyyy HH:mm:ss 'GMT'Z"
//	),
//
//	/**
//	 * Custom ISO date time format: <i>yyyy-MM-dd HH:mm:ss</i>
//	 */
//	CUSTOM_ISO_DATE_TIME(
//		"^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$",
//		"yyyy-MM-dd HH:mm:ss"
//	),
//
//	/**
//	 * Custom ISO date time format: <i>yyyy-MM-dd HH:mm:ss</i>
//	 */
//	CUSTOM_ISO_DATE_TIME_MILLIS(
//		"^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}$",
//		"yyyy-MM-dd HH:mm:ss.SSS"
//	),
//
//	/**
//	 * Custom ISO date time format with offset and without millis:
//	 * <i>yyyy-MM-dd HH:mm:ssZ</i>
//	 */
//	CUSTOM_ISO_DATE_TIME_OFFSET(
//		"^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}([+-]\\d{2,4}|Z)$",
//		"yyyy-MM-dd HH:mm:ssZ"
//	),
//
//	/**
//	 * Date time format with offset and without millis used in
//	 * Apache log files. E.g.: 26/Mar/2015:04:39:15 +0100
//	 * <i>dd/MMM/yyyy:HH:mm:ss Z</i>
//	 */
//	APACHE_LOG_DATE_TIME_OFFSET(
//		"^\\d{2}/[a-z,A-Z]{3}/\\d{4}:\\d{2}:\\d{2}:\\d{2} ([+-]\\d{2,4}|Z)$",
//		"dd/MMM/yyyy:HH:mm:ss Z"
//	),
//
//	/**
//	 * Date time format without offset and without time as used in EBAY user report.
//	 * <i>dd/MM/yyyy</i>
//	 */
//	UK_DATE(
//		"^\\d{2}/\\d{2}/\\d{4}$",
//		"dd/MM/yyyy"
//	);

	private final Pattern _pattern;
	private final DateTimeFormatter _formatter;

	private TimeFormat(final Pattern pattern, final DateTimeFormatter formatter) {
		_pattern = requireNonNull(pattern);
		_formatter = requireNonNull(formatter);
	}

	private TimeFormat(final String pattern, final DateTimeFormatter formatter) {
		this(Pattern.compile(pattern), formatter);
	}

	private boolean matches(final String time) {
		return _pattern.matcher(time).matches();
	}

	public String format(final ZonedDateTime time) {
		return _formatter.format(time);
	}

	public ZonedDateTime parse(final String time) {
		return ZonedDateTime.parse(time, _formatter.withZone(ZoneId.systemDefault()));
	}

	public static Optional<TimeFormat> findFormatFor(final String time) {
		return Stream.of(values())
			.filter(tf -> tf.matches(time))
			.findFirst();
	}

	public static Optional<ZonedDateTime> parseOption(final String time) {
		return Stream.of(values())
			.filter(tf -> tf.matches(time))
			.map(tf -> tf.parse(time))
			.findFirst();
	}

}

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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 3.0
 * @since 1.2
 */
final class Instants {

	private Instants() {
	}

	static void write(final Instant time, final DataOutput out)
		throws IOException
	{
		IO.writeLong(time.toEpochMilli(), out);
	}

	static Instant read(final DataInput in) throws IOException {
		return Instant.ofEpochMilli(IO.readLong(in));
	}

	/**
	 * Return the hash code of the given date time object. Actually the hash
	 * code of its {@link Instant}, truncated to milliseconds, is returned. The
	 * argument may be {@code null}.
	 *
	 * @param a the instant, for which the hash code is calculated
	 * @return the <em>truncated</em> hash code
	 */
	static int hashCode(final Instant a) {
		return Objects.hashCode(truncate(a));
	}

	private static Instant truncate(final Instant instant) {
		return instant != null
			? instant.truncatedTo(ChronoUnit.MILLIS)
			: null;
	}

	/**
	 * Tests if the given date times represents the same point on the time-line.
	 * The used resolution for comparison is <em>seconds</em>. If two
	 * {@link Instant} objects are equal to its seconds, they are treated
	 * as equal, even if the millisecond part is different. The argument may be
	 * {@code null}.
	 *
	 * @param a the first date time
	 * @param b the second date time
	 * @return {@code true} if the two date times represents the same point on
	 *         the time-line, {@code false} otherwise
	 */
	static boolean equals(final Instant a, final Instant b) {
		return Objects.equals(truncate(a), truncate(b));
	}

}

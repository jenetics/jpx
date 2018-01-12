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
package io.jenetics.jpx;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class ZonedDateTimes {

	private ZonedDateTimes() {
	}

	static void write(final ZonedDateTime time, final DataOutput out)
		throws IOException
	{
		IO.writeLong(time.toEpochSecond(), out);
		writeZoneOffset(time.getOffset(), out);
	}

	private static
	void writeZoneOffset(final ZoneOffset os, final DataOutput out)
		throws IOException
	{
		final int offsetSecs = os.getTotalSeconds();
		int offsetByte = offsetSecs%900 == 0 ? offsetSecs/900 : 127;
		out.writeByte(offsetByte);
		if (offsetByte == 127) {
			IO.writeInt(offsetSecs, out);
		}
	}

	static ZonedDateTime read(final DataInput in) throws IOException {
		final long seconds = IO.readLong(in);
		final ZoneOffset offset = readZoneOffset(in);
		return ZonedDateTime.ofInstant(Instant.ofEpochSecond(seconds), offset);
	}

	private static ZoneOffset readZoneOffset(final DataInput in)
		throws IOException
	{
		int offsetByte = in.readByte();
		return offsetByte == 127
			? ZoneOffset.ofTotalSeconds(IO.readInt(in))
			: ZoneOffset.ofTotalSeconds(offsetByte*900);
	}

	/**
	 * Tests if the given date times represents the same point on the time-line.
	 *
	 * @param a the first date time
	 * @param b the second date time
	 * @return {@code true} if the two date times represents the same point on
	 *         the time-line, {@code false} otherwise
	 */
	static boolean equals(final ZonedDateTime a, final ZonedDateTime b) {
		final Instant i1 = a != null
			? a.toInstant().truncatedTo(ChronoUnit.SECONDS)
			: null;
		final Instant i2 = b != null
			? b.toInstant().truncatedTo(ChronoUnit.SECONDS)
			: null;

		return Objects.equals(i1, i2);
	}

}

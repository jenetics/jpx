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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class IO {

	static String readNullableString(final DataInput in) throws IOException {
		final int length = in.readInt();
		String result = null;
		if (length >= 0) {
			final byte[] bytes = new byte[length];
			in.readFully(bytes);
			result = new String(bytes, "UTF-8");
		}
		return result;
	}

	static void writeNullableString(final String value, final DataOutput out)
		throws IOException
	{
		if (value == null) {
			out.writeInt(-1);
		} else {
			writeString(value, out);
		}
	}

	static void writeString(final String value, final DataOutput out)
		throws IOException
	{
		final byte[] bytes = value.getBytes("UTF-8");
		out.writeInt(bytes.length);
		out.write(bytes);
	}

	static String readString(final DataInput in) throws IOException {
		final byte[] bytes = new byte[in.readInt()];
		in.readFully(bytes);
		return new String(bytes, "UTF-8");
	}

	static void writeZonedDateTime(final ZonedDateTime time, final DataOutput out)
		throws IOException
	{
		/*
		out.writeInt(time.getYear());
		out.writeByte(time.getMonthValue());
		out.writeByte(time.getDayOfMonth());

		out.writeByte(time.getHour());
		out.writeByte(time.getMinute());
		out.writeByte(time.getSecond());
		writeZoneOffset(time.getOffset(), out);
		*/
		out.writeLong(time.toInstant().toEpochMilli());
	}

	static ZonedDateTime readZonedDateTime(final DataInput in) throws IOException {
		/*
		final int year = in.readInt();
		final int month = in.readByte();
		final int day = in.readByte();
		final int hour = in.readByte();
		final int minute = in.readByte();
		final int second = in.readByte();
		final ZoneOffset offset = readZoneOffset(in);
		*/
		/*
		return return ZonedDateTime.ofLenient(
			dateTime, offset, zone
		);
		*/

		final Instant instant = Instant.ofEpochMilli(in.readLong());
		return ZonedDateTime.ofInstant(instant, ZoneOffset.UTC);
	}

	private static void writeZoneOffset(final ZoneOffset offset, final DataOutput out)
		throws IOException
	{
		final int offsetSecs = offset.getTotalSeconds();
		int offsetByte = offsetSecs % 900 == 0 ? offsetSecs / 900 : 127;
		out.writeByte(offsetByte);
		if (offsetByte == 127) {
			out.writeInt(offsetSecs);
		}
	}

	private static ZoneOffset readZoneOffset(final DataInput in) throws IOException {
		int offsetByte = in.readByte();
		return (offsetByte == 127
			? ZoneOffset.ofTotalSeconds(in.readInt())
			: ZoneOffset.ofTotalSeconds(offsetByte * 900));
	}

	interface Writer<T> {
		void write(final T value, final DataOutput out) throws IOException;
	}

	interface Reader<T> {
		T read(final DataInput in) throws IOException;
	}

	static <T> void writes(
		final Collection<? extends T> elements,
		final Writer<? super T> writer,
		final DataOutput out
	)
		throws IOException
	{
		out.writeInt(elements.size());
		for (T element : elements) {
			writer.write(element, out);
		}
	}

	static <T> List<T> reads(
		final Reader<? extends T> reader,
		final DataInput in
	)
		throws IOException
	{
		final int length = in.readInt();
		final List<T> elements = new ArrayList<>(length);
		for (int i = 0; i < length; ++i) {
			elements.add(reader.read(in));
		}
		return elements;
	}

}

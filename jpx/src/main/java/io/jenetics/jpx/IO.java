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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class IO {

	interface Writer<T> {
		void write(final T value, final DataOutput out) throws IOException;
	}

	interface Reader<T> {
		T read(final DataInput in) throws IOException;
	}

	static <T> void writeNullable(
		final T value,
		final Writer<? super T> writer,
		final DataOutput out
	)
		throws IOException
	{
		out.writeBoolean(value != null);
		if (value != null) {
			writer.write(value, out);
		}
	}

	static <T> T readNullable(
		final Reader<? extends T> reader,
		final DataInput in
	)
		throws IOException
	{
		T value = null;
		if (in.readBoolean()) {
			value = reader.read(in);
		}

		return value;
	}

	static void writeNullableString(final String value, final DataOutput out)
		throws IOException
	{
		writeNullable(value, IO::writeString, out);
	}

	static String readNullableString(final DataInput in) throws IOException {
		return readNullable(IO::readString, in);
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
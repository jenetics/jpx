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

import static java.nio.charset.StandardCharsets.UTF_8;
import static io.jenetics.jpx.Lists.copyOf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Helper methods needed for implementing the Java serializations.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.2
 * @since 1.2
 */
final class IO {

	private IO() {
	}

	/**
	 * Object writer interface.
	 *
	 * @param <T> the object type
	 */
	@FunctionalInterface
	interface Writer<T> {
		void write(final T value, final DataOutput out) throws IOException;
	}

	/**
	 * Object reader interface
	 *
	 * @param <T> the object type
	 */
	@FunctionalInterface
	interface Reader<T> {
		T read(final DataInput in) throws IOException;
	}

	/**
	 * Write the given, possible {@code null}, {@code value} to the data output
	 * using the given {@code writer}.
	 *
	 * @param value the, possible {@code null}, value to write
	 * @param writer the object writer
	 * @param out the data output
	 * @param <T> the object type
	 * @throws NullPointerException if the {@code writer} or data output is
	 *         {@code null}
	 * @throws IOException if an I/O error occurs
	 */
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

	/**
	 * Reads a possible {@code null} value from the given data input.
	 *
	 * @param reader the object reader
	 * @param in the data input
	 * @param <T> the object type
	 * @return the read object
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 * @throws IOException if an I/O error occurs
	 */
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

	/**
	 * Write the given string {@code value} to the given data output.
	 *
	 * @param value the string value to write
	 * @param out the data output
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 * @throws IOException if an I/O error occurs
	 */
	static void writeString(final String value, final DataOutput out)
		throws IOException
	{
		final byte[] bytes = value.getBytes(UTF_8);
		writeInt(bytes.length, out);
		out.write(bytes);
	}

	/**
	 * Reads a string value from the given data input.
	 *
	 * @param in the data input
	 * @return the read string value
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 * @throws IOException if an I/O error occurs
	 */
	static String readString(final DataInput in) throws IOException {
		final byte[] bytes = new byte[readInt(in)];
		in.readFully(bytes);
		return new String(bytes, UTF_8);
	}

	/**
	 * Write the given string, possible {@code null}, {@code value} to the given
	 * data output.
	 *
	 * @param value the string value to write
	 * @param out the data output
	 * @throws NullPointerException if the given data output is {@code null}
	 * @throws IOException if an I/O error occurs
	 */
	static void writeNullableString(final String value, final DataOutput out)
		throws IOException
	{
		writeNullable(value, IO::writeString, out);
	}

	/**
	 * Reads a, possible {@code null}, string value from the given data input.
	 *
	 * @param in the data input
	 * @return the read string value
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 * @throws IOException if an I/O error occurs
	 */
	static String readNullableString(final DataInput in) throws IOException {
		return readNullable(IO::readString, in);
	}

	/**
	 * Write the given elements to the data output.
	 *
	 * @param elements the elements to write
	 * @param writer the element writer
	 * @param out the data output
	 * @param <T> the element type
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 * @throws IOException if an I/O error occurs
	 */
	static <T> void writes(
		final Collection<? extends T> elements,
		final Writer<? super T> writer,
		final DataOutput out
	)
		throws IOException
	{
		writeInt(elements.size(), out);
		for (T element : elements) {
			writer.write(element, out);
		}
	}

	/**
	 * Reads a list of elements from the given data input.
	 *
	 * @param reader the element reader
	 * @param in the data input
	 * @param <T> the element type
	 * @return the read element list
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 * @throws IOException if an I/O error occurs
	 */
	static <T> List<T> reads(
		final Reader<? extends T> reader,
		final DataInput in
	)
		throws IOException
	{
		final int length = readInt(in);
		final List<T> elements = new ArrayList<>(length);
		for (int i = 0; i < length; ++i) {
			elements.add(reader.read(in));
		}
		return copyOf(elements);
	}

	/**
	 * Writes an int value to a series of bytes. The values are written using
	 * <a href="http://lucene.apache.org/core/3_5_0/fileformats.html#VInt">variable-length</a>
	 * <a href="https://developers.google.com/protocol-buffers/docs/encoding?csw=1#types">zig-zag</a>
	 * coding. Each {@code int} value is written in 1 to 5 bytes.
	 *
	 * @see #readInt(DataInput)
	 *
	 * @param value the integer value to write
	 * @param out the data output the integer value is written to
	 * @throws NullPointerException if the given data output is {@code null}
	 * @throws IOException if an I/O error occurs
	 */
	static void writeInt(final int value, final DataOutput out) throws IOException {
		// Zig-zag encoding.
		int n = (value << 1)^(value >> 31);
		if ((n & ~0x7F) != 0) {
			out.write((byte)((n | 0x80) & 0xFF));
			n >>>= 7;
			if (n > 0x7F) {
				out.write((byte)((n | 0x80) & 0xFF));
				n >>>= 7;
				if (n > 0x7F) {
					out.write((byte)((n | 0x80) & 0xFF));
					n >>>= 7;
					if (n > 0x7F) {
						out.write((byte)((n | 0x80) & 0xFF));
						n >>>= 7;
					}
				}
			}
		}
		out.write((byte)n);
	}

	/**
	 * Reads an int value from the given data input. The integer value must have
	 * been written by the {@link #writeInt(int, DataOutput)} before.
	 *
	 * @see #writeInt(int, DataOutput)
	 *
	 * @param in the data input where the integer value is read from
	 * @return the read integer value
	 * @throws NullPointerException if the given data input is {@code null}
	 * @throws IOException if an I/O error occurs
	 */
	static int readInt(final DataInput in) throws IOException {
		int b = in.readByte() & 0xFF;
		int n = b & 0x7F;

		if (b > 0x7F) {
			b = in.readByte() & 0xFF;
			n ^= (b & 0x7F) << 7;
			if (b > 0x7F) {
				b = in.readByte() & 0xFF;
				n ^= (b & 0x7F) << 14;
				if (b > 0x7F) {
					b = in.readByte() & 0xFF;
					n ^= (b & 0x7F) << 21;
					if (b > 0x7F) {
						b = in.readByte() & 0xFF;
						n ^= (b & 0x7F) << 28;
						if (b > 0x7F) {
							throw new IOException("Invalid int encoding.");
						}
					}
				}
			}
		}

		return (n >>> 1)^-(n & 1);
	}

	/**
	 * Writes a long value to a series of bytes. The values are written using
	 * <a href="http://lucene.apache.org/core/3_5_0/fileformats.html#VInt">variable-length</a>
	 * <a href="https://developers.google.com/protocol-buffers/docs/encoding?csw=1#types">zig-zag</a>
	 * coding. Each {@code long} value is written in 1 to 10 bytes.
	 *
	 * @see #readLong(DataInput)
	 *
	 * @param value the long value to write
	 * @param out the data output the integer value is written to
	 * @throws NullPointerException if the given data output is {@code null}
	 * @throws IOException if an I/O error occurs
	 */
	static void writeLong(final long value, final DataOutput out)
		throws IOException
	{
		// Zig-zag encoding.
		long n = (value << 1)^(value >> 63);
		if ((n & ~0x7FL) != 0) {
			out.write((byte)((n | 0x80) & 0xFF));
			n >>>= 7;
			if (n > 0x7F) {
				out.write((byte)((n | 0x80) & 0xFF));
				n >>>= 7;
				if (n > 0x7F) {
					out.write((byte)((n | 0x80) & 0xFF));
					n >>>= 7;
					if (n > 0x7F) {
						out.write((byte)((n | 0x80) & 0xFF));
						n >>>= 7;
						if (n > 0x7F) {
							out.write((byte)((n | 0x80) & 0xFF));
							n >>>= 7;
							if (n > 0x7F) {
								out.write((byte)((n | 0x80) & 0xFF));
								n >>>= 7;
								if (n > 0x7F) {
									out.write((byte)((n | 0x80) & 0xFF));
									n >>>= 7;
									if (n > 0x7F) {
										out.write((byte)((n | 0x80) & 0xFF));
										n >>>= 7;
										if (n > 0x7F) {
											out.write((byte)((n | 0x80) & 0xFF));
											n >>>= 7;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		out.write((byte)n);
	}

	/**
	 * Reads a long value from the given data input. The integer value must have
	 * been written by the {@link #writeInt(int, DataOutput)} before.
	 *
	 * @see #writeLong(long, DataOutput)
	 *
	 * @param in the data input where the integer value is read from
	 * @return the read long value
	 * @throws NullPointerException if the given data input is {@code null}
	 * @throws IOException if an I/O error occurs
	 */
	static long readLong(final DataInput in) throws IOException {
		int b = in.readByte() & 0xFF;
		int n = b & 0x7F;
		long l;
		if (b > 0x7F) {
			b = in.readByte() & 0xFF;
			n ^= (b & 0x7F) << 7;
			if (b > 0x7F) {
				b = in.readByte() & 0xFF;
				n ^= (b & 0x7F) << 14;
				if (b > 0x7F) {
					b = in.readByte() & 0xFF;
					n ^= (b & 0x7F) << 21;
					if (b > 0x7F) {
						l = innerLongDecode(n, in);
					} else {
						l = n;
					}
				} else {
					l = n;
				}
			} else {
				l = n;
			}
		} else {
			l = n;
		}
		return (l >>> 1)^-(l & 1);
	}

	private static long innerLongDecode(long l, final DataInput in)
		throws IOException
	{
		int b = in.readByte() & 0xFF;
		l ^= (b & 0x7FL) << 28;
		if (b > 0x7F) {
			b = in.readByte() & 0xFF;
			l ^= (b & 0x7FL) << 35;
			if (b > 0x7F) {
				b = in.readByte() & 0xFF;
				l ^= (b & 0x7FL) << 42;
				if (b > 0x7F) {
					b = in.readByte() & 0xFF;
					l ^= (b & 0x7FL) << 49;
					if (b > 0x7F) {
						b = in.readByte() & 0xFF;
						l ^= (b & 0x7FL) << 56;
						if (b > 0x7F) {
							b = in.readByte() & 0xFF;
							l ^= (b & 0x7FL) << 63;
							if (b > 0x7F) {
								throw new IOException("Invalid long encoding.");
							}
						}
					}
				}
			}
		}
		return l;
	}

	/**
	 * Write the given XML document to the given data output.
	 *
	 * @since 1.5
	 *
	 * @param value the value to write
	 * @param out the data output the integer value is written to
	 * @throws NullPointerException if one of the arguments is {@code null}
	 * @throws IOException if an I/O error occurs
	 */
	static void write(final Document value, final DataOutput out)
		throws IOException
	{
		final ByteArrayOutputStream bout = new ByteArrayOutputStream();
		XML.copy(value, bout);

		final byte[] data = bout.toByteArray();
		IO.writeInt(data.length, out);
		out.write(data);
	}

	/**
	 * Reads a long value from the given data input. The integer value must have
	 * been written by the {@link #writeInt(int, DataOutput)} before.
	 *
	 * @since 1.5
	 *
	 * @param in the data input where the value is read from
	 * @return the read XML document
	 * @throws NullPointerException if the given data input is {@code null}
	 * @throws IOException if an I/O error occurs
	 */
	static Document readDoc(final DataInput in) throws IOException {
		final int length = IO.readInt(in);
		final byte[] data = new byte[length];
		in.readFully(data);

		final ByteArrayInputStream bin = new ByteArrayInputStream(data);
		try {
			return XMLProvider
				.provider()
				.documentBuilderFactory()
				.newDocumentBuilder()
				.parse(bin);
		} catch (ParserConfigurationException|SAXException e) {
			throw new IOException(e);
		}
	}

}

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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package jpx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Random;
import java.util.function.Supplier;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public abstract class XMLStreamTestBase<T> {

	public static interface XMLWriter<T> {
		void write(T value, final XMLStreamWriter writer) throws XMLStreamException;
	}

	public static final class Params<T> {
		public final Supplier<T> supplier;
		public final XMLReader<T> reader;
		public final XMLWriter<T> writer;

		public Params(
			final Supplier<T> supplier,
			final XMLReader<T> reader,
			final XMLWriter<T> writer
		) {
			this.supplier = supplier;
			this.reader = reader;
			this.writer = writer;
		}
	}

	private final Random _random = new Random(1000);

	protected abstract Params<T> params(final Random random);


	@Test(invocationCount = 10)
	public void marshalling() throws Exception {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final XMLStreamWriter writer = XMLOutputFactory.newInstance()
			.createXMLStreamWriter(out);

		final Params<T> params = params(_random);

		final T expected = params.supplier.get();
		final byte[] marshaled = toBytes(expected, params.writer);
		final T actual = fromBytes(marshaled, params.reader);

		Assert.assertEquals(actual, expected);
	}

	private static <T> byte[] toBytes(final T value, final XMLWriter<T> writer)
		throws XMLStreamException
	{
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final XMLStreamWriter streamWriter = XMLOutputFactory.newInstance()
			.createXMLStreamWriter(out);

		writer.write(value, streamWriter);
		return out.toByteArray();
	}

	private static <T> T fromBytes(final byte[] bytes, final XMLReader<T> reader)
		throws XMLStreamException
	{
		final ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		final XMLStreamReader streamReader = XMLInputFactory
			.newFactory()
			.createXMLStreamReader(in);

		streamReader.next();
		return reader.read(streamReader);
	}

}

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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.jpx.GPX.Version;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public abstract class XMLStreamTestBase<T> extends ObjectTester<T> {

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

	protected abstract Params<T> params(final Version version, final Random random);

	public static <T> List<T> nextObjects(final Supplier<T> supplier, final Random random) {
		return Stream.generate(supplier)
			.limit(random.nextInt(20))
			.collect(Collectors.toList());
	}

	@Test(invocationCount = 10)
	public void marshallingV10() throws Exception {
		final Params<T> params = params(Version.v10, _random);

		final T object = params.supplier.get();
		byte[] marshaled = toBytes(object, params.writer);
		final T expected = fromBytes(marshaled, params.reader);

		marshaled = toBytes(expected, params.writer);
		final T actual = fromBytes(marshaled, params.reader);

		/*
		if (!Objects.equals(actual, expected)) {
			System.out.println(new String(marshaled));
			System.out.println();
			System.out.println(new String(toBytes(actual, params.writer)));
		}
		*/

		assertEquals(actual, expected);
	}

	@Test(invocationCount = 10)
	public void marshallingV11() throws Exception {
		final Params<T> params = params(Version.v11, _random);

		final T expected = params.supplier.get();
		final byte[] marshaled = toBytes(expected, params.writer);
		final T actual = fromBytes(marshaled, params.reader);

		assertEquals(actual, expected);
	}

	private void marshalling(final Version version) throws Exception {
		final Params<T> params = params(version, _random);

		final T expected = params.supplier.get();
		final byte[] marshaled = toBytes(expected, params.writer);
		final T actual = fromBytes(marshaled, params.reader);

		if (!Objects.equals(actual, expected)) {
			System.out.println(new String(marshaled));
			System.out.println();
			System.out.println(new String(toBytes(actual, params.writer)));
		}

		assertEquals(actual, expected);
	}

	void assertEquals(final T actual, final T expected) {
		Assert.assertEquals(actual, expected);
	}

	private static <T> byte[] toBytes(final T value, final XMLWriter<T> writer)
		throws XMLStreamException
	{
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final XMLOutputFactory factory = XMLOutputFactory.newFactory();
		final XMLStreamWriter streamWriter = new IndentingXMLStreamWriter(
			factory.createXMLStreamWriter(out, "UTF-8"), "    ");

		writer.write(streamWriter, value);
		return out.toByteArray();
	}

	static <T> T fromBytes(final byte[] bytes, final XMLReader<T> reader)
		throws XMLStreamException
	{
		final ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		final XMLStreamReader streamReader = XMLInputFactory
			.newFactory()
			.createXMLStreamReader(in);

		streamReader.next();
		return reader.read(streamReader, false);
	}

}

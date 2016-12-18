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

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Helper class for simplifying XML stream writing.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class XMLWriter {

	/**
	 * Represents an XML attribute.
	 */
	static final class Attr {
		final String name;
		final String value;

		private Attr(final String name, final Object value) {
			this.name = requireNonNull(name);
			this.value = requireNonNull(value).toString();
		}

		@Override
		public int hashCode() {
			int hash = 37;
			hash += 17*Objects.hashCode(name) + 31;
			hash += 17*Objects.hashCode(value) + 31;
			return hash;
		}

		@Override
		public boolean equals(final Object object) {
			return object instanceof Attr &&
				((Attr) object).name.equals(name) &&
				((Attr) object).value.equals(value);
		}

		@Override
		public String toString() {
			return format("%s=%s", name, value);
		}
	}

	/**
	 * The element writer.
	 */
	@FunctionalInterface
	interface Elem {
		void write() throws XMLStreamException;
	}

	interface Writer<T> {
		void write(final T data, final XMLStreamWriter writer) throws XMLStreamException;
	}


	private final XMLStreamWriter _writer;

	XMLWriter(final XMLStreamWriter writer) {
		_writer = requireNonNull(writer);
	}

	/**
	 * Create a new attribute with the given name and value.
	 *
	 * @param name the attribute name
	 * @param value the attribute value
	 * @return a new attribute with the given name and value
	 */
	Attr attr(final String name, final Object value) {
		return new Attr(name, value);
	}

	/**
	 * Create a new XML element writer.
	 * @param name
	 * @param text
	 * @return
	 */
	Elem elem(final String name, final Object text) {
		requireNonNull(name);

		return () -> {
			if (text != null) {
				_writer.writeStartElement(name);
				_writer.writeCharacters(text.toString());
				_writer.writeEndElement();
			}
		};
	}

	/**
	 *
	 * @param name
	 * @param object
	 * @param converter
	 * @param <T>
	 * @return
	 */
	<T> Elem elem(
		final String name,
		final T object,
		final Function<T, Object> converter
	) {
		requireNonNull(name);
		requireNonNull(converter);

		return () -> {
			if (object != null) {
				_writer.writeStartElement(name);
				_writer.writeCharacters(converter.apply(object).toString());
				_writer.writeEndElement();
			}
		};

	}

	/**
	 *
	 * @param data
	 * @param writer
	 * @param <T>
	 */
	<T> Elem elems(final Iterable<T> data, final Writer<T> writer) {
		requireNonNull(writer);
		return () -> {
			if (data != null) {
				for (T d : data) {
					if (d != null) {
						writer.write(d, _writer);
					}
				}
			}
		};
	}

	/**
	 *
	 * @param data
	 * @param writer
	 * @param <T>
	 * @return
	 * @throws XMLStreamException
	 */
	<T> Elem elem(final T data, final Writer<T> writer) {
		requireNonNull(writer);
		return () -> {
			if (data != null) {
				writer.write(data, _writer);
			}
		};
	}

	/**
	 *
	 * @param name
	 * @param firstChild
	 * @param children
	 * @throws XMLStreamException
	 */
	void write(final String name, final Elem firstChild, final Elem... children)
		throws XMLStreamException
	{
		requireNonNull(name);
		requireNonNull(children);

		_writer.writeStartElement(name);
		firstChild.write();
		for (Elem child : children) {
			child.write();
		}
		_writer.writeEndElement();
	}

	/**
	 *
	 * @param name
	 * @param attr
	 * @param firstChild
	 * @param children
	 * @throws XMLStreamException
	 */
	void write(final String name, final Attr attr, final Elem firstChild, final Elem... children)
		throws XMLStreamException
	{
		requireNonNull(name);
		requireNonNull(attr);
		requireNonNull(children);

		_writer.writeStartElement(name);
		if (attr.value != null) {
			_writer.writeAttribute(attr.name, attr.value);
		}

		firstChild.write();
		for (Elem child : children) {
			child.write();
		}
		_writer.writeEndElement();
	}

	void write(final String name, final Attr attr1, final Attr attr2, final Elem firstChild, final Elem... children)
		throws XMLStreamException
	{
		elem(name, asList(attr1, attr2), firstChild, children);
	}

	void elem(final String name, final List<Attr> attrs, final Elem firstChild, final Elem... children)
		throws XMLStreamException
	{
		requireNonNull(name);
		requireNonNull(attrs);
		requireNonNull(children);

		_writer.writeStartElement(name);
		for (Attr attr : attrs) {
			_writer.writeAttribute(attr.name, attr.value);
		}

		firstChild.write();
		for (Elem child : children) {
			child.write();
		}
		_writer.writeEndElement();
	}

	void write(final String name, final Attr... attrs)
		throws XMLStreamException
	{
		requireNonNull(name);
		requireNonNull(attrs);

		_writer.writeStartElement(name);
		for (Attr attr : attrs) {
			_writer.writeAttribute(attr.name, attr.value);
		}
		_writer.writeEndElement();
	}

}

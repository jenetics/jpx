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

import static java.util.Objects.requireNonNull;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class XMLWriter {

	interface Elem {
		void write() throws XMLStreamException;
	}

	static final class Attr {
		final String name;
		final String value;

		private Attr(final String name, final Object value) {
			this.name = requireNonNull(name);
			this.value = requireNonNull(value).toString();
		}

		static Attr of(final String name, final Object value) {
			return new Attr(name, value);
		}
	}


	private final XMLStreamWriter _writer;

	XMLWriter(final XMLStreamWriter writer) {
		_writer = requireNonNull(writer);
	}

	Attr attr(final String name, final Object value) {
		return Attr.of(name, value);
	}

	void elem(final String name, final Attr attr, final Elem firstChild, final Elem... children)
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

	void elem(final String name, final Elem firstChild, final Elem... children)
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

	void elem(final String name, final Attr... attrs)
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

	void elem(final String name, final Object text) throws XMLStreamException {
		requireNonNull(name);
		if (text != null) {
			_writer.writeStartElement(name);
			_writer.writeCharacters(text.toString());
			_writer.writeEndElement();
		}
	}

	public static void main(final String[] args) throws Exception {
		final XMLWriter xml = new XMLWriter(null);

		xml.elem("link", Attr.of("href", "asdfasdf"),
			() -> xml.elem("text", "asdfad"),
			() -> xml.elem("type", "adsf")
		);
	}

}

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
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
interface XMLReader<T> {

	static final class Attr {
		final String name;

		private Attr(final String name) {
			this.name = requireNonNull(name);
		}
	}

	static Attr attr(final String name) {
		return new Attr(name);
	}

	public String name();

	public int argSize();

	public T read(final XMLStreamReader reader) throws XMLStreamException;

	public static <T> XMLReader<T> of(
		final Function<Object[], T> creator,
		final String name,
		final Attr attr,
		final XMLReader<?>... children
	) {
		return new XMLReaderImpl<T>(
			name, singletonList(attr),
			asList(children),
			creator
		);
	}

	public static XMLReader<String> of(final String name) {
		return new XMLTextReader(name, emptyList());
	}

	public static XMLReader<String> of(final String name, final Attr... attrs) {
		return new XMLTextReader(name, Arrays.asList(attrs));
	}

}

abstract class AbstractXMLReader<T> implements XMLReader<T> {

	final String _name;
	final List<Attr> _attrs;

	AbstractXMLReader(final String name, final List<Attr> attrs) {
		_name = requireNonNull(name);
		_attrs = requireNonNull(attrs);
	}

	@Override
	public String name() {
		return _name;
	}

	@Override
	public String toString() {
		return format("XMLReader[%s]", name());
	}

}

class XMLReaderImpl<T> extends AbstractXMLReader<T> {

	private final Map<String, XMLReader<?>> _children = new HashMap<>();
	private final Function<Object[], T> _creator;

	XMLReaderImpl(
		final String name,
		final List<Attr> attrs,
		final List<XMLReader<?>> children,
		final Function<Object[], T> creator
	) {
		super(name, attrs);
		_creator = creator;

		for (XMLReader<?> child : children) {
			_children.put(child.name(), child);
		}
	}

	@Override
	public int argSize() {
		int size = _attrs.size();
		for (XMLReader<?> child : _children.values()) {
			size += child.argSize();
		}

		return size;
	}

	@Override
	public T read(final XMLStreamReader reader) throws XMLStreamException {
		final Object[] args = new Object[argSize()];
		int index = 0;
		for (; index < _attrs.size(); ++index) {
			args[index] = reader.getAttributeValue(null, _attrs.get(index).name);
		}

		while (reader.hasNext()) {
			switch (reader.next()) {
				case XMLStreamReader.START_ELEMENT:
					final XMLReader<?> child = _children.get(reader.getLocalName());
					if (child != null) {
						args[index++] = child.read(reader);
					}
				case XMLStreamReader.END_ELEMENT:
					if (name().equals(reader.getLocalName())) {
						return _creator.apply(args);
					}
			}
		}

		throw new XMLStreamException("Premature end of file.");
	}

}


final class XMLTextReader extends AbstractXMLReader<String> {

	XMLTextReader(final String name, final List<Attr> attrs) {
		super(name, attrs);
	}

	@Override
	public int argSize() {
		return _attrs.size() + 1;
	}

	@Override
	public String read(final XMLStreamReader reader) throws XMLStreamException {
		final StringBuilder result = new StringBuilder();
		while (reader.hasNext()) {
			switch (reader.next()) {
				case XMLStreamReader.CHARACTERS:
				case XMLStreamReader.CDATA:
					result.append(reader.getText());
					break;
				case XMLStreamReader.END_ELEMENT:
					if (name().equals(reader.getLocalName())) {
						return result.toString();
					}
			}
		}

		throw new XMLStreamException("Premature end of file.");
	}

}

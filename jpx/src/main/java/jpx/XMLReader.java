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

	public static <T> XMLReader<T> of(
		final Function<Object[], T> creator,
		final String name,
		final XMLReader<?>... children
	) {
		return new XMLReaderImpl<T>(
			name, emptyList(),
			asList(children),
			creator
		);
	}

	public static <T> XMLReader<T> of(
		final Function<Object[], T> creator,
		final String name,
		final Attr... attrs
	) {
		return new XMLReaderImpl<T>(
			name, asList(attrs),
			emptyList(),
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

	private final List<XMLReader<?>> _children;
	private final Map<String, XMLReader<?>> _childMap = new HashMap<>();
	private final Function<Object[], T> _creator;

	XMLReaderImpl(
		final String name,
		final List<Attr> attrs,
		final List<XMLReader<?>> children,
		final Function<Object[], T> creator
	) {
		super(name, attrs);
		_creator = creator;

		_children = requireNonNull(children);
		for (XMLReader<?> child : children) {
			_childMap.put(child.name(), child);
		}
	}

	@Override
	public int argSize() {
		return _attrs.size() + _childMap.size();
	}

	@Override
	public T read(final XMLStreamReader reader) throws XMLStreamException {
		final Map<String, Object> param = new HashMap<>();
		for (Attr attr : _attrs) {
			final Object value = reader.getAttributeValue(null, attr.name);
			param.put(attr.name, value);
		}

		while (reader.hasNext()) {
			switch (reader.next()) {
				case XMLStreamReader.START_ELEMENT:
					final XMLReader<?> child = _childMap.get(reader.getLocalName());
					if (child != null) {
						param.put(child.name(), child.read(reader));
					}
				case XMLStreamReader.END_ELEMENT:
					if (name().equals(reader.getLocalName())) {
						final Object[] args = new Object[argSize()];
						for (int i = 0; i < _attrs.size(); ++i) {
							args[i] = param.get(_attrs.get(i).name);
						}
						for (int i = 0; i < _children.size(); ++i) {
							args[_attrs.size() + i] = param.get(_children.get(i).name());
						}
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

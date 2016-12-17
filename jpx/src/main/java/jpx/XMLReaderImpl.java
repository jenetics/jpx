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

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

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
class XMLReaderImpl<T> {

	interface Fun3< R> {
		R apply(String t1, String t2, String t3);
	}

	static final class Attr {
		final String name;

		private Attr(final String name) {
			this.name = requireNonNull(name);
		}

		static Attr of(final String name) {
			return new Attr(name);
		}
	}


	private final String _name;
	private final List<Attr> _attrs;
	private final Map<String, XMLReaderImpl<?>> _children = new HashMap<>();
	final Function<Object[], T> _creator;

	XMLReaderImpl(
		final String name,
		final List<Attr> attrs,
		final List<XMLReaderImpl<?>> children,
		final Function<Object[], T> creator
	) {
		_name = name;
		_attrs = attrs;
		_creator = creator;

		for (XMLReaderImpl<?> child : children) {
			_children.put(child._name, child);
		}
	}

	String elem(final String name) {
		return name;
	}

	Attr attr(final String name) {
		return Attr.of(name);
	}

	private int argSize() {
		int size = _attrs.size() + _children.size() + 1;
		for (XMLReaderImpl<?> child : _children.values()) {
			size += child.argSize();
		}

		return size;
	}

	T read(final XMLStreamReader reader) throws XMLStreamException {
		System.out.println("READ " + _name);
		final Object[] args = new Object[argSize()];
		System.out.println("SIZE: " + args.length);
		int index = 0;
		for (; index < _attrs.size(); ++index) {
			args[index] = reader.getAttributeValue(null, _attrs.get(index).name);
		}

		while (reader.hasNext()) {
			switch (reader.next()) {
				case XMLStreamReader.START_ELEMENT:
					final String name = reader.getLocalName();
					final XMLReaderImpl<?> child = _children.get(name);
					if (child != null) {
						System.out.println(name + ": " + child._name);
						args[index] = child.read(reader);
					}
				case XMLStreamReader.END_ELEMENT:
					System.out.println("END TAG: " + _name);
					return _creator.apply(args);
			}

			++index;
		}


		throw new XMLStreamException("Premature end of file.");
	}

	public static <T> XMLReaderImpl<T> of(
		final Function<Object[], T> creator,
		final String name,
		final Attr attr,
		final XMLReaderImpl<?>... children
	) {
		return new XMLReaderImpl<T>(
			name, singletonList(attr),
			asList(children),
			creator
		);
	}

	public static XMLReaderImpl<String> of(final String name) {
		return new XMLReaderImpl<>(name, emptyList(), emptyList(), a -> (String)a[0]);
	}

}
